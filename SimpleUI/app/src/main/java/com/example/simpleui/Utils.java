package com.example.simpleui;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ggm on 7/20/15.
 */
public class Utils {

    public void writeFile(Context context, String text, String fileName) {
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
}
