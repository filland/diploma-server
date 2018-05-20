package bntu.diploma.repository;

import bntu.diploma.model.Token;
import bntu.diploma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    Token findByUserAndExpired(User user, boolean expired);
}
