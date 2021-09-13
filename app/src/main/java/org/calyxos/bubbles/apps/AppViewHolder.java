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

import android.annotation.NonNull;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.calyxos.bubbles.R;
import org.calyxos.bubbles.apps.dialog.AppInfoDialogFragment;

import java.util.ArrayList;

import static org.calyxos.bubbles.apps.AppInstallerService.APKS;
import static org.calyxos.bubbles.apps.AppInstallerService.PACKAGENAMES;
import static org.calyxos.bubbles.apps.AppInstallerService.PATH;


class AppViewHolder extends ViewHolder implements AppInstallerService.InstallListener {

    private final ImageView icon;
    private final TextView name;
    private final TextView summary;
    private final ImageButton installButton;
    private final ProgressBar progressBar;
    private Activity mActivity;
    private final AppAdapter mAdapter;
    private AppItem appItem;

    private final AppInfoDialogFragment dialog;

    AppViewHolder(@NonNull View v, AppAdapter adapter, FragmentActivity activity) {
        super(v);
        mActivity = activity;
        mAdapter = adapter;

        icon = v.findViewById(R.id.icon);
        name = v.findViewById(R.id.name);
        summary = v.findViewById(R.id.summary);
        installButton = v.findViewById(R.id.installButton);
        progressBar = v.findViewById(R.id.progress);

        dialog = new AppInfoDialogFragment();

        v.setOnClickListener(view -> dialog.show(activity.getSupportFragmentManager(), "Dialog"));
    }

    void bind(AppItem item) {
        dialog.setApp(item);
        dialog.setAdapter(mAdapter);
        dialog.setAdapterPosition(getAbsoluteAdapterPosition());

        appItem = item;

        icon.setImageDrawable(item.icon);
        name.setText(item.name);
        summary.setText(item.summary);

        installButton.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, AppInstallerService.class);

            String path = mActivity.getString(R.string.calyx_fdroid_repo_location);
            i.putExtra(PATH, path);

            ArrayList<String> apks = new ArrayList<>();
            apks.add(item.getApkName());
            i.putStringArrayListExtra(APKS, apks);

            ArrayList<String> pks = new ArrayList<>();
            pks.add(item.getPackageName());
            i.putStringArrayListExtra(PACKAGENAMES, pks);

            mActivity.startForegroundService(i);

            AppInstallerService.addListener(this);
        });
    }

    @Override
    public void onInstallStart(String apk) {
        installButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInstallSuccess(String apk) {
        Toast.makeText(mActivity, mActivity.getString(R.string.successful_install, apk), Toast.LENGTH_SHORT).show();
        switchViews();
        installButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_baseline_check_24));
        mAdapter.removeItem(appItem, getAbsoluteAdapterPosition());
    }

    @Override
    public void onInstallFailed(String apk) {
        switchViews();
        Toast.makeText(mActivity, mActivity.getString(R.string.failed_install, apk), Toast.LENGTH_SHORT).show();
    }

    private void switchViews() {
        progressBar.setVisibility(View.GONE);
        installButton.setVisibility(View.VISIBLE);
    }
}
