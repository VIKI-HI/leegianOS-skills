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

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.linzn.leegianOS.LeegianOSApp;
import de.linzn.leegianOS.internal.interfaces.ISkill;
import de.linzn.leegianOS.internal.objectDatabase.clients.SkillClient;
import de.linzn.leegianOS.internal.objectDatabase.skillType.PrimarySkill;
import de.linzn.leegianOS.internal.objectDatabase.skillType.SecondarySkill;
import org.json.JSONObject;

import java.util.Random;


public class TeamspeakTemplate implements ISkill {
    private TS3Api api;
    private TS3Query query;
    private TS3Config config;

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


    public void clientKick() {
        this.setupConnection();
        final Client selectedClient = selectClient();
        JSONObject dataValues = new JSONObject();
        dataValues.put("needResponse", false);

        JSONObject textValues = new JSONObject();


        if (selectedClient != null) {
            LeegianOSApp.logger(prefix + "clientKick-->" + "kick client " + selectedClient.getNickname());
            textValues.put("notificationText", ((String) this.secondarySkill.serial_data.get("success")).replace("${name}", selectedClient.getNickname()));
            this.api.kickClientFromServer("Wurde auf Skillanfrage entfernt!", selectedClient);
        } else {
            LeegianOSApp.logger(prefix + "clientKick-->" + "no client found");
            textValues.put("notificationText", this.secondarySkill.serial_data.get("failed"));
        }
        JSONObject main = new JSONObject();
        main.put("dataValues", dataValues);
        main.put("textValues", textValues);

        this.skillClient.sendResponse(main);
        this.closeConnection();

    }


    private Client selectClient() {
        LeegianOSApp.logger(prefix + "selectClient-->" + "select client from array");
        for (Client client : api.getClients()) {
            if (!client.isServerQueryClient()) {
                for (String name : this.secondarySkill.inputArray) {
                    if (client.getNickname().toLowerCase().matches(".*" + name.toLowerCase() + ".*")) {
                        LeegianOSApp.logger(prefix + "selectClient-->" + "found client " + client.getNickname());
                        return client;
                    }
                }
            }
        }
        return null;
    }

    private void setupConnection() {
        LeegianOSApp.logger(prefix + "setupConnection-->" + "create connection");
        config = new TS3Config();
        config.setHost((String) this.secondarySkill.serial_data.get("hostName"));

        query = new TS3Query(config);
        query.connect();

        api = query.getApi();
        api.login((String) this.secondarySkill.serial_data.get("systemUser"), (String) this.secondarySkill.serial_data.get("systemPassword"));
        api.selectVirtualServerById(Integer.valueOf((String) this.secondarySkill.serial_data.get("id")));
        if (!api.setNickname((String) this.secondarySkill.serial_data.get("systemName"))) {
            api.setNickname((String) this.secondarySkill.serial_data.get("systemName") + "-" + (new Random().nextInt(30) + 1));
        }
    }

    private void closeConnection() {
        LeegianOSApp.logger(prefix + "closeConnection-->" + "close connection");
        this.query.exit();
    }


}
