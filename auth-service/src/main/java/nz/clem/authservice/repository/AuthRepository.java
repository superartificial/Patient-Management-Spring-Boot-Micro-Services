package nz.clem.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository {
    boolean existsByUsername(String username);
}
