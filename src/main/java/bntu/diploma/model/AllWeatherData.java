package bntu.diploma.model;

import bntu.diploma.model.WeatherInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllWeatherData {

    /**
     *  the key - name of the oblast, the value is a
     *  list of maps with stations where the key is the name + id of the stations
     *  the value is a list of weather info
     *
     * */
    private Map<String, List<Map>> allData;

    public AllWeatherData() {

        allData = new HashMap<>();
    }

    public Map<String, List<Map>> getAllData() {
        return allData;
    }

    public void addOblastsData(String oblastsName, List<Map> oblastsData) {
        this.allData.put(oblastsName, oblastsData);
    }
}
