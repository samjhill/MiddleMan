package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.team.hv.middleman.middleman.XmlReader;

public class MiddleManMainActivity extends FragmentActivity {

    public static boolean clComplete = false;
    public static boolean ebayComplete = false;

    public static Double ebayAvg = 0.0;
    public static  Double ebayHighPrice = 0.0;
    public static Double ebayLowPrice = 0.0;

    public static ArrayList<CraigslistItem> craigsItems;

    private static ProgressDialog dialog;
    private static ListView selectedItemsListView;
    private static ArrayAdapter listViewAdapter;


    private static Context thisContext;

    private static View thisLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_middle_man_main);
        setContentView(R.layout.results_list_view);
        //setContentView(R.layout.default_empty_view);


        thisContext = getApplicationContext();
        thisLayout = getWindow().getDecorView();

        selectedItemsListView = (ListView)findViewById(R.id.selectedItemsListView);
        selectedItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                Log.w("item", ""+position);
                Toast.makeText(getBaseContext(), ""+position, Toast.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                ItemView itemView = new ItemView();

                CraigslistItem thisItem = craigsItems.get(position);
                bundle.putString("itemTitle",thisItem.itemTitle);
                Log.v("itemTitle",thisItem.itemTitle);
                bundle.putString("link",thisItem.link);
                Log.v("itemTitle",thisItem.link);
                bundle.putString("description",thisItem.description);
                Log.v("itemTitle",thisItem.description);
                bundle.putString("location",thisItem.location);
                Log.v("itemTitle",thisItem.location);
                bundle.putDouble("average", thisItem.average);
                Log.v("itemTitle",""+thisItem.average);
                bundle.putDouble("profit",thisItem.expectedProfit);
                Log.v("itemTitle",""+thisItem.expectedProfit);
                bundle.putDouble("price",thisItem.price);
                Log.v("itemTitle",""+thisItem.price);
                itemView.setArguments(bundle);

                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, itemView);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
            }
        });

        craigsItems = new ArrayList<CraigslistItem>();
        dialog = new ProgressDialog(this);
        Log.v("Starting:","it's goin");

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });
        checkInputs();
        //searchCraigslistAndEBay("ipod", "Rochester");
    }

    private void checkInputs(){
        EditText locationField = (EditText)findViewById(R.id.locationEditText);
        EditText itemField = (EditText)findViewById(R.id.itemEditText);
        String location = locationField.getText().toString().trim();
        String item = itemField.getText().toString().trim();

        if ( (location.equals("") || location == null) && (item.equals("")||item == null)){
            Toast.makeText(this, "A location and item to search are required", Toast.LENGTH_LONG).show();
        } else if (location.equals("") || location == null) {
            Toast.makeText(this, "A location is required", Toast.LENGTH_LONG).show();
        } else if (item.equals("")||item == null){
            Toast.makeText(this, "An item name to search is required", Toast.LENGTH_LONG).show();
        } else {
            searchCraigslistAndEBay(item, location);
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
        dialog.setMessage("Loading data from Craigslist and eBay. Sit tight, grab a cold drink.");
        dialog.show();
    }

    public static void closeLoadingDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        addCraigsItemsToListView();
    }

    private static void addCraigsItemsToListView() {
        listViewAdapter = new ArrayAdapter<CraigslistItem>(thisContext, android.R.layout.simple_list_item_1, craigsItems);
        selectedItemsListView.setAdapter(listViewAdapter);
    }

    public static void addItemToMain(CraigslistItem item){
        View view;
        LayoutInflater inflater = (LayoutInflater)  thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_middle_man_main, null);
        //populate the fields
        TextView mainDesc = (TextView) view.findViewById(R.id.itemMainDescTextView);
        mainDesc.setText(item.itemTitle);
        TextView itemCost = (TextView) view.findViewById(R.id.itemCostTextView);
        itemCost.setText(item.price + "");
        TextView profit = (TextView) view.findViewById(R.id.itemProfitTextView);
        profit.setText(item.expectedProfit + "");
        //add to parent layout
        LinearLayout mainLayout = (LinearLayout) thisLayout.findViewById(android.R.id.content);
        mainLayout.addView(view);
    }

    public static void addItemToItemView(CraigslistItem item){
        View view;
        LayoutInflater inflater = (LayoutInflater)  thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_view, null);
        //populate the fields
        TextView itemName = (TextView) view.findViewById(R.id.itemNameTextView);
        itemName.setText(item.itemTitle);
        TextView description = (TextView) view.findViewById(R.id.itemDescTextView);
        description.setText(item.description);
        TextView location = (TextView) view.findViewById(R.id.itemLocationTextView);
        location.setText(item.location);
        TextView price = (TextView) view.findViewById(R.id.itemOfferedPriceTextView);
        price.setText(item.price + "");
        TextView avgPrice = (TextView) view.findViewById(R.id.itemAveragePriceTextView);
        avgPrice.setText(item.average + "");
        TextView profit = (TextView) view.findViewById(R.id.itemExpectedProfitTextView);
        profit.setText(item.expectedProfit + "");
        //add to parent layout
        LinearLayout mainLayout = (LinearLayout) thisLayout;
        mainLayout.addView(view);
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
