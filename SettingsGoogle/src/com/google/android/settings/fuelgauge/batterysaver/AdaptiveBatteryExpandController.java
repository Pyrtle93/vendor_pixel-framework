package com.google.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.android.settingslib.widget.TopIntroPreference;
import com.google.android.settings.fuelgauge.batterysaver.ExpandDividerPreference;

import com.android.settings.R;

public class AdaptiveBatteryExpandController extends BasePreferenceController implements ExpandDividerPreference.OnExpandListener, OnMainSwitchChangeListener, LifecycleObserver, OnSaveInstanceState, OnCreate {
    static final String ADAPTIVE_BATTERY_DES_KEY = "adaptive_battery_description";
    static final String ADAPTIVE_BATTERY_INTRO_KEY = "adaptive_battery_top_intro";
    static final String ADAPTIVE_BATTERY_SWITCH_KEY = "adaptive_battery";
    private static final String KEY_EXPAND_STATE = "expand_state";
    static final int OFF = 0;
    static final int ON = 1;
    private MainSwitchPreference mAdaptiveBatterySwitchPreference;
    private TopIntroPreference mAdaptiveBatteryTopIntroPreference;
    private ExpandDividerPreference mExpandDividerPreference;
    private boolean mIsExpanded;
    private final PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    private PreferenceCategory mPreferenceCategory;

    @Override
    public Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override
    public IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override
    public boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override
    public boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override
    public boolean isSliceable() {
        return super.isSliceable();
    }

    @Override
    public boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AdaptiveBatteryExpandController(Context context, String str) {
        super(context, str);
        mPowerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
        initExpandPreference();
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mIsExpanded = bundle.getBoolean(KEY_EXPAND_STATE, mIsExpanded);
    }

    @Override
    public int getAvailabilityStatus() {
        return mPowerUsageFeatureProvider.isSmartBatterySupported() ? 0 : 3;
    }

    @Override
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        mPreferenceCategory = preferenceCategory;
        if (preferenceCategory == null) {
            return;
        }
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceCategory.findPreference(ADAPTIVE_BATTERY_SWITCH_KEY);
        mAdaptiveBatterySwitchPreference = mainSwitchPreference;
        mainSwitchPreference.addOnSwitchChangeListener(this);
        mAdaptiveBatterySwitchPreference.updateStatus(Settings.Global.getInt(mContext.getContentResolver(), "adaptive_battery_management_enabled", 1) == 1);
        mAdaptiveBatteryTopIntroPreference = (TopIntroPreference) mPreferenceCategory.findPreference(ADAPTIVE_BATTERY_INTRO_KEY);
        mPreferenceCategory.removeAll();
        mPreferenceCategory.addPreference(mExpandDividerPreference);
        boolean z = mIsExpanded;
        if (z) {
            mExpandDividerPreference.setExpanded(z);
            onExpand(mIsExpanded);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        ExpandDividerPreference expandDividerPreference;
        if (bundle == null || (expandDividerPreference = mExpandDividerPreference) == null) {
            return;
        }
        bundle.putBoolean(KEY_EXPAND_STATE, expandDividerPreference.isExpended());
    }

    @Override // com.google.android.settings.fuelgauge.batterysaver.ExpandDividerPreference.OnExpandListener
    public void onExpand(boolean z) {
        if (z) {
            mPreferenceCategory.addPreference(mAdaptiveBatteryTopIntroPreference);
            mPreferenceCategory.addPreference(mAdaptiveBatterySwitchPreference);
            return;
        }
        mPreferenceCategory.removePreference(mAdaptiveBatteryTopIntroPreference);
        mPreferenceCategory.removePreference(mAdaptiveBatterySwitchPreference);
    }

    private void initExpandPreference() {
        ExpandDividerPreference expandDividerPreference = new ExpandDividerPreference(mContext);
        mExpandDividerPreference = expandDividerPreference;
        expandDividerPreference.setTitle(mContext.getString(R.string.smart_battery_title));
        mExpandDividerPreference.setOnExpandListener(this);
        mExpandDividerPreference.setOrder(90);
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        Settings.Global.putInt(mContext.getContentResolver(), "adaptive_battery_management_enabled", z ? 1 : 0);
    }
}
