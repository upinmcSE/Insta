package init.upinmcse.backend.repository;

import init.upinmcse.backend.model.TokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRevokedRepository extends JpaRepository<TokenRevoked, String> {
    boolean existsById(String id);
}
