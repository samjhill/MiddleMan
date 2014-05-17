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

    //apply standard deviation to arrayList<Product>
    //filters out products that are not what we're looking for
    public Double stdDev(ArrayList<Product> products){
        int numItems = products.size();
        double price = 0;
        double maxPrice = 0;
        int minPrice = 99999999;

        for(int i = 0; i < products.size(); i++){
            int currentPrice = Integer.parseInt(products.get(i).price);

            if(currentPrice > maxPrice){
                maxPrice = currentPrice;
            }
            if(currentPrice < minPrice){
                minPrice = currentPrice;
            }
            price += currentPrice;
        }
        //get average
        double avgPrice = price / numItems;

        Log.v("MiddleManMainActivity","Highest price: " + maxPrice);
        Log.v("MiddleManMainActivity", "Average price: " + avgPrice);
        Log.v("MiddleManMainActivity","Lowest price: " + minPrice);

        Log.v("MiddleManMainActivity","Number of items: " + (numItems));

        //get sAvg
        double sAvgPrice = 0;
        for(int i = 0; i < products.size(); i++){
            sAvgPrice += (Double.parseDouble(products.get(i).price) / 30);
        }

        //calculate stdDev
        double stddev = 0;
        double sum = 0;
        for(int i = 0; i < 30; i++) {
            double difference = Double.parseDouble(products.get(i).price) - sAvgPrice;
            sum += difference * difference/30;
        }

        stddev = Math.pow(sum, .5);
        Log.v("MiddleManMainActivity","stdDev: " + (stddev));
        return stddev;
        }
    }
}