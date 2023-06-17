package com.google.android.settings.overlay;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.accessibility.AccessibilityMetricsFeatureProvider;
import com.android.settings.accessibility.AccessibilitySearchFeatureProvider;
import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.bluetooth.BluetoothFeatureProvider;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProvider;
import com.android.settings.fuelgauge.BatterySettingsFeatureProvider;
import com.android.settings.fuelgauge.BatteryStatusFeatureProvider;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.overlay.DockUpdaterFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;
import com.android.settings.overlay.SurveyFeatureProvider;
import com.android.settings.search.SearchFeatureProvider;
import com.android.settings.security.SecuritySettingsFeatureProvider;
import com.android.settings.wifi.WifiTrackerLibProvider;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.google.android.settings.accessibility.AccessibilityMetricsFeatureProviderGoogleImpl;
import com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl;
import com.google.android.settings.accounts.AccountFeatureProviderGoogleImpl;
import com.google.android.settings.applications.ApplicationFeatureProviderGoogleImpl;
import com.google.android.settings.aware.AwareFeatureProviderGoogleImpl;
import com.google.android.settings.bluetooth.BluetoothFeatureProviderGoogleImpl;
import com.google.android.settings.connecteddevice.dock.DockUpdaterFeatureProviderGoogleImpl;
import com.google.android.settings.core.instrumentation.SettingsGoogleMetricsFeatureProvider;
import com.google.android.settings.dashboard.suggestions.SuggestionFeatureProviderGoogleImpl;
import com.google.android.settings.experiments.PhenotypeProxy;
import com.google.android.settings.fuelgauge.BatterySettingsFeatureProviderGoogleImpl;
import com.google.android.settings.fuelgauge.BatteryStatusFeatureProviderGoogleImpl;
import com.google.android.settings.fuelgauge.PowerUsageFeatureProviderGoogleImpl;
import com.google.android.settings.gestures.assist.AssistGestureFeatureProviderGoogleImpl;
import com.google.android.settings.search.SearchFeatureProviderGoogleImpl;
import com.google.android.settings.security.SecuritySettingsFeatureProviderGoogleImpl;
import com.google.android.settings.support.SupportFeatureProviderImpl;
import com.google.android.settings.survey.SurveyFeatureProviderImpl;
import com.google.android.settings.wifi.WifiTrackerLibProviderGoogleImpl;

public class FeatureFactoryImpl extends com.android.settings.overlay.FeatureFactoryImpl {
    private AccessibilityMetricsFeatureProvider mAccessibilityMetricsFeatureProvider;
    private AccessibilitySearchFeatureProvider mAccessibilitySearchFeatureProvider;
    private AccountFeatureProvider mAccountFeatureProvider;
    private ApplicationFeatureProvider mApplicationFeatureProvider;
    private AssistGestureFeatureProvider mAssistGestureFeatureProvider;
    private AwareFeatureProvider mAwareFeatureProvider;
    private BatterySettingsFeatureProvider mBatterySettingsFeatureProvider;
    private BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;
    private BluetoothFeatureProvider mBluetoothFeatureProvider;
    private DockUpdaterFeatureProvider mDockUpdaterFeatureProvider;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private PowerUsageFeatureProvider mPowerUsageProvider;
    private SearchFeatureProvider mSearchFeatureProvider;
    private SecuritySettingsFeatureProvider mSecuritySettingsFeatureProvider;
    private SuggestionFeatureProvider mSuggestionFeatureProvider;
    private SupportFeatureProvider mSupportProvider;
    private SurveyFeatureProvider mSurveyFeatureProvider;
    private WifiTrackerLibProvider mWifiTrackerLibProvider;

    @Override
    public ApplicationFeatureProvider getApplicationFeatureProvider(Context context) {
        if (mApplicationFeatureProvider == null) {
            Context applicationContext = context.getApplicationContext();
            mApplicationFeatureProvider = new ApplicationFeatureProviderGoogleImpl(applicationContext, applicationContext.getPackageManager(), AppGlobals.getPackageManager(), (DevicePolicyManager) applicationContext.getSystemService("device_policy"));
        }
        return mApplicationFeatureProvider;
    }

    @Override
    public MetricsFeatureProvider getMetricsFeatureProvider() {
        if (mMetricsFeatureProvider == null) {
            mMetricsFeatureProvider = new SettingsGoogleMetricsFeatureProvider();
        }
        return mMetricsFeatureProvider;
    }

    @Override
    public BatteryStatusFeatureProvider getBatteryStatusFeatureProvider(Context context) {
        if (mBatteryStatusFeatureProvider == null) {
            mBatteryStatusFeatureProvider = new BatteryStatusFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return mBatteryStatusFeatureProvider;
    }

    @Override
    public BatterySettingsFeatureProvider getBatterySettingsFeatureProvider(Context context) {
        if (mBatterySettingsFeatureProvider == null) {
            mBatterySettingsFeatureProvider = new BatterySettingsFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return mBatterySettingsFeatureProvider;
    }

    @Override
    public PowerUsageFeatureProvider getPowerUsageFeatureProvider(Context context) {
        if (mPowerUsageProvider == null) {
            mPowerUsageProvider = new PowerUsageFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return mPowerUsageProvider;
    }

    @Override
    public DockUpdaterFeatureProvider getDockUpdaterFeatureProvider() {
        if (mDockUpdaterFeatureProvider == null) {
            mDockUpdaterFeatureProvider = new DockUpdaterFeatureProviderGoogleImpl();
        }
        return mDockUpdaterFeatureProvider;
    }

    @Override
    public SearchFeatureProvider getSearchFeatureProvider() {
        if (mSearchFeatureProvider == null) {
            mSearchFeatureProvider = new SearchFeatureProviderGoogleImpl();
        }
        return mSearchFeatureProvider;
    }

    @Override
    public SurveyFeatureProvider getSurveyFeatureProvider(Context context) {
        if (PhenotypeProxy.getBooleanFlagByPackageAndKey(context,
                context.getString(R.string.config_settingsintelligence_package_name),
                "HatsConfig__is_enabled", false)) {
            if (mSurveyFeatureProvider == null) {
                mSurveyFeatureProvider = new SurveyFeatureProviderImpl(context);
            }
            return mSurveyFeatureProvider;
        }
        return null;
    }

    @Override
    public SuggestionFeatureProvider getSuggestionFeatureProvider(Context context) {
        if (mSuggestionFeatureProvider == null) {
            mSuggestionFeatureProvider = new SuggestionFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return mSuggestionFeatureProvider;
    }

    @Override
    public AssistGestureFeatureProvider getAssistGestureFeatureProvider() {
        if (mAssistGestureFeatureProvider == null) {
            mAssistGestureFeatureProvider = new AssistGestureFeatureProviderGoogleImpl();
        }
        return mAssistGestureFeatureProvider;
    }

    @Override
    public AccountFeatureProvider getAccountFeatureProvider() {
        if (mAccountFeatureProvider == null) {
            mAccountFeatureProvider = new AccountFeatureProviderGoogleImpl();
        }
        return mAccountFeatureProvider;
    }

    @Override
    public AwareFeatureProvider getAwareFeatureProvider() {
        if (mAwareFeatureProvider == null) {
            mAwareFeatureProvider = new AwareFeatureProviderGoogleImpl();
        }
        return mAwareFeatureProvider;
    }

    @Override
    public WifiTrackerLibProvider getWifiTrackerLibProvider() {
        if (mWifiTrackerLibProvider == null) {
            mWifiTrackerLibProvider = new WifiTrackerLibProviderGoogleImpl();
        }
        return mWifiTrackerLibProvider;
    }

    @Override
    public SecuritySettingsFeatureProvider getSecuritySettingsFeatureProvider() {
        if (mSecuritySettingsFeatureProvider == null) {
            mSecuritySettingsFeatureProvider = new SecuritySettingsFeatureProviderGoogleImpl(FeatureFactory.getAppContext());
        }
        return mSecuritySettingsFeatureProvider;
    }

    @Override
    public BluetoothFeatureProvider getBluetoothFeatureProvider() {
        if (mBluetoothFeatureProvider == null) {
            mBluetoothFeatureProvider = new BluetoothFeatureProviderGoogleImpl(FeatureFactory.getAppContext());
        }
        return mBluetoothFeatureProvider;
    }

    @Override
    public AccessibilitySearchFeatureProvider getAccessibilitySearchFeatureProvider() {
        if (mAccessibilitySearchFeatureProvider == null) {
            mAccessibilitySearchFeatureProvider = new AccessibilitySearchFeatureProviderGoogleImpl();
        }
        return mAccessibilitySearchFeatureProvider;
    }

    @Override
    public AccessibilityMetricsFeatureProvider getAccessibilityMetricsFeatureProvider() {
        if (mAccessibilityMetricsFeatureProvider == null) {
            mAccessibilityMetricsFeatureProvider = new AccessibilityMetricsFeatureProviderGoogleImpl();
        }
        return mAccessibilityMetricsFeatureProvider;
    }
}
