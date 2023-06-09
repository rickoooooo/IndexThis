package com.example.indexthis.ui.home;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
//import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.example.indexthis.MainActivity;
import com.example.indexthis.R;
import com.example.indexthis.databinding.ActivityMainBinding;
import com.example.indexthis.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String TAG = "IndexThis";

    // Home Screen
    private Button bIndexThis;
    private SeekBar sbDepth;
    public EditText etIndexUrl;
    private EditText etDepth;
    private String yacyHost;
    private String yacyUser;
    private String yacyPassword;
    private String defaultDepth;
    private SharedPreferences sharedPref;

    private OkHttpClient client;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Main Screen
        sbDepth = binding.sbDepth;
        etIndexUrl = binding.etUrl;
        bIndexThis = binding.bIndexThis;
        etDepth = binding.etDepth;

        MainActivity ma = (MainActivity) getActivity();
        sharedPref = ma.getPreferences(Context.MODE_PRIVATE);
        loadPreferences();
        etDepth.setText(defaultDepth);

        // Process incoming data shared from other apps
        Intent intent = ma.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                etIndexUrl.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }

        //
        // Main Screen actions
        //
        // Depth slider action
        sbDepth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                etDepth.setText(String.format("%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bIndexThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!yacyHost.toLowerCase().startsWith("http://")) {
                    if (!yacyHost.toLowerCase().startsWith("https://")) {
                        Log.e(TAG, "Unsupported scheme. Must be http:// or https://");
                        return;
                    }
                }

                HttpUrl.Builder urlBuilder = HttpUrl.parse(yacyHost + "/Crawler_p.html").newBuilder();
                urlBuilder.addQueryParameter("crawlingstart", "")
                        .addQueryParameter("crawlingMode", "url")
                        .addQueryParameter("crawlingURL", etIndexUrl.getText().toString())
                        .addQueryParameter("crawlingDepth", etDepth.getText().toString())
                        .addQueryParameter("range", "width")
                        .addQueryParameter("deleteOld", "off")
                        .addQueryParameter("recrawl", "reload")
                        .addQueryParameter("reloadIfOlderNumber", "7")
                        .addQueryParameter("reloadIfOlderUnit", "day")
                        .addQueryParameter("indexText", "on")
                        .addQueryParameter("indexMedia", "on");

                Log.d(TAG, "URL: " + urlBuilder.toString());

                String url = urlBuilder.build().toString();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                /*
                // DIGEST authentication

                final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
                DigestAuthenticator authenticator = new DigestAuthenticator(new Credentials(yacyUser, yacyPassword));
                client = new OkHttpClient.Builder()
                        .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                        .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                        .build();
                */

                // BASIC auth
                client = new OkHttpClient.Builder()
                        .authenticator(new Authenticator() {
                            @Override
                            public Request authenticate(Route route, Response response) throws IOException {
                                String credential = Credentials.basic(yacyUser, yacyPassword);
                                return response.request().newBuilder().header("Authorization", credential).build();
                            }
                        })
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                        Log.e(TAG, "Error making request");
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Request failed!", Snackbar.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String myResponse = response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar(response.code());
                                if (response.code() == 200) {
                                    Log.d(TAG, "HTTP 200 Response");
                                } else {
                                    Log.e(TAG, "Bad HTTP response: Code " + response.code());
                                }
                            }
                        });
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void loadPreferences() {
        yacyHost = sharedPref.getString("host", "");
        yacyUser = sharedPref.getString("user", "admin");
        yacyPassword = sharedPref.getString("password", "");
        defaultDepth = sharedPref.getString("defaultDepth", "0");
    }

    void showSnackbar(int statusCode) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Response: HTTP " + statusCode, Snackbar.LENGTH_SHORT)
                .show();
    }
}