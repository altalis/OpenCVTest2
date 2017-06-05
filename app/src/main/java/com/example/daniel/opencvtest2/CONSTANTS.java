package com.example.daniel.opencvtest2;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import java.util.List;

/**
 * Created by Alberto on 9/5/2017.
 */

public class CONSTANTS {
    private static final CONSTANTS ourInstance = new CONSTANTS();

    public static CONSTANTS getInstance() {
        return ourInstance;
    }

    public static List<ScanResult> wifis = null;
    public static ArrayAdapter<String> listAdapter;
    public static int LEVEL_OF_SIGNAL = 100000;
    public static String BSSID1 = "";
    public static String BSSID2 = "";
    public static String BSSID3 = "";

    public static double level1 = 0;
    public static double level2 = 0;
    public static double level3 = 0;

    public static  ArrayList<ArrayList<Float>> PANELES = new ArrayList<ArrayList<Float>>();

    public static double XActual = 0;
    public static double YActual = 0;

    public static int delay = 1000;
    public static Context CON = null;
    public static WifiManager WIFIMAN =  null;

    private CONSTANTS() {
    }
}
