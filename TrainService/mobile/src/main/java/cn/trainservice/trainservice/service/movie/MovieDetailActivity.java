package cn.trainservice.trainservice.service.movie;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.RunnableFuture;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.service.serviceItem.ServiceItemListActivity;
import dalvik.system.DexClassLoader;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageLoader imageLoader;
    private Class<?> localClass;

    /**
     * 目标Activity的实例
     */
    private Object localInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ImageButton btn_movie_play = (ImageButton) findViewById(R.id.btn_movie_play);
        CubeImageView thumb_movie = (CubeImageView) findViewById(R.id.thumb_movie);
        TextView mv_introduce = (TextView) findViewById(R.id.mv_introduce);
        TextView mv_title = (TextView) findViewById(R.id.tv_mv_title);
        TextView tv_mv_year = (TextView) findViewById(R.id.tv_mv_year);
        TextView tv_mv_genres = (TextView) findViewById(R.id.tv_mv_genres);
        TextView tv_mv_rate = (TextView) findViewById(R.id.tv_mv_rate);
        Intent intent = getIntent();
        String filmType = intent.getStringExtra("filmType");
        String introduce = intent.getStringExtra("introduce");
        String imgUrl = intent.getStringExtra("imgUrl");
        final String title = intent.getStringExtra("title");
        final String sourceUrl = intent.getStringExtra("sourceUrl");
        String filmYear = intent.getStringExtra("filmYear");
        String filmRate = intent.getStringExtra("filmRate");
        ActionBar actionbar = getSupportActionBar();
        mv_title.setText(title);
        actionbar.setTitle(title);
        mv_introduce.setText(introduce);
        tv_mv_year.setText(filmYear);
        tv_mv_genres.setText(filmType);
        tv_mv_rate.setText(filmRate);
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(this);
        handler.setLoadingResources(R.mipmap.loading);
        imageLoader = ImageLoaderFactory.create(this, handler);
        thumb_movie.loadImage(imageLoader, imgUrl);
        btn_movie_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this,ProxyActivity.class);


                File storageDirectory = Environment.getExternalStorageDirectory();
                intent.putExtra("title", title);
                intent.putExtra("sourceUrl", sourceUrl);
                intent.setClassName("cn.wangbaiyuan.superplayer", "cn.wangbaiyuan.superplayer.VideoDetailActivity");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    installSuperPlayer(title, sourceUrl);
                    // Toast.makeText(MovieDetailActivity.this,"Plugin not found!",Toast.LENGTH_SHORT).show();
                }

            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void onResume() {

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onResume();
    }


    private void loadAPK(String dexPath, String dexOutputPath, String title, String sourceUrl) {
        try {

            ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
            DexClassLoader localDexClassLoader = new DexClassLoader(dexPath, dexOutputPath, null, localClassLoader.getParent());
            PackageInfo localPackageInfo = this.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);


            if ((localPackageInfo.activities != null) && (localPackageInfo.activities.length > 0)) {
                String activityName = localPackageInfo.activities[1].name;
               localClass = localDexClassLoader.loadClass(activityName);
                Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});
                localInstance = localConstructor.newInstance(new Object[]{});
                Method onCreateMethod = localClass.getDeclaredMethod("onCreate", Bundle.class);



                onCreateMethod.setAccessible(true);
                onCreateMethod.invoke(localInstance, new Object[]{null});

              /*  String packageName = localPackageInfo.packageName;
                String className = activityName;
                Intent intent = new Intent();
                intent.putExtra("title",title);
                intent.putExtra("sourceUrl",sourceUrl);
                Log.d("data1",packageName+"  "+className);
                intent.setClassName(packageName,className);
                startActivity(intent);*/

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void installSuperPlayer(String title, String sourcel) {
        new Thread(new InstallApk(title, sourcel)).start();
    }

    class InstallApk implements Runnable {
        private String title;
        private String sourcel;

        InstallApk(String title, String sourcel) {

            this.title = title;
            this.sourcel = sourcel;
        }

        @Override
        public void run() {
            openFile(downLoadFile(TrainServiceApplication.URLGetSuperPlayerApk()), title, sourcel);
        }
    }

    protected File downLoadFile(String httpUrl) {
        // TODO Auto-generated method stub
        File dataFile = null;
        final String fileName = "superplayer.apk";
        try {
            File storageDirectory = Environment.getExternalStorageDirectory();
            String path = storageDirectory.getPath() + "/trainserver/";
            File dataPath = new File(path);
            if (!dataPath.exists()) {
                dataPath.mkdir();
            }
            String FileName = path + fileName;
            dataFile = new File(FileName);
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(dataFile);
            byte[] buf = new byte[1024];
            conn.connect();
            double count = 0;
            if (conn.getResponseCode() >= 400) {
                Toast.makeText(this, "连接超时", Toast.LENGTH_SHORT)
                        .show();
            } else {
                while (true) {
                    if (is != null) {
                        int numRead = is.read(buf);
                        if (numRead <= 0) {
                            break;
                        } else {
                            fos.write(buf, 0, numRead);
                        }

                    } else {
                        break;
                    }

                }
            }

            conn.disconnect();
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFile;
    }
//打开APK程序代码

    private void openFile(File file, String title, String sourceUrl) {
        // TODO Auto-generated method stub

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        // intent.putExtra("title", title);
        //  intent.putExtra("sourceUrl", sourceUrl);

        //  intent.setClassName("cn.wangbaiyuan.superplayer", "cn.wangbaiyuan.superplayer.VideoDetailActivity");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            // startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void plugload(File apk, String name) {
        //  Class<?> classname = Class.forName(name);
    }


}