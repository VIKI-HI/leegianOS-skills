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
import de.linzn.leegianOS.internal.objectDatabase.skillType.ParentSkill;
import de.linzn.leegianOS.internal.objectDatabase.skillType.SubSkill;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VIKITemplate implements ISkill {
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

    public void reboot() {

        JSONObject dataValues = new JSONObject();
        dataValues.put("needResponse", true);
        JSONObject textValues = new JSONObject();
        textValues.put("notificationText", "Bist du dir sicher dass ich mich neustarten soll?");
        JSONObject main = new JSONObject();
        main.put("dataValues", dataValues);
        main.put("textValues", textValues);

        this.skillClient.sendResponse(main);
        String[] test = this.skillClient.waitingSkillForResponse(this, 20);


        dataValues = new JSONObject();
        dataValues.put("needResponse", false);
        textValues = new JSONObject();

        main = new JSONObject();
        main.put("dataValues", dataValues);
        main.put("textValues", textValues);

        if (test == null) {
            textValues.put("notificationText", "Da ich keine Rückmeldung bekommen habe, breche ich dann hier ab!");
            this.skillClient.sendResponse(main);
            return;
        } else if (!test[0].equalsIgnoreCase("ja")) {
            textValues.put("notificationText", "Ok Vorgang wurde abgebrochen!");
            this.skillClient.sendResponse(main);
            return;
        }

        try {

            dataValues = new JSONObject();
            dataValues.put("needResponse", false);
            textValues = new JSONObject();

            main = new JSONObject();
            main.put("dataValues", dataValues);
            main.put("textValues", textValues);

            LeegianOSApp.logger(prefix + "reboot-->viki ");
            for (SkillClient skillClient1 : LeegianOSApp.leegianOSAppInstance.skillClientList.values()) {
                textValues.put("notificationText", this.subSkill.serial_data.get("begin"));
            }
            Runtime.getRuntime().exec("service viki restart").waitFor(1000, TimeUnit.MILLISECONDS);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            textValues.put("notificationText", this.subSkill.serial_data.get("failed"));
            System.err.println();
        }
        this.skillClient.sendResponse(main);
    }

    public void stop() {

        try {
            LeegianOSApp.logger(prefix + "stop-->viki ");
            Runtime.getRuntime().exec("service viki stop").waitFor(1000, TimeUnit.MILLISECONDS);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
