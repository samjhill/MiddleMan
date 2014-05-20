package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.w3c.dom.Element;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


class Product
{
    //public String title;
    public double price;

    public void Product(double thisPrice){
        price = thisPrice;
    }

}

class CraigslistItem implements Comparable<CraigslistItem> {
    public String itemTitle;
    public String link;
    public Double price;
    public String description;
    public String location;
    public Double average;
    public Double expectedProfit;


    public CraigslistItem (String theTitle, String theLink, Double thePrice, String theDesc, String theLoc, Double avgPrice){
        itemTitle = theTitle;
        link = theLink;
        price = thePrice*1.0;
        description = theDesc;
        location = theLoc;
        average = avgPrice*1.0;
        expectedProfit = average - price;
    }

    public int compareTo(CraigslistItem other) {
        return price.compareTo(other.price);
    }

    public String toString() {
        return itemTitle;
    }
}
//"http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&
// keywords=ITEMNAMEHERE"
public class XmlReader extends AsyncTask<Object, Integer, Boolean>{

    private static ArrayList<Product> products;
    private static double eBayAvgPrice;
    ArrayList<CraigslistItem> items;
    String cityName;

    @Override
    protected Boolean doInBackground(Object...search) {
        eBayAvgPrice = 0.0;
        try {
            getProductsFromEBay((String)search[0]);
            items = new ArrayList<CraigslistItem>();
            getItemsFromCragislist((String) search[1], (String) search[0]);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    protected void onPreExecute() {
        MiddleManMainActivity.displayLoadingDialog();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        MiddleManMainActivity.closeLoadingDialog();
    }


    //@Override
    protected static void getProductsFromEBay(String itemName) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        products = new ArrayList<Product>();



        Log.v("Stuff","Right before try/catch");
        try {
            URL url = new URL("http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&keywords="+URLEncoder.encode(itemName, "UTF-8"));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            //parser.setInput(in_s, null);

            parseEBayXML(doc);

        } catch (XmlPullParserException e) {

            e.printStackTrace();
            Log.v("XmlParser","try/catch fucked up");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException pce){
            pce.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


    }

    private static void parseEBayXML(Document doc) throws XmlPullParserException,IOException
    {
        try {
            //Log.v("Count Attribute", doc.getElementsByTagName("searchResult").item(0).getAttributes().getNamedItem("count").getNodeValue());

            // If count of returned items is not 0
            if (!doc.getElementsByTagName("searchResult").item(0).getAttributes().getNamedItem("count").getNodeValue().trim().equals("0")){
                NodeList nodeListOfItems = doc.getElementsByTagName("item");

                for (int i=0;i < nodeListOfItems.getLength(); i++) {
                    // cast Node as Element
                    Element thisItem = (Element) nodeListOfItems.item(i);
                    Log.v("thisItem", thisItem.getTagName());
                    Element priceNode = (Element) thisItem.getElementsByTagName("sellingStatus").item(0);
                    Log.v("priceNode", priceNode.getTagName());
                    Node convertedCost = priceNode.getElementsByTagName("convertedCurrentPrice").item(0);
                    Log.v("convertedCost", convertedCost.getNodeName());
                    Product product = new Product();
                    product.price = Double.parseDouble(convertedCost.getChildNodes().item(0).getNodeValue());
                    products.add(product);
                    Log.v("Product", "Added: " + product.price);
                }
            } else {
                //TODO let user know nothing was returned
            }
            /*
            name = new TextView[nodeList.getLength()];
            website = new TextView[nodeList.getLength()];
            category = new TextView[nodeList.getLength()];
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                //name[i] = new TextView(this);
                //website[i] = new TextView(this);
                //category[i] = new TextView(this);
                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("name");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                name[i].setText("Name = "
                        + ((Node) nameList.item(0)).getNodeValue());
                NodeList websiteList = fstElmnt.getElementsByTagName("website");
                Element websiteElement = (Element) websiteList.item(0);
                websiteList = websiteElement.getChildNodes();
                website[i].setText("Website = "
                        + ((Node) websiteList.item(0)).getNodeValue());
                category[i].setText("Website Category = "
                        + websiteElement.getAttribute("category"));

            }
            */
        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);

        }
        /*
        ArrayList<Product> products = null;
        int eventType = parser.getEventType();
        Product currentProduct = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    products = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("item")){
                        currentProduct = new Product();
                    } else if (currentProduct != null){
                        if (name.equals("title")){
                            currentProduct.title = parser.nextText();
                        } else if (name.equals("currentPrice")){
                            currentProduct.price = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && currentProduct != null){
                        products.add(currentProduct);
                    }
            }
            eventType = parser.next();
        }
        */
        printProducts();
    }

    private static void printProducts()
    {
        products = stdDev(products);
        //if (MiddleManMainActivity.ebayItems.size()>0){
        //    MiddleManMainActivity.ebayItems.clear();
        //}
        //MiddleManMainActivity.ebayItems.addAll(products);
        MiddleManMainActivity.ebayComplete = true;

        Log.v("Count Attribute", ""+products.size());

        String content = "";
        Iterator<Product> it = products.iterator();
        while(it.hasNext())
        {
            Product currProduct = it.next();
            //content = content + "title :" +  currProduct.title + "n";
            content = content + "price :" +  currProduct.price + "\n";
        }

        Log.v("XmlReader",content);
        Log.v("Total:", products.size() + " Products");
    }

    public static ArrayList<Product> stdDev(ArrayList<Product> products){
        int numItems = products.size();
        double totalPrice = 0;
        double maxPrice = 0;
        double minPrice = 99999999;

        //get sAvg
        double sAvgPrice = 0;
        for(int i = 0; i < products.size(); i++){
            sAvgPrice += products.get(i).price / 30;
        }

        //calculate stdDev
        double stddev = 0;
        double sum = 0;
        for(int i = 0; i < 30; i++) {
            double difference = products.get(i).price - sAvgPrice;
            sum += difference * difference/30;
        }

        stddev = Math.pow(sum, .5);
        Log.v("MiddleManMainActivity","stdDev: " + (stddev));

        for(int i = 0; i < products.size(); i++){
            double currentPrice = products.get(i).price;

            if(currentPrice > maxPrice){
                maxPrice = currentPrice;
            }
            if(currentPrice < minPrice){
                minPrice = currentPrice;
            }
            totalPrice += currentPrice;
        }
        //get average
        double avgPrice = totalPrice / numItems;

        int higherThanAvg = 0;
        int lowerThanAvg = 0;

        for(int i = 0; i < products.size(); i++){
            if(products.get(i).price > avgPrice){
                higherThanAvg++;
            }
            if(products.get(i).price < avgPrice){
                lowerThanAvg++;
            }
        }

        //drop lower tail of stdDev
        //anything less than avg-(stddev * 2)
        double standardizedsum = 0;

        double lowerTail = avgPrice - (lowerThanAvg / (lowerThanAvg + higherThanAvg) * stddev);
        for( int i = 0; i < products.size(); i++) {
            //if item's price is below the lower tail
            if (products.get(i).price < lowerTail) {
                //remove it
                products.remove(i);
            } else { //the number is within boundaries; keep it
                standardizedsum += products.get(i).price; //add value to standardized sum
            }
        }

        avgPrice = standardizedsum / (products.size()*1.0);

        maxPrice = Double.MIN_VALUE;
        minPrice = Double.MAX_VALUE;
        for(int i = 0; i < products.size(); i++){
            double currentPrice = products.get(i).price;

            if(currentPrice > maxPrice){
                maxPrice = currentPrice;
            }
            if(currentPrice < minPrice){
                minPrice = currentPrice;
            }
            totalPrice += currentPrice;
        }





        Log.v("MiddleManMainActivity","Highest price: " + maxPrice);
        Log.v("MiddleManMainActivity", "Average price: " + avgPrice);
        Log.v("MiddleManMainActivity","Lowest price: " + minPrice);

        Log.v("MiddleManMainActivity","Number of items: " + (numItems));
        /*
        double ebayHighPrice = 0; //lowest price of remaining items in products arrayList
        for(int i = 0; i > products.size(); i++) {
            if(products.get(i).price > ebayHighPrice) {
                ebayHighPrice = products.get(i).price;
            }
        }
        double ebagAvgPrice = standardizedsum / products.size()*1.0;
        double ebayLowPrice = 99999999; //highest price of remaining items in products arrayList
        for(int i = 0; i > products.size(); i++) {
            if(products.get(i).price < ebayLowPrice) {
                ebayLowPrice = products.get(i).price;
            }
        }
        */

        eBayAvgPrice = avgPrice;
        MiddleManMainActivity.ebayHighPrice = maxPrice;
        MiddleManMainActivity.ebayAvg = avgPrice;
        MiddleManMainActivity.ebayLowPrice = minPrice;
        Log.v("","High: "+maxPrice+" - Avg: "+avgPrice+" - Low: "+minPrice +" - Count: "+products.size());



        return products;

    }

    public static ArrayList<Product> getProducts() {
        return products;
    }

    public void getItemsFromCragislist(String city, String itemToSearch) {
        cityName = city.toLowerCase();
        //replace all white space
        //cityName = cityName.replaceAll("\\s","");
        String item = itemToSearch;

        try {
            // http://cityname.craigslist.org/search/?areaID=126&catAbb=sss&query=itemtosearch&sort=rel&format=rss
            URL url = new URL("http://" + cityName.replaceAll("\\s","") + ".craigslist.org/search/?areaID=126&catAbb=sss&query=" + URLEncoder.encode(item,"UTF-8") + "&sort=rel&format=rss");
            URL shortUrlForWebBrowser = new URL("http://" + cityName + ".craigslist.org/search/?areaID=126&catAbb=sss&query=" + item + "&sort=rel");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            //parser.setInput(in_s, null);

            parseCraigsXML(doc);

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

    private void parseCraigsXML(Document doc) throws XmlPullParserException,IOException
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
                    Double price;
                    String description;
                    String location;

                    // cast Node as Element
                    Element thisItem = (Element) nodeListOfItems.item(i);
                    Log.v("thisItem", thisItem.getTagName());
                    Element linkNode = (Element)thisItem.getElementsByTagName("link").item(0);
                    link = linkNode.getChildNodes().item(0).getNodeValue();
                    Log.v("linkNode", linkNode.getTagName());
                    Element descNode = (Element)thisItem.getElementsByTagName("description").item(0);
                    description = descNode.getChildNodes().item(0).getNodeValue();
                    Element titleNode = (Element) thisItem.getElementsByTagName("title").item(0);
                    Log.v("titleNode", titleNode.getTagName());


                    String titleText = titleNode.getChildNodes().item(0).getNodeValue();
                    if (!titleText.contains("(")||!titleText.contains(")")){
                        location = cityName;
                    } else {
                        location = titleText.substring(titleText.lastIndexOf("(") + 1, titleText.lastIndexOf(")")).trim();
                    }


                    //&#x0024; is unicode for $ (dollar sign)
                    if (titleText.contains("&#x0024;")) {
                        title = titleText.substring(0, titleText.indexOf("&#x0024;"));
                        Log.v("Title", titleText);
                        price = Double.parseDouble(titleText.substring(titleText.indexOf("&#x0024;")+8, titleText.length()));
                        Log.v("Index of $",""+titleText.indexOf("&#x0024;"));
                        Log.v("Index of last",""+(titleText.length()-1));
                        //Log.v("price:",price);
                        //price = "";
                        if (price < eBayAvgPrice) {
                            items.add(new CraigslistItem(title, link, price, description, location, eBayAvgPrice));
                        }
                    } else if (titleText.contains("$")) {
                        title = titleText.substring(0, titleText.indexOf("$"));
                        price = Double.parseDouble(titleText.substring(titleText.indexOf("$")+1, titleText.length()));
                        if (price > eBayAvgPrice) {
                            items.add(new CraigslistItem(title, link, price, description, location, eBayAvgPrice));
                        }
                    }
                    Log.v("At number",""+i);
                }
                Collections.sort(items);
            } else {
                //TODO let user know nothing was returned
            }

            printItems();

        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);

        }
    }

    public void printItems() {

        if (MiddleManMainActivity.craigsItems.size()>0){
            MiddleManMainActivity.craigsItems.clear();
        }
        MiddleManMainActivity.craigsItems.addAll(items);
        MiddleManMainActivity.clComplete = true;

        for (int i=0;i<items.size();i++){
            Log.v("Items Contains: ","title - "+items.get(i).itemTitle+" | link -  "+items.get(i).link +" | price - "+items.get(i).price+" | desc - "+ items.get(i).description +" | location - "+items.get(i).location);
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
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }
*/
//apply standard deviation to arrayList<Product>
//filters out products that are not what we're looking for


}
