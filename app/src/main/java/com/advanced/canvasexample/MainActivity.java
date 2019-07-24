package com.advanced.canvasexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyView myView = new MyView(this);
        myView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // SOS: setContentView takes either a layout resource file OR a View!
        setContentView(myView);
    }
}
