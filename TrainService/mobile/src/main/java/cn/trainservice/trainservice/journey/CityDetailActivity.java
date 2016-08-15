package cn.trainservice.trainservice.journey;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.trainservice.trainservice.R;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class CityDetailActivity extends AppCompatActivity {

    private WebView webview;
    private ImageLoader imageLader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String url = intent.getStringExtra("url");

        Log.d("data3", "url : " + url);
        final String cityName = intent.getStringExtra("cityName");
        final String imgUrl = intent.getStringExtra("imgUrl");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(cityName + "-" + getString(R.string.title_activity_city_detail));
        }
        final SwipeRefreshLayout swipe_web_container = (SwipeRefreshLayout) findViewById(R.id.container);
        swipe_web_container.setAddStatesFromChildren(false);
        //设置刷新时动画的颜色，可以设置4个
        swipe_web_container.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipe_web_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.reload();
            }
        });
        CubeImageView thumb_city_detail = (CubeImageView) findViewById(R.id.thumb_city_detail);
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(this);
        // handler.setLoadingResources(Color.BLUE);
        imageLader = ImageLoaderFactory.create(this, handler);
        thumb_city_detail.loadImage(imageLader, imgUrl);
        webview = (WebView) findViewById(R.id.city_webview);
        WebSettings Settings = webview.getSettings();
      //  Settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        Settings.setJavaScriptEnabled(true);
        Settings.setAllowFileAccess(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                swipe_web_container.post(new Runnable() {

                    @Override
                    public void run() {
                        swipe_web_container.setRefreshing(true);
                    }
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String iurl) {


                webview.clearHistory();
                webview.loadUrl(iurl);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                swipe_web_container.post(new Runnable() {

                    @Override
                    public void run() {
                        swipe_web_container.setRefreshing(false);
                    }
                });
            }
        });
        webview.loadUrl(url);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.destroy();
        }
        super.onDestroy();
    }
}
