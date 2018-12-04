package bntu.diploma;

public class WhatToDo {

    /**
     *
     * TODO todo list:
     *
     * Agenda: 16. Make tokens expired after a period of AFK time
     *
     * - write more unit tests and fix existing ones
     *
     * 18. Rename params to keep consistence
     * - use hibernate validator to validate input do i need to do it here ?
     * - split controller to two ones: one for weather station and one for GUI app ?
     *
     * DONE
     * -  what's wrong with it  ? -> 3. method in Controller  for adding new station. FIXED
     * 7. logging in WeatherServerController implemented (17.11.2018)
     * 14. Is a user want to loginUser check is his/her session is not expired her first and if yes then generate a new token
     * 17. Rename API resource paths !!! (e.g. 'station' -> 'all_stations')
     * 15. LOGIN/ LOGOUT do not response bodies - resolved by using headers
     * 4. method in Controller for editing station's info ?
     * 12. AES - BullyWiiPlaza's answer -  https://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
     * 11. Add the 'station_id' field to the json response
     * 6. How to generate keys ?
     * 10. Implement any type of security !
     * 1. map Oblast and Station
     * 2. make sure that mapping between WeatherInfo and Station works fine
     * 5. Where to store keys for stations and for users (for stations - in Station
     *  for users - in User)
     *
     *
     * Weird:
     * 9. authentication class for checking keys (there is a special interface of abs class)
     * 13. How to find out client's IP ? THis is necessary for securing a session after AES loginUser
     *
     * */
    public class WhatToDo2{}
}
