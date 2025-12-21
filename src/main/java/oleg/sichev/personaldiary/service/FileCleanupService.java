package oleg.sichev.personaldiary.service;

import lombok.extern.slf4j.Slf4j;
import oleg.sichev.personaldiary.entity.Attachment;
import oleg.sichev.personaldiary.repository.AttachmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileCleanupService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    public FileCleanupService(
            AttachmentRepository attachmentRepository,
            FileStorageService fileStorageService
    ) {
        this.fileStorageService = fileStorageService;
        this.attachmentRepository = attachmentRepository;
    }

    Logger logger = LoggerFactory.getLogger(FileCleanupService.class);

    @Scheduled(cron = "0 0 3 * * ?") // Каждый день в 3 часа ночи
    @Transactional
    public void cleanupDeletedFiles() {
        // Найти все вложения удаленные более 30 дней назад
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        List<Attachment> oldDeleted = attachmentRepository.findAllByDeletedIsNotNullAndDeletedAtBefore(monthAgo);

        for (Attachment attachment : oldDeleted) {
            try {
                // Удалить файл с диска
                fileStorageService.deleteFile(attachment.getFilePath());
                // Удалить запись из БД (hard delete)
                attachmentRepository.delete(attachment);
                logger.info("Удален файл {}", attachment.getOriginalFileName());
            } catch (IOException e) {
                logger.error("Ошибка удаления файла: {}. {}", attachment.getFilePath(), e.getMessage());
            }
        }
    }
}
