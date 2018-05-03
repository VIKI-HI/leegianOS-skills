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
import de.linzn.whatsappApi.ValidMessage;
import de.linzn.whatsappApi.WhatsappClient;


public class WhatsappTemplate implements ISkill {
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

    public void sendPhoneMessage() {
        String loginPhone = (String) this.subSkill.serial_data.get("loginPhone");
        String loginPassphrase = (String) this.subSkill.serial_data.get("loginPassphrase");
        String receiverPhone = (String) this.subSkill.serial_data.get("receiverPhone");
        String message = (String) this.subSkill.serial_data.get("message");

        ValidMessage validMessage = new ValidMessage(receiverPhone, message);
        WhatsappClient.sendStandaloneMessage(loginPhone, loginPassphrase, validMessage);
        LeegianOSApp.logger(prefix + "sendPhoneMSG-->" + receiverPhone);
    }

}
