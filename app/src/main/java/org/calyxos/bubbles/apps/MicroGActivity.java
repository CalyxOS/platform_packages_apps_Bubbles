/*
 * Copyright (C) 2019 The Calyx Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.calyxos.bubbles.apps;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.calyxos.bubbles.R;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

public class MicroGActivity extends AppCompatActivity {

    public static final String TAG = MicroGActivity.class.getSimpleName();
    private static final String[] MICROG_PACKAGES = new String[]{
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.android.vending",
            "org.fitchfamily.android.dejavu",
            "org.microg.nlp.backend.ichnaea",
            "org.microg.nlp.backend.nominatim"
    };

    private PackageManager pm;
    private Switch enableSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microg_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pm = getPackageManager();

        enableSwitch = findViewById(R.id.enableSwitch);
        //init state
        enableSwitch.setChecked(isMicroGEnabled());

        findViewById(R.id.switchLayout).setOnClickListener(v -> {
            enableSwitch.toggle();
            if(enableSwitch.isChecked()) {
                iteratePackages(true);
            } else {
                iteratePackages(false);
            }
        });
    }

    private boolean isMicroGEnabled() {
        boolean enabled = false;
        for (String packageId : MICROG_PACKAGES) {
            if (pm.getApplicationEnabledSetting(packageId) == COMPONENT_ENABLED_STATE_DISABLED) {
                enabled = false;
                break;
            } else enabled = true;
        }

        return enabled;
    }

    private void iteratePackages(boolean enabled) {
        for (String packageId : MICROG_PACKAGES) {
            setAppEnabled(packageId, true);
        }
    }

    protected int getLayoutResId() {
        return R.layout.microg_activity;
    }

    protected int getTitleResId() {
        return R.string.microg_title;
    }

    protected int getIconResId() {
        return R.drawable.microg_icon;
    }

    public void onNextPressed() {
        boolean enabled = enableSwitch.isChecked();
        for (String packageId : MICROG_PACKAGES) {
            setAppEnabled(packageId, enabled);
        }
    }

    private void setAppEnabled(String packageName, boolean enabled) {
        int state = enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        pm.setApplicationEnabledSetting(packageName, state, 0);
    }
}
