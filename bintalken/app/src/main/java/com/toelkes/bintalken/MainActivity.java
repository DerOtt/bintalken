package com.toelkes.bintalken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//danke an chris fuer die rechenleistung, ein dualcore ist doch ein wenig grausam um darauf zu arbeiten <3

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        WebView webView  = new WebView(this);
        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        webView.loadUrl("https://binhacken.app");
        String cookies = CookieManager.getInstance().getCookie("https://binhacken.app");
        Log.d("Cookies", cookies);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token

                        String token = task.getResult().getToken();
                        // Log and toast
                        //String msg = getString(token);
                        Log.d("Main Activity, Token", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        if (((WebView) findViewById(R.id.web_view)).getUrl() == "https://binhacken.app/home"){
                            String ur = "https://binhacken.app/token?data=" + token;
                            try {
                                URL url = new URL(ur);
                                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                                urlc.setRequestProperty("User-Agent", "Android Application:");
                                urlc.setRequestProperty("Connection", "close");
                                urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
                                urlc.connect();
                                if (urlc.getResponseCode() == 200){
                                    Toast.makeText(MainActivity.this, "Token successfully sent", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Token not sent, please restart the app or contact the dev", Toast.LENGTH_LONG).show();
                                }
                                urlc.disconnect();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }




                        }
                    }
                });
        //Log.d("something", FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken());



    }


}
