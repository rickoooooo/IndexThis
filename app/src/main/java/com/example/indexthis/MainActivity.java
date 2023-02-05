package com.example.indexthis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.indexthis.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Home Screen
    private ActivityMainBinding binding;

    // Other vars
    public String yacyHost;
    public String yacyUser;
    public String yacyPassword;
    public SharedPreferences sharedPref;
    public String incomingUrl = "";

    private String TAG = "IndexThis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_options)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        loadPreferences();
    }

    void loadPreferences() {
        yacyHost = sharedPref.getString("host", "");
        yacyUser = sharedPref.getString("user", "admin");
        yacyPassword = sharedPref.getString("password", "");
    }
}