package bntu.diploma.controller;

import bntu.diploma.domain.Station;
import bntu.diploma.domain.User;
import bntu.diploma.domain.Token;
import bntu.diploma.domain.Oblast;
import bntu.diploma.domain.WeatherInfo;
import bntu.diploma.domain.AllWeatherData;
import bntu.diploma.repository.StationRepository;
import bntu.diploma.repository.UserRepository;
import bntu.diploma.repository.WeatherInfoRepository;
import bntu.diploma.repository.OblastRepository;
import bntu.diploma.repository.TokenRepository;
import bntu.diploma.utils.AdvancedEncryptionStandard;
import bntu.diploma.utils.SecureTokenGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 *
 *
 * */

@RestController
public class WeatherServerController {

    private final Logger logger = Logger.getLogger(WeatherServerController.class);

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

    private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    /**
     * Storing formed JSON with all weather info
     */
    private StringBuilder allWeatherInfo;

    /**
     * This request is used by weather stations to report on weather's conditions
     */
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public void weatherReport(@RequestParam(value = "key") String stationsKey,
                              @RequestParam(value = "temp") String temperature,
                              @RequestParam(value = "hum") String humidity,
                              @RequestParam(value = "pres") String pressure,
                              @RequestParam(value = "wspeed") String windSpeed,
                              @RequestParam(value = "wdir") String windDirection,
                              @RequestParam(value = "battery") Integer batteryLevel,
                              HttpServletResponse response) {

        logger.info("====> Station with key = " + stationsKey + "reporting");

        Station station = stationRepository.findByStationUniqueKey(stationsKey);

        if (station != null) {

            logger.info("Reading received weather info");

            WeatherInfo weatherInfo = new WeatherInfo();
            try {
                weatherInfo.setStation(station);
                weatherInfo.setDateTime(DATE_FORMATTER.format(new Date()));
                weatherInfo.setTemperature(Double.valueOf(temperature));
                weatherInfo.setHumidity(Double.valueOf(humidity));
                weatherInfo.setPressure(Double.valueOf(pressure));
                weatherInfo.setWindSpeed(Double.valueOf(windSpeed));
                weatherInfo.setWindDirection(Integer.valueOf(windDirection));
                weatherInfo.setBatteryLevel(batteryLevel);

                // save received data to the database
                weatherInfoRepository.save(weatherInfo);

                // update the station's battery level
                station.setCurrentBatteryLevel(batteryLevel);
                stationRepository.save(station);

                logger.info("weather data was successfully save to the table");
                response.setStatus(HttpStatus.OK.value());

            } catch (NumberFormatException e) {

                logger.error("a parameter received from the station is invalid");
                logger.error(e.getMessage());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }

        } else {

            logger.info("No station with the key " + stationsKey + " found");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

        }


        // TODO location of the station is found out by the KEY which is is meant for authentication as well
    }

    /**
     * This request is used to login a client application
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public void loginUser(@RequestHeader(value = "id") Long usersID,
                          @RequestBody byte[] encryptedKey,
                          HttpServletResponse response) {

        logger.info("====> user with id = " + usersID + " trying to log in");

        Optional<User> optional = userRepository.findById(usersID);
        User user;

        if (optional.isPresent()) {

            logger.info("The user's id exists");

            user = optional.get();

            String decryptionResult = null;
            try {

                logger.info("Decrypting encrypted key");
                decryptionResult = AdvancedEncryptionStandard.decrypt(encryptedKey, user.getEncryptionKey().getBytes());

                logger.info("Decrypted successfully");

            } catch (Exception e) {

                logger.error("Error while decrypting encrypted key ");
                logger.error(e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }

            String generatedToken;

            // if user's actual key matches the key gotten after encryption
            if (user.getApiKey().equals(decryptionResult)) {

                logger.info("Decrypted key equals the original key");

                Token token = tokenRepository.findByUserAndExpired(user, false);

                // user already has a token
                if (token != null && !token.isExpired()) {

                    logger.info("Token for the user exists");

                    response.setStatus(HttpStatus.OK.value());

                    logger.info("Return the existing token = " + token.getToken());

                    response.setHeader("key", token.getToken());

                    // no token found -> generate new token
                } else {

                    logger.info("Generating new token for user id = " + usersID);

                    Token usersToken = new Token();
                    usersToken.setExpired(false);
                    usersToken.setLoginDateTime(DATE_FORMATTER.format(new Date()));
                    usersToken.setUser(user);

                    generatedToken = SecureTokenGenerator.nextToken();

                    usersToken.setToken(generatedToken);

                    tokenRepository.save(usersToken);

                    response.setHeader("key", generatedToken);
                    response.setStatus(HttpStatus.OK.value());

                    logger.info("new token for user id = " + usersID + " generated.");
                }

            } else {

                logger.info("decrypted key does not equal the origin key");
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        } else {

            logger.info("Such user id not found");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }


    }

    /**
     * This request is used to logout a client application
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public void logout(@RequestHeader(value = "token") String sessionToken,
                       HttpServletResponse response) {

        logger.info("====> User is logging out");

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired (meaning user has already logged out");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }

            logger.info("Session token provided and is not expired");

            token.setExpired(true);
            token.setLogoutDateTime(DATE_FORMATTER.format(new Date()));
            tokenRepository.save(token);

            response.setStatus(HttpStatus.OK.value());

            logger.info("User has logged out successfully");

        } else {

            response.setStatus(HttpStatus.FORBIDDEN.value());
            logger.info("Error. Session token is not found");
        }

    }

    /**
     * Gets all collected information from all stations by a client application
     */
    @RequestMapping(value = "/all_weather", method = RequestMethod.GET)
    public Map<String, List<Map>> getAllWeatherData(@RequestParam(value = "token") String sessionToken,
                                                    HttpServletResponse response) {

        logger.info("====> user with session token = " + sessionToken + " querying all weather data");

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                return null;
            }

            logger.info("User with id = " + token.getUser().getId() + " was allowed to getAllWeatherData");

            AllWeatherData a = new AllWeatherData();

            for (Oblast oblast : oblastRepository.findAll()) {

                List<Station> stations = stationRepository.findByOblast(oblast);
                List<Map> oblastsData = new ArrayList<>();
                Map m;

                for (Station station : stations) {
                    List<WeatherInfo> weatherInfos = weatherInfoRepository.findAllByStation(station);

                    m = new HashMap<>();
                    m.put("station_id", station.getStationsId());
                    m.put("town", station.getNearestTown());
                    m.put("data", weatherInfos);
                    oblastsData.add(m);
                }

                a.addOblastsData(oblast.getName(), oblastsData);
            }

            response.setStatus(HttpStatus.OK.value());

            logger.info("all weather info gathered and is about to be sent");

            return a.getAllData();

        } else {

            logger.info("Error. Session token is not found");
            response.setStatus(HttpStatus.FORBIDDEN.value());

            return null;
        }
    }


    /**
     * Gets all collected information on one station by a client application
     */
    @RequestMapping(value = "/all_one_station", method = RequestMethod.GET)
    public List<WeatherInfo> getAllWeatherDataOfOneStation(@RequestParam(value = "token") String sessionToken,
                                                           @RequestParam(value = "id") long stationsID,
                                                           HttpServletResponse response) {

        logger.info("====> user with session token = " + sessionToken + " querying all weather data for station with id= " + stationsID);

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                return null;
            }

            logger.info("User with id -" + token.getUser().getId() + " was allowed to getAllWeatherDataOfOneStation");

            Station station = new Station();
            station.setStationsId(stationsID);

            response.setStatus(HttpStatus.OK.value());
            return weatherInfoRepository.findAllByStation(station);

        } else {

            logger.info("Error. Session token is not found");
            response.setStatus(HttpStatus.FORBIDDEN.value());

            return null;
        }
    }


    /**
     * Gets info about all stations
     */
    @RequestMapping(value = "/all_stations", method = RequestMethod.GET)
    public List<Station> getAllStationsInfo(@RequestParam(value = "token") String sessionToken,
                                            HttpServletResponse response) {

        logger.info("====> user with session token = " + sessionToken + " querying info for all stations");

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                return null;
            }

            List<Station> stations = stationRepository.findAll();

            logger.info("info on all stations found and is about to be sent");
            response.setStatus(HttpStatus.OK.value());

            return stations;

        } else {

            logger.info("Error. Session token is not found");
            response.setStatus(HttpStatus.FORBIDDEN.value());

            return null;
        }

    }

    /**
     * Used to add a new station
     */
    @RequestMapping(value = "/add_station", method = RequestMethod.POST)
    public void addNewStation(@RequestHeader(value = "token") String sessionToken,
                              @RequestBody String newStationAsJson,
                              HttpServletResponse response) {

        logger.info("====> user with session token = " + sessionToken + " adding new station");

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                return;
            }

            try {

                JsonObject asJsonObject = new JsonParser().parse(newStationAsJson).getAsJsonObject();

                Station newStation = new Gson().fromJson(newStationAsJson, Station.class);

                Long oblastId = Long.valueOf(asJsonObject.get("oblastId").toString());

                newStation.setOblast(oblastRepository.getOne(oblastId));

                stationRepository.save(newStation);

                response.setStatus(HttpStatus.OK.value());

                logger.info("New station created successfully");

            } catch (Exception e) {

                logger.error("Json with info about new station is invalid");
                logger.error(e.getMessage());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }

        } else {

            logger.info("Error. Session token is not found");
            response.setStatus(HttpStatus.FORBIDDEN.value());

        }

    }

    // not described in docs
    @RequestMapping(value = "/delete_station", method = RequestMethod.PUT)
    public void deleteNewStation(@RequestHeader(value = "token") String sessionToken,
                                 @RequestHeader(value = "station_id") Long stationId,
                                 HttpServletResponse response) {

        logger.info("====> user with session token = " + sessionToken + " deleting the station with id= " + stationId);

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                return;
            }

            try {

                Station station = new Station();
                station.setStationsId(stationId);

                weatherInfoRepository.deleteAllByStation(station);

                stationRepository.deleteById(stationId);

                response.setStatus(HttpStatus.OK.value());

                logger.info("Station with id = " + stationId + " was deleted");

            } catch (Exception e) {

                logger.info("An error while deleting station");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }

        } else {

            logger.info("Error. Session token is not found");
            response.setStatus(HttpStatus.FORBIDDEN.value());

        }

    }

    @RequestMapping(value = "/change_station", method = RequestMethod.PUT)
    public void changeStationsInfo(@RequestHeader(value = "token") String sessionToken,
                                   @RequestHeader(value = "station_id") Long stationId,
                                   @RequestBody String updatedStationInfo,
                                   HttpServletResponse response) {

        logger.info("====> user with session token = " + sessionToken + " changing the station with id = " + stationId);

        Token token = tokenRepository.findByToken(sessionToken);

        // if the token exists
        if (token != null) {

            if (token.isExpired()) {

                logger.info("token is expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                return;
            }

            try {
                Optional<Station> station = stationRepository.findById(stationId);

                if (station.isPresent()) {

                    // deserialize json into Station
                    Station updatedStation = new Gson().fromJson(updatedStationInfo, Station.class);
                    updatedStation.setStationsId(station.get().getStationsId());
                    updatedStation.setOblast(station.get().getOblast());
                    updatedStation.setStationUniqueKey(station.get().getStationUniqueKey());

                    stationRepository.save(updatedStation);

                    response.setStatus(HttpStatus.OK.value());

                    logger.info("Station with id = " + stationId + " was updated successfully");

                } else {

                    logger.info("Station with id = " + stationId + " not found");

                }
            } catch (Exception e) {

                logger.error("Error while updating station info");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error(e.getMessage());
            }

        } else {

            logger.info("Error. Session token is not found");
            response.setStatus(HttpStatus.FORBIDDEN.value());

        }

    }


    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public void serverIsAvailable(HttpServletResponse response) {

        logger.info("====>  Someone checking if the server id available");
        response.setStatus(HttpStatus.OK.value());
    }


}
