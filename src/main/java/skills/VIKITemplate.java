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
import de.linzn.leegianOS.internal.ifaces.ISkill;
import de.linzn.leegianOS.internal.lifeObjects.ParentSkill;
import de.linzn.leegianOS.internal.lifeObjects.SkillClient;
import de.linzn.leegianOS.internal.lifeObjects.SubSkill;

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
        this.skillClient.sendResponse(true, "Bist du dir sicher dass ich mich neustarten soll?");
        String[] test = this.skillClient.waitingSkillForResponse(this, 20);

        if (test == null) {
            this.skillClient.sendResponse(false, "Da ich keine Rückmeldung bekommen habe, breche ich dann hier ab!");
            return;
        } else if (!test[0].equalsIgnoreCase("ja")) {
            this.skillClient.sendResponse(false, "Ok Vorgang wurde abgebrochen!");
            return;
        }

        try {
            LeegianOSApp.logger(prefix + "reboot-->viki ");
            for (SkillClient skillClient1 : LeegianOSApp.leegianOSAppInstance.skillClientList.values()) {
                this.skillClient.sendResponse(false, ((String) this.subSkill.serial_data.get("begin")));
            }
            Runtime.getRuntime().exec("service viki restart").waitFor(1000, TimeUnit.MILLISECONDS);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.skillClient.sendResponse(false, (String) this.subSkill.serial_data.get("failed"));
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
