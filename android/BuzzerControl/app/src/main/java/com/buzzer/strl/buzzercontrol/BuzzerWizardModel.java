package com.buzzer.strl.buzzercontrol;

import com.buzzer.strl.buzzercontrol.page.CustomerBuzzerPage;
import com.tech.freak.wizardpager.model.AbstractWizardModel;

import android.content.Context;

import com.buzzer.strl.buzzercontrol.page.CustomerWiFiPage;
import com.tech.freak.wizardpager.model.PageList;

/**
 * Created by iqor on 12/7/16.
 */
public class BuzzerWizardModel extends AbstractWizardModel {
    public BuzzerWizardModel(Context context) {
        super(context);

    }

    @Override
    protected PageList onNewRootPageList() {


        return new PageList(
                new CustomerWiFiPage(this, "WiFi Information").setRequired(true),
                new CustomerBuzzerPage(this, "Buzzer Password").setRequired(true));
    }
}