package init.upinmcse.backend.repository.db;

import init.upinmcse.backend.model.TokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TokenRevokedRepository extends JpaRepository<TokenRevoked, String> {
    boolean existsById(String id);
}
