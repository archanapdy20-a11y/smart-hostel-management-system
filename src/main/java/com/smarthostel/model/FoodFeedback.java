package com.smarthostel.model;

import java.sql.Date;
import java.sql.Timestamp;

public class FoodFeedback {
    private int feedbackId;
    private int studentId;
    private Date menuDate;
    private String mealType;
    private int rating; // 1 to 5
    private String comments;
    private Timestamp submittedAt;

    // Display helpers
    private String studentName;
    private String registerNumber;

    public FoodFeedback() {}

    public FoodFeedback(int feedbackId, int studentId, Date menuDate, String mealType,
                        int rating, String comments, Timestamp submittedAt) {
        this.feedbackId = feedbackId;
        this.studentId = studentId;
        this.menuDate = menuDate;
        this.mealType = mealType;
        this.rating = rating;
        this.comments = comments;
        this.submittedAt = submittedAt;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
}
