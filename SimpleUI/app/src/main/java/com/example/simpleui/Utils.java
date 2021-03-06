package com.example.simpleui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by ggm on 7/20/15.
 */
public class Utils {

    public static void writeFile(Context context, String text, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(
                    fileName, Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String readFile(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            fis.read(buffer);
            fis.close();
            return new String(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] uriToBytes(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;

            while( (len = is.read(buffer)) != -1) {
                baos.write(buffer);
            }

            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri getOutputUri() {
        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        File file = new File(dir, "photo.png");
        return Uri.fromFile(file);
    }

    /**
     * https://maps.googleapis.com/maps/api/geocode/json?address=taipei101
     * @param urlStr
     * @return
     */

    public static String fetchUrl(String urlStr) {
        return new String(fetchUrlToByte(urlStr));
    }

    public static byte[] fetchUrlToByte(String urlStr) {

        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;

            while( (len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    final static String GEO_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public static String getGeoQueryUrl(String address) {
        try {
            return GEO_URL + URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    final static String STATIC_MAP_URL =
            "https://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=16&size=400x400&maptype=roadmap";
    public static String getStaticMapUrl(String center) {
        try {
            center = URLEncoder.encode(center, "utf-8");
            return String.format(STATIC_MAP_URL, center);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static class NetworkTask extends AsyncTask<String, Void, byte[]> {

        private Callback callback;
        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected byte[] doInBackground(String... params) {

            String url = params[0];
            return Utils.fetchUrlToByte(url);
        }

        @Override
        protected void onPostExecute(byte[] fetchResult) {
            callback.done(fetchResult);
        }

        interface Callback {
            void done(byte[] fetchResult);
        }
    }

    public static double[] getGeoPoint(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONObject location = object.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            return new double[]{lat, lng};

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getDrinkSum(JSONArray menu) {
        //TODO homework2
        return "41";
    }

}