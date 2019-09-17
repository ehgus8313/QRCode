package com.ehgus.qrcode;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoActivity extends Activity {


    private TextView Appversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.75));

        TextView github = (TextView) findViewById(R.id.github);
        Appversion = (TextView) findViewById(R.id.version);

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern1 = Pattern.compile("https://github.com/ehgus8313");
        String versionName = BuildConfig.VERSION_NAME;
        Appversion.setText("앱 버전 : " + versionName + " Ver");

        Linkify.addLinks(github, pattern1, "https://github.com/ehgus8313",null,mTransform);

        ImageButton close = (ImageButton) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }


}
