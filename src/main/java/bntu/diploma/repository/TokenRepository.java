package bntu.diploma.repository;

import bntu.diploma.domain.Token;
import bntu.diploma.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    Token findByUserAndExpired(User user, boolean expired);
}
