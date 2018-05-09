package bntu.diploma.controller;

import bntu.diploma.model.*;
import bntu.diploma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 *
 *
 * */

@RestController
public class WeatherServerController {


    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;

    @Autowired
    private OblastRepository oblastRepository;


    private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat ("ddMMyyyy HHmm");

    /**
     * Storing formed JSON with all weather info
     * */
    private StringBuilder allWeatherInfo;

    /**
     *
     * Gets all collected information from all stations
     *
     * */
    @RequestMapping(value = "/weather", method = RequestMethod.GET)
    public Map<String, List<Map<String, List<WeatherInfo>>>> getAllWeatherData(@RequestParam(value = "key") String clientsAppKey){

        User user = userRepository.findByApiKey(clientsAppKey);

        if (user != null){

            System.out.println("if");

           /* if(allWeatherInfo == null){

                allWeatherInfo = new StringBuilder();



            }*/

           AllWeatherData a = new AllWeatherData();

           for (Oblast oblast : oblastRepository.findAll()){

               List <Station> stations = stationRepository.findByOblast(oblast);

               List<Map<String, List<WeatherInfo>>> oblastsData = new ArrayList<>();

               Map<String, List<WeatherInfo>> m;

               for (Station station: stations){

                   List<WeatherInfo> weatherInfos = weatherInfoRepository.findAllByStation(station);

                   m = new HashMap<>();
                   m.put(station.getNearestTown()+"_"+station.getStationsId(), weatherInfos);
                   oblastsData.add(m);
               }

               a.addOblastsData(oblast.getName(), oblastsData);
           }

            return a.getAllData();

        } else{

            System.out.println("else");

            return null;

        }
    }


    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public String weatherReport(@RequestParam(value = "key") String stationsKey,
                              @RequestParam(value = "temp") String temperature,
                              @RequestParam(value = "hum") String humidity,
                              @RequestParam(value = "pres") String pressure,
                              @RequestParam(value = "wspeed") String windSpeed,
                              @RequestParam(value = "wdir") String windDirection){

        Station station = stationRepository.findByStationUniqueKey(stationsKey);

        if (station != null){

            WeatherInfo weatherInfo = new WeatherInfo();
            try {
                weatherInfo.setStation(station);
                weatherInfo.setDateTime(DATE_FORMATTER.format(new Date()));
                weatherInfo.setTemperature(Double.valueOf(temperature));
                weatherInfo.setHumidity(Double.valueOf(humidity));
                weatherInfo.setPressure(Double.valueOf(pressure));
                weatherInfo.setWindSpeed(Double.valueOf(windSpeed));
                weatherInfo.setWindDirection(Double.valueOf(windDirection));

                weatherInfoRepository.save(weatherInfo);
            } catch (NumberFormatException e) {

                e.printStackTrace();
                return "a problem with data";
            }

            return "done";

        } else {

            return "bad key";
        }


        // TODO location of the station is found out by the KEY which is is meant for authentication as well
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "key") String encyptedKey,
                        @RequestParam(value = "id") String usersID){
        // TODO
        return "token";
    }

    @RequestMapping(value = "/change", method = RequestMethod.POST)
    public String addNewStation(String addNecessaryParams){
        // TODO
        return "added";
    }

    @RequestMapping(value = "/change", method = RequestMethod.PUT)
    public String changeStationsInfo(String addNecessaryParams){
        // TODO
        return "added";
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public String check(){

        /*Oblast oblast=new Oblast();
        oblast.setName("Gomelskaya");
        oblastRepository.save(oblast);
        Oblast oblast1=new Oblast();
        oblast1.setName("Minskaya");
        oblastRepository.save(oblast1);
        Oblast oblast2=new Oblast();
        oblast2.setName("VItebskaya");
        oblastRepository.save(oblast2);*/


        /*Station station = new Station();
        Oblast ob = new Oblast();
        ob.setOblastsId(1L);
        station.setOblast(ob);
        station.setStationLatitude(1.1);
        station.setStationLongitude(1.1);
        station.setNearestTown("minsk");
        station.setStationUniqueKey("1234567890");
        station.setInstallationDate("03041999");
        station.setLastInspection("05052010");
        stationRepository.save(station);*/

        Station s = new Station();
        s.setStationsId(2L);

        WeatherInfo weatherInfo = new WeatherInfo("1", 10.8,10.8, 1.1, 11.1, 1.2);
        weatherInfo.setStation(s);
        WeatherInfo weatherInfo2 = new WeatherInfo("2", 10.8,10.8, 1.1, 11.1, 1.2);
        weatherInfo2.setStation(s);
        WeatherInfo weatherInfo3 = new WeatherInfo("3", 10.8,10.8, 1.1, 11.1, 1.2);
        weatherInfo3.setStation(s);
        WeatherInfo weatherInfo4 = new WeatherInfo("4", 10.8,10.8, 1.1, 11.1, 1.2);
        weatherInfo4.setStation(s);

        weatherInfoRepository.save(weatherInfo);
        weatherInfoRepository.save(weatherInfo2);
        weatherInfoRepository.save(weatherInfo3);
        weatherInfoRepository.save(weatherInfo4);




        return "done";
    }

}
