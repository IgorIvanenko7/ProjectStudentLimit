package com.example.ProjectStudent.service;

import com.example.ProjectStudent.Utils.DateTimeUtils;
import com.example.ProjectStudent.repo.LimitRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LimitScheduleService {

    private final LimitRepo limitRepo;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    // Ежедневное (00:00) обновление дневного лимита значением базового лимита
    public void updateDayLimit() {
        var updateDayLimitList = limitRepo.updateDayLimit();
        log.info("Ежедневное обновление дневного лимита; Дата: {}, Количество: {}",
                DateTimeUtils.uniqueTimestampMicros(), updateDayLimitList.size());
    }
}


