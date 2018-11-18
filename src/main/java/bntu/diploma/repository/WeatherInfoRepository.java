package bntu.diploma.repository;

import bntu.diploma.domain.Station;
import bntu.diploma.domain.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Long> {

    List<WeatherInfo> findByDateTime(String date);

    List<WeatherInfo> findAllByStation(Station station);

    void deleteAllByStation(Station station);


}
