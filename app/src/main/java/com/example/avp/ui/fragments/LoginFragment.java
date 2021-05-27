package com.example.avp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avp.R;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private Button login;
    private EditText email, password;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.login_fragment, container, false);
        login = view.findViewById(R.id.button_login);
        email = view.findViewById(R.id.edit_text_email);
        password = view.findViewById(R.id.edit_text_password);

        login.setOnClickListener(v -> {
            //TODO
        });

        return view;
    }

}