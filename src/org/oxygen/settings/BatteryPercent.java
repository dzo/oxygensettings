package org.oxygen.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;

public class BatteryPercent extends PreferenceActivity {
    private static final String KEY_BATTERY_PERCENTAGE = "battery_percentage";
    private static final String KEY_BATTERY_PERCENTAGE_CHARGE = "battery_percentage_charge";

    private CheckBoxPreference mBatteryPercentage;
    private CheckBoxPreference mBatteryPercentageCharge;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.menu_battery_percentage);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mBatteryPercentage = (CheckBoxPreference) prefSet.findPreference(KEY_BATTERY_PERCENTAGE);
        mBatteryPercentage.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.BATTERY_PERCENTAGE, 0) == 1));
        mBatteryPercentageCharge = (CheckBoxPreference) prefSet.findPreference(KEY_BATTERY_PERCENTAGE_CHARGE);
        mBatteryPercentageCharge.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.BATTERY_PERCENTAGE_CHARGE, 0) == 1));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mBatteryPercentage) {
            value = mBatteryPercentage.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.BATTERY_PERCENTAGE, value ? 1 : 0);
        } else if (preference == mBatteryPercentageCharge) {
            value = mBatteryPercentageCharge.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.BATTERY_PERCENTAGE_CHARGE, value ? 1 : 0);
        }
        return true;
    }
}
