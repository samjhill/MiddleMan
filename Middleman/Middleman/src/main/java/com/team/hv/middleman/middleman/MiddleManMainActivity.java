package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.team.hv.middleman.middleman.XmlReader;

public class MiddleManMainActivity extends Activity {

    private boolean clComplete = false;
    private boolean ebayComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_man_main);
        Log.v("MiddleManMainActivity","Oh fuck, it's happening!");
        String dummyUrl = "www.whatever.com";
        XmlReader reader = new XmlReader();
        reader.execute();
        //Intent intent = new Intent(this,XmlReader.class);
        //startActivity(intent);
        //XmlReader xr = new XmlReader();
    }



}