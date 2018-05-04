package com.tradelink.scandocapp.utils;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipManager {

    public static void unzip(Context context, String zipFile, String location) throws IOException {
        File file = new File(location);
        if (!file.exists()) {
            file.mkdir();
        }
        ZipInputStream zipStream = new ZipInputStream(context.getAssets().open(zipFile));
        ZipEntry zEntry = null;
        while ((zEntry = zipStream.getNextEntry()) != null) {
            FileOutputStream fout = new FileOutputStream(
                    location + "/" + zEntry.getName());
            BufferedOutputStream bufout = new BufferedOutputStream(fout);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = zipStream.read(buffer)) != -1) {
                bufout.write(buffer, 0, read);
            }

            zipStream.closeEntry();
            bufout.close();
            fout.close();
        }
        zipStream.close();
    }
}