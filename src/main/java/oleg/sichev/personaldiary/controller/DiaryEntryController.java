package oleg.sichev.personaldiary.controller;

import oleg.sichev.personaldiary.entity.DiaryEntry;
import oleg.sichev.personaldiary.repository.DiaryEntryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Просто Controller возвращает "как есть", RestController возвращает все в виде json

public class DiaryEntryController {

    private DiaryEntryRepository diaryEntryRepository;

    public DiaryEntryController(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    @GetMapping("/entries")
    public ResponseEntity<?> getDiaryEntries() {

        // возвращает json со списком diaryEntries (никак не обработанным списком, без DTO и т.п.)
        List<DiaryEntry> diaryEntries = diaryEntryRepository.findAllByDeleteIsNull();
        return ResponseEntity.ok(diaryEntries);
    }

}
