package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.NotificationResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.repository.db.NotificationRepository;
import init.upinmcse.backend.service.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService implements INotificationService {
    NotificationRepository notificationRepository;


    @Override
    public PageResponse<NotificationResponse> getNotifications(int page, int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = notificationRepository.findAllByToUserId(userId, pageable);

        var notifications = pageData.getContent().stream().map(
                notification -> NotificationResponse.builder()
                        .content(notification.getContent())
                        .notificationId(notification.getId())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build()).toList();


        return PageResponse.<NotificationResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(notifications)
                .build();
    }

    @Override
    public void readNotification(int notificationId) {
        var notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new ErrorException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
