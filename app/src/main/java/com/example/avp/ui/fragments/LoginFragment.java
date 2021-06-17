package com.example.avp.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.lists.VideoList;
import com.example.avp.lists.impls.gdrive.GDriveVideosHolder;
import com.example.avp.lists.impls.gdrive.GdriveVideoList;
import com.example.avp.model.Model;
import com.example.avp.ui.Constants;
import com.gdrive.GoogleAccountHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 1337;
    private GoogleSignInClient googleClient;
    private GoogleSignInAccount googleAccount;
    private final Scope driveScope = new Scope(Scopes.DRIVE_FULL);

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private TextView statusText;
    private static Model model;
    private static VideoList gdriveVideoList = null;

    public LoginFragment(Model model) {
        this.model = model;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init() {
        statusText = getActivity().findViewById(R.id.login_status_textview);
        getActivity().findViewById(R.id.google_login_button).setOnClickListener(v -> signIn());

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(driveScope)
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(getActivity(), options);
        googleAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (googleAccount != null) {
            GoogleAccountHolder.getInstance().setAccount(googleAccount);
            updateVideoList(googleAccount);
        }
        updateUI(googleAccount);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnSuccessListener(this::handleSignInResult);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleSignInResult(@NotNull GoogleSignInAccount account) {
        googleAccount = account;
        GoogleAccountHolder.getInstance().setAccount(googleAccount);
        updateVideoList(account);
        updateUI(googleAccount);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void updateVideoList(GoogleSignInAccount account) {
        RecyclerView gdriveVideoRV = getActivity().findViewById(R.id.rv_gdrive);
        if (gdriveVideoList == null) {
            gdriveVideoList = new GdriveVideoList(
                    gdriveVideoRV,
                    GDriveVideosHolder.getInstance(),
                    model.getVideoListSettings(),
                    Set.of(Constants.DisplayMode.GALLERY, Constants.DisplayMode.LIST),
                    this,
                    account);

            model.addVideoList(gdriveVideoList);
            gdriveVideoList.fetchVideosAndUpdate();
        }
        else {
            gdriveVideoList.setVideoListRV(gdriveVideoRV);
        }
    }

    private void signIn() {
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(GoogleSignInAccount account) {

        GoogleAccountHolder holder = GoogleAccountHolder.getInstance();
        holder.setAccount(account);

        if (account != null) {
            statusText.setText("Login with " + account.getDisplayName());
        } else {
            statusText.setText("Not login");
        }
    }
}