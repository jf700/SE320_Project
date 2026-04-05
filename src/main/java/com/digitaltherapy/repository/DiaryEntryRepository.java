package com.digitaltherapy.repository;

import com.digitaltherapy.entity.DiaryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, UUID> {

    Page<DiaryEntry> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<DiaryEntry> findByUserIdAndDeletedFalse(UUID userId);

    @Query("SELECT d.distortions FROM DiaryEntry d WHERE d.user.id = :userId AND d.deleted = false")
    List<Object[]> findTopDistortionsByUser(@Param("userId") UUID userId);

    @Query("SELECT AVG(d.moodAfter - d.moodBefore) FROM DiaryEntry d WHERE d.user.id = :userId AND d.deleted = false AND d.moodBefore IS NOT NULL AND d.moodAfter IS NOT NULL")
    Double calculateAverageMoodImprovement(@Param("userId") UUID userId);
}
