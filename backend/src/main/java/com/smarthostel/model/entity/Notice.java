package com.smarthostel.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String category;

    @Column(name = "target_role", length = 30)
    private String targetRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "published_by_user_id")
    private User publishedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Notice() {}

    public Notice(Long id, String title, String content, String category, String targetRole, User publishedBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.targetRole = targetRole;
        this.publishedBy = publishedBy;
        this.createdAt = createdAt;
    }

    public static NoticeBuilder builder() { return new NoticeBuilder(); }

    public static class NoticeBuilder {
        private Long id;
        private String title;
        private String content;
        private String category;
        private String targetRole;
        private User publishedBy;
        private LocalDateTime createdAt;

        public NoticeBuilder id(Long id) { this.id = id; return this; }
        public NoticeBuilder title(String title) { this.title = title; return this; }
        public NoticeBuilder content(String content) { this.content = content; return this; }
        public NoticeBuilder category(String category) { this.category = category; return this; }
        public NoticeBuilder targetRole(String targetRole) { this.targetRole = targetRole; return this; }
        public NoticeBuilder publishedBy(User publishedBy) { this.publishedBy = publishedBy; return this; }
        public NoticeBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Notice build() {
            return new Notice(id, title, content, category, targetRole, publishedBy, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public User getPublishedBy() { return publishedBy; }
    public void setPublishedBy(User publishedBy) { this.publishedBy = publishedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
