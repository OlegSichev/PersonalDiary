package oleg.sichev.personaldiary.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "diary_entries")
public class DiaryEntry {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "Delete")
    private String delete;
}
