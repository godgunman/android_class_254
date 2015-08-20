package com.example.simpleui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class OrderDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private TextView address;
    private WebView staticMapWebView;
    private ImageView staticMapImage;
    private Spinner showMethod;

    private GoogleMap googleMap;
    private double[] geoPoint;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        viewsInit();

        String addressString = getIntent().getStringExtra("address").split(",")[1];

        address.setText(addressString);
        staticMapWebView.loadUrl(Utils.getStaticMapUrl(addressString));

        loadGeoPoint(addressString);
        loadStaticMapToImageView(addressString);

    }

    private void viewsInit() {
        address = (TextView) findViewById(R.id.textView_address);
        staticMapWebView = (WebView) findViewById(R.id.webView_static_map);
        staticMapImage = (ImageView) findViewById(R.id.imageView_static_map);

        showMethod = (Spinner) findViewById(R.id.spinner_show_method);
        showMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"WebView", "ImageView", "MapFragment"}));
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        showMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                switch (selected) {
                    case "WebView":
                        staticMapWebView.setVisibility(View.VISIBLE);
                        staticMapImage.setVisibility(View.INVISIBLE);
                        mapFragment.getView().setVisibility(View.INVISIBLE);
                        break;
                    case "ImageView":
                        staticMapWebView.setVisibility(View.INVISIBLE);
                        staticMapImage.setVisibility(View.VISIBLE);
                        mapFragment.getView().setVisibility(View.INVISIBLE);
                        break;
                    case "MapFragment":
                        staticMapWebView.setVisibility(View.INVISIBLE);
                        staticMapImage.setVisibility(View.INVISIBLE);
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadStaticMapToImageView(String addressString) {
        String staticMapUrl = Utils.getStaticMapUrl(addressString);
        Utils.NetworkTask getStaticMapTask = new Utils.NetworkTask();
        getStaticMapTask.setCallback(new Utils.NetworkTask.Callback() {
            @Override
            public void done(byte[] fetchResult) {
                Bitmap bm = Utils.byteToBitmap(fetchResult);
                staticMapImage.setImageBitmap(bm);
            }
        });
        getStaticMapTask.execute(staticMapUrl);
    }

    private void loadGeoPoint(final String addressString) {
        String geoQueryUrl = Utils.getGeoQueryUrl(addressString);

        Utils.NetworkTask task = new Utils.NetworkTask();
        task.setCallback(new Utils.NetworkTask.Callback() {
            @Override
            public void done(byte[] fetchResult) {
                String jsonString = new String(fetchResult);
                geoPoint = Utils.getGeoPoint(jsonString);
                address.setText(addressString + "(" + geoPoint[0] + "," + geoPoint[1] + ")");

                if (googleMap != null)
                    setUpGoogleMap();
            }
        });
        task.execute(geoQueryUrl);
    }

    private void setUpGoogleMap() {

        String[] tmp = getIntent().getStringExtra("address").split(",");
        String title = tmp[0];
        String snippet = tmp[1];

        LatLng store = new LatLng(geoPoint[0], geoPoint[1]);

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(store, 15));
        googleMap.setMyLocationEnabled(true);

        googleMap.addMarker(new MarkerOptions()
                .position(store)
                .title(title)
                .snippet(snippet));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (geoPoint != null)
            setUpGoogleMap();

    }
}
