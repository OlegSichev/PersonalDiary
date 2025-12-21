package oleg.sichev.personaldiary.service;

import oleg.sichev.personaldiary.entity.AttachmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.allowed-images}")
    private String allowedImages;

    @Value("${app.upload.allowed-documents}")
    private String allowedDocuments;

    @Value("${app.upload.allowed-audio}")
    private String allowedAudio;

    @Value("${app.upload.allowed-video}")
    private String allowedVideo;

    @Value("${app.upload.max-image-size}")
    private long maxImageSize;

    @Value("${app.upload.max-document-size}")
    private long maxDocumentSize;

    @Value("${app.upload.max-audio-size}")
    private long maxAudioSize;

    @Value("${app.upload.max-video-size}")
    private long maxVideoSize;

    private Path fileStorageLocation;

    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
            logger.info("Директория для загрузок создана: {}", this.fileStorageLocation);

            // Создаем поддиректории для каждого типа файлов
            for (AttachmentType type : AttachmentType.values()) {
                String subdirectory = getTypeSubdirectory(type);
                Path typeDirectory = this.fileStorageLocation.resolve(subdirectory);
                Files.createDirectories(typeDirectory);
                logger.debug("Создана поддиректория: {}", typeDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для загрузок: " + uploadDir, e);
        }
    }

    /**
     * Сохраняет файл на диск и возвращает относительный путь
     */

    public String storeFile(MultipartFile multipartFile, AttachmentType attachmentType) throws IOException {
        // 1. Валидация
        validateFile(multipartFile, attachmentType);

        // 2. Нормализация имени файла
        String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        // 3. Проверка безопасности
        if (originalFileName.contains("..")) {
            throw new SecurityException("Имя файла содержит небезопасную последовательность: " + originalFileName);
        }

        // 4. Генерация уникального имени
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        // 5. Определяем поддиректорию по типу
        String typeSubdirectory = getTypeSubdirectory(attachmentType);
        Path typeDirectory = this.fileStorageLocation.resolve(typeSubdirectory);

        // 6. Сохраняем файл
        Path targetLocation = typeDirectory.resolve(storedFileName);
        Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 7. Формируем относительный путь для БД
        String relativePath = typeSubdirectory + "/" + storedFileName;

        logger.info("Файл сохранен: {} -> {}", originalFileName, relativePath);
        return relativePath;
    }

    /**
     * Загружает файл как Resource
     */
    public Resource loadFileAdResource(String filePath) {
        try {
            Path fullPath = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(fullPath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден или недоступен: " + filePath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка загрузки файла: " + filePath, e);
        }
    }

    /**
     * Удаляет файл с диска
     */
    public void deleteFile(String filePath) throws IOException {
        Path fullPath = this.fileStorageLocation.resolve(filePath).normalize();
        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
            logger.debug("Файл удален с диска: {}", filePath);
        } else {
            logger.warn("Файл не найден для удаления: {}", filePath);
        }
    }

    /**
     * Проверяет - существует ли файл
     */
    public boolean fileExists(String filePath) {
        Path fullPath = this.fileStorageLocation.resolve(filePath).normalize();
        return Files.exists(fullPath) && Files.exists(fullPath);
    }

    /**
     * Получает размер файла в байтах
     */
    public long getFileSize(String filePath) throws IOException {
        Path fullPath = this.fileStorageLocation.resolve(filePath).normalize();
        return Files.size(fullPath);
    }

    /**
     * Получает MIME тип файла по расширению
     */
    public String getMimeType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".pdf" -> "application/pdf";
            case ".doc" -> "application/msword";
            case ".docs" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".txt>" -> "text/plain";
            case ".mp3" -> "audio/mpeg";
            case ".wav" -> "audio/wav";
            case ".ogg" -> "audio/ogg";
            case ".mp4" -> "video/mp4";
            case "webm" -> "video/webm";
            default -> "application/octet-stream";
        };
    }

    /**
     * Валидация файла
     */
    private void validateFile(MultipartFile multipartFile, AttachmentType attachmentType) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        // Проверка размера
        long maxSize = getMaxSizeForType(attachmentType);
        if (multipartFile.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("Размер файла превышает допустимый лимит: %.2f MB (максимум %.2f MB)",
                            multipartFile.getSize() / 1024.0 / 1024.0,
                            maxSize / 1024.0 / 1024.0));
        }

        // Проверка MIME типа
        String contentType = multipartFile.getContentType();
        List<String> allowerTypes = getAllowedMimeTypes(attachmentType);

        if (contentType == null || !allowerTypes.contains(contentType)) {
            throw new IllegalArgumentException(
                    String.format("Неподдерживаемый тип файла: %s. Разрешены: %s.",
                            contentType, String.join(", ", allowerTypes)));
        }
    }

    private long getMaxSizeForType(AttachmentType attachmentType) {
        return switch (attachmentType) {
            case IMAGE -> maxImageSize;
            case DOCUMENT -> maxDocumentSize;
            case AUDIO -> maxAudioSize;
            case VIDEO -> maxVideoSize;
        };
    }

    private List<String> getAllowedMimeTypes(AttachmentType attachmentType) {
        String allowedTypesString = switch (attachmentType) {
            case IMAGE -> allowedImages;
            case DOCUMENT -> allowedDocuments;
            case AUDIO -> allowedAudio;
            case VIDEO -> allowedVideo;
        };
        return Arrays.asList(allowedTypesString.split(","));
    }

    private String getTypeSubdirectory(AttachmentType attachmentType) {
        return switch (attachmentType) {
            case IMAGE -> "images";
            case DOCUMENT -> "documents";
            case AUDIO -> "audio";
            case VIDEO -> "video";
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
