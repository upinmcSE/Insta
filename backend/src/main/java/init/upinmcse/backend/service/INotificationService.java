package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.NotificationResponse;

public interface INotificationService {
    PageResponse<NotificationResponse> getNotifications(int page, int size);
    void readNotification(int notificationId);
}
