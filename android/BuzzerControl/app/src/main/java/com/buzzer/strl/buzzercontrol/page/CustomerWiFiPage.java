package com.buzzer.strl.buzzercontrol.page;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;


/**
 * A page asking for a name and an email.
 */
public class CustomerWiFiPage extends Page {
    public static final String WIFI_DATA_KEY = "wifi";
    public static final String PASS_WIFI_DATA_KEY = "passwifi";

    public CustomerWiFiPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {

        return CustomerWiFiFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Your WiFi", mData.getString(WIFI_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Your WiFi Pass", mData.getString(PASS_WIFI_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(WIFI_DATA_KEY));
    }
}