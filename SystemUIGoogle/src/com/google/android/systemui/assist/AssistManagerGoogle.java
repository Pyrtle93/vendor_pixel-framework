/*
 * Copyright (C) 2022 The PixelExperience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.systemui.assist;

import static android.view.Display.DEFAULT_DISPLAY;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.IWindowManager;

import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.assist.AssistLogger;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistantSessionEvent;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.assist.ui.DefaultUiController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.model.SysUiState;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.settings.DisplayTracker;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.google.android.systemui.assist.uihints.GoogleDefaultUiController;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.NgaUiController;
import com.android.systemui.util.settings.SecureSettings;

import javax.inject.Inject;

import dagger.Lazy;

@SysUISingleton
public class AssistManagerGoogle extends AssistManager {
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private final GoogleDefaultUiController mDefaultUiController;
    private final NgaMessageHandler mNgaMessageHandler;
    private final NgaUiController mNgaUiController;
    private final OpaEnabledReceiver mOpaEnabledReceiver;
    private final Handler mUiHandler;
    private final IWindowManager mWindowManagerService;
    private final Runnable mOnProcessBundle;
    private boolean mCheckAssistantStatus = true;
    private boolean mGoogleIsAssistant;
    private int mNavigationMode;
    private boolean mNgaIsAssistant;
    private boolean mSqueezeSetUp;
    private AssistManager.UiController mUiController;

    @Inject
    public AssistManagerGoogle(DeviceProvisionedController controller,
                               Context context,
                               AssistUtils assistUtils,
                               CommandQueue commandQueue,
                               PhoneStateMonitor phoneStateMonitor,
                               OverviewProxyService overviewProxyService,
                               Lazy<SysUiState> sysUiState,
                               DefaultUiController defaultUiController,
                               GoogleDefaultUiController googleDefaultUiController,
                               AssistLogger assistLogger,
                               @Main Handler handler,
                               BroadcastDispatcher broadcastDispatcher,
                               OpaEnabledDispatcher opaEnabledDispatcher,
                               OpaEnabledReceiver opaEnabledReceiver,
                               KeyguardUpdateMonitor keyguardUpdateMonitor,
                               NavigationModeController navigationModeController,
                               AssistantPresenceHandler assistantPresenceHandler,
                               NgaUiController ngaUiController,
                               NgaMessageHandler ngaMessageHandler,
			       UserTracker userTracker,
			       DisplayTracker displayTracker,
                               SecureSettings secureSettings,
                               IWindowManager iWindowManager) {
        super(controller, context, assistUtils, commandQueue,
                phoneStateMonitor, overviewProxyService,
                sysUiState, defaultUiController,
                assistLogger, handler, userTracker,
                displayTracker, secureSettings);
        mUiHandler = handler;
        mDefaultUiController = googleDefaultUiController;
        mUiController = googleDefaultUiController;
        mNgaUiController = ngaUiController;
        mWindowManagerService = iWindowManager;
        mOpaEnabledReceiver = opaEnabledReceiver;
        addOpaEnabledListener(opaEnabledDispatcher);
        keyguardUpdateMonitor.registerCallback(new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitching(int i) {
                mOpaEnabledReceiver.onUserSwitching(i);
            }
        });
        mNavigationMode = navigationModeController.addListener(new NavigationModeController.ModeChangedListener() {
            @Override
            public final void onNavigationModeChanged(int i) {
                mNavigationMode = i;
            }
        });
        mAssistantPresenceHandler = assistantPresenceHandler;
        mAssistantPresenceHandler.registerAssistantPresenceChangeListener(new AssistantPresenceHandler.AssistantPresenceChangeListener() {
            @Override
            public final void onAssistantPresenceChanged(boolean isGoogleAssistant, boolean isNga) {


                if (mGoogleIsAssistant != isGoogleAssistant || mNgaIsAssistant != isNga) {
                    if (isNga) {
                        if (!mUiController.equals(mNgaUiController)) {
                            mUiController = mNgaUiController;
                            mUiHandler.post(() -> mUiController.hide());
                        }
                    } else {
                        if (!mUiController.equals(mDefaultUiController)) {
                            mUiController = mDefaultUiController;
                            mUiHandler.post(() -> mUiController.hide());
                        }
                        mDefaultUiController.setGoogleAssistant(isGoogleAssistant);
                    }
                    mGoogleIsAssistant = isGoogleAssistant;
                    mNgaIsAssistant = isNga;
                }
                mCheckAssistantStatus = false;
            }
        });
        mNgaMessageHandler = ngaMessageHandler;
        mOnProcessBundle = new Runnable() {
            @Override
            public final void run() {
                mAssistantPresenceHandler.requestAssistantPresenceUpdate();
                mCheckAssistantStatus = false;
            }
        };
    }

    public boolean shouldUseHomeButtonAnimations() {
        return !QuickStepContract.isGesturalMode(mNavigationMode);
    }

    @Override
    protected void registerVoiceInteractionSessionListener() {
        mAssistUtils.registerVoiceInteractionSessionListener(new IVoiceInteractionSessionListener.Stub() {
            @Override
            public void onVoiceSessionShown() throws RemoteException {
                mAssistLogger.reportAssistantSessionEvent(AssistantSessionEvent.ASSISTANT_SESSION_UPDATE);
            }

            @Override
            public void onVoiceSessionHidden() throws RemoteException {
                mAssistLogger.reportAssistantSessionEvent(AssistantSessionEvent.ASSISTANT_SESSION_CLOSE);
            }

            @Override
            public final void onVoiceSessionWindowVisibilityChanged(boolean z) throws RemoteException {
            }

            @Override
            public void onSetUiHints(Bundle bundle) {
                String string = bundle.getString("action");
                if ("set_assist_gesture_constrained".equals(string)) {
                    mSysUiState.get()
                            .setFlag(8192, bundle.getBoolean(CONSTRAINED_KEY, false))
                            .commitUpdate(DEFAULT_DISPLAY);
                } else if ("show_global_actions".equals(string)) {
                    try {
                        mWindowManagerService.showGlobalActions();
                    } catch (RemoteException e) {
                        Log.e("AssistManagerGoogle", "showGlobalActions failed", e);
                    }
                } else {
                    mNgaMessageHandler.processBundle(bundle, mOnProcessBundle);
                }
            }
        });
    }

    @Override
    public void onInvocationProgress(int i, float f) {
        if (f == 0.0f || f == 1.0f) {
            mCheckAssistantStatus = true;
            if (i == 2) {
                checkSqueezeGestureStatus();
            }
        }
        if (mCheckAssistantStatus) {
            mAssistantPresenceHandler.requestAssistantPresenceUpdate();
            mCheckAssistantStatus = false;
        }
        if (i != 2 || mSqueezeSetUp) {
            mUiController.onInvocationProgress(i, f);
        }
    }

    @Override
    public void onGestureCompletion(float f) {
        mCheckAssistantStatus = true;
        mUiController.onGestureCompletion(f / mContext.getResources().getDisplayMetrics().density);
    }

    public void addOpaEnabledListener(OpaEnabledListener opaEnabledListener) {
        mOpaEnabledReceiver.addOpaEnabledListener(opaEnabledListener);
    }

    public void dispatchOpaEnabledState() {
        mOpaEnabledReceiver.dispatchOpaEnabledState();
    }

    public boolean isActiveAssistantNga() {
        return mNgaIsAssistant;
    }

    private void checkSqueezeGestureStatus() {
        boolean z = Settings.Secure.getInt(mContext.getContentResolver(), "assist_gesture_setup_complete", 0) == 1;
        mSqueezeSetUp = z;
    }
}
