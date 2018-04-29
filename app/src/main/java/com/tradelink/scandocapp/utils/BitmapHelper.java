package com.tradelink.scandocapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapHelper {

    public static String saveToFile(Context context, Bitmap image, String fileName) throws IOException {
        if (context == null) {
            return "context is null";
        }
        File file = new File(context.getFilesDir(), fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
        image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

        return file.getAbsolutePath();
    }

    public static Bitmap getBitmapFromStorage(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
