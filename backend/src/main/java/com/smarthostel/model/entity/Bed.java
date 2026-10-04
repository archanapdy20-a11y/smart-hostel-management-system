package com.smarthostel.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "beds")
public class Bed {

    public enum BedStatus {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "bed_code", nullable = false, length = 20)
    private String bedCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BedStatus status;

    public Bed() {}

    public Bed(Long id, Room room, String bedCode, BedStatus status) {
        this.id = id;
        this.room = room;
        this.bedCode = bedCode;
        this.status = status;
    }

    public static BedBuilder builder() { return new BedBuilder(); }

    public static class BedBuilder {
        private Long id;
        private Room room;
        private String bedCode;
        private BedStatus status;

        public BedBuilder id(Long id) { this.id = id; return this; }
        public BedBuilder room(Room room) { this.room = room; return this; }
        public BedBuilder bedCode(String bedCode) { this.bedCode = bedCode; return this; }
        public BedBuilder status(BedStatus status) { this.status = status; return this; }

        public Bed build() {
            return new Bed(id, room, bedCode, status);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getBedCode() { return bedCode; }
    public void setBedCode(String bedCode) { this.bedCode = bedCode; }

    public BedStatus getStatus() { return status; }
    public void setStatus(BedStatus status) { this.status = status; }
}
