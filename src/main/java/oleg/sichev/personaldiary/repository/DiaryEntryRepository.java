package oleg.sichev.personaldiary.repository;

import oleg.sichev.personaldiary.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
    Optional<DiaryEntry> findByIdAndDeleteIsNull(Long id);
    List<DiaryEntry> findAllByDeleteIsNull();
    List<DiaryEntry> findAllByDeleteIsNotNull(); // НИКАКИХ Optional в списке т.к. если записей нет, то вернется пустой список
}
