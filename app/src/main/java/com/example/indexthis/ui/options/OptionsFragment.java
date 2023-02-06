package com.example.indexthis.ui.options;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.indexthis.MainActivity;
import com.example.indexthis.databinding.FragmentOptionsBinding;

public class OptionsFragment extends Fragment {

    private EditText etYacyHost;
    private EditText etYacyUser;
    private EditText etYacyPassword;
    private SharedPreferences sharedPref;
    private EditText etDefaultDepth;

    private FragmentOptionsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OptionsViewModel dashboardViewModel =
                new ViewModelProvider(this).get(OptionsViewModel.class);

        binding = FragmentOptionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Options Screen
        etYacyHost = binding.etYacyHost;
        etYacyUser = binding.etYacyUser;
        etYacyPassword = binding.etYacyPassword;
        etDefaultDepth = binding.etDefaultDepth;

        MainActivity ma = (MainActivity)getActivity();
        sharedPref = ma.sharedPref;

        loadPreferences();

        //
        // Options Screen actions
        //

        etYacyHost.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                sharedPref.edit().putString("host", etYacyHost.getText().toString()).commit();
            }
        });

        etYacyUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                sharedPref.edit().putString("user", etYacyUser.getText().toString()).commit();
            }
        });

        etYacyPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                sharedPref.edit().putString("password", etYacyPassword.getText().toString()).commit();
            }
        });

        etDefaultDepth.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                sharedPref.edit().putString("defaultDepth", etDefaultDepth.getText().toString()).commit();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadPreferences() {
        String yacyHost = sharedPref.getString("host", "");
        String yacyUser = sharedPref.getString("user", "admin");
        String yacyPassword = sharedPref.getString("password", "");
        String defaultDepth = sharedPref.getString("defaultDepth", "0");

        if (yacyHost != null) {
            etYacyHost.setText(yacyHost);
        }
        if (yacyUser != null) {
            etYacyUser.setText(yacyUser);
        }
        if (yacyPassword != null) {
            etYacyPassword.setText("********");
        }
        if (defaultDepth != null) {
            etDefaultDepth.setText(defaultDepth);
        } else {
            etDefaultDepth.setText("0");
        }
    }
}