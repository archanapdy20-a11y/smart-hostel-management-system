package com.smarthostel.model.entity;

import com.smarthostel.model.enums.GenderEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "roll_number", nullable = false, unique = true, length = 30)
    private String rollNumber;

    @Column(length = 50)
    private String department;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GenderEnum gender;

    @Column(name = "guardian_name", length = 100)
    private String guardianName;

    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    @Column(columnDefinition = "TEXT")
    private String address;

    public Student() {}

    public Student(Long id, User user, String rollNumber, String department, Integer academicYear, GenderEnum gender, String guardianName, String guardianPhone, String address) {
        this.id = id;
        this.user = user;
        this.rollNumber = rollNumber;
        this.department = department;
        this.academicYear = academicYear;
        this.gender = gender;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.address = address;
    }

    public static StudentBuilder builder() { return new StudentBuilder(); }

    public static class StudentBuilder {
        private Long id;
        private User user;
        private String rollNumber;
        private String department;
        private Integer academicYear;
        private GenderEnum gender;
        private String guardianName;
        private String guardianPhone;
        private String address;

        public StudentBuilder id(Long id) { this.id = id; return this; }
        public StudentBuilder user(User user) { this.user = user; return this; }
        public StudentBuilder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public StudentBuilder department(String department) { this.department = department; return this; }
        public StudentBuilder academicYear(Integer academicYear) { this.academicYear = academicYear; return this; }
        public StudentBuilder gender(GenderEnum gender) { this.gender = gender; return this; }
        public StudentBuilder guardianName(String guardianName) { this.guardianName = guardianName; return this; }
        public StudentBuilder guardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; return this; }
        public StudentBuilder address(String address) { this.address = address; return this; }

        public Student build() {
            return new Student(id, user, rollNumber, department, academicYear, gender, guardianName, guardianPhone, address);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getAcademicYear() { return academicYear; }
    public void setAcademicYear(Integer academicYear) { this.academicYear = academicYear; }

    public GenderEnum getGender() { return gender; }
    public void setGender(GenderEnum gender) { this.gender = gender; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
