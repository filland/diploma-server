package bntu.diploma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class WeatherInfo {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long weatherInfoId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "stationsId", nullable = false)
    private Station station;

    @Column(nullable = false)
    private String dateTime;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double pressure;

    @Column(nullable = false)
    private Double humidity;

    @Column(nullable = false)
    private Double windSpeed;

    @Column(nullable = false)
    private Integer windDirection;

    public WeatherInfo() {  }

    public WeatherInfo(String dateTime, double temperature, double pressure, double humidity, double windSpeed, int windDirection) {

        this.dateTime = dateTime;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }


    public Long getWeatherInfoId() {
        return weatherInfoId;
    }

    public void setWeatherInfoId(Long weatherInfoId) {
        this.weatherInfoId = weatherInfoId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Integer getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Integer windDirection) {
        this.windDirection = windDirection;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
