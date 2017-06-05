package com.example.daniel.opencvtest2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PanelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panels);

        Intent intent = getIntent();
        if (intent != null) {
            byte[] bytes = intent.getByteArrayExtra("BitmapPanel");
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            BitmapDrawable background = new BitmapDrawable(bm);
            FrameLayout iv = (FrameLayout) findViewById(R.id.layoutImage);
            iv.setBackground(background);
        }

        try
        {
            String jsonInput =  "[" +
                    "\"panel:01, ubicacionX: 0, ubicacionY: 0, panelEstado: true, panelCantidad: 99\"," +
                    "\"panel:02, ubicacionX: 0, ubicacionY: 1, panelEstado: false, panelCantidad: 0\"," +
                    "\"panel:03, ubicacionX: 0, ubicacionY: 2, panelEstado: true, panelCantidad: 75\"," +
                    "\"panel:04, ubicacionX: 0, ubicacionY: 3, panelEstado: true, panelCantidad: 47\"," +
                    "\"panel:05, ubicacionX: 1, ubicacionY: 0, panelEstado: true, panelCantidad: 34\"," +
                    "\"panel:06, ubicacionX: 1, ubicacionY: 1, panelEstado: true, panelCantidad: 28\"," +
                    "\"panel:07, ubicacionX: 1, ubicacionY: 2, panelEstado: true, panelCantidad: 21\"," +
                    "\"panel:08, ubicacionX: 1, ubicacionY: 3, panelEstado: true, panelCantidad: 56\"," +
                    "\"panel:09, ubicacionX: 2, ubicacionY: 0, panelEstado: true, panelCantidad: 89\"," +
                    "\"panel:10, ubicacionX: 2, ubicacionY: 1, panelEstado: true, panelCantidad: 69\"," +
                    "\"panel:11, ubicacionX: 2, ubicacionY: 2, panelEstado: true, panelCantidad: 78\"," +
                    "\"panel:12, ubicacionX: 2, ubicacionY: 3, panelEstado: true, panelCantidad: 40\"" +
                    "]";
            JSONArray jsonArray = new JSONArray(jsonInput);
            int length = jsonArray.length();
            List<String> listContents = new ArrayList<String>(length);
            for (int i = 0; i < length; i++)
            {
                listContents.add(jsonArray.getString(i));
            }

            ListView myListView = (ListView) findViewById(R.id.listViewPanels);
            myListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContents));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
