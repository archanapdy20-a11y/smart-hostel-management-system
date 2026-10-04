package com.smarthostel.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wardens")
public class Warden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "employee_id", nullable = false, unique = true, length = 30)
    private String employeeId;

    @Column(length = 100)
    private String qualification;

    public Warden() {}

    public Warden(Long id, User user, String employeeId, String qualification) {
        this.id = id;
        this.user = user;
        this.employeeId = employeeId;
        this.qualification = qualification;
    }

    public static WardenBuilder builder() { return new WardenBuilder(); }

    public static class WardenBuilder {
        private Long id;
        private User user;
        private String employeeId;
        private String qualification;

        public WardenBuilder id(Long id) { this.id = id; return this; }
        public WardenBuilder user(User user) { this.user = user; return this; }
        public WardenBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public WardenBuilder qualification(String qualification) { this.qualification = qualification; return this; }

        public Warden build() {
            return new Warden(id, user, employeeId, qualification);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
}
