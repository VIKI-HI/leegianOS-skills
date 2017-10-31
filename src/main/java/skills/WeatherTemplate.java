package skills;

import de.linzn.leegianOS.LeegianOSApp;
import de.linzn.leegianOS.internal.ifaces.ISkillTemplate;
import de.linzn.leegianOS.internal.ifaces.ParentSkill;
import de.linzn.leegianOS.internal.ifaces.SkillClient;
import de.linzn.leegianOS.internal.ifaces.SubSkill;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import java.io.IOException;


public class WeatherTemplate implements ISkillTemplate {
    private SkillClient skillClient;
    private ParentSkill parentSkill;
    private SubSkill subSkill;
    private String prefix = this.getClass().getSimpleName() + "->";

    @Override
    public void setEnv(SkillClient requestOwner, ParentSkill parentSkill, SubSkill subSkill) {
        this.skillClient = requestOwner;
        this.subSkill = subSkill;
        this.parentSkill = parentSkill;
    }

    public void getWeatherCurrent() {
        String location;
        String key;
        if (this.parentSkill != null) {
            location = (String) this.parentSkill.serial_data.get("location");
            key = (String) this.parentSkill.serial_data.get("weatherKey");
        } else {
            location = (String) this.subSkill.serial_data.get("location");
            key = (String) this.subSkill.serial_data.get("weatherKey");
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

        String message = "Das Wetter in " + location + " ist " + wetterValue + ". Die Temperatur beträgt " + temperature + " °C.";
        this.skillClient.sendResponseToClient(true, message);


    }

}
