package org.oxygen.settings;

import org.oxygen.settings.PowerWidgetUtil;
import org.oxygen.settings.TouchInterceptor;

import android.app.ListActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;

public class PowerWidgetOrderActivity extends ListActivity
{
    private static final String TAG = "PowerWidgetOrderActivity";

    private ListView mButtonList;
    private ButtonAdapter mButtonAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.order_power_widget_buttons_activity);

        mButtonList = getListView();
        ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
        mButtonAdapter = new ButtonAdapter(this);
        setListAdapter(mButtonAdapter);

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
    public void onDestroy() {
        ((TouchInterceptor) mButtonList).setDropListener(null);
        setListAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // reload our buttons and invalidate the views for redraw
        mButtonAdapter.reloadButtons();
        mButtonList.invalidateViews();
    }

    private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
                // get the current button list
                ArrayList<String> buttons = PowerWidgetUtil.getButtonListFromString(
                        PowerWidgetUtil.getCurrentButtons(PowerWidgetOrderActivity.this));

                // move the button
                if(from < buttons.size()) {
                    String button = buttons.remove(from);

                    if(to <= buttons.size()) {
                        buttons.add(to, button);

                        // save our buttons
                        PowerWidgetUtil.saveCurrentButtons(PowerWidgetOrderActivity.this,
                                PowerWidgetUtil.getButtonStringFromList(buttons));

                        // tell our adapter/listview to reload
                        mButtonAdapter.reloadButtons();
                        mButtonList.invalidateViews();
                    }
                }
            }
        };

    private class ButtonAdapter extends BaseAdapter {
        private Context mContext;
        private Resources mSystemUIResources = null;
        private LayoutInflater mInflater;
        private ArrayList<PowerWidgetUtil.ButtonInfo> mButtons;

        public ButtonAdapter(Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);

            PackageManager pm = mContext.getPackageManager();
            if(pm != null) {
                try {
                    mSystemUIResources = pm.getResourcesForApplication("com.android.systemui");
                } catch(Exception e) {
                    mSystemUIResources = null;
                    Log.e(TAG, "Could not load SystemUI resources", e);
                }
            }

            reloadButtons();
        }

        public void reloadButtons() {
            ArrayList<String> buttons = PowerWidgetUtil.getButtonListFromString(
                    PowerWidgetUtil.getCurrentButtons(mContext));

            mButtons = new ArrayList<PowerWidgetUtil.ButtonInfo>();
            for(String button : buttons) {
                if(PowerWidgetUtil.BUTTONS.containsKey(button)) {
                    mButtons.add(PowerWidgetUtil.BUTTONS.get(button));
                }
            }
        }

        public int getCount() {
            return mButtons.size();
        }

        public Object getItem(int position) {
            return mButtons.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final View v;
            if(convertView == null) {
                v = mInflater.inflate(R.layout.order_power_widget_button_list_item, null);
            } else {
                v = convertView;
            }

            PowerWidgetUtil.ButtonInfo button = mButtons.get(position);

            final TextView name = (TextView)v.findViewById(R.id.name);
            final ImageView icon = (ImageView)v.findViewById(R.id.icon);

            name.setText(button.getTitleResId());

            // assume no icon first
            icon.setVisibility(View.GONE);

            // attempt to load the icon for this button
            if(mSystemUIResources != null) {
                int resId = mSystemUIResources.getIdentifier(button.getIcon(), null, null);
                if(resId > 0) {
                    try {
                        Drawable d = mSystemUIResources.getDrawable(resId);
                        icon.setVisibility(View.VISIBLE);
                        icon.setImageDrawable(d);
                    } catch(Exception e) {
                        Log.e(TAG, "Error retrieving icon drawable", e);
                    }
                }
            }

            return v;
        }
    }
}

