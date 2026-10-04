package com.smarthostel.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "complaint_categories")
public class ComplaintCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Double baseWeight;

    public ComplaintCategory() {}

    public ComplaintCategory(Long id, String name, Double baseWeight) {
        this.id = id;
        this.name = name;
        this.baseWeight = baseWeight;
    }

    public static ComplaintCategoryBuilder builder() { return new ComplaintCategoryBuilder(); }

    public static class ComplaintCategoryBuilder {
        private Long id;
        private String name;
        private Double baseWeight;

        public ComplaintCategoryBuilder id(Long id) { this.id = id; return this; }
        public ComplaintCategoryBuilder name(String name) { this.name = name; return this; }
        public ComplaintCategoryBuilder baseWeight(Double baseWeight) { this.baseWeight = baseWeight; return this; }

        public ComplaintCategory build() {
            return new ComplaintCategory(id, name, baseWeight);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getBaseWeight() { return baseWeight; }
    public void setBaseWeight(Double baseWeight) { this.baseWeight = baseWeight; }
}
