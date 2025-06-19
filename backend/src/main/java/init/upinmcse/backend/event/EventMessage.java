package init.upinmcse.backend.event;

import init.upinmcse.backend.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {
    private EventType eventType;
    private Object data;
    private LocalDateTime timestamp;
}
