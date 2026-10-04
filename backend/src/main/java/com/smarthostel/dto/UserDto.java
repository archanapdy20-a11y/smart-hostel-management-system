package com.smarthostel.dto;

import com.smarthostel.model.enums.GenderEnum;
import com.smarthostel.model.enums.RoleEnum;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private RoleEnum role;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    // Student fields
    private Long studentId;
    private String rollNumber;
    private String department;
    private Integer academicYear;
    private GenderEnum gender;

    // Warden fields
    private Long wardenId;
    private String employeeId;

    public UserDto() {}

    public UserDto(Long id, String username, String email, RoleEnum role, String firstName, String lastName, String phoneNumber, Long studentId, String rollNumber, String department, Integer academicYear, GenderEnum gender, Long wardenId, String employeeId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.department = department;
        this.academicYear = academicYear;
        this.gender = gender;
        this.wardenId = wardenId;
        this.employeeId = employeeId;
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long id;
        private String username;
        private String email;
        private RoleEnum role;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private Long studentId;
        private String rollNumber;
        private String department;
        private Integer academicYear;
        private GenderEnum gender;
        private Long wardenId;
        private String employeeId;

        public UserDtoBuilder id(Long id) { this.id = id; return this; }
        public UserDtoBuilder username(String username) { this.username = username; return this; }
        public UserDtoBuilder email(String email) { this.email = email; return this; }
        public UserDtoBuilder role(RoleEnum role) { this.role = role; return this; }
        public UserDtoBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserDtoBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public UserDtoBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public UserDtoBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public UserDtoBuilder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public UserDtoBuilder department(String department) { this.department = department; return this; }
        public UserDtoBuilder academicYear(Integer academicYear) { this.academicYear = academicYear; return this; }
        public UserDtoBuilder gender(GenderEnum gender) { this.gender = gender; return this; }
        public UserDtoBuilder wardenId(Long wardenId) { this.wardenId = wardenId; return this; }
        public UserDtoBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }

        public UserDto build() {
            return new UserDto(id, username, email, role, firstName, lastName, phoneNumber, studentId, rollNumber, department, academicYear, gender, wardenId, employeeId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public RoleEnum getRole() { return role; }
    public void setRole(RoleEnum role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getAcademicYear() { return academicYear; }
    public void setAcademicYear(Integer academicYear) { this.academicYear = academicYear; }

    public GenderEnum getGender() { return gender; }
    public void setGender(GenderEnum gender) { this.gender = gender; }

    public Long getWardenId() { return wardenId; }
    public void setWardenId(Long wardenId) { this.wardenId = wardenId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
