package com.example.lab11;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String key = "c4be30abf2f14984b5682816231105";
    EditText txt;
    TextView lab;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.text_city);
        lab = findViewById(R.id.label_temp);
        img = findViewById(R.id.img_icon);
    }

    public void onQueryClick(View v)
    {
        String city = txt.getText().toString();

        Thread t = new Thread(() -> {
            try
            {
                URL url = new URL("https://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + city + "&aqi=no");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                byte [] buf = new byte [1024];
                String res = "";
                while (true)
                {
                    int len = is.read(buf, 0, buf.length);
                    if (len < 0) break;
                    res = res + new String(buf, 0, len);
                }
                con.disconnect();

                Log.d("json", res);

                JSONObject doc = new JSONObject(res);

                JSONObject curr = doc.getJSONObject("current");
                float temp = (float) curr.getDouble("temp_c");

                JSONObject cond = curr.getJSONObject("condition");
                String icon = cond.getString("icon");

                URL url1 = new URL("http:" + icon);
                HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                InputStream is1 = con1.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is1);
                con1.disconnect();

                runOnUiThread(() -> {
                    lab.setText(String.valueOf(temp) + " C");
                    img.setImageBitmap(bmp);
                });
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t.start();
    }
}