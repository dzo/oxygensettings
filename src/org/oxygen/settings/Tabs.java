package org.oxygen.settings;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Tabs extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Mods", getResources().getDrawable(R.drawable.ic_main))
                .setContent(new Intent(this, MainTab.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Power Widget", getResources().getDrawable(R.drawable.ic_powerwidget))
                .setContent(new Intent(this, PowerWidgetTab.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("Updater", getResources().getDrawable(R.drawable.ic_updater))
                .setContent(new Intent(this, UpdateTab.class)));
    }

}
