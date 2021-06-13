package com.gdrive;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleAccountHolder {

    private static GoogleAccountHolder holder = null;
    private GoogleSignInAccount account = null;

    private GoogleAccountHolder() {}

    public static GoogleAccountHolder getInstance() {
        if (holder == null) {
            holder = new GoogleAccountHolder();
        }
        return holder;
    }

    public void setAccount(GoogleSignInAccount newAccount) {
        account = newAccount;
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

}
