package oleg.sichev.personaldiary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import oleg.sichev.personaldiary.dto.DiaryEntryDTO;

import oleg.sichev.personaldiary.service.DiaryEntryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<?> getDiaryEntries(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                             Pageable pageAble) {
        // Реализована пагинация, сортируется по дате создания, возвращается по 10 записей на странице. Страницы считаются с 0.
        return ResponseEntity.ok(diaryEntryService.getDiaryEntries(pageAble));
    }

    @PostMapping("/entries/add")
    // @Valid - используется для проверки корректно тела запроса (@RequestBody), что все поля заполнены и все хорошо
    public ResponseEntity<?> postDiaryEntry(@Valid @RequestBody DiaryEntryDTO diaryEntryDTO) {
        // все операции происходят в сервисе, в т.ч. сохранение в репозитории
        return ResponseEntity.ok(diaryEntryService.postDiaryEntry(diaryEntryDTO));
    }

    @PatchMapping("/entries-edit/{id}")
    // @Min - это аннотация - проверка, что id будет не меньше единицы. 0 - некорректное значение. Используется вместо @Valid
    public ResponseEntity<?> patchDiaryEntry(@PathVariable @Min(1) Long id, @Valid @RequestBody DiaryEntryDTO diaryEntryDTO) {
        // все операции происходят в сервисе, в т.ч. сохранение в репозитории
        return ResponseEntity.ok(diaryEntryService.patchDiaryEntry(id, diaryEntryDTO));
    }

    @DeleteMapping("/entries-delete/{id}")
    public ResponseEntity<?> deleteDiaryEntry(@PathVariable @Min(1) Long id) {
        return diaryEntryService.deleteDiaryEntry(id) ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
