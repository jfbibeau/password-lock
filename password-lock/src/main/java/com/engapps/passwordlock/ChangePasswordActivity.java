package com.engapps.passwordlock;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

	    /* Handle the Update Password button */
        Button button = (Button)findViewById(R.id.updatePasswordBtn);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
		/* Get the input from the fields */
        EditText newPasswordView = (EditText)findViewById(R.id.txtNewPassword);
        EditText newPasswordConfirmView = (EditText)findViewById(R.id.txtNewPasswordConfirm);

	    /* Convert the field's input into strings */
        String newPwd = newPasswordView.getText().toString();
        String newPwdConfirm = newPasswordConfirmView.getText().toString();

		/* Get the password preference */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

	    /* If fields are populated properly than we save the new password */
        if (!newPwd.equals("")) {
            if (newPwd.equals(newPwdConfirm)) {
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putString(getString(R.string.pref_password), newPwd);
                prefsEditor.putBoolean(getString(R.string.pref_password_enabled), true);
                prefsEditor.apply();
                setResult(RESULT_OK);
                finish();
            } else {
                new AlertDialog.Builder(this).setTitle(R.string.dialog_title).setMessage(R.string.dialog_error_newPassword).setNeutralButton(R.string.dialog_close, null).show();
            }
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.dialog_title).setMessage(R.string.dialog_error_noNewPassword).setNeutralButton(R.string.dialog_close, null).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppLock.getInstance().lockIfNeeded(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppLock.getInstance().onPause();
    }

}