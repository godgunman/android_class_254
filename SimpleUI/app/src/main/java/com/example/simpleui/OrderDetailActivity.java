package com.example.simpleui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class OrderDetailActivity extends ActionBarActivity
        implements OnMapReadyCallback {

    private TextView textView;
    private WebView webView;
    private ImageView imageView;

    private GoogleMap googleMap;
    private double[] geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        textView = (TextView) findViewById(R.id.textView);
        webView = (WebView) findViewById(R.id.webView);
        imageView = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        String note = intent.getStringExtra("note");
        String address = intent.getStringExtra("address").split(",")[1];
        String sum = intent.getStringExtra("sum");

        webView.loadUrl(Utils.getStaticMapUrl(address));

        String url = Utils.getGeoQueryUrl(address);

        Utils.NetworkTask task = new Utils.NetworkTask();
        task.setCallback(new Utils.NetworkTask.Callback() {
            @Override
            public void done(byte[] fetchResult) {
                String jsonString = new String(fetchResult);
                geoPoint = Utils.getGeoPoint(jsonString);
                textView.setText("" + geoPoint[0] + "," + geoPoint[1]);

                if (googleMap != null)
                    setUpGoogleMap();
            }
        });
        task.execute(url);

        final String staticMapUrl = Utils.getStaticMapUrl(address);
        Utils.NetworkTask getStaticMapTask = new Utils.NetworkTask();
        getStaticMapTask.setCallback(new Utils.NetworkTask.Callback() {
            @Override
            public void done(byte[] fetchResult) {
                Bitmap bm = Utils.byteToBitmap(fetchResult);
//                Log.d("debug", "len:" + fetchResult.length);
//                Log.d("debug", "bitmap:" + bm);
                imageView.setImageBitmap(bm);
            }
        });
        getStaticMapTask.execute(staticMapUrl);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    //25.017409, 121.540402
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

        if(geoPoint != null)
            setUpGoogleMap();

    }
}
