package com.visionxoft.abacus.rehmantravel.utils;

import android.content.Context;

import com.google.common.collect.HashBiMap;
import com.visionxoft.abacus.rehmantravel.model.Currency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

public class XmlConverter {

    public XmlPullParser getXmlParser(Context context, String filename) {
        XmlPullParser parser = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(context.getAssets().open(filename), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parser;
    }

    public List<Currency> getCurrencies(XmlPullParser parser) {
        HashBiMap<Integer, Currency> currencies = HashBiMap.create();
        Currency currency = new Currency();
        String text = null;
        try {
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        switch (name) {
                            case "ccyntry":
                                currencies.put(currency.currency_number, currency);
                                currency = new Currency();
                                break;
                            case "ctrynm":
                                if (text != null) currency.country_name = text.trim();
                                break;
                            case "ccynm":
                                if (text != null) currency.currency_name = text.trim();
                                break;
                            case "ccy":
                                if (text != null) currency.currency_code = text.trim();
                                break;
                            case "ccynbr":
                                if (text != null)
                                    currency.currency_number = Integer.parseInt(text.trim());
                                break;
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Currency> list = new ArrayList<>();
        list.addAll(currencies.values());
        return list;
    }
}
