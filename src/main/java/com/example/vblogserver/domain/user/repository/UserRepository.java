package com.example.vblogserver.domain.user.repository;

import com.example.vblogserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 정보 조회

    Optional<User> findByLoginid(String loginid);

    Optional<User> findByRefreshToken(String refreshToken);

    /*
    @Query(value = "select u from User u where u.id IN (select r.userId from RefreshToken r where r.refreshToken =:token)")
    Optional<User> findByRefreshTokenWithUser(@Param("token") String refreshToken);

    Optional<User> findByNickname(String nickname);

     */
}