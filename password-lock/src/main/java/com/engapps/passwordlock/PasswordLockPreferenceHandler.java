package com.engapps.passwordlock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PasswordLockPreferenceHandler implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final int ACT_UPDATE_PWD = 20;
    private PreferenceFragment mCallingFragment;
    private Context mContext;

    /**
     * Handler for onChange, onClick, and onActivityResult dealing with password lock preferences.
     * @param context Application context (usually the Settings Activity).
     * @param callingFragment The fragment hosting the password preferences.
     */
    public PasswordLockPreferenceHandler(Context context, PreferenceFragment callingFragment) {
        mCallingFragment = callingFragment;
        mContext = context;

        /* Attach listener to enable password */
        Preference enablePasswordPref = mCallingFragment.findPreference(mContext.getString(R.string.pref_password_enabled));
        if (enablePasswordPref != null) {
            enablePasswordPref.setOnPreferenceChangeListener(this);
        }

        /* Launch update password when you click on the password preference */
        Preference passwordPref = mCallingFragment.findPreference(mContext.getString(R.string.pref_password));
        if (passwordPref != null) {
            passwordPref.setOnPreferenceClickListener(this);
        }
    }

    /**
     * Handles when the password_enabled preferences changes (enabled or disabled)
     * @param preference The password_enabled preference object
     * @param newValue New value, true if password lock enabled, false if disabled.
     * @return true if preference change should be committed.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (newValue.equals(false)) {
            /* When disabling password, wipe the saved password and proceed */
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString(mContext.getString(R.string.pref_password), "");
            prefsEditor.putBoolean(mContext.getString(R.string.pref_password_enabled), false);
            prefsEditor.apply();
            return true;
        } else if (newValue.equals(true)) {
            /* When enabling password, immediately allow the user to pick a password */
            launchPasswordChangeActivity();
            return false;
        }
        return true;
    }

    /**
     * Handles when the change_password preference is clicked.
     * @param preference The change_password preference object.
     * @return true if preference change should be committed.
     */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        launchPasswordChangeActivity();
        return true;
    }

    /**
     * Handles logic after returning from the password change activity.
     * @param resultCode One of the valid Activity result codes.
     */
    public void onActivityResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            /* When coming back from setting the password, manually tick the password enable box since the screen won't refresh */
            CheckBoxPreference chkPref = (CheckBoxPreference) mCallingFragment.findPreference(mContext.getString(R.string.pref_password_enabled));
            chkPref.setChecked(true);
        }
    }

    private void launchPasswordChangeActivity() {
        Intent i = new Intent(mContext, ChangePasswordActivity.class);
        mCallingFragment.startActivityForResult(i, ACT_UPDATE_PWD);
    }
}
