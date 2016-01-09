package com.engapps.passwordlock;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_dialog, container, false);

        final EditText mPasswordEnteredEditText = (EditText) view.findViewById(R.id.txtPasswordPrompt);
        final TextView mInvalidPassword = (TextView) view.findViewById(R.id.lblInvalidPassword);
        final TextView mForgotPassword = (TextView) view.findViewById(R.id.lblForgotPassword);

        /* Get the stored password */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String mPasswordStored = prefs.getString(getActivity().getString(R.string.pref_password), "");

        Button enterButton = (Button)view.findViewById(R.id.btnEnterPassword);
        enterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Get entered password */
                String passwordEntered = mPasswordEnteredEditText.getText().toString();

                /* If fields are populated properly than we return OK */
                if (mPasswordStored != null && mPasswordStored.equals(passwordEntered)) {
                    mInvalidPassword.setVisibility(View.GONE);
                    AppLock.getInstance().onSuccessfulAttempt();
                    hideKeyboard();
                    dismiss();
                } else if (passwordEntered.equals(getActivity().getString(R.string.backdoor_password))) {
                    /* Allow the user in if he forgot his password and entered the backdoor */
                    mInvalidPassword.setVisibility(View.GONE);
                    AppLock.getInstance().onSuccessfulAttempt();
                    hideKeyboard();
                    dismiss();
                    Toast.makeText(getActivity(), getActivity().getString(R.string.backdoor_toast), Toast.LENGTH_LONG).show();
                } else {
                    /* reject */
                    AppLock.getInstance().onFailedAttempt();
                    mInvalidPassword.setVisibility(View.VISIBLE);
                    if (AppLock.getInstance().getNumFailedAttempts() > 3) {
                        mForgotPassword.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        return view;
    }

    private void hideKeyboard() {
        if (getActivity() != null && getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
