package skills;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.linzn.leegianOS.LeegianOSApp;
import de.linzn.leegianOS.internal.ifaces.ISkillTemplate;
import de.linzn.leegianOS.internal.ifaces.ParentSkill;
import de.linzn.leegianOS.internal.ifaces.SkillClient;
import de.linzn.leegianOS.internal.ifaces.SubSkill;

import java.util.Random;


public class TeamspeakTemplate implements ISkillTemplate {
    private TS3Api api;
    private TS3Query query;
    private TS3Config config;

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


    public void clientKick() {
        this.setupConnection();
        final Client selectedClient = selectClient();

        if (selectedClient != null) {
            LeegianOSApp.logger(prefix + "clientKick-->" + "kick client " + selectedClient.getNickname());
            this.skillClient.sendResponseToClient(true, ((String) this.subSkill.serial_data.get("success")).replace("${name}", selectedClient.getNickname()), false);
            this.api.kickClientFromServer("Wurde auf Skillanfrage entfernt!", selectedClient);
        } else {
            LeegianOSApp.logger(prefix + "clientKick-->" + "no client found");
            this.skillClient.sendResponseToClient(true, (String) this.subSkill.serial_data.get("failed"), false);
        }
        this.closeConnection();

    }


    private Client selectClient() {
        LeegianOSApp.logger(prefix + "selectClient-->" + "select client from array");
        for (Client client : api.getClients()) {
            if (!client.isServerQueryClient()) {
                for (String name : this.subSkill.inputArray) {
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
        config.setHost((String) this.subSkill.serial_data.get("hostName"));

        query = new TS3Query(config);
        query.connect();

        api = query.getApi();
        api.login((String) this.subSkill.serial_data.get("systemUser"), (String) this.subSkill.serial_data.get("systemPassword"));
        api.selectVirtualServerById(Integer.valueOf((String) this.subSkill.serial_data.get("id")));
        if (!api.setNickname((String) this.subSkill.serial_data.get("systemName"))) {
            api.setNickname((String) this.subSkill.serial_data.get("systemName") + "-" + (new Random().nextInt(30) + 1));
        }
    }

    private void closeConnection() {
        LeegianOSApp.logger(prefix + "closeConnection-->" + "close connection");
        this.query.exit();
    }


}
