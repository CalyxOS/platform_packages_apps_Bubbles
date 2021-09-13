package org.calyxos.bubbles.apps.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import org.calyxos.bubbles.R;
import org.calyxos.bubbles.apps.AppInstallerService;
import org.calyxos.bubbles.apps.AppItem;
import org.calyxos.bubbles.databinding.AppInfoDialogViewBinding;

import java.util.ArrayList;
import java.util.List;

import static org.calyxos.bubbles.apps.AppInstallerService.APKS;
import static org.calyxos.bubbles.apps.AppInstallerService.PACKAGENAMES;
import static org.calyxos.bubbles.apps.AppInstallerService.PATH;

public class AppInfoDialogFragment extends DialogFragment {

    private static String path;
    private AppItem app;
    private Activity activity;
    private AppInfoDialogViewBinding binding;

    public AppInfoDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (app != null) {

            binding = AppInfoDialogViewBinding.inflate(requireActivity().getLayoutInflater());
            View view = binding.getRoot();//inflater.inflate(R.layout.app_info_dialog_view, null);
            builder.setView(view);

            //initialize UI
            binding.appIcon.setImageDrawable(app.getIcon());
            binding.appName.setText(app.getName());
            binding.appDeveloper.setText(app.getAuthor().isEmpty()? getString(R.string.developer) : app.getAuthor());
            binding.appDesc.setText(app.getSummary());

            binding.appSize.setText(getString(R.string.size));//placeholder
            binding.appRating.setText(getString(R.string.zero_double));//placeholder
            binding.appDownloadCount.setText(getString(R.string.zero_int));//placeholder

            binding.installButton.setOnClickListener(v -> {
                Intent i = new Intent(requireActivity(), AppInstallerService.class);
                path = getString(R.string.calyx_fdroid_repo_location);
                i.putExtra(PATH, path);
                ArrayList<String> apks = new ArrayList<>();
                apks.add(app.getApkName());
                i.putStringArrayListExtra(APKS, apks);
                ArrayList<String> pks = new ArrayList<>();
                pks.add(app.getPackageName());
                i.putStringArrayListExtra(PACKAGENAMES, pks);
                requireActivity().startForegroundService(i);

                dismiss();
            });

            binding.launchFDroid.setOnClickListener(v -> {
                Uri uri = Uri.parse("fdroid.app:" + app.getPackageName());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

                dismiss();
            });

        } else {
            builder.setMessage(getString(R.string.null_app))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        }
        return builder.create();
    }

    public AppItem getApp() {
        return app;
    }

    public void setApp(AppItem app) {
        this.app = app;
    }

    public Activity getAssociatedActivity() {
        return activity;
    }

    public void setAssociatedActivity(Activity activity) {
        this.activity = activity;
    }
}
