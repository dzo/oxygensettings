package org.oxygen.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;

public class MainActivity extends PreferenceActivity {
    private static final String KEY_BATTERY_PERCENT = "battery_percent";
    private static final String KEY_LONG_PRESS_KILL = "long_press_kill";
    private static final String KEY_PINCH_REFLOW = "pinch_reflow";
    private static final String KEY_STATUS_BAR_WIDGET = "status_bar_widget";
    private static final String KEY_TORCH = "torch";

    private PreferenceScreen mBatteryPercent;
    private CheckBoxPreference mLongPressKill;
    private CheckBoxPreference mPinchReflow;
    private PreferenceScreen mStatusBarWidget;
    private PreferenceScreen mTorch;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.menu_main);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mBatteryPercent = (PreferenceScreen)prefSet.findPreference(KEY_BATTERY_PERCENT);
        mLongPressKill = (CheckBoxPreference) prefSet.findPreference(KEY_LONG_PRESS_KILL);
        mLongPressKill.setChecked((Settings.Secure.getInt(getContentResolver(),
            Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) == 1));
        mPinchReflow = (CheckBoxPreference) prefSet.findPreference(KEY_PINCH_REFLOW);
        mPinchReflow.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.WEB_VIEW_PINCH_REFLOW, 1) == 1));
        mStatusBarWidget = (PreferenceScreen)prefSet.findPreference(KEY_STATUS_BAR_WIDGET);
        mTorch = (PreferenceScreen)prefSet.findPreference(KEY_TORCH);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mBatteryPercent) {
            startActivity(mBatteryPercent.getIntent());
        } else if (preference == mLongPressKill) {
            value = mLongPressKill.isChecked();
            Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK, value ? 1 : 0);
        } else if (preference == mPinchReflow) {
            value = mPinchReflow.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.WEB_VIEW_PINCH_REFLOW, value ? 1 : 0);
        } else if (preference == mStatusBarWidget) {
            startActivity(mStatusBarWidget.getIntent());
        } else if (preference == mTorch) {
            startActivity(mTorch.getIntent());
        }
        return true;
    }
}
