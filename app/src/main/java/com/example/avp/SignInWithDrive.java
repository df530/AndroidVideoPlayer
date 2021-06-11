package com.example.avp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gdrive.GDriveWrapper;
import com.gdrive.GoogleAccountHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignInWithDrive extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1337;
    private GoogleSignInClient googleClient;
    private GoogleSignInAccount googleAccount;
    private final Scope driveScope = new Scope(Scopes.DRIVE_FULL);
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_fragment);

        statusText = findViewById(R.id.login_status_textview);
        findViewById(R.id.google_login_button).setOnClickListener(v -> signIn());

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(driveScope)
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this, options);
    }

    @Override
    public void onStart() {
        super.onStart();

        googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(googleAccount);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        task.addOnSuccessListener(googleAccount -> {
            GoogleAccountHolder.setAccount(googleAccount);
            updateUI(googleAccount);
        });
    }

    private void signIn() {
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        googleClient.signOut().addOnCompleteListener(this, (task) -> updateUI(null));
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            statusText.setText("Login with " + account.getDisplayName());
        } else {
            statusText.setText("Not login");
        }
    }

}
