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
import de.linzn.leegianOS.internal.interfaces.ISkill;
import de.linzn.leegianOS.internal.objectDatabase.clients.SkillClient;
import de.linzn.leegianOS.internal.objectDatabase.skillType.PrimarySkill;
import de.linzn.leegianOS.internal.objectDatabase.skillType.SecondarySkill;


public class NetworkTemplate implements ISkill {
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


    public boolean cbnModemRestart() {
        String cbnHost = (String) this.secondarySkill.serial_data.get("hostName");
        String cbnUsername = (String) this.secondarySkill.serial_data.get("systemUser");
        String cbnPassword = (String) this.secondarySkill.serial_data.get("systemPassword");
        LeegianOSApp.logger(prefix + "cbnModemRestart-->hostName " + cbnHost, true);
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
