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


import de.linzn.leegianOS.internal.interfaces.ISkill;
import de.linzn.leegianOS.internal.objectDatabase.clients.SkillClient;
import de.linzn.leegianOS.internal.objectDatabase.skillType.PrimarySkill;
import de.linzn.leegianOS.internal.objectDatabase.skillType.SecondarySkill;
import org.json.JSONObject;

import java.io.IOException;


public class UnixTemplate implements ISkill {
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

    public void upgradeUnixSystem() {
        String hostName = (String) this.secondarySkill.serial_data.get("hostName");
        int port = 22;
        if (this.secondarySkill.serial_data.containsKey("port")) {
            port = (int) this.secondarySkill.serial_data.get("port");
        }
        int exitCode;
        try {
            String[] cmd = {
                    "/bin/sh",
                    "-c",
                    "ssh root@" + hostName + " -p " + port + " -C 'apt-get update && apt-get -y -o DPkg::options::=--force-confdef -o DPkg::options::=--force-confold dist-upgrade'"
            };
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            exitCode = p.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            exitCode = -1;
        }

        JSONObject dataValues = new JSONObject();
        dataValues.put("needResponse", false);
        dataValues.put("exitCode", exitCode);
        dataValues.put("hostname", hostName);
        dataValues.put("port", port);

        JSONObject textValues = new JSONObject();
        textValues.put("notificationText", "Beendet mit ExitCode " + exitCode);

        JSONObject main = new JSONObject();
        main.put("dataValues", dataValues);
        main.put("textValues", textValues);
        this.skillClient.sendResponse(main);

    }


}
