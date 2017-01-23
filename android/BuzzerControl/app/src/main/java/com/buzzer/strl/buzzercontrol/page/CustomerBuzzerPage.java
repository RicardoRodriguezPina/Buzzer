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
public class CustomerBuzzerPage extends Page {
    public static final String PASS_BUZZER_DATA_KEY = "passbuzzer";

    public CustomerBuzzerPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return CustomerBuzzerFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Your Buzzer Pass", mData.getString(PASS_BUZZER_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {

        return !TextUtils.isEmpty(mData.getString(PASS_BUZZER_DATA_KEY));
    }
}