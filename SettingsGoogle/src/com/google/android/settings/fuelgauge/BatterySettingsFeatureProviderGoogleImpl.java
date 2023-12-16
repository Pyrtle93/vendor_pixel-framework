package com.google.android.settings.fuelgauge;

import android.content.Context;
import com.android.settings.fuelgauge.BatterySettingsFeatureProviderImpl;
import com.android.settings.R;


public class BatterySettingsFeatureProviderGoogleImpl extends BatterySettingsFeatureProviderImpl {
    static final long DEFAULT_FIRST_USE_DATE_MS = 1606780800000L;

    @Override
    public boolean isManufactureDateAvailable(Context context, long j) {
        return j > 0;
    }

    @Override
    public boolean isFirstUseDateAvailable(Context context, long j) {
        return true;
    }
}
