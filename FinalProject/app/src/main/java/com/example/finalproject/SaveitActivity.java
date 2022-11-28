 package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//colour inspriation based on third colour in the generated palette
 public class SaveitActivity extends AppCompatActivity {
    WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveit);

        myWebView = (WebView)findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());

//        get values from explicit intent
        Bundle extras = getIntent().getExtras();
        String hex;
        hex = extras.getString("HEX_CODE");

        Log.e(" hex in SAVE IT" , hex);

        //open url in webview
        myWebView.loadUrl("https://savee.it/search/?q=%23" + hex);
    }
}

