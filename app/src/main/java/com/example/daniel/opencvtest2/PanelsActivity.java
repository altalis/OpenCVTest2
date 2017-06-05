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
                    "\"Panel: 01"+ "\n" + "Ubicación(0,0)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 99\"," +
                    "\"Panel: 02"+ "\n" + "Ubicación(0,1)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 0\"," +
                    "\"Panel: 03"+ "\n" + "Ubicación(0,2)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 75\"," +
                    "\"Panel: 04"+ "\n" + "Ubicación(0,3)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 47\"," +
                    "\"Panel: 05"+ "\n" + "Ubicación(1,0)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 34\"," +
                    "\"Panel: 06"+ "\n" + "Ubicación(1,1)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 28\"," +
                    "\"Panel: 07"+ "\n" + "Ubicación(1,2)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 21\"," +
                    "\"Panel: 08"+ "\n" + "Ubicación(1,3)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 56\"," +
                    "\"Panel: 09"+ "\n" + "Ubicación(3,0)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 89\"," +
                    "\"Panel: 10"+ "\n" + "Ubicación(3,1)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 69\"," +
                    "\"Panel: 11"+ "\n" + "Ubicación(3,2)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 78\"," +
                    "\"Panel: 12"+ "\n" + "Ubicación(3,3)"+ "\n" +"Estado: true"+ "\n" +"Cantidad: 40\"" +
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
