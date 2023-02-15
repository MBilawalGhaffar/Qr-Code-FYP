package com.example.qrcodefyp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qrcodefyp.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

public class QrScannerActivity extends AppCompatActivity {

    ImageView imageView;

    private TextView text;
    String URL = "https://www.youtube.com/watch?v=ufaK_Hd6BpI";
    String URL1 = "https://lightenpic.pro/qr-image?sandbox=true&name=642bf002026d6157aadeaa9718c09cdb&ext=jpg";
    String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        imageView=findViewById(R.id.load_img);
        new FetchMetadataFromURL().execute();
    }
    private class FetchMetadataFromURL extends AsyncTask<Void, Void, Void> {
        String websiteTitle, websiteDescription, imgurl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to website
                Document document = Jsoup.connect(URL).get();
                // Get the html document title
                websiteTitle = document.title();




                //Here It's just print whole property of URL
                Elements metaElems = document.select("meta");
                for (Element metaElem : metaElems) {
                    String property = metaElem.attr("property");
                    Log.e("Property", "Property =" + property + " \n Value =" + metaElem.attr("content"));
                }


                // Locate the content attribute
                websiteDescription = metaElems.attr("content");
                String ogImage = null;
                Elements metaOgImage = document.select("meta[property=og:image]");
                if (metaOgImage != null) {
                    imgurl = metaOgImage.first().attr("content");
                    System.out.println("src :<<<------>>> " + ogImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            text.setText("Title : " + websiteTitle + "\n\nImage Url :: " + imgurl);

            //t2.setText(websiteDescription);
            Glide.with(getApplicationContext()).load(imgurl).into(imageView);

        }

    }
}