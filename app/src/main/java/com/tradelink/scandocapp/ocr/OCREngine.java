package com.tradelink.scandocapp.ocr;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.tradelink.scandocapp.model.Languages;
import com.tradelink.scandocapp.utils.ZipManager;

import java.io.IOException;

public class OCREngine {

    private Context mContext;
    private String mPath;

    public OCREngine(Context context) {
        mContext = context;
        mPath = Environment.getExternalStorageDirectory().toString() + "/tessdata";
    }

    public String ocr(Bitmap bitmap, String language) {
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(Environment.getExternalStorageDirectory().toString(), language);
        tessBaseAPI.setImage(bitmap);
        String extractResult = "";
        try {
            extractResult = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tessBaseAPI.end();
        return extractResult;
    }

    public void unzipTessDataFile(String language) {

        try {
            ZipManager.unzip(mContext, language, mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
