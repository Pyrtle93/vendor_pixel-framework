package com.google.android.settings.fuelgauge.batterysaver;

import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.R;

public class BatterySaverScheduleAndRemindersSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.battery_saver_schedule_and_reminders);

    @Override
    public String getLogTag() {
        return "BatterySaverScheduleAndRemindersSettings";
    }

    @Override
    public int getMetricsCategory() {
        return 1977;
    }

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.battery_saver_schedule_and_reminders;
    }
}
