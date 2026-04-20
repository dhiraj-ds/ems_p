package com.ems.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name="announcements") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Announcement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String title;
    @Enumerated(EnumType.STRING) @Builder.Default private Audience audience = Audience.ALL_STAFF;
    @Enumerated(EnumType.STRING) @Builder.Default private Priority priority = Priority.NORMAL;
    @Column(columnDefinition="TEXT") private String message;
    private LocalDate postDate;
    private String postedBy;
    public enum Audience { ALL_STAFF, ENGINEERING, MARKETING, HR, FINANCE, DESIGN, OPERATIONS }
    public enum Priority { NORMAL, IMPORTANT, URGENT, HR_UPDATE }
}