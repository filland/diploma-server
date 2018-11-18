package bntu.diploma.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 *
 *
 *
 *
 * */
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationsId;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String stationUniqueKey;

    /**
     * The oblast where the station is located
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "oblastsId", nullable = false)
    private Oblast oblast;

    @Column(nullable = false)
    private String nearestTown;

    /**
     * Coordinates of the station
     */
    @Column(nullable = false)
    private Double stationLongitude;

    @Column(nullable = false)
    private Double stationLatitude;

    /**
     * The date when the station was set up
     */
    @Column(nullable = false)
    private String installationDate;

    /**
     * The date when the station was inspected last time
     */
    @Column(nullable = false)
    private String lastInspection;

    /**
     * Shows the current level of the station's battery
     * <p>
     * The param can equal the value from 1 to 100.
     */
    private Integer currentBatteryLevel;


    /**
     * Used to place a representation of the station as a dot on a map
     */
    private Double coordinateXOnInteractiveMap;


    /**
     * Used to place a representation of the station as a dot on a map
     */
    private Double coordinateYOnInteractiveMap;

    public Station() {
    }

    public Long getStationsId() {
        return stationsId;
    }

    public void setStationsId(Long stationsId) {
        this.stationsId = stationsId;
    }

    public String getStationUniqueKey() {
        return stationUniqueKey;
    }

    public void setStationUniqueKey(String stationUniqueKey) {
        this.stationUniqueKey = stationUniqueKey;
    }

    public Oblast getOblast() {
        return oblast;
    }

    public void setOblast(Oblast oblast) {
        this.oblast = oblast;
    }

    public String getNearestTown() {
        return nearestTown;
    }

    public void setNearestTown(String nearestTown) {
        this.nearestTown = nearestTown;
    }

    public Double getStationLongitude() {
        return stationLongitude;
    }

    public void setStationLongitude(Double stationLongitude) {
        this.stationLongitude = stationLongitude;
    }

    public Double getStationLatitude() {
        return stationLatitude;
    }

    public void setStationLatitude(Double stationLatitude) {
        this.stationLatitude = stationLatitude;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getLastInspection() {
        return lastInspection;
    }

    public void setLastInspection(String lastInspection) {
        this.lastInspection = lastInspection;
    }

    public Integer getCurrentBatteryLevel() {
        return currentBatteryLevel;
    }

    public void setCurrentBatteryLevel(Integer currentBatteryLevel) {
        this.currentBatteryLevel = currentBatteryLevel;
    }

    public Double getCoordinateXOnInteractiveMap() {
        return coordinateXOnInteractiveMap;
    }

    public void setCoordinateXOnInteractiveMap(Double coordinateXOnInteractiveMap) {
        this.coordinateXOnInteractiveMap = coordinateXOnInteractiveMap;
    }

    public Double getCoordinateYOnInteractiveMap() {
        return coordinateYOnInteractiveMap;
    }

    public void setCoordinateYOnInteractiveMap(Double coordinateYOnInteractiveMap) {
        this.coordinateYOnInteractiveMap = coordinateYOnInteractiveMap;
    }
}
