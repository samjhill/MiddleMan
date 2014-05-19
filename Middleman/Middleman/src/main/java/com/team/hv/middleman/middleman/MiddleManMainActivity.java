package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.team.hv.middleman.middleman.XmlReader;

public class MiddleManMainActivity extends Activity {

    public static boolean clComplete = false;
    public static boolean ebayComplete = false;

    public static Double ebayAvg = 0.0;
    public static  Double ebayHighPrice = 0.0;
    public static Double ebayLowPrice = 0.0;

    public static ArrayList<CraigslistItem> craigsItems;
    public static ArrayList<Product> ebayItems;

    private static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_middle_man_main);
        setContentView(R.layout.results_list_view);

        craigsItems = new ArrayList<CraigslistItem>();
        ebayItems = new ArrayList<Product>();
        dialog = new ProgressDialog(this);
        String dummyUrl = "www.whatever.com";
        Log.v("Starting:","it's goin");
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });

        searchCraigslistAndEBay("ipod", "Rochester");
    }

    private void checkInputs(){
        EditText locationField = (EditText)findViewById(R.id.locationEditText);
        EditText itemField = (EditText)findViewById(R.id.itemEditText);
        String location = locationField.getText().toString().trim();
        String item = itemField.getText().toString().trim();

        if ( (location.equals("") || location == null) && (item.equals("")||item == null)){
            Toast.makeText(this, "A location and item to search is required", Toast.LENGTH_LONG).show();
        } else {
            //
        }
    }

    public void searchCraigslistAndEBay(String itemName, String location) {
        ebayComplete = false;
        Object[] params = {itemName, location, this.getContext()};
        XmlReader reader = new XmlReader();
        reader.execute(params);
        //clComplete = false;
        //CraigslistXmlReader cxr = new CraigslistXmlReader();
        //cxr.execute(params);

        //while (!clComplete && !ebayComplete){

        //}

        Log.v("Both are done", "DONE!");
    }

    public Context getContext() {
        return this;
    }

    public static void displayLoadingDialog() {
        dialog.setMessage("Loading data from Craigslist and EBay. Sit tight, grab a cold drink.");
        dialog.show();
    }

    public static void closeLoadingDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

/*class LoadingDialog extends AsyncTask<String, Void, Boolean> {


    public LoadingDialog(ListActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(MiddleManMainActivity.getContext());
    }

    *//** progress dialog to show user that the backup is processing. *//*
    private ProgressDialog dialog;
    *//** application context. *//*
    private ListActivity activity;

    protected void onPreExecute() {
        this.dialog.setMessage("Progress start");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }


        MessageListAdapter adapter = new MessageListAdapter(activity, titles);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();


        if (success) {
            Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }

    protected Boolean doInBackground(final String... args) {
        try{
            BaseFeedParser parser = new BaseFeedParser();
            messages = parser.parse();
            List<Message> titles = new ArrayList<Message>(messages.size());
            for (Message msg : messages){
                titles.add(msg);
            }
            activity.setMessages(titles);
            return true;
        } catch (Exception e)
        Log.e("tag", "error", e);
        return false;
    }
}
}

public class Soirees extends ListActivity {
    private List<Message> messages;
    private TextView tvSorties;
    private MyProgressDialog dialog;
    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);

        setContentView(R.layout.sorties);

        tvSorties=(TextView)findViewById(R.id.TVTitle);
        tvSorties.setText("Programme des soirées");

        // just call here the task
        AsyncTask task = new ProgressTask(this).execute();
    }

    public void setMessages(List<Message> msgs) {
        messages = msgs;
    }

}*/
