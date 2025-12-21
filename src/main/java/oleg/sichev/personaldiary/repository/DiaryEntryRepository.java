package oleg.sichev.personaldiary.repository;

import oleg.sichev.personaldiary.entity.DiaryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
    Optional<DiaryEntry> findByIdAndDeletedIsNull(Long id);
    List<DiaryEntry> findAllByDeletedIsNull();
    List<DiaryEntry> findAllByDeletedIsNotNull(); // НИКАКИХ Optional в списке т.к. если записей нет, то вернется пустой список

    Page<DiaryEntry> findAllByDeletedIsNull(Pageable pageable);
}
