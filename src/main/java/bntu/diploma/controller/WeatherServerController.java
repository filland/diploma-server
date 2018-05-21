package bntu.diploma.controller;

import bntu.diploma.model.*;
import bntu.diploma.repository.*;
import bntu.diploma.utils.AdvancedEncryptionStandard;
import bntu.diploma.utils.SecureTokenGenerator;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
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
     * This request is used by weather stations to report on weather's conditions
     *
     * */
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

    /**
     *
     * This request is used to login a client application
     *
     * */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public void loginUser(@RequestHeader(value = "id") Long usersID,
                            @RequestBody byte[] encryptedKey,
                            HttpServletResponse response) {

        Optional<User> optional = userRepository.findById(usersID);
        User user;

        System.out.println("user id - " + usersID);

        if (optional.isPresent()) {
            user = optional.get();

            String decryptionResult = null;
            try {
                System.out.println("decrypt key - " + AdvancedEncryptionStandard.decrypt(encryptedKey, user.getEncryptionKey().getBytes()));
                decryptionResult = AdvancedEncryptionStandard.decrypt(encryptedKey, user.getEncryptionKey().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String generatedToken;

            // if user's actual key matches the key gotten after encryption
            if (user.getApiKey().equals(decryptionResult)) {

                Token token = tokenRepository.findByUserAndExpired(user, false);

                System.out.println("checking if there is a token");

                // user already has a token
                if (token != null && !token.isExpired()) {

                    System.out.println("get token");

                    response.setStatus(200);
                    response.setHeader("key", token.getToken());
                    // no token found
                } else {

                    Token usersToken = new Token();
                    usersToken.setExpired(false);
                    usersToken.setLoginDateTime(DATE_FORMATTER.format(new Date()));
                    usersToken.setUser(user);

                    generatedToken = SecureTokenGenerator.nextToken();

                    usersToken.setToken(generatedToken);

                    System.out.println("generated token - " + generatedToken);

                    try {
                        tokenRepository.save(usersToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    response.setHeader("key", generatedToken);
                }
            } else{
                response.setStatus(401);
            }
        } else{
            response.setStatus(401);
        }


    }

    /**
     *
     * This request is used to logout a client application
     *
     * */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public void logout(@RequestHeader(value = "token") String sessionToken,
                       HttpServletResponse response){

        Token token = tokenRepository.findByToken(sessionToken);

        System.out.println("logout triggered");

        // if the token exists
        if (token != null && !token.isExpired()){

            token.setExpired(true);
            token.setLogoutDateTime(DATE_FORMATTER.format(new Date()));
            tokenRepository.save(token);

            response.setStatus(200);
            System.out.println("user has logged out");

        } else {

            response.setStatus(400);
            System.out.println("fail while logging out");
        }

    }

    /**
     *
     * Gets all collected information from all stations by a client application
     *
     * */
    @RequestMapping(value = "/all_weather", method = RequestMethod.GET)
    public Map<String, List<Map>> getAllWeatherData(@RequestParam(value = "token") String sessionToken){


        Token token = tokenRepository.findByToken(sessionToken);

        System.out.println("A user wants to @getAllWeatherData");

        // if the token exists
        if (token != null && !token.isExpired()){

            System.out.println("User with id -"+token.getUser().getId()+" was allowed to getAllWeatherData");

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

        } else {

            System.out.println("else");
            return null;

        }
    }


    /**
     *
     * Gets all collected information on one station by a client application
     *
     * */
    @RequestMapping(value = "/all_one_station", method = RequestMethod.GET)
    public List<WeatherInfo> getAllWeatherDataOfOneStation(@RequestParam(value = "token") String sessionToken,
                                                       @RequestParam(value = "id") long stationsID){

        Token token = tokenRepository.findByToken(sessionToken);

        System.out.println("A user wants to @getAllWeatherDataOfOneStation");

        // if the token exists
        if (token != null && !token.isExpired()){

            System.out.println("User with id -"+token.getUser().getId()+" was allowed to getAllWeatherDataOfOneStation");

            Station station = new Station();
            station.setStationsId(stationsID);

            return weatherInfoRepository.findAllByStation(station);

        }

        return  null;
    }


    /**
     *
     * Gets info about all stations
     *
     * */
    @RequestMapping(value = "/all_stations", method = RequestMethod.GET)
    public List<Station> getAllStationsInfo(@RequestParam(value = "token") String sessionToken){

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null && !token.isExpired()){

            return stationRepository.findAll();

        }

        return null;
    }

    /**
     *
     * Used to add a new station
     *
     * */
    @RequestMapping(value = "/add_station", method = RequestMethod.POST)
    public void addNewStation(@RequestHeader(value = "token") String sessionToken,
                              @RequestBody String newStationAsJson,
                              HttpServletResponse response){

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null && !token.isExpired()){

            try {

                JsonObject asJsonObject = new JsonParser().parse(newStationAsJson).getAsJsonObject();

                Station newStation = new Gson().fromJson(newStationAsJson, Station.class);
                newStation.setOblast(oblastRepository.getOne(Long.valueOf(asJsonObject.get("oblast").toString())));
                newStation.setStationUniqueKey(UUID.randomUUID().toString());

//                newStation.setInstallationDate(DATE_FORMATTER.format(LocalDateTime.parse(installationDate)));
//                newStation.setLastInspection(DATE_FORMATTER.format(LocalDateTime.parse(lastInspectionDate)));
//                newStation.setNearestTown(nearestTown);
//                newStation.setStationLongitude(Double.valueOf(longitude));
//                newStation.setStationLatitude(Double.valueOf(latitude));

                stationRepository.save(newStation);

                response.setStatus(200);

            } catch (Exception e) {

                e.printStackTrace();
                response.setStatus(401);
            }

        } else {
            response.setStatus(401);
        }

    }

    @RequestMapping(value = "/change_station", method = RequestMethod.PUT)
    public void changeStationsInfo(@RequestHeader(value = "token") String sessionToken,
                                     @RequestHeader(value = "station_id") Long stationId,
                                     @RequestBody String updatedStationInfo,
                                   HttpServletResponse response){

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null && !token.isExpired()){

            System.out.println("received json - "+updatedStationInfo);

            Optional<Station> station = stationRepository.findById(stationId);

            if (station.isPresent()){

                //JsonObject rootElement = new JsonParser().parse(updatedStationInfo).getAsJsonObject();

                // deserialize json into Station
                Station updatedStation = new Gson().fromJson(updatedStationInfo, Station.class);
                updatedStation.setStationsId(station.get().getStationsId());
                updatedStation.setOblast(station.get().getOblast());
                updatedStation.setStationUniqueKey(station.get().getStationUniqueKey());


                stationRepository.save(updatedStation);

                response.setStatus(200);
            }

        } else {
            response.setStatus(401);
        }

    }


    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public void serverIsAvailable(HttpServletResponse response){

        response.setStatus(200);
    }


}
