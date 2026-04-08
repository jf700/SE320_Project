package com.digitaltherapy.repository;

import com.digitaltherapy.entity.CbtSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CbtSessionRepository extends JpaRepository<CbtSession, UUID> {
    List<CbtSession> findAllByOrderByOrderIndexAsc();
    List<CbtSession> findByModuleIdOrderByOrderIndexAsc(UUID moduleId);
}
