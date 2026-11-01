package com.smarthostel.model;

import java.sql.Date;

public class FoodMenu {
    private int menuId;
    private Date menuDate;
    private String dayOfWeek;
    private String mealType;
    private String menuItems;
    private String specialNotes;
    private Integer createdBy;
    private String createdByName;

    public FoodMenu() {}

    public FoodMenu(int menuId, Date menuDate, String dayOfWeek, String mealType,
                    String menuItems, String specialNotes, Integer createdBy) {
        this.menuId = menuId;
        this.menuDate = menuDate;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.menuItems = menuItems;
        this.specialNotes = specialNotes;
        this.createdBy = createdBy;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(String menuItems) {
        this.menuItems = menuItems;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public String toString() {
        return dayOfWeek + " (" + mealType + "): " + menuItems;
    }
}
