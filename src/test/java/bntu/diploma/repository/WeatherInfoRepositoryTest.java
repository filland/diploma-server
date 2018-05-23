package bntu.diploma.repository;


import bntu.diploma.model.WeatherInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherInfoRepositoryTest {

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;

    @Test
    public void findByUserName() {
        Optional<WeatherInfo> weatherInfo = weatherInfoRepository.findById(2L);
        assertNotNull(weatherInfo.get());
    }
}