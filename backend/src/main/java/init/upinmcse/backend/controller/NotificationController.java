package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.NotificationResponse;
import init.upinmcse.backend.service.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    INotificationService notificationService;

    @GetMapping
    public BaseResponse<PageResponse<NotificationResponse>> getNotifications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return BaseResponse.<PageResponse<NotificationResponse>>builder()
                .message("get notifications success")
                .result(notificationService.getNotifications(page, size))
                .build();
    }

    @PatchMapping("/{notificationId}")
    public BaseResponse<Void> updateNotification(@PathVariable int notificationId){
        notificationService.readNotification(notificationId);
        return BaseResponse.<Void>builder()
                .message("update notification success")
                .build();
    }
}
