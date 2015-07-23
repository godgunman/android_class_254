package com.example.simpleui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void add1(View view) {
        Button button = (Button) view;
        int count = Integer.parseInt(button.getText().toString());
        button.setText(String.valueOf(count + 1));
    }

    private String getResult() {
        LinearLayout root = (LinearLayout) findViewById(R.id.root);
        int count = root.getChildCount();
        for (int i = 0; i < count -1 ; i++) {
            LinearLayout drinkStatus = (LinearLayout) root.getChildAt(i);
            String drinkName =
                    ((TextView)drinkStatus.getChildAt(0)).getText().toString();
            String l =
                    ((Button)drinkStatus.getChildAt(1)).getText().toString();
            String m =
                    ((Button)drinkStatus.getChildAt(2)).getText().toString();
            String s =
                    ((Button)drinkStatus.getChildAt(3)).getText().toString();
        }

        return "";
    }

    public void done(View view) {
        Intent intent = new Intent();
        intent.putExtra("result", getResult());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
