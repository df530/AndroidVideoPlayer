package com.example.avp.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avp.R;

public class NoStoragePermissionFragment extends Fragment {

    private NoStoragePermissionViewModel mViewModel;

    public static NoStoragePermissionFragment newInstance() {
        return new NoStoragePermissionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.no_storage_permission_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NoStoragePermissionViewModel.class);
        // TODO: Use the ViewModel
    }

}