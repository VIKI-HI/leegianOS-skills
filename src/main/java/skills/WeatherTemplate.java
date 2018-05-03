/*
 * Copyright (C) 2018. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 */

package skills;

import de.linzn.leegianOS.LeegianOSApp;
import de.linzn.leegianOS.internal.interfaces.ISkill;
import de.linzn.leegianOS.internal.objectDatabase.clients.SkillClient;
import de.linzn.leegianOS.internal.objectDatabase.skillType.PrimarySkill;
import de.linzn.leegianOS.internal.objectDatabase.skillType.SecondarySkill;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;
import org.json.JSONObject;

import java.io.IOException;


public class WeatherTemplate implements ISkill {
    private SkillClient skillClient;
    private PrimarySkill primarySkill;
    private SecondarySkill secondarySkill;
    private String prefix = this.getClass().getSimpleName() + "->";

    @Override
    public void setEnv(SkillClient requestOwner, PrimarySkill primarySkill, SecondarySkill secondarySkill) {
        this.skillClient = requestOwner;
        this.secondarySkill = secondarySkill;
        this.primarySkill = primarySkill;
    }

    public void getWeatherCurrent() {
        String location;
        String key;
        if (this.primarySkill != null) {
            location = (String) this.primarySkill.serial_data.get("location");
            key = (String) this.primarySkill.serial_data.get("weatherKey");
        } else {
            location = (String) this.secondarySkill.serial_data.get("location");
            key = (String) this.secondarySkill.serial_data.get("weatherKey");
        }
        float temperature = 0;
        float clouds = 0;
        float rain = 0;

        try {
            OpenWeatherMap owm = new OpenWeatherMap(key);
            owm.setUnits(OpenWeatherMap.Units.METRIC);
            owm.setLang(OpenWeatherMap.Language.GERMAN);
            LeegianOSApp.logger(prefix + "getWeather-->" + "location " + location);
            CurrentWeather weatherCurrent = owm.currentWeatherByCityName(location);
            temperature = weatherCurrent.getMainInstance().getTemperature();
            if (weatherCurrent.hasCloudsInstance() && weatherCurrent.getCloudsInstance().hasPercentageOfClouds()) {
                clouds = weatherCurrent.getCloudsInstance().getPercentageOfClouds();
            }
            if (weatherCurrent.hasRainInstance() && weatherCurrent.getRainInstance().hasRain()) {
                rain = weatherCurrent.getRainInstance().getRain();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String wetterValue = "N.A";
        System.out.println("Cloud: " + clouds);
        System.out.println("Rain: " + rain);
        if (rain > 0) {
            if (clouds < 20) {
                wetterValue = "regnerrich";
            } else if (clouds < 50) {
                wetterValue = "leicht bewölkt mit Regen";
            } else if (clouds < 70) {
                wetterValue = "bewölkt mit Regen";
            } else if (clouds <= 100) {
                wetterValue = "start bewölkt mit Regen";
            }
        } else {
            if (clouds < 20) {
                wetterValue = "sonnig";
            } else if (clouds < 50) {
                wetterValue = "leicht bewölkt";
            } else if (clouds < 70) {
                wetterValue = "bewölkt";
            } else if (clouds <= 100) {
                wetterValue = "stark bewölkt";
            }
        }
        JSONObject dataValues = new JSONObject();
        dataValues.put("needResponse", false);

        JSONObject textValues = new JSONObject();
        textValues.put("notificationText", "Das Wetter in " + location + " ist " + wetterValue + ". Die Temperatur beträgt " + temperature + " °C.");

        JSONObject main = new JSONObject();
        main.put("dataValues", dataValues);
        main.put("textValues", textValues);

        this.skillClient.sendResponse(main);


    }

}
