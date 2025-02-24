package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    List<User> findByIdIn(List<Long> user_ids);

    @Query(value = "SELECT * FROM users \n" +
            "               WHERE :query IS NULL OR :query = '' OR MATCH(username, name) AGAINST(:query IN NATURAL LANGUAGE MODE) \n" +
            "               LIMIT 20",
            nativeQuery = true)
    List<User> searchUsers(@Param("query") String query);
}
