package bntu.diploma.controller;

import bntu.diploma.model.Oblast;
import bntu.diploma.model.Station;
import bntu.diploma.repository.StationRepository;
import bntu.diploma.utils.AdvancedEncryptionStandard;
import bntu.diploma.utils.SecureTokenGenerator;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest

public class WeatherServerControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private WebApplicationContext wac;
    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void reportPositiveTest() throws Exception {

        String query = "/report?key=unique4&temp=30.1&hum=40&pres=740.0&wspeed=10.0&wdir=260&battery=300";
        mockMvc.perform(get(query)).
                andExpect(status().isOk());
    }

    @Test
    public void reportGetResourceException400Test() throws Exception {

        String q = "http://localhost:8080/report";
        mockMvc.perform(get(q)).andExpect(status().isBadRequest());
    }

    @Test
    public void reportGetResourceException400EmptyWindDirectionParameterTest() throws Exception {
        String query = "http://localhost:8080/report?key=unique ke213fdsafdsaf&temp=30.1&hum=40&pres=740.0&wspeed=10.0";
        mockMvc.perform(get(query)).andExpect(status().isBadRequest());
    }


    @Test
    public void loginAndLogoutUserTest() throws Exception {

        String apiKeyForFirstClient = "6666666666";
        String encryptionKeyForFirstClient = "asasasasasasasas";
        byte[] encryptedApiKey = AdvancedEncryptionStandard.encrypt(
                apiKeyForFirstClient.getBytes(),
                encryptionKeyForFirstClient.getBytes());

        MvcResult result = mockMvc.perform(post("http://localhost:8080/login").
                header("id", "1").content(encryptedApiKey)).andExpect(header().
                exists("key")).andReturn();

        String token = result.getResponse().getHeader("key");

        mockMvc.perform(post("http://localhost:8080/logout").
                header("token", token)).andExpect(status().isOk());
    }

    @Test
    public void logoutUserNegativeTest() throws Exception {

        String badToken = "fffEuAfffpsX8XSZNKSCX9EDSfffW46Y";

        mockMvc.perform(post("http://localhost:8080/logout").
                header("token", badToken)).andExpect(status().isUnauthorized());
    }

    @Test
    public void loginUserNegativeTest() throws Exception {

        String apiKeyForFirstClient = "bad key apparently";
        String encryptionKeyForFirstClient = "asasasasasasasas";
        byte[] encryptedApiKey = AdvancedEncryptionStandard.encrypt(
                apiKeyForFirstClient.getBytes(),
                encryptionKeyForFirstClient.getBytes());

        mockMvc.perform(post("http://localhost:8080/login").
                header("id", "1").
                content(encryptedApiKey)).
                andExpect(status().
                        isNotFound());
    }


    @Test
    public void getAllWeatherDataTest() throws Exception {


        String apiKeyForFirstClient = "6666666666";
        String encryptionKeyForFirstClient = "asasasasasasasas";
        byte[] encryptedApiKey = AdvancedEncryptionStandard.encrypt(
                apiKeyForFirstClient.getBytes(),
                encryptionKeyForFirstClient.getBytes());
        MvcResult result = mockMvc.perform(post("http://localhost:8080/login").
                header("id", "1").content(encryptedApiKey)).andExpect(header().
                exists("key")).andReturn();

        String token = result.getResponse().getHeader("key");

        mockMvc.perform(
                get("http://localhost:8080/all_weather").param("token", token)).
                andExpect(status().isOk());


    }

    @Test
    public void getAllWeatherDataNegativeTest() throws Exception {

        String badToken = "fffEuAfffpsX8XSZNKSCX9EDSfffW46Y";

        mockMvc.perform(
                get("http://localhost:8080/all_weather").param("token", badToken)).
                andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllWeatherDataOfOneStationTest() throws Exception {
        String apiKeyForFirstClient = "6666666666";
        String encryptionKeyForFirstClient = "asasasasasasasas";
        byte[] encryptedApiKey = AdvancedEncryptionStandard.encrypt(
                apiKeyForFirstClient.getBytes(),
                encryptionKeyForFirstClient.getBytes());
        MvcResult result = mockMvc.perform(post("http://localhost:8080/login").
                header("id", "1").content(encryptedApiKey)).andExpect(header().
                exists("key")).andReturn();

        String token = result.getResponse().getHeader("key");

        mockMvc.perform(
                get("http://localhost:8080/all_one_station").
                        param("token", token).
                        param("id", "1")).
                andExpect(status().isOk());
    }

    @Test
    public void getAllWeatherDataOfOneStationNegativeTest() throws Exception {

        String badToken = "fffEuAfffpsX8XSZNKSCX9EDSfffW46Y";

        mockMvc.perform(
                get("http://localhost:8080/all_one_station").
                        param("token", badToken).
                        param("id", "1")).
                andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllStationsInfoTest() throws Exception {
        String apiKeyForFirstClient = "6666666666";
        String encryptionKeyForFirstClient = "asasasasasasasas";
        byte[] encryptedApiKey = AdvancedEncryptionStandard.encrypt(
                apiKeyForFirstClient.getBytes(),
                encryptionKeyForFirstClient.getBytes());
        MvcResult result = mockMvc.perform(post("http://localhost:8080/login").
                header("id", "1").content(encryptedApiKey)).
                andExpect(header().
                exists("key")).andReturn();

        String token = result.getResponse().getHeader("key");

        mockMvc.perform(
                get("http://localhost:8080/all_stations").
                        param("token", token)).
                andExpect(status().isOk());
    }

    @Test
    public void getAllStationsInfoNegativeTest() throws Exception {

        String badToken = "fffEuAfffpsX8XSZNKSCX9EDSfffW46Y";

        mockMvc.perform(
                get("http://localhost:8080/all_stations").
                        param("token", badToken)).
                andExpect(status().isUnauthorized());
    }

    @Test
    public void addNewStationTest() throws Exception {


        String apiKeyForFirstClient = "6666666666";
        String encryptionKeyForFirstClient = "asasasasasasasas";
        byte[] encryptedApiKey = AdvancedEncryptionStandard.encrypt(
                apiKeyForFirstClient.getBytes(),
                encryptionKeyForFirstClient.getBytes());

        MvcResult result = mockMvc.perform(post("http://localhost:8080/login").
                header("id", "1").content(encryptedApiKey)).andExpect(header().
                exists("key")).andReturn();

        String token = result.getResponse().getHeader("key");

        Station station = new Station();
        station.setStationUniqueKey("aaaa33aaaa");
        station.setNearestTown("Vitebsk");
        station.setStationLatitude(33.333);
        station.setStationLongitude(44.444);
        station.setLastInspection("2018-05-22 23:25");
        station.setInstallationDate("2018-05-22 23:25");

        Oblast oblast = new Oblast();
        oblast.setOblastsId(2L);
        station.setOblast(oblast);

        Gson g = new Gson();
        String stationAsJson = g.toJson(station);


        mockMvc.perform(post("http://localhost:8080/add_station").
                header("token", token).
                content(stationAsJson)).
                andExpect(status().isOk());

        Long stationsId = stationRepository.findAll().get(stationRepository.findAll().size() - 1).getStationsId();

        mockMvc.perform(put("http://localhost:8080/delete_station").
                header("token", token).
                header("station_id", stationsId)).
                andExpect(status().isOk());

    }

    @Test
    public void addNewStationNegativeTest() throws Exception {

        String badToken = "fffEuAfffpsX8XSZNKSCX9EDSfffW46Y";

        Station station = new Station();
        station.setStationUniqueKey("11aa33aaaa");
        station.setNearestTown("Vitebsk");
        station.setStationLatitude(33.333);
        station.setStationLongitude(44.444);
        station.setLastInspection("3018-05-22 23:25");
        station.setInstallationDate("3018-05-22 23:25");

        Oblast oblast = new Oblast();
        oblast.setOblastsId(2L);
        station.setOblast(oblast);

        Gson g = new Gson();
        String stationAsJson = g.toJson(station);


        mockMvc.perform(post("http://localhost:8080/add_station").
                header("token", badToken).
                content(stationAsJson)).
                andExpect(status().isUnauthorized());
    }

    @Test
    public void serverIsAvailableTest() throws Exception {

        mockMvc.perform(
                get("http://localhost:8080/available")).andExpect(status().isOk());
    }


    @Test
    public void serverIsAvailableNegativeTest() throws Exception {

        mockMvc.perform(
                get("http://localhost:8080/available")).andExpect((status().isOk()));
    }



}