package com.getiton.android.app.core;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by christophesmet on 06/09/15.
 */

public class CoreActivity extends AppCompatActivity {

    public void showloading(View v, boolean show) {
        v.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}