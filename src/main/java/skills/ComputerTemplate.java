/*
 * Copyright (C) 2017. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package skills;

import de.linzn.leegianOS.LeegianOSApp;
import de.linzn.leegianOS.internal.ifaces.ISkillTemplate;
import de.linzn.leegianOS.internal.ifaces.ParentSkill;
import de.linzn.leegianOS.internal.ifaces.SkillClient;
import de.linzn.leegianOS.internal.ifaces.SubSkill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ComputerTemplate implements ISkillTemplate {
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


    public boolean startComputer() {
        // Need "apt-get install etherwake" packet installed
        try {
            String pcName = (String) this.subSkill.serial_data.get("systemName");
            String mac = (String) this.subSkill.serial_data.get("macAddress");
            LeegianOSApp.logger(prefix + "startComputer-->systemName " + pcName + " mac " + mac);
            Runtime.getRuntime().exec("etherwake " + mac).waitFor(1000, TimeUnit.MILLISECONDS);
            return true;
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public boolean restartUnix() {
        // Need sshpass installed
        String systemName = (String) this.subSkill.serial_data.get("systemName");
        String ip = (String) (String) this.subSkill.serial_data.get("hostName");
        int port = Integer.parseInt((String) this.subSkill.serial_data.get("portNumber"));
        String user = (String) this.subSkill.serial_data.get("systemUser");
        String password = (String) this.subSkill.serial_data.get("systemPassword");
        try {
            LeegianOSApp.logger(prefix + "restartUnix-->systemName " + systemName + " hostName " + ip);
            Runtime.getRuntime().exec("sshpass -p '" + password + "' ssh " + user + "@" + ip + " -p " + port + " 'reboot'").waitFor(1000, TimeUnit.MILLISECONDS);
            return true;
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public boolean shutdownUnix() {
        // Need sshpass installed
        String systemName = (String) this.subSkill.serial_data.get("systemName");
        String ip = (String) (String) this.subSkill.serial_data.get("hostName");
        int port = Integer.parseInt((String) this.subSkill.serial_data.get("portNumber"));
        String user = (String) this.subSkill.serial_data.get("systemUser");
        String password = (String) this.subSkill.serial_data.get("systemPassword");
        try {
            LeegianOSApp.logger(prefix + "shutdownUnix-->systemName " + systemName + " hostName " + ip);
            Runtime.getRuntime().exec("sshpass -p '" + password + "' ssh " + user + "@" + ip + " -p " + port + " 'shutdown -h now'").waitFor(1000, TimeUnit.MILLISECONDS);
            return true;
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }


    public void getSystemTemperature() {
        try {
            Float[] coreTemp = {};
            String[] cmd = {
                    "/bin/sh",
                    "-c",
                    "sensors | grep -A 0 'id' | cut -c17-21 && sensors | grep -A 0 'Core' | cut -c17-21"
            };
            LeegianOSApp.logger(prefix + "getSystemTemperature-->systemName " + null + " hostName " + null);
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            int i = 0;

            while ((line = b.readLine()) != null) {
                float temp = Float.parseFloat(line);
                coreTemp[i] = temp;
                i++;
            }
            System.out.println("Core Temp: " + coreTemp.toString());
            this.skillClient.sendResponseToClient(true, "Die Core Temperature des Systems beträgt " + coreTemp[0] + " °C", false);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
