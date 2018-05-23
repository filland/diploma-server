package bntu.diploma.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherServerControllerTest {

    @Test
    public void reportPositiveTest() {
        String query = "http://localhost:8080/report?key=unique ke213fdsafdsaf&temp=30.1&hum=40&pres=740.0&wspeed=10.0&wdir=260&battery=300";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.getForEntity(query, Object.class);
        int actualStatusCode = response.getStatusCodeValue();
        assertEquals(actualStatusCode, 200);
    }

    @Test
    public void reportGetResourceException400Test() {
        String q = "http://localhost:8080/report";
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForEntity(q, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        } catch (Exception e) {
            fail("this isn't the expected exception: " + e.getMessage());
        }

    }

    @Test
    public void reportGetResourceException400EmptyBatteryParameterTest() {
        try {
            String query = "http://localhost:8080/report?key=unique ke213fdsafdsaf&temp=30.1&hum=40&pres=740.0&wspeed=10.0&wdir=260&battery=";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity response = restTemplate.getForEntity(query, Object.class);
            //fail("this isn't the expected case");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        } catch (Exception e) {
            fail("this isn't the expected exception: " + e.getMessage());
        }

    }
}