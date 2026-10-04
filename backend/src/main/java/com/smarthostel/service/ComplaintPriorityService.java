package com.smarthostel.service;

import com.smarthostel.model.entity.Complaint;
import com.smarthostel.model.entity.ComplaintCategory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ComplaintPriorityService {

    private static final List<String> EMERGENCY_KEYWORDS = Arrays.asList(
            "fire", "leakage", "spark", "short circuit", "gas", "flood", "breakage", "water overflow", "electric shock"
    );

    private static final List<String> HIGH_KEYWORDS = Arrays.asList(
            "no water", "power failure", "lock broken", "theft", "drainage block", "fan broken"
    );

    public Complaint.Priority calculatePriority(String title, String description, ComplaintCategory category) {
        String combined = (title + " " + description).toLowerCase();

        // 1. Keyword check for immediate emergency
        for (String keyword : EMERGENCY_KEYWORDS) {
            if (combined.contains(keyword)) {
                return Complaint.Priority.EMERGENCY;
            }
        }

        for (String keyword : HIGH_KEYWORDS) {
            if (combined.contains(keyword)) {
                return Complaint.Priority.HIGH;
            }
        }

        // 2. Category weight check
        double baseWeight = category != null && category.getBaseWeight() != null ? category.getBaseWeight() : 0.4;

        if (baseWeight >= 0.8) {
            return Complaint.Priority.HIGH;
        } else if (baseWeight >= 0.5) {
            return Complaint.Priority.MEDIUM;
        } else {
            return Complaint.Priority.LOW;
        }
    }
}
