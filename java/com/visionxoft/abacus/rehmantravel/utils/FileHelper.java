package com.visionxoft.abacus.rehmantravel.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.visionxoft.abacus.rehmantravel.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for Filing related functions used in Project.
 */
public class FileHelper {

    private static File directory;
    public static final String pathSeparator = "/";
    public static final String fileExtPDF = ".pdf";
    public static final String fileExtPNG = ".png";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String IMAGE_PNG = "image/png";

    /**
     * Create Project main directory
     *
     * @param context Context
     * @return true if success, else false
     */
    public static boolean createMainDirectory(Context context) {
        if (isStorageAvailable()) {
            directory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),
                    context.getString(R.string.directory_name));
        } else {
            directory = new File(context.getFilesDir().getPath() + pathSeparator +
                    context.getString(R.string.directory_name));
        }

        directory.mkdir();
        return directory.exists() && directory.isDirectory();
    }

    /**
     * Log file to log desired information
     *
     * @param context   Context
     * @param directory Parent directory of file
     * @param filename  Name of file
     * @param data      Data to log
     * @throws Exception
     */
    public static void logData(Context context, File directory, String filename, String data) throws Exception {
        File log_dir = new File(directory + pathSeparator + context.getString(R.string.directory_log));
        if (!log_dir.exists()) log_dir.mkdirs();
        writeToFile(log_dir, filename, data);
    }

    /**
     * Write data in file
     *
     * @param directory Parent directory of file
     * @param filename  Name of file
     * @param data      Data to write
     * @throws Exception
     */
    public static void writeToFile(File directory, String filename, Object data) throws Exception {
        File file_to_write = new File(directory, filename);
        if (data instanceof String) {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file_to_write, true));
            bufferedWriter.write((String) data);
            bufferedWriter.flush();
            bufferedWriter.close();
        } else if (data instanceof byte[]) {
            FileOutputStream fos = new FileOutputStream(file_to_write);
            fos.write((byte[]) data);
            fos.close();
        }
    }

    /**
     * Check if secondary storage is available
     *
     * @return true if success, else false
     */
    private static boolean isStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Get Instance of Main directory of Application
     *
     * @return Main directory file
     */
    public static File getDirectory(Context context) {
        if (directory == null) createMainDirectory(context);
        return directory;
    }

    /**
     * Read file from asset directory
     *
     * @param context  Context
     * @param filename Name of file to access
     * @return Contents of file in String
     */
    public static String readAssetFileToString(Context context, String filename) {
        BufferedReader reader = null;
        String data = "";
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            String mLine;
            while ((mLine = reader.readLine()) != null) data += mLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reader != null) {
            try {
                reader.close();
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Read file from asset directory
     *
     * @param context  Context
     * @param filename Name of file to access
     * @return Contents of file in List of String
     */
    public static List<String> readAssetFileToList(Context context, String filename) {
        BufferedReader reader = null;
        List<String> data = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            String mLine;
            while ((mLine = reader.readLine()) != null) data.add(mLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reader != null) {
            try {
                reader.close();
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * View pdf file in external application
     *
     * @param context Context
     * @param file    File in pdf format
     */
    public static void viewFile(Context context, File file, String fileType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), fileType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
