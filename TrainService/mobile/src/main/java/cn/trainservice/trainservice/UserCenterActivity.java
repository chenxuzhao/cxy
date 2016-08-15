package cn.trainservice.trainservice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class UserCenterActivity extends AppCompatActivity {
   private Button signl_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        signl_out = (Button)findViewById(R.id.signl_out);
        signl_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainTravel.reset();
                finish();
            }
        });
    }


}
