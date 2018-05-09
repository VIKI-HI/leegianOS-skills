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
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import org.json.JSONObject;


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
        /* Day weather*/
        double tempMin = -1;
        double tempMax = -1;
        double tempCurrent = -1;
        double snowData = -1;
        double rainData = -1;
        double cloudData = -1;

        try {
            OWM owm = new OWM(key);
            owm.setUnit(OWM.Unit.METRIC);
            owm.setLanguage(OWM.Language.GERMAN);

            LeegianOSApp.logger(prefix + "getWeather-->" + "location " + location, true);
            CurrentWeather crWeather = owm.currentWeatherByCityName(location);

            if (crWeather.hasRespCode() && crWeather.getRespCode() == 200) {
                if (crWeather.hasMainData() && crWeather.getMainData() != null) {
                    if (crWeather.getMainData().hasTempMin()) {
                        tempMin = crWeather.getMainData().getTempMin();
                    }
                    if (crWeather.getMainData().hasTempMax()) {
                        tempMax = crWeather.getMainData().getTempMax();
                    }
                    if (crWeather.getMainData().hasTemp()) {
                        tempCurrent = crWeather.getMainData().getTemp();
                    }
                }

                if (crWeather.getSnowData() != null && crWeather.hasSnowData() && crWeather.getSnowData().hasSnowVol3h()) {
                    snowData = crWeather.getSnowData().getSnowVol3h();
                }

                if (crWeather.getRainData() != null && crWeather.hasRainData() && crWeather.getRainData().hasPrecipVol3h()) {
                    rainData = crWeather.getRainData().getPrecipVol3h();
                }

                if (crWeather.getCloudData() != null && crWeather.hasCloudData() && crWeather.getCloudData().hasCloud()) {
                    cloudData = crWeather.getCloudData().getCloud();
                }
            }
        } catch (APIException e) {
            e.printStackTrace();
        }

        JSONObject dataValues = new JSONObject();
        dataValues.put("needResponse", false);

        JSONObject textValues = new JSONObject();
        //textValues.put("notificationText", "Das Wetter in " + location + " ist " + wetterValue + ". Die Temperatur beträgt " + temperature + " °C.");
        textValues.put("notificationText", "Data-> T:" + tempCurrent + " miT:" + tempMin + " maT:" + tempMax + " SN:" + snowData + " RA:" + rainData + " CL: " + cloudData);

        JSONObject main = new JSONObject();
        main.put("dataValues", dataValues);
        main.put("textValues", textValues);

        this.skillClient.sendResponse(main);


    }

}
