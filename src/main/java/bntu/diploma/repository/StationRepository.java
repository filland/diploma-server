package bntu.diploma.repository;

import bntu.diploma.domain.Oblast;
import bntu.diploma.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    Station findByStationUniqueKey(String stationUniqueKey);

    List<Station> findByOblast(Oblast oblast);

    List<Station> findByInstallationDate(String installationDate);
}
