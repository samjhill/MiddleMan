package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Ben on 5/17/2014.
 */
class CraigslistItem {
    public String itemTitle;
    public String link;
    public Double price;

    public CraigslistItem (String theTitle, String theLink, Double thePrice){
        itemTitle = theTitle;
        link = theLink;
        price = thePrice;
    }
}

public class CraigslistXmlReader extends AsyncTask<String, Integer, Boolean> {
    //http://cityname.craigslist.org/search/?areaID=126&catAbb=sss&query=' +itemtosearch+ '&sort=rel&format=rss
    ArrayList<CraigslistItem> items;


    @Override
    protected Boolean doInBackground(String...search){
        //getItemsFromCragislist(search[0], search[1]);
        items = new ArrayList<CraigslistItem>();
        getItemsFromCragislist("Rochester", "ipod");
        return true;
    }

    public void getItemsFromCragislist(String city, String itemToSearch) {
        String cityName = city.toLowerCase();
        String item = itemToSearch;

        try {
            // http://cityname.craigslist.org/search/?areaID=126&catAbb=sss&query=itemtosearch&sort=rel&format=rss
            URL url = new URL("http://" + cityName + ".craigslist.org/search/?areaID=126&catAbb=sss&query=" + item + "&sort=rel&format=rss");
            URL shortUrlForWebBrowser = new URL("http://" + cityName + ".craigslist.org/search/?areaID=126&catAbb=sss&query=" + item + "&sort=rel");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            //parser.setInput(in_s, null);

            parseXML(doc);

        } catch (XmlPullParserException e) {

            e.printStackTrace();
            Log.v("XmlParser", "try/catch fucked up");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private void parseXML(Document doc) throws XmlPullParserException,IOException
    {
        try {
            //Log.v("Count Attribute", doc.getElementsByTagName("searchResult").item(0).getAttributes().getNamedItem("count").getNodeValue());
            NodeList nodeListOfItems = doc.getElementsByTagName("item");


            // If count of returned items is not 0
            if (nodeListOfItems.getLength()>0){
                //NodeList nodeListOfItems = doc.getElementsByTagName("item");

                for (int i=0;i < nodeListOfItems.getLength(); i++) {
                    String title;
                    String link;
                    String price;

                    // cast Node as Element
                    Element thisItem = (Element) nodeListOfItems.item(i);
                    Log.v("thisItem", thisItem.getTagName());
                    Element linkNode = (Element)thisItem.getElementsByTagName("link").item(0);
                    link = linkNode.getChildNodes().item(0).getNodeValue();
                    Log.v("linkNode", linkNode.getTagName());
                    Element titleNode = (Element) thisItem.getElementsByTagName("title").item(0);
                    Log.v("titleNode", titleNode.getTagName());

                    String titleText = titleNode.getChildNodes().item(0).getNodeValue();
                    //&#x0024; is unicode for $ (dollar sign)
                    if (titleText.contains("&#x0024;")) {
                        title = titleText.substring(0, titleText.indexOf("&#x0024;"));
                        Log.v("Title", titleText);
                        price = titleText.substring(titleText.indexOf("&#x0024;")+8, titleText.length());
                        Log.v("Index of $",""+titleText.indexOf("&#x0024;"));
                        Log.v("Index of last",""+(titleText.length()-1));
                        //Log.v("price:",price);
                        //price = "";
                        items.add(new CraigslistItem(title,link,Double.parseDouble(price)));
                    } else if (titleText.contains("$")) {
                        title = titleText.substring(0, titleText.indexOf("$"));
                        price = titleText.substring(titleText.indexOf("$")+1, titleText.length());
                        items.add(new CraigslistItem(title,link,Double.parseDouble(price)));
                    }
                    Log.v("At number",""+i);
                }
            } else {
                //TODO let user know nothing was returned
            }

            printItems();

        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);

        }
    }

    public void printItems() {
        for (int i=0;i<items.size();i++){
            Log.v("Items Contains: ","title - "+items.get(i).itemTitle+" | link -  "+items.get(i).link +" | price - "+items.get(i).price );
        }
    }
    /*
    function parseXml(xml) {
        //console.log(xml);
        $(xml).find("item").each(function() {
            var title = $(this).find("title").text();
            var link = $(this).find("link").text();
            //parse title down so it's not super long
            var prettyTitle = title.substr(0, title.indexOf("$"));
            //parse price out of title
            var price = title.substr(title.indexOf("$"), title.indexOf(" ") + 1);


            var priceInt = parseInt(price.substr(1));

            var items = new Array();
            //this will get rid of TRADE items
            if ( priceInt >= 0 ) {
                generatePost(link, priceInt, prettyTitle);
                items.push( new Object(link, priceInt, prettyTitle));

            }
            ////console.log(items);
            craigslistItems = items;
            clComplete = true;

        });
    }
     */
        //get user location: http://developer.android.com/training/location/retrieve-current.html#CheckServices
    /*
        // Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult

        private final static int
                CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        
        // Define a DialogFragment that displays the error dialog
        public static class ErrorDialogFragment extends DialogFragment {
            // Global field to contain the error dialog
            private Dialog mDialog;
            // Default constructor. Sets the dialog field to null
            public ErrorDialogFragment() {
                super();
                mDialog = null;
            }
            // Set the dialog to display
            public void setDialog(Dialog dialog) {
                mDialog = dialog;
            }
            // Return a Dialog to the DialogFragment.
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return mDialog;
            }
        }
        
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services

        //@Override
        protected void onActivityResult(
        int requestCode, int resultCode, Intent data) {
            // Decide what to do based on the original request code
            switch (requestCode) {
                
                case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again

                    switch (resultCode) {
                        case Activity.RESULT_OK :
                    /*
                     * Try the request again

                            
                            break;
                    }
                    
            }
        }
        
        private boolean servicesConnected() {
            // Check that Google Play services is available
            int resultCode =
                    GooglePlayServicesUtil.
                            isGooglePlayServicesAvailable(this);
            // If Google Play services is available
            if (ConnectionResult.SUCCESS == resultCode) {
                // In debug mode, log the status
                Log.d("Location Updates",
                        "Google Play services is available.");
                // Continue
                return true;
                // Google Play services was not available for some reason
            } else {
                // Get the error code
                int errorCode = connectionResult.getErrorCode();
                // Get the error dialog from Google Play services
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                        errorCode,
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

                // If Google Play services can provide an error dialog
                if (errorDialog != null) {
                    // Create a new DialogFragment for the error dialog
                    ErrorDialogFragment errorFragment =
                            new ErrorDialogFragment();
                    // Set the dialog in the DialogFragment
                    errorFragment.setDialog(errorDialog);
                    // Show the error dialog in the DialogFragment
                    errorFragment.show(getSupportFragmentManager(),
                            "Location Updates");
                }
            }
        }
        */



}
