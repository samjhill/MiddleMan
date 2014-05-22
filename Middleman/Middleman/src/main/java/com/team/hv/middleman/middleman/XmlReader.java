package com.team.hv.middleman.middleman;

import android.os.AsyncTask;
import android.util.Log;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import org.w3c.dom.Element;

import java.io.IOException;
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

    // this app's key for accessing the craigslist rss feed
    private static final String craigsKey = "rit483d65-f477-4935-ac6d-35e12287a5b";

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
            URL url = new URL("http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME="+craigsKey+"&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&keywords="+URLEncoder.encode(itemName, "UTF-8"));
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
            }
        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }
        printProducts();
    }

    private static void printProducts()
    {
        products = stdDev(products);

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

    // apply a standard deviation calculation to the combined prices of the passed in products, and remove the lower tail
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

        // LOg prices and number of items
        Log.v("MiddleManMainActivity","Highest price: " + maxPrice);
        Log.v("MiddleManMainActivity", "Average price: " + avgPrice);
        Log.v("MiddleManMainActivity","Lowest price: " + minPrice);
        Log.v("MiddleManMainActivity","Number of items: " + (numItems));
        Log.v("","High: "+maxPrice+" - Avg: "+avgPrice+" - Low: "+minPrice +" - Count: "+products.size());

        eBayAvgPrice = avgPrice;

        MiddleManMainActivity.ebayHighPrice = maxPrice;
        MiddleManMainActivity.ebayAvg = avgPrice;
        MiddleManMainActivity.ebayLowPrice = minPrice;

        return products;

    }

    // search for items from craigslist, using an item or caterory to search and a city
    public void getItemsFromCragislist(String city, String itemToSearch) {
        cityName = city.toLowerCase();
        String item = itemToSearch;

        try {
            // http://cityname.craigslist.org/search/?areaID=126&catAbb=sss&query=itemtosearch&sort=rel&format=rss
            URL url = new URL("http://" + cityName.replaceAll("\\s","") + ".craigslist.org/search/?areaID=126&catAbb=sss&query=" + URLEncoder.encode(item,"UTF-8") + "&sort=rel&format=rss");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            //parse the resultant xml document
            parseCraigsXML(doc);

        } catch (XmlPullParserException e) {

            e.printStackTrace();
            Log.v("XmlParser", "try/catch fucked up");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    //parse the Craigslist XML
    private void parseCraigsXML(Document doc) throws XmlPullParserException,IOException
    {
        try {
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
                    // get different values from ever 'item' node's tag name
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
                    //title must contain dollar sign, or else we are not interested in it (must be selling, must have a price to compare)
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
                // sort the collection
                Collections.sort(items);
            }

            printItems();

        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);

        }
    }

    // print the outputs and add completed list of craigslist items to the craigsItems ArrayList in MiddleManMainActivity
    public void printItems() {

        if (MiddleManMainActivity.craigsItems.size()>0){
            MiddleManMainActivity.craigsItems.clear();
        }
        MiddleManMainActivity.craigsItems.addAll(items);

        //display information on every item
        for (int i=0;i<items.size();i++){
            Log.v("Items Contains: ","title - "+items.get(i).itemTitle+" | link -  "+items.get(i).link +" | price - "+items.get(i).price+" | desc - "+ items.get(i).description +" | location - "+items.get(i).location);
        }
    }
}
