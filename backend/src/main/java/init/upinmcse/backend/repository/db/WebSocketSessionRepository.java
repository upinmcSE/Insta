package init.upinmcse.backend.repository.db;

import init.upinmcse.backend.model.WebSocketSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebSocketSessionRepository extends JpaRepository<WebSocketSession, String> {
    void deleteBySocketSessionId(String sessionId);

    List<WebSocketSession> findAllByUserIdIn(List<String> userId);
}
