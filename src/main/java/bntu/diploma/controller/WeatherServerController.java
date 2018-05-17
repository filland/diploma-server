package bntu.diploma.controller;

import bntu.diploma.model.*;
import bntu.diploma.repository.*;
import bntu.diploma.utils.CipherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

    @Autowired
    private TokenRepository tokenRepository;


    private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat ("dd-MM-yyyy HH:mm");

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
    public Map<String, List<Map>> getAllWeatherData(@RequestParam(value = "key") String clientsAppKey){

        User user = userRepository.findByApiKey(clientsAppKey);

        if (user != null){

            System.out.println("if");

           /* if(allWeatherInfo == null){

                allWeatherInfo = new StringBuilder();



            }*/

           AllWeatherData a = new AllWeatherData();

           for (Oblast oblast : oblastRepository.findAll()){

               List <Station> stations = stationRepository.findByOblast(oblast);

               List<Map> oblastsData = new ArrayList<>();

               Map m;

               for (Station station: stations){

                   List<WeatherInfo> weatherInfos = weatherInfoRepository.findAllByStation(station);

                   m = new HashMap<>();
                   m.put("station_id", station.getStationsId());
                   m.put("town", station.getNearestTown());
                   m.put("data", weatherInfos);
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
    public void weatherReport(@RequestParam(value = "key") String stationsKey,
                              @RequestParam(value = "temp") String temperature,
                              @RequestParam(value = "hum") String humidity,
                              @RequestParam(value = "pres") String pressure,
                              @RequestParam(value = "wspeed") String windSpeed,
                              @RequestParam(value = "wdir") String windDirection,
                              @RequestParam(value = "battery") String batteryLevel){

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
                weatherInfo.setWindDirection(Integer.valueOf(windDirection));

                // save received data to the database
                weatherInfoRepository.save(weatherInfo);

                // update the station's battery level
                station.setCurrentBatteryLevel(Integer.valueOf(batteryLevel));
                stationRepository.save(station);

            } catch (NumberFormatException e) {

                e.printStackTrace();

            }

        }


        // TODO location of the station is found out by the KEY which is is meant for authentication as well
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "key") String encryptedKey,
                        @RequestParam(value = "id") String usersID){

        Optional<User> optional = userRepository.findById(Long.valueOf(usersID));
        User user;

        // if a user with such id was found
        if (optional.isPresent()){
            user = optional.get();

            String decryptionResult = CipherUtils.decrypt(encryptedKey, null);

            // if user's actual key matches the key gotten after encryption
            if (user.getApiKey().equals(decryptionResult)){

                Token usersToken = new Token();
                usersToken.setExpired(false);
                usersToken.setLoginDateTime(DATE_FORMATTER.format(new Date()));
                usersToken.setUser(user);

                // TODO generate a token
                usersToken.setToken(null);

                try {
                    tokenRepository.save(usersToken);
                } catch (Exception e) {
                    e.printStackTrace();

                    return "token was not saved";
                }


                return "success according to HTTP protocol";
            }

        }

        return null;
    }


    @RequestMapping(value = "/station", method = RequestMethod.GET)
    public List<Station> getAllStationsInfo(@RequestParam(value = "key") String stationsKey){

        return stationRepository.findAll();
    }

    @RequestMapping(value = "/add_station", method = RequestMethod.POST)
    public String addNewStation(@RequestParam(value = "token") String token,
                                @RequestParam(value = "oblast")String oblast,
                                @RequestParam(value = "installation") String installationDate,
                                @RequestParam(value = "inspection") String lastInspectionDate,
                                @RequestParam(value = "n_town") String nearestTown,
                                @RequestParam(value = "longitude") String longitude,
                                @RequestParam(value = "latitude") String latitude){

        if(tokenRepository.findByToken(token) != null){

            try {
                Station newStation = new Station();

                Oblast oblast2 = new Oblast();
                oblast2.setOblastsId(Long.valueOf(oblast));

                newStation.setOblast(oblast2);

                // TODO generator of unique keys
                newStation.setStationUniqueKey("key");

                newStation.setInstallationDate(DATE_FORMATTER.format(LocalDateTime.parse(installationDate)));
                newStation.setLastInspection(DATE_FORMATTER.format(LocalDateTime.parse(lastInspectionDate)));
                newStation.setNearestTown(nearestTown);
                newStation.setStationLongitude(Double.valueOf(longitude));
                newStation.setStationLatitude(Double.valueOf(latitude));

                stationRepository.save(newStation);

                // TODO change
                return "return a message which complies HTTP " +
                        "protocol that creation was done successfully";

            } catch (Exception e) {

                e.printStackTrace();

                return null;
            }

        } else {


            return null;
        }

    }

    @RequestMapping(value = "/change", method = RequestMethod.PUT)
    public String changeStationsInfo(String stationId, Object updatedStation){
        // TODO how to send a serialized object

        Station station = (Station) updatedStation;

        return "added";
    }


}
