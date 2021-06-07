package com.gdrive;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleAccountHolder {

    private static GoogleAccountHolder holder = new GoogleAccountHolder();
    private static GoogleSignInAccount account = null;

    private GoogleAccountHolder() {}

    public static GoogleAccountHolder getInstance() {
        return holder;
    }

    public static void setAccount(GoogleSignInAccount newAccount) {
        account = newAccount;
    }

    public static GoogleSignInAccount getAccount() {
        return account;
    }

}
