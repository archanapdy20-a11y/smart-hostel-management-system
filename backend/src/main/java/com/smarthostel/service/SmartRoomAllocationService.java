package com.smarthostel.service;

import com.smarthostel.model.entity.*;
import com.smarthostel.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SmartRoomAllocationService {

    private final BedRepository bedRepository;
    private final StudentRepository studentRepository;
    private final RoomAllocationRepository roomAllocationRepository;

    public SmartRoomAllocationService(BedRepository bedRepository, StudentRepository studentRepository, RoomAllocationRepository roomAllocationRepository) {
        this.bedRepository = bedRepository;
        this.studentRepository = studentRepository;
        this.roomAllocationRepository = roomAllocationRepository;
    }

    public static class RoomRecommendation {
        private Room room;
        private Bed availableBed;
        private double suitabilityScore;
        private String matchReason;

        public RoomRecommendation() {}

        public RoomRecommendation(Room room, Bed availableBed, double suitabilityScore, String matchReason) {
            this.room = room;
            this.availableBed = availableBed;
            this.suitabilityScore = suitabilityScore;
            this.matchReason = matchReason;
        }

        public static RoomRecommendationBuilder builder() { return new RoomRecommendationBuilder(); }

        public static class RoomRecommendationBuilder {
            private Room room;
            private Bed availableBed;
            private double suitabilityScore;
            private String matchReason;

            public RoomRecommendationBuilder room(Room room) { this.room = room; return this; }
            public RoomRecommendationBuilder availableBed(Bed availableBed) { this.availableBed = availableBed; return this; }
            public RoomRecommendationBuilder suitabilityScore(double suitabilityScore) { this.suitabilityScore = suitabilityScore; return this; }
            public RoomRecommendationBuilder matchReason(String matchReason) { this.matchReason = matchReason; return this; }

            public RoomRecommendation build() {
                return new RoomRecommendation(room, availableBed, suitabilityScore, matchReason);
            }
        }

        public Room getRoom() { return room; }
        public void setRoom(Room room) { this.room = room; }

        public Bed getAvailableBed() { return availableBed; }
        public void setAvailableBed(Bed availableBed) { this.availableBed = availableBed; }

        public double getSuitabilityScore() { return suitabilityScore; }
        public void setSuitabilityScore(double suitabilityScore) { this.suitabilityScore = suitabilityScore; }

        public String getMatchReason() { return matchReason; }
        public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
    }

    public List<RoomRecommendation> recommendRoomsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Bed> availableBeds = bedRepository.findByStatus(Bed.BedStatus.AVAILABLE);
        List<RoomRecommendation> recommendations = new ArrayList<>();

        for (Bed bed : availableBeds) {
            Room room = bed.getRoom();
            Hostel hostel = room.getBlock().getHostel();

            if (student.getGender() != null && hostel.getGenderType() != student.getGender()) {
                continue;
            }

            double score = 50.0;
            List<String> reasons = new ArrayList<>();

            long sameYearOccupants = roomAllocationRepository.findAll().stream()
                    .filter(a -> a.getBed().getRoom().getId().equals(room.getId()) && a.getStatus() == RoomAllocation.AllocationStatus.ACTIVE)
                    .map(RoomAllocation::getStudent)
                    .filter(s -> Objects.equals(s.getAcademicYear(), student.getAcademicYear()))
                    .count();

            if (sameYearOccupants > 0) {
                score += 20.0;
                reasons.add("Shares academic year with room occupants");
            }

            long sameDeptOccupants = roomAllocationRepository.findAll().stream()
                    .filter(a -> a.getBed().getRoom().getId().equals(room.getId()) && a.getStatus() == RoomAllocation.AllocationStatus.ACTIVE)
                    .map(RoomAllocation::getStudent)
                    .filter(s -> Objects.equals(s.getDepartment(), student.getDepartment()))
                    .count();

            if (sameDeptOccupants > 0) {
                score += 15.0;
                reasons.add("Department peer synergy (" + student.getDepartment() + ")");
            }

            score += (10.0 / room.getCapacity());

            recommendations.add(RoomRecommendation.builder()
                    .room(room)
                    .availableBed(bed)
                    .suitabilityScore(Math.min(score, 100.0))
                    .matchReason(reasons.isEmpty() ? "Standard available room" : String.join(", ", reasons))
                    .build());
        }

        recommendations.sort((a, b) -> Double.compare(b.getSuitabilityScore(), a.getSuitabilityScore()));
        return recommendations;
    }
}
