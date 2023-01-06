package com.visionxoft.abacus.rehmantravel.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import com.visionxoft.abacus.rehmantravel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for Print Framework introduced in API 21
 */
public class PrintHelper {

    List<PrintJob> printJobs;

    public PrintHelper() {
        printJobs = new ArrayList<>();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void createWebPrintJob(Fragment fragment, WebView webView, String docName) {

        // Get a PrintHelper instance
        PrintManager printManager = (PrintManager) fragment.getActivity().getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = null;

        printAdapter = webView.createPrintDocumentAdapter(docName);

        // Create a print business with name and adapter instance
        String jobName = fragment.getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

        // Save the business object for later status checking
        printJobs.add(printJob);
    }
}
