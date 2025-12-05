package oleg.sichev.personaldiary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import oleg.sichev.personaldiary.dto.DiaryEntryDTO;

import oleg.sichev.personaldiary.service.DiaryEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Просто Controller возвращает "как есть", RestController возвращает все в виде json
@RequestMapping("/diary")

public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;

    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    @GetMapping("/entries")
    public ResponseEntity<?> getDiaryEntries() {
        // возвращает json со списком diaryEntries (благодаря RestController - json)
        return ResponseEntity.ok(diaryEntryService.getDiaryEntries());
    }

    @PostMapping("/entries/add")
    public ResponseEntity<?> postDiaryEntry(@Valid @RequestBody DiaryEntryDTO diaryEntryDTO) {
        // все операции происходят в сервисе, в т.ч. сохранение в репозитории
        return ResponseEntity.ok(diaryEntryService.postDiaryEntry(diaryEntryDTO));
    }

    @PatchMapping("/entries-edit/{id}")
    public ResponseEntity<?> patchDiaryEntry(@PathVariable @Min(1) Long id, @Valid @RequestBody DiaryEntryDTO diaryEntryDTO) {
        // все операции происходят в сервисе, в т.ч. сохранение в репозитории
        return ResponseEntity.ok(diaryEntryService.patchDiaryEntry(id, diaryEntryDTO));
    }

    @DeleteMapping("/entries-delete/{id}")
    public ResponseEntity<?> deleteDiaryEntry(@PathVariable @Min(1) Long id) {
        return diaryEntryService.deleteDiaryEntry(id) ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
