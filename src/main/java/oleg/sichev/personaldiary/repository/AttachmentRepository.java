package oleg.sichev.personaldiary.repository;

import oleg.sichev.personaldiary.entity.Attachment;
import oleg.sichev.personaldiary.entity.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findAllByDiaryEntryIdAndDeletedIsNull(Long diaryEntryId);

    // Ищем по типу (только не удаленные)
    List<Attachment> findAllByDiaryEntryIdAndTypeAndDeletedIsNull(
            Long diaryEntryId, AttachmentType type
    );

    // Вложения пользователя (не удаленные)
    List<Attachment> findAllByUploadedByIdAndDeletedIsNull(Long userId);

    // Выведи объект по id, не удаленный
    Optional<Attachment> findByIdAndDeletedIsNull(Long id);

    // Для проверки прав: пользователь + не удален
    Optional<Attachment> findByIdAndUploadedByIdAndDeletedIsNull(Long id, Long uploadedById);

    // Найти "осиротевшие" вложения (без diaryEntry, не удаленные)
    @Query("SELECT a FROM Attachment a WHERE a.diaryEntry IS NULL AND a.deleted IS NULL AND a.uploadedBy.id = :userId")
    List<Attachment> findOrphanedAttachments(@Param("userId") Long userId);

    // Найди все удаленные записи (файлы)
    List<Attachment> findAllByDeletedIsNull();

    List<Attachment> findAllByDeletedIsNotNullAndDeletedAtBefore(LocalDateTime deletedBefore);

    // Мягкое удаление по ID напрямую по запросу в репозиторий
    @Modifying
    @Transactional
    @Query("UPDATE Attachment a SET a.deleted = 'DELETED', a.deletedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int softDeleteById(@Param("id") Long id);

    // Восстановление напрямую через репозиторий
    @Modifying
    @Transactional
    @Query("UPDATE Attachment a SET a.deleted = NULL, a.deletedAt = NULL WHERE a.id = :id")
    long countByDiaryEntryIdAndDeletedIsNull(Long diaryEntryId);
}
