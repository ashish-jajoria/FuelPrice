package com.moldedbits.petrolprices.petrolprice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private Spinner mSpinner;
    private TextView mTextView;
    private Document mDocument;
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayAdapter<String> mStringArrayAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSpinner = (Spinner) findViewById(R.id.spinner_location);
        mTextView = (TextView) findViewById(R.id.tv_location);

        mStringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());

        new LoadStates().execute("http://www.petroldieselprice.com/petrol-diesel-fuel-price-state-wise-list");

        mSpinner.setAdapter(mStringArrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new LoadPrice().execute(addresses.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public class LoadPrice extends AsyncTask<String , Void, Document> {

        @Override
        protected Document doInBackground(String... strings) {
            try {
                mDocument = Jsoup.connect(strings[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mDocument;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            if (document != null) {
                Elements elements = mDocument.select(".amount");
                if (elements.size() != 0) {
                    mTextView.setText(elements.get(1).text());
                }
            }
        }
    }

    public class LoadStates extends AsyncTask<String , Void, Document> {

        @Override
        protected Document doInBackground(String... strings) {
            try {
                mDocument = Jsoup.connect(strings[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mDocument;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            if (document != null) {
                Elements elements = mDocument.select(".col-md-8 .single-sidebar ul li a[href]");
                for (Element element : elements) {
                    String[] names = element.ownText().split(" ");
                    mStringArrayAdapter.add(names[names.length-1]);
                    addresses.add("http://www.petroldieselprice.com" + element.attr("href"));
                }
                mStringArrayAdapter.notifyDataSetChanged();
            }
        }
    }
}
