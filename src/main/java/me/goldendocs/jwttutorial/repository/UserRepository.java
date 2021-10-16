package me.goldendocs.jwttutorial.repository;

import me.goldendocs.jwttutorial.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // username을 기준으로 user 정보를 가져올 때 권한 정보도 같이 가져옴
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
