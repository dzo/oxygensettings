package org.oxygen.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

public class MainTab extends PreferenceActivity {

    private static final String KEY_BATTERY_PERCENTAGE = "battery_percentage";
    private static final String KEY_LONG_PRESS_KILL = "long_press_kill";
    private static final String KEY_PINCH_REFLOW = "pinch_reflow";
    private static final String KEY_TORCH = "torch";
    private static final String KEY_VOLBTN_MUSIC_CONTROLS = "volbtn_music_controls";

    private CheckBoxPreference mBatteryPercentage;
    private CheckBoxPreference mLongPressKill;
    private CheckBoxPreference mPinchReflow;
    private CheckBoxPreference mVolBtnMusicControls;
    private PreferenceScreen mTorch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.tab_main);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mBatteryPercentage = (CheckBoxPreference) prefSet.findPreference(KEY_BATTERY_PERCENTAGE);
        mBatteryPercentage.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.BATTERY_PERCENTAGE, 0) == 1));

        mLongPressKill = (CheckBoxPreference) prefSet.findPreference(KEY_LONG_PRESS_KILL);
        mLongPressKill.setChecked((Settings.Secure.getInt(getContentResolver(),
            Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) == 1));

        mPinchReflow = (CheckBoxPreference) prefSet.findPreference(KEY_PINCH_REFLOW);
        mPinchReflow.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.WEB_VIEW_PINCH_REFLOW, 1) == 1));

        mVolBtnMusicControls = (CheckBoxPreference) prefSet.findPreference(KEY_VOLBTN_MUSIC_CONTROLS);
        mVolBtnMusicControls.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.VOLBTN_MUSIC_CONTROLS, 1) == 1));

        mTorch = (PreferenceScreen) prefSet.findPreference(KEY_TORCH);

        // Start animation
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        ListView listView = getListView();
        listView.setLayoutAnimation(controller);
        // End animation
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mBatteryPercentage) {
            value = mBatteryPercentage.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.BATTERY_PERCENTAGE, value ? 1 : 0);
        } else if (preference == mLongPressKill) {
            value = mLongPressKill.isChecked();
            Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK, value ? 1 : 0);
        } else if (preference == mPinchReflow) {
            value = mPinchReflow.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.WEB_VIEW_PINCH_REFLOW, value ? 1 : 0);
        } else if (preference == mVolBtnMusicControls) {
            value = mVolBtnMusicControls.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.VOLBTN_MUSIC_CONTROLS, value ? 1 : 0);
        } else if (preference == mTorch) {
            startActivity(mTorch.getIntent());
        }
        return true;
    }

}
