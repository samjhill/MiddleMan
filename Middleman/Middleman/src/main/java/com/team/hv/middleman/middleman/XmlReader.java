package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


class Product
{

    public String title;
    public String price;

}
//"http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&
// keywords=ITEMNAMEHERE"

//http://androiddevelopement.blogspot.in/2011/06/android-xml-parsing-tutorial-using.html
public class XmlReader{

    //@Override
    protected static void start() {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        XmlPullParserFactory pullParserFactory;
        Log.v("Stuff","Right before try/catch");
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();


            InputStream in_s = theContext.getAssets().open("http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&keywords=ipod");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseXML(parser);

        } catch (XmlPullParserException e) {

            e.printStackTrace();
            Log.v("XmlParser","try/catch fucked up");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
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

        printProducts(products);
    }

    private static void printProducts(ArrayList<Product> products)
    {
        String content = "";
        Iterator<Product> it = products.iterator();
        while(it.hasNext())
        {
            Product currProduct = it.next();
            content = content + "title :" +  currProduct.title + "n";
            content = content + "price :" +  currProduct.price + "n";
        }

        Log.v("XmlReader",content);
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