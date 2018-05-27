package bntu.diploma.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest

public class WeatherServerControllerTest {

    private MockMvc mockMvc;
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
}