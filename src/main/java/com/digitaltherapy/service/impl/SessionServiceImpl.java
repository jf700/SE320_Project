package com.digitaltherapy.service.impl;

import com.digitaltherapy.dto.SessionRequest;
import com.digitaltherapy.entity.SessionModule;
import com.digitaltherapy.repository.SessionModuleRepository;
import com.digitaltherapy.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    /*
    private final SessioModuleRepository sessionModuleRepository;

    @Override
    public SessionModule createSession(SessionRequest request) {

        SessionModule session = SessionModule.builder()
                .userId(request.getUserId())
                .therapistName(request.getTherapistName())
                .scheduledTime(request.getScheduledTime())
                .notes(request.getNotes())
                .build();

        return sessionRepository.save(session);
    }

    @Override
    public List<SessionModule> getSessionsByUser(Long userId) {
        return sessionMRepository.findByUserId(userId);
    }

    @Override
    public void cancelSession(Long sessionId) {
        sessionModuleRepository.deleteById(sessionId);
    }

     */
}