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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.calyxos.bubbles.R;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;
import static org.calyxos.bubbles.apps.AppInstallerService.APKS;
import static org.calyxos.bubbles.apps.AppInstallerService.PACKAGENAMES;
import static org.calyxos.bubbles.apps.AppInstallerService.PATH;
import static org.calyxos.bubbles.apps.FDroidRepo.FDROID_CATEGORY_DEFAULT;

public class InstallAppsActivity extends AppCompatActivity {

    public static final String TAG = InstallAppsActivity.class.getSimpleName();

    private static String path;

    private RecyclerView list;
    private AppAdapter adapter;
    private ImageButton installAll;
    private boolean appUnchecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_apps_activity);

        list = findViewById(R.id.list);
        adapter = new AppAdapter(this);
        list.setAdapter(adapter);

        installAll = findViewById(R.id.installAllButton);
        installAll.setOnClickListener(v -> {
            installAllApps();
        });

        View allLayout = findViewById(R.id.allLayout);
        allLayout.setOnClickListener(v ->
                installAllApps());

        path = getString(R.string.calyx_fdroid_repo_location);

        getApps();
    }

    private void installAllApps() {
        ArrayList<String> apks = adapter.getAllPackageNameAPKs();
        if (apks.size() > 0) {
            Intent i = new Intent(this, AppInstallerService.class);
            i.putExtra(PATH, path);
            i.putStringArrayListExtra(APKS, apks);
            i.putStringArrayListExtra(PACKAGENAMES, adapter.getAllPackageNames());
            startForegroundService(i);

            //TODO add a progress indicator dialog and automatically remove all items from list when done installing.
        }
    }

    protected int getLayoutResId() {
        return R.layout.install_apps_activity;
    }

    protected int getTitleResId() {
        return R.string.install_apps_title;
    }


    protected int getIconResId() {
        return R.drawable.fdroid_logo;
    }

    public void onNextPressed() {
        // scroll to the end, if the user didn't
        LinearLayoutManager layoutManager = (LinearLayoutManager) requireNonNull(list.getLayoutManager());
        int lastPosition = adapter.getItemCount() - 1;
        if (layoutManager.findLastCompletelyVisibleItemPosition() != lastPosition) {
            list.smoothScrollToPosition(lastPosition);
            return;
        }

        ArrayList<String> apks = adapter.getSelectedPackageNameAPKs();
        if (apks.size() > 0) {
            Intent i = new Intent(this, AppInstallerService.class);
            i.putExtra(PATH, path);
            i.putStringArrayListExtra(APKS, apks);
            i.putStringArrayListExtra(PACKAGENAMES, adapter.getSelectedPackageNames());
            startForegroundService(i);
        }
    }

    private void getApps() {
        File repoPath = new File(path);
        if (!repoPath.isDirectory()) {
            Log.e(TAG, "Local repo does not exist: " + repoPath);
        } else {
            try {
                FDroidRepo.checkFdroidRepo(path);
            } catch (IOException | JSONException e) {
                // Do nothing.
            }
        }
        new Thread(() -> {
            FDroidRepo.loadFdroidJson(FDROID_CATEGORY_DEFAULT, path, list, adapter);
            list.post(() -> list.scrollToPosition(0));
        }).start();
    }

}
