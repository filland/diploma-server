package bntu.diploma.domain;

import bntu.diploma.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUserName() {
        User user = userRepository.findByUserName("username5");
        assertEquals("username5", user.getUserName());

    }

    @Test
    public void findAll() {
        assertNotNull(userRepository.findAll());
    }
}

