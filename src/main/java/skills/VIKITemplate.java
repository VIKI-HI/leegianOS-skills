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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VIKITemplate implements ISkillTemplate {
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
        this.skillClient.sendResponseToClient(true, "Bist du dir sicher dass ich mich neustarten soll?", true);
        String[] test = this.skillClient.waitingSkillForResponse(this, 20);

        if (test == null) {
            this.skillClient.sendResponseToClient(true, "Da ich keine Rückmeldung bekommen habe, breche ich dann hier ab!", false);
            return;
        } else if (!test[0].equalsIgnoreCase("ja")) {
            this.skillClient.sendResponseToClient(true, "Ok Vorgang wurde abgebrochen!", false);
            return;
        }

        try {
            LeegianOSApp.logger(prefix + "reboot-->viki ");
            for (SkillClient skillClient1 : LeegianOSApp.leegianOSAppInstance.skillClientList.values()) {
                skillClient1.sendResponseToClient(true, ((String) this.subSkill.serial_data.get("begin")), false);
            }
            Runtime.getRuntime().exec("service viki restart").waitFor(1000, TimeUnit.MILLISECONDS);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.skillClient.sendResponseToClient(true, (String) this.subSkill.serial_data.get("failed"), false);
            System.err.println();
        }
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
