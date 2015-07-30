package com.example.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 1;
    private EditText editText;
    private CheckBox hideCheckBox;
    private ListView listView;
    private Spinner spinner;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String menuResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "6As46KZTL6DzHlA0YrdQcHxe2Kkb6Z7guxjqH86f",
                "77G3RUogihUrOHAsIFxOFsd1O98R79mPAxHWsBbo");

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sp.edit();

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToOrderDetailActivity(view, position);
            }
        });

        spinner = (Spinner) findViewById(R.id.store_info);

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String text = editText.getText().toString();
                editor.putString("text", text);
                editor.commit();

                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    submit(v);
                    return true;
                }
                return false;
            }
        });

        editText.setText(sp.getString("text", ""));


        hideCheckBox = (CheckBox) findViewById(R.id.checkBox);
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("checkbox", isChecked);
                editor.commit();
            }
        });
        hideCheckBox.setChecked(sp.getBoolean("checkbox", false));

        loadHistory();
        loadStoreInfo();
    }

    public void submit(View view) {
        String text = editText.getText().toString();
        if (hideCheckBox.isChecked()) {
            text = "***********";
        }

        if (menuResult != null) {
            try {
                String storeInfo = (String) spinner.getSelectedItem();

                JSONObject order = new JSONObject();
                JSONArray menuResultArray = new JSONArray(menuResult);
                order.put("note", text);
                order.put("menu", menuResultArray);
                order.put("address", storeInfo);

                ParseObject orderObject = new ParseObject("Order");
                orderObject.put("note", text);
                orderObject.put("menu", menuResultArray);
                orderObject.put("address", storeInfo);


                orderObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                    }
                });

                Utils.writeFile(this, order.toString() + "\n", "history.txt");

                Toast.makeText(this, order.toString(), Toast.LENGTH_LONG).show();
                editText.setText("");
                menuResult = null;
                loadHistory();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void putDataToIntent(View view, Intent intent) {
        TextView note = (TextView) view.findViewById(R.id.listview_item_note);
        TextView address = (TextView) view.findViewById(R.id.listview_item_address);
        TextView sum = (TextView) view.findViewById(R.id.listview_item_sum);

        String noteStr = note.getText().toString();
        String addressStr = address.getText().toString();
        String sumStr = sum.getText().toString();

        intent.putExtra("note", noteStr);
        intent.putExtra("address", addressStr);
        intent.putExtra("sum", sumStr);
    }

    private void putDataToIntent(int position, Intent intent) {
        SimpleAdapter adapter = (SimpleAdapter) listView.getAdapter();
        Map<String, String> item = (Map<String, String>)
                adapter.getItem(position);

        intent.putExtra("note", (String) item.get("note"));
        intent.putExtra("address", (String) item.get("address"));
        intent.putExtra("sum", (String) item.get("sum"));
    }

    private void goToOrderDetailActivity(View view, int position) {
        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);

//        putDataToIntent(view, intent);
        putDataToIntent(position, intent);


        startActivity(intent);
    }

    public void goToMenuActivity(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MenuActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
    }

    public String getDrinkSum(JSONArray menu) {
        return "41";
    }

    private void loadStoreInfo() {

        ParseQuery<ParseObject> query = new ParseQuery<>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    String[] data = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getString("name");
                        String address = list.get(i).getString("address");
                        data[i] = name + "," + address;
                    }
                    setDataToSpinner(data);
                }
            }
        });
    }

    private void setDataToSpinner(String[] data) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);

        spinner.setAdapter(adapter);
    }

    private void loadHistory() {

        final List<Map<String, String>> data = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    for (ParseObject object : list) {
                        String note = object.getString("note");
                        String sum = getDrinkSum(object.getJSONArray("menu"));
                        String address = object.getString("address");

                        Map<String, String> item = new HashMap<>();
                        item.put("note", note);
                        item.put("sum", sum);
                        item.put("address", address);

                        data.add(item);
                    }
                    setDataToListView(data);
                }
            }
        });

    }

    private void setDataToListView(List<Map<String, String>> data) {
        String[] from = new String[]{"note", "sum", "address"};
        int[] to = new int[]{R.id.listview_item_note,
                R.id.listview_item_sum, R.id.listview_item_address};

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.listview_item, from, to);

        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                menuResult = data.getStringExtra("result");
                Toast.makeText(this, menuResult, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
