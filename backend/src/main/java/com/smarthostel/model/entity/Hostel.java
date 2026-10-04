package com.smarthostel.model.entity;

import com.smarthostel.model.enums.GenderEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "hostels")
public class Hostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", nullable = false)
    private GenderEnum genderType;

    @Column(name = "total_blocks")
    private Integer totalBlocks;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Hostel() {}

    public Hostel(Long id, String name, String code, GenderEnum genderType, Integer totalBlocks, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.genderType = genderType;
        this.totalBlocks = totalBlocks;
        this.description = description;
    }

    public static HostelBuilder builder() { return new HostelBuilder(); }

    public static class HostelBuilder {
        private Long id;
        private String name;
        private String code;
        private GenderEnum genderType;
        private Integer totalBlocks;
        private String description;

        public HostelBuilder id(Long id) { this.id = id; return this; }
        public HostelBuilder name(String name) { this.name = name; return this; }
        public HostelBuilder code(String code) { this.code = code; return this; }
        public HostelBuilder genderType(GenderEnum genderType) { this.genderType = genderType; return this; }
        public HostelBuilder totalBlocks(Integer totalBlocks) { this.totalBlocks = totalBlocks; return this; }
        public HostelBuilder description(String description) { this.description = description; return this; }

        public Hostel build() {
            return new Hostel(id, name, code, genderType, totalBlocks, description);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public GenderEnum getGenderType() { return genderType; }
    public void setGenderType(GenderEnum genderType) { this.genderType = genderType; }

    public Integer getTotalBlocks() { return totalBlocks; }
    public void setTotalBlocks(Integer totalBlocks) { this.totalBlocks = totalBlocks; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
