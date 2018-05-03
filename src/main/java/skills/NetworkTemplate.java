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


import de.linzn.cbn.api.ch7466ce.CBNApi;
import de.linzn.leegianOS.LeegianOSApp;
import de.linzn.leegianOS.internal.ifaces.ISkill;
import de.linzn.leegianOS.internal.lifeObjects.ParentSkill;
import de.linzn.leegianOS.internal.lifeObjects.SkillClient;
import de.linzn.leegianOS.internal.lifeObjects.SubSkill;

public class NetworkTemplate implements ISkill {
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


    public boolean cbnModemRestart() {
        String cbnHost = (String) this.subSkill.serial_data.get("hostName");
        String cbnUsername = (String) this.subSkill.serial_data.get("systemUser");
        String cbnPassword = (String) this.subSkill.serial_data.get("systemPassword");
        LeegianOSApp.logger(prefix + "cbnModemRestart-->hostName " + cbnHost);
        CBNApi api = new CBNApi(cbnHost, cbnUsername, cbnPassword);
        try {
            api.restartCBN();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
