package com.google.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.SelectorWithWidgetPreference;

public class BatterySaverModePreferenceController extends BasePreferenceController implements SelectorWithWidgetPreference.OnClickListener, LifecycleObserver, OnResume, OnPause {
    private static final String TAG = "BatterySaverModePreferenceController";
    @VisibleForTesting
    SelectorWithWidgetPreference mBasicPreference;
    private final ContentObserver mContentObserver;
    @VisibleForTesting
    boolean mCurrentBatterySaverMode;
    @VisibleForTesting
    SelectorWithWidgetPreference mExtremePreference;
    private HandlerThread mHandlerThread;
    @VisibleForTesting
    boolean mIsFlipendoAggressiveMode;
    @VisibleForTesting
    boolean mIsFlipendoEnabled;

    @Override 
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public BatterySaverModePreferenceController(Context context, String str) {
        super(context, str);
        mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override 
            public void onChange(boolean z) {
                refreshFlipendoStates();
                if (mIsFlipendoAggressiveMode) {
                    return;
                }
                updateSaverModeSelection(!mIsFlipendoEnabled);
            }
        };
    }

    @Override
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        if (preferenceCategory != null) {
            refreshFlipendoStates();
            initRadioButton(preferenceCategory);
        }
    }

    @Override 
    public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        String key = selectorWithWidgetPreference.getKey();
        key.hashCode();
        if (key.equals("extreme_battery_saver_entry")) {
            updateSaverModeSelection(false);
        } else if (key.equals("basic_battery_saver_entry")) {
            updateSaverModeSelection(true);
        }
        if (mIsFlipendoEnabled) {
            mCurrentBatterySaverMode = mExtremePreference.isChecked();
        }
    }

    @Override 
    public void onResume() {
        try {
            boolean z = false;
            mContext.getContentResolver().registerContentObserver(FlipendoUtils.FLIPENDO_ENABLED_OBSERVABLE_URI, false, mContentObserver);
            SelectorWithWidgetPreference selectorWithWidgetPreference = mBasicPreference;
            if (selectorWithWidgetPreference != null) {
                mCurrentBatterySaverMode = selectorWithWidgetPreference.isChecked();
            }
            refreshFlipendoStates();
            if (!mIsFlipendoEnabled && !mIsFlipendoAggressiveMode) {
                z = true;
            }
            updateSaverModeSelection(z);
        } catch (Exception e) {
            Log.e(TAG, "onResume() failed", e);
        }
    }

    @Override 
    public void onPause() {
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        if (mCurrentBatterySaverMode == mBasicPreference.isChecked() || 
            (!mIsFlipendoAggressiveMode && mIsFlipendoEnabled && mExtremePreference.isChecked())) {
            return;
        }
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        new Handler(mHandlerThread.getLooper()).post(() -> updateBatterySaverMode());
    }

    private void updateBatterySaverMode() {
        updateBatterySaverMode(mContext, mBasicPreference.isChecked() ? 0 : 1);
    }

    private void initRadioButton(PreferenceCategory preferenceCategory) {
        mBasicPreference = (SelectorWithWidgetPreference) preferenceCategory.findPreference("basic_battery_saver_entry");
        if (mBasicPreference != null) {
            mBasicPreference.setExtraWidgetOnClickListener(null);
            mBasicPreference.setOnClickListener(this);
            mBasicPreference.setChecked(!mIsFlipendoAggressiveMode);
        }

        mExtremePreference = (SelectorWithWidgetPreference) preferenceCategory.findPreference("extreme_battery_saver_entry");
        if (mExtremePreference != null) {
            mExtremePreference.setExtraWidgetOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchFlipendo();
                }
            });
            mExtremePreference.setOnClickListener(this);
            mExtremePreference.setChecked(mIsFlipendoAggressiveMode);
        }
    }

    public void updateSaverModeSelection(boolean z) {
        SelectorWithWidgetPreference selectorWithWidgetPreference = mBasicPreference;
        if (selectorWithWidgetPreference == null || mExtremePreference == null) {
            return;
        }
        selectorWithWidgetPreference.setChecked(z);
        mExtremePreference.setChecked(!z);
    }

    public void refreshFlipendoStates() {
        Pair<Boolean, Boolean> flipendoState = FlipendoUtils.getFlipendoState(mContext);
        mIsFlipendoAggressiveMode = ((Boolean) flipendoState.first).booleanValue();
        mIsFlipendoEnabled = ((Boolean) flipendoState.second).booleanValue();
    }

    private void updateBatterySaverMode(Context context, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("update_flipendo_mode", i);
        try {
            context.getContentResolver().call(FlipendoUtils.FLIPENDO_STATE_AUTHORITY, "update_flipendo_mode_method", (String) null, bundle);
        } catch (Exception e) {
            Log.e(TAG, "updateBatterySaverMode() failed", e);
        }
        HandlerThread handlerThread = mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            mHandlerThread = null;
        }
    }

    private void launchFlipendo() {
        try {
            mContext.startActivity(new Intent("android.settings.batterysaver.flipendo"));
        } catch (Exception e) {
            Log.e(TAG, "launchFlipendo() failed", e);
        }
    }
}
