package bntu.diploma.repository;

import bntu.diploma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
 * CRUD refers Create, Read, Update, Delete
 */

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByAccessLevel(Integer accessLevel);

    User findByApiKey(String apiKey);

    User findByUserName(String userName);
}
