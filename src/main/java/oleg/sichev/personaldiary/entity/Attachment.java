package oleg.sichev.personaldiary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Setter
@Getter
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "stored_file_name")
    private String storedFileName;

    @Column(name = "file_path")
    private String filePath;

    // Формат image/jpeg, application/pdf
    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AttachmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_entry_id")
    private DiaryEntry diaryEntry;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "description")
    private String description;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    @Column(name = "deleted")
    private String deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Дата и время - когда удалили


    // Удобные методы
    public boolean isImage() {
        return type == AttachmentType.IMAGE;
    }

    public boolean isDocument() {
        return type == AttachmentType.DOCUMENT;
    }

    public boolean isAudio() {
        return type == AttachmentType.AUDIO;
    }

    public boolean isVideo() {
        return type == AttachmentType.VIDEO;
    }

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
