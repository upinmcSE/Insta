package init.upinmcse.backend.repository.db;

import init.upinmcse.backend.model.Conversation;
import init.upinmcse.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    @Override
    Optional<Conversation> findById(String id);

    Optional<Conversation> findByHashConversation(String hashConversation);

    @Query("SELECT c FROM Conversation c JOIN FETCH c.participants p WHERE :user MEMBER OF c.participants")
    List<Conversation> findAllByParticipant(@Param("user") User user);
}
