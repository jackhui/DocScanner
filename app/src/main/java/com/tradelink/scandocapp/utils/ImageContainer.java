package com.tradelink.scandocapp.utils;

import android.graphics.Bitmap;

public class ImageContainer {
    private Bitmap mOrigin;
    private Bitmap mDocument;
    private static ImageContainer mInstance;

    public static ImageContainer getInstance() {
        if (mInstance == null) {
            mInstance = new ImageContainer();
        }
        return mInstance;
    }

    public void setOrigin(Bitmap origin) {
        mOrigin = Bitmap.createBitmap(origin);
    }

    public void setDocument(Bitmap document) {
        mDocument = Bitmap.createBitmap(document);
    }

    public Bitmap getOrigin() {
        return mOrigin;
    }

    public Bitmap getDocument() {
        return mDocument;
    }

    public void clearImages() {
        if(!mOrigin.isRecycled()) {
            mOrigin.recycle();
        }
        if (!mDocument.isRecycled()) {
            mDocument.recycle();
        }
    }
}
