package oleg.sichev.personaldiary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
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

    // Дата и время создания записи в базе данных
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Метод задаст дату и время СОЗДАНИЯ (POST) записи в момент сохранения объекта в репозитории (в базе данных)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Метод задаст дату и время ИЗМЕНЕНИЯ (PATCH) записи в момент сохранения объекта в репозитории (в базе данных)
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Column(name = "Delete")
    private String delete;
}
