package oleg.sichev.personaldiary.dto;

import lombok.Data;

// Используется для добавления записи в дневник
@Data
public class DiaryEntryDTO {
    private String title;
    private String description;
}
