package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MiddleManMainActivity extends Activity {

    var priceList = new Array();
    private boolean clComplete = false;
    private boolean ebayComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_man_main);
    }
}