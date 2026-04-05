package com.digitaltherapy.repository;

import com.digitaltherapy.entity.UserSession;
import com.digitaltherapy.entity.UserSession.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    List<UserSession> findByUserIdOrderByStartedAtDesc(UUID userId);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :userId AND us.startedAt BETWEEN :start AND :end")
    List<UserSession> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(us) FROM UserSession us WHERE us.user.id = :userId AND us.status = 'COMPLETED'")
    long countCompletedSessionsByUser(@Param("userId") UUID userId);

    Optional<UserSession> findFirstByUserIdAndStatusOrderByStartedAtDesc(UUID userId, SessionStatus status);
}
