package com.echo.acknowledgehub.repository;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.Target;
import com.echo.acknowledgehub.constant.ReceiverType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface TargetRepository extends JpaRepository<Target, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Target> findByAnnouncementAndReceiverTypeAndSendTo(Announcement announcement, ReceiverType receiverType, Long sendTo);
}
