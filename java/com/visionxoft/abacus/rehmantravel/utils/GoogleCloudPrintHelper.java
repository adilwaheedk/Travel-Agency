package com.visionxoft.abacus.rehmantravel.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.visionxoft.abacus.rehmantravel.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Javascript Interface for Google Cloud Print
 */
public class GoogleCloudPrintHelper {

    public static final String PRINT_DIALOG_URL = "https://www.google.com/cloudprint/dialog.html";
    public static final String ZXING_URL = "http://zxing.appspot.com";
    public static final String JS_INTERFACE = "AndroidPrintDialog";

    private static final String docMimeType = "application/pdf";
    private static final String CONTENT_TRANSFER_ENCODING = "base64";
    private static final String CLOSE_POST_MESSAGE_NAME = "cp-dialog-on-close";
    private Activity activity;
    private File outputFile;
    private String docName;
    private Dialog dialog;

    public GoogleCloudPrintHelper(Activity activity, File outputFile, String docName) {
        this.activity = activity;
        this.outputFile = outputFile;
        this.docName = docName;
    }

    /**
     * Initialization of webView and dialog for Google Cloud Print
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void showGCPDialog() {

        dialog = DialogHelper.createCustomDialog(activity, R.layout.dialog_google_cloud_print, Gravity.CENTER);

        // Find Views
        final WebView webView = (WebView) dialog.findViewById(R.id.webview_print_cloud);
        final View loading_progress_bar = dialog.findViewById(R.id.loading_progress_bar);
        final LinearLayout btn_back_print_cloud = (LinearLayout) dialog.findViewById(R.id.btn_back_print_cloud);

        // Init Views
        webView.setVisibility(View.GONE);
        loading_progress_bar.setVisibility(View.VISIBLE);

        // WebView settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new PrintDialogWebClient(webView, loading_progress_bar));
        webView.addJavascriptInterface(this, JS_INTERFACE);
        webView.loadUrl(PRINT_DIALOG_URL);

        dialog.show();

        // Back Button
        btn_back_print_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * WebView client to control webView behaviour
     */
    private final class PrintDialogWebClient extends WebViewClient {

        private WebView webview_print_cloud;
        private View loading_progress_bar;
        private static final int ZXING_SCAN_REQUEST = 65743;
        private static final String WEB_CLIENT_INTENT_ACTION = "com.google.zxing.client.android.SCAN";
        private static final String WEB_CLIENT_INTENT_EXTRA_NAME = "SCAN_MODE";
        private static final String WEB_CLIENT_INTENT_EXTRA_VALUE = "QR_CODE_MODE";

        public PrintDialogWebClient(WebView webview_print_cloud, View loading_progress_bar) {
            this.webview_print_cloud = webview_print_cloud;
            this.loading_progress_bar = loading_progress_bar;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(ZXING_URL)) {
                Intent intentScan = new Intent(WEB_CLIENT_INTENT_ACTION);
                intentScan.putExtra(WEB_CLIENT_INTENT_EXTRA_NAME, WEB_CLIENT_INTENT_EXTRA_VALUE);
                try {
                    activity.startActivityForResult(intentScan, ZXING_SCAN_REQUEST);
                } catch (ActivityNotFoundException error) {
                    view.loadUrl(url);
                }
            } else {
                view.loadUrl(url);
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            webview_print_cloud.setVisibility(View.VISIBLE);
            loading_progress_bar.setVisibility(View.GONE);

            if (PRINT_DIALOG_URL.equals(url)) {
                // Submit print document
                view.loadUrl("javascript:printDialog.setPrintDocument(printDialog.createPrintDocument("
                        + "window." + JS_INTERFACE + ".getType(),window." + JS_INTERFACE + ".getTitle(),"
                        + "window." + JS_INTERFACE + ".getContent(),window." + JS_INTERFACE + ".getEncoding()))");

                // Add post messages listener
                view.loadUrl("javascript:window.addEventListener('message',"
                        + "function(evt){window." + JS_INTERFACE + ".onPostMessage(evt.data)}, false)");
            }
        }
    }

    @JavascriptInterface
    public String getType() {
        return docMimeType;
    }

    @JavascriptInterface
    public String getTitle() {
        return docName;
    }

    @JavascriptInterface
    public String getContent() {
        try {
            ContentResolver contentResolver = activity.getContentResolver();
            InputStream is = contentResolver.openInputStream(Uri.fromFile(outputFile));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int n = is.read(buffer);
            while (n >= 0) {
                outputStream.write(buffer, 0, n);
                n = is.read(buffer);
            }
            is.close();
            outputStream.flush();

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @JavascriptInterface
    public String getEncoding() {
        return CONTENT_TRANSFER_ENCODING;
    }

    @JavascriptInterface
    public void onPostMessage(String message) {
        if (message.startsWith(CLOSE_POST_MESSAGE_NAME)) {
            dialog.dismiss();
            PhoneFunctionality.showNotification(activity.getClass(), activity, IdGenerator.generateViewId(),
                    activity.getString(R.string.gcp_notif_title), activity.getString(R.string.gcp_notif_msg), true);
        }
    }
}
