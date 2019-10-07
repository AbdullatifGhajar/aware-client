package com.aware.phone.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aware.phone.R;
import com.aware.phone.ui.Aware_Join_Study;
import com.aware.phone.utils.AwareUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Manages dialog that is used to join a study by typing in a URL of the study config.
 */
public class JoinStudyDialog extends DialogFragment {
    private static final String TAG = "AWARE::JoinStudyDialog";
    private Activity mActivity;
    private ProgressBar mProgressBar;

    public JoinStudyDialog(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_join_study, null);

        builder.setView(dialogView);
        builder.setTitle("Enter URL for study")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText etStudyConfigUrl = dialogView.findViewById(R.id.et_join_study_url);
                        etStudyConfigUrl.setText("https://drive.google.com/file/d/1sejHjxRKUnjTN6vYXgSe6R4OIXUHVRMq");  // TODO RIO: Remove this later
//                        validateStudyConfig(etStudyConfigUrl.getText().toString());
                        new ValidateStudyConfig().execute(etStudyConfigUrl.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinStudyDialog.this.dismiss();
                    }
                });
        return builder.create();
    }

    public void showDialog() {
        this.show(mActivity.getFragmentManager(), "dialog");
    }

    private class ValidateStudyConfig extends AsyncTask<String, Void, String> {
        private ProgressDialog mLoader;
        private String url;

        @Override
        protected void onPreExecute() {
            mLoader = new ProgressDialog(mActivity);
            mLoader.setTitle(R.string.loading_join_study_title);
            mLoader.setMessage(getResources().getString(R.string.loading_join_study_msg));
            mLoader.setCancelable(true);
            mLoader.setIndeterminate(true);
            mLoader.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject studyConfig;
            url = strings[0];
            Log.i(TAG, "Joining study with URL " + url);

            try {
                studyConfig = AwareUtil.getStudyConfig(url);
                JoinStudyDialog.this.dismiss();

                if (studyConfig == null || !AwareUtil.validateStudyConfig(studyConfig)) {
                    Log.d(TAG, "Failed to join study with URL: " + url);
                } else {
                    return studyConfig.toString();
                }
            } catch (JSONException e) {
                Log.d(TAG, "Failed to join study with URL: " + url + ", reason: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String studyConfig) {
            mLoader.dismiss();
            JoinStudyDialog.this.dismiss();

            if (studyConfig == null) {
                Toast.makeText(mActivity, "Invalid study config. Please contact the " +
                                "administrator of this study or enter a different study URL.",
                        Toast.LENGTH_LONG).show();
            } else {
                Intent studyInfo = new Intent(mActivity, Aware_Join_Study.class);
                studyInfo.putExtra(Aware_Join_Study.EXTRA_STUDY_URL, url);
                studyInfo.putExtra(Aware_Join_Study.EXTRA_STUDY_CONFIG, studyConfig);
                studyInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(studyInfo);
            }
        }
    }

    private void validateStudyConfig(String url) {
        ProgressDialog loader = new ProgressDialog(mActivity);
        loader.setTitle(R.string.loading_join_study_title);
        loader.setMessage(getResources().getString(R.string.loading_join_study_msg));
        loader.setCancelable(true);
        loader.setIndeterminate(true);
        loader.show();

        Log.i(TAG, "Joining study with URL " + url);

        try {
            mProgressBar = new ProgressBar(mActivity);
            JSONObject studyConfig = AwareUtil.getStudyConfig(url);
            loader.dismiss();
            JoinStudyDialog.this.dismiss();

            if (studyConfig == null || !AwareUtil.validateStudyConfig(studyConfig)) {
                Log.d(TAG, "Failed to join study with URL: " + url);
                Toast.makeText(mActivity, "Invalid study config. Please contact the " +
                                "administrator of this study or enter a different study URL.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Intent studyInfo = new Intent(mActivity, Aware_Join_Study.class);
            studyInfo.putExtra(Aware_Join_Study.EXTRA_STUDY_URL, url);
            studyInfo.putExtra(Aware_Join_Study.EXTRA_STUDY_CONFIG, studyConfig.toString());
            studyInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(studyInfo);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to join study with URL: " + url + ", reason: " + e.getMessage());
            Toast.makeText(mActivity, "Invalid study config. Please contact the " +
                            "administrator of this study or enter a different study URL.",
                    Toast.LENGTH_LONG).show();
            loader.dismiss();
            JoinStudyDialog.this.dismiss();
        }
    }
}
