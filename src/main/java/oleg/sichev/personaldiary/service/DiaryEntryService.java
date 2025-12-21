package oleg.sichev.personaldiary.service;

import oleg.sichev.personaldiary.dto.DiaryEntryDTO;
import oleg.sichev.personaldiary.dto.DiaryEntryResponseDTO;
import oleg.sichev.personaldiary.entity.DiaryEntry;
import oleg.sichev.personaldiary.repository.DiaryEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class DiaryEntryService {

    Logger logger = LoggerFactory.getLogger(DiaryEntryService.class);

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryEntryService(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    // Все операции в одну транзакцию - ничего не прервется - ресурсы экономятся - скорость быстрее
    @Transactional(readOnly = true)
    public Page<DiaryEntryResponseDTO> getDiaryEntries(Pageable pageable) {
        Page<DiaryEntry> page = diaryEntryRepository.findAllByDeletedIsNull(pageable);

        return page.map(entry -> DiaryEntryResponseDTO.builder()
                .id(entry.getId())
                .title(entry.getTitle())
                .description(entry.getDescription())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build()
        );

    }

    public DiaryEntry postDiaryEntry(DiaryEntryDTO diaryEntryDTO) {
        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setTitle(diaryEntryDTO.getTitle());
        diaryEntry.setDescription(diaryEntryDTO.getDescription());

        return diaryEntryRepository.save(diaryEntry);
    }

    public DiaryEntry patchDiaryEntry(Long id, DiaryEntryDTO diaryEntryDTO) {
        // Если null, то выбрасывает ошибку 404, если не null - то продолжаем выполнение метода
        DiaryEntry diaryEntry = diaryEntryRepository.findByIdAndDeletedIsNull(id).orElseThrow(() -> {
            logger.error("ОШИБКА! ЗАПИСЬ С ID: {} НЕ НАЙДЕНА В ДНЕВНИКЕ!", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        if (diaryEntryDTO == null) {
            logger.error("ОШИБКА! ПЕРЕДАН ПУСТОЙ DTO!");
            return diaryEntry;
        }

        if (diaryEntryDTO.getTitle() != null && diaryEntryDTO.getDescription() != null && diaryEntryDTO.getTitle().equals(diaryEntry.getTitle()) && diaryEntryDTO.getDescription().equals(diaryEntry.getDescription())) {
            logger.info("ВСЕ ПОЛЯ У diaryEntryDTO И diaryEntry СОВПАДАЮТ, ПОЭТОМУ ИЗМЕНЕНИЙ ВНЕСЕНО НЕ БЫЛО. В БАЗУ ДАННЫХ НИКАКИХ ИЗМЕНЕНИЙ НЕ ВНЕСЕНО.\nDiaryEntryDTO.getTitle(): {}.\nDiaryEntryDTO.getDescription(): {}.\nDiaryEntry.getTitle(): {}.\nDiaryEntry.getDescription(): {}.",
                    diaryEntryDTO.getTitle(), diaryEntryDTO.getDescription(), diaryEntry.getTitle(), diaryEntry.getDescription());
            return diaryEntry;
        } else {

            if (diaryEntryDTO.getTitle() != null && !diaryEntryDTO.getTitle().equals(diaryEntry.getTitle())) {
                logger.info("НАЗВАНИЕ ЗАПИСИ ДНЕВНИКА УСПЕШНО ИЗМЕНЕНО С {} НА {}", diaryEntry.getTitle(), diaryEntryDTO.getTitle());
                diaryEntry.setTitle(diaryEntryDTO.getTitle());
            }

            if (diaryEntryDTO.getDescription() != null && !diaryEntryDTO.getDescription().equals(diaryEntry.getDescription())) {
                logger.info("ОПИСАНИЕ ЗАПИСИ ДНЕВНИКА УСПЕШНО ИЗМЕНЕНО С {} НА {}", diaryEntry.getDescription(), diaryEntryDTO.getDescription());
                diaryEntry.setDescription(diaryEntryDTO.getDescription());
            }
            return diaryEntryRepository.save(diaryEntry);
        }
    }

    public Boolean deleteDiaryEntry(Long id) {
        DiaryEntry diaryEntry = diaryEntryRepository.findByIdAndDeletedIsNull(id).orElseThrow(() -> {
            logger.error("ЗАПИСЬ ДНЕВНИКА С ID: {} НЕ НАЙДЕНА!", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        diaryEntry.setDeleted("DELETE");
        diaryEntryRepository.save(diaryEntry);

        // возвращаем true, если delete не равен null (то есть, объект мягко удален)
        return diaryEntry.getDeleted() != null;
    }
}
