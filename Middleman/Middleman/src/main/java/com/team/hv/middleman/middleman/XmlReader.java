package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
//"http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&
// keywords=ITEMNAMEHERE"
public class XmlReader extends AsyncTask<String, Integer, Boolean>{

    private static ArrayList<Product> products;

    @Override
    protected Boolean doInBackground(String...urls) {
        start();
        return true;
    }

    //@Override
    protected static void start() {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        products = new ArrayList<Product>();

        XmlPullParserFactory pullParserFactory;
        Log.v("Stuff","Right before try/catch");
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            URL url = new URL("http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&keywords=ipod");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            //parser.setInput(in_s, null);

            parseXML(doc);

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

    private static void parseXML(Document doc) throws XmlPullParserException,IOException
    {
        try {
            Log.v("Count Attribute", doc.getElementsByTagName("searchResult").item(0).getAttributes().getNamedItem("count").getNodeValue());

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
        printProducts(products);
    }

    private static void printProducts(ArrayList<Product> products)
    {
        String content = "";
        Iterator<Product> it = products.iterator();
        while(it.hasNext())
        {
            Product currProduct = it.next();
            //content = content + "title :" +  currProduct.title + "n";
            content = content + "price :" +  currProduct.price + "\n";
        }

        Log.v("XmlReader",content);
        Log.v("Total:",products.size()+" Products");
    }

    public static ArrayList<Product> getProducts() {
        return products;
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }
*/
}