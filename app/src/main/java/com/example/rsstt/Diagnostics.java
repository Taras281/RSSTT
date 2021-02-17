package com.example.rsstt;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Diagnostics {
    public static final String TAG = "Diagnostics";
    public static boolean DEBUG = BuildConfig.DEBUG;


    public static DiagnosticLog i(String msg) {
        return i(TAG, msg);
    }

    public static DiagnosticLog i(String tag, String msg) {
        msg = TextUtils.isEmpty(msg) ? "" : msg;
        Log.i(tag, msg);
        return new DiagnosticLog(msg);
    }

    public static DiagnosticLog e(String tag, String msg) {
        msg = TextUtils.isEmpty(msg) ? "" : msg;
        Log.e(tag, msg);
        return new DiagnosticLog(msg);
    }

    public static DiagnosticLog i(Object caller, String msg) {
        return i(caller, TAG, msg);
    }

    public static DiagnosticLog i(Object caller, String tag, String msg) {
        return i(tag, caller.getClass().getSimpleName() + "." + msg);
    }

    public static DiagnosticLog e(Object caller, String msg) {
        return e(caller, TAG, msg);
    }

    public static DiagnosticLog e(Object caller, String tag, String msg) {
        return e(tag, caller.getClass().getSimpleName() + "." + msg);
    }

    public static DiagnosticLog e(String msg) {
        return e(TAG, msg);
    }

    public static File getLogFile(String filename) {
        File file = new File(MainActivity.application.getCacheDir(), filename + ".log");
        return file;
    }

    public static void createLog(String filename) {
        File file = getLogFile(filename);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();

            appendLog(filename, "Created at " + new Date().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void appendLog(String filename, String line) {
        File file = getLogFile(filename);
        if (!file.exists()) createLog(filename);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class DiagnosticLog {
        private String msg;

        public DiagnosticLog(String msg) {
            this.msg = msg;
        }

        public void append(String fileName) {
            Diagnostics.appendLog(fileName, msg);
        }
    }
}