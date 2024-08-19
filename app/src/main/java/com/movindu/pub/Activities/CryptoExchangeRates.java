package com.movindu.pub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.movindu.pub.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CryptoExchangeRates extends AppCompatActivity {
    TextView FromCurenncy,ToCurenncy,ExRate,BidPrice,AskPrice,Heading;
    LinearLayout LO,Search,NODATA;
    private Spinner From,To;
    private String FromC;
    private String ToC ;
    String API_KEY = "M15QWL1KVCMNPHA4";

    public String getFromC() {
        return FromC;
    }

    public void setFromC(String fromC) {
        FromC = fromC;
    }

    public String getToC() {
        return ToC;
    }

    public void setToC(String toC) {
        ToC = toC;
    }

    //String API_KEY = "demo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        initialize();
        From = findViewById(R.id.from);
        Heading = findViewById(R.id.heading);
        To = findViewById(R.id.to);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fromC, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        From.setAdapter(adapter);
        From.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setFromC(parent.getItemAtPosition(position).toString());

                Toast.makeText(CryptoExchangeRates.this, "Selected: " + FromC, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStockDetails("https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency="+getFromC()+"&to_currency="+getToC()+"&apikey="+API_KEY);
            }
        });


        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(this,
                R.array.toC, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        To.setAdapter(adapterTo);

        To.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                setToC(parent.getItemAtPosition(position).toString());
                Toast.makeText(CryptoExchangeRates.this, "Selected: " + ToC, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


    }
    private void initialize(){
        ExRate = (TextView) findViewById(R.id.rate);
        BidPrice = (TextView) findViewById(R.id.bid);
        AskPrice = (TextView) findViewById(R.id.ask);
        LO = (LinearLayout) findViewById(R.id.lo);
        NODATA = (LinearLayout) findViewById(R.id.noData);
        Search = (LinearLayout) findViewById(R.id.btn_search);
        LO.setVisibility(View.GONE);
        setFromC("BTC");
        setToC("USD");

    }
    public void getStockDetails(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject realtimeExchangeRate = response.getJSONObject("Realtime Currency Exchange Rate");
                            String fromCurrencyCode = realtimeExchangeRate.getString("1. From_Currency Code");
                            String exchangeRate = realtimeExchangeRate.getString("5. Exchange Rate");
                            String toCurrencyCode = realtimeExchangeRate.getString("3. To_Currency Code");
                            String bidPrice = realtimeExchangeRate.optString("8. Bid Price", null);
                            String askPrice = realtimeExchangeRate.optString("9. Ask Price", null);

                            Heading.setText("Exchange Rates "+fromCurrencyCode+" to "+toCurrencyCode);
                            ExRate.setText(exchangeRate);
                            BidPrice.setText(bidPrice);
                            AskPrice.setText(askPrice);
                            LO.setVisibility(View.VISIBLE);

                            //System.out.println(fromCurrencyName);
                        } catch (JSONException e) {
                            //throw new RuntimeException(e);
                            NODATA.setVisibility(View.VISIBLE);
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //textView.setText("Error: " + error.getMessage());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(CryptoExchangeRates.this);
        queue.add(jsonObjectRequest);
    }
}