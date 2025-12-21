package oleg.sichev.personaldiary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "diary_entries")
public class DiaryEntry {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    // Описание (description) - основной контент. Может содержать маркеры [attachment:X]
    // columnDefinition = "TEXT" - означает,
    // что Мы создаем поле в БД не Varchar, а TEXT (можем хранить огромное количество текста)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "diaryEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setDiaryEntry(this); // неоч понимаю, как работает, спросить у ИИ
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setDiaryEntry(this);
    }

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

    @Column(name = "deleted")
    private String deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    // Удобные методы

    // Объект удален - возвращает true, объект НЕ удален - возвращает false
    public boolean isDeleted() {
        return deleted != null && !deleted.isEmpty();
    }

    public void delete() {
        this.deleted = "DELETED";
        this.deletedAt = LocalDateTime.now();
    }

    // Метод убирает маркер удаление, т.е. ВОССТАНАВЛИВАЕТ ОБЪЕКТ
    public void restore() {
        this.deleted = null;
        this.deletedAt = null;
    }
}
