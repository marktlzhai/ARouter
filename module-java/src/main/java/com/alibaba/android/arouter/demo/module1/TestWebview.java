package com.alibaba.android.arouter.demo.module1;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/test/webview")
public class TestWebview extends AppCompatActivity {

    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webview);


        webview = findViewById(R.id.webview);
        webview.loadUrl(getIntent().getStringExtra("url"));
    }
}
