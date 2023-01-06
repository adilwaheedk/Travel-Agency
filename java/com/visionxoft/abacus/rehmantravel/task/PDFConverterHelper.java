package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.fragment.FranchiseRecordFragment;
import com.visionxoft.abacus.rehmantravel.fragment.HajjPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahDesignFragment;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.GoogleCloudPrintHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * AsyncTask conversion of Html String into PDF File
 */
public class PDFConverterHelper extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private Fragment fragment;
    private Dialog dialog;
    private File outputFile;
    private String title, docName;
    private Object data;

    /**
     * Convert Html String data into PDF File
     *
     * @param fragment   Parent Fragment Class
     * @param outputFile File in which content will be written
     * @param data       Data (html or bytes) that can be converted to PDF
     */
    public PDFConverterHelper(Fragment fragment, File outputFile, Object data, String title, String docName) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.outputFile = outputFile;
        this.data = data;
        this.title = title;
        this.docName = docName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.file_converting,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!PDFConverterHelper.this.isCancelled()) PDFConverterHelper.this.cancel(true);
            }
        });
    }

    @Override
    protected Boolean doInBackground(Void... aVoid) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            Document document = new Document();
            PdfWriter pdfWriter;
            InputStream inputStream;
            if (data instanceof String) {
                // HTML to PDF
                document.setPageSize(PageSize.A4);
                pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
                document.open();
                document = setDocumentProperties(document);
                inputStream = new ByteArrayInputStream(((String) data).getBytes());
                XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, inputStream);
            } else if (data instanceof byte[]) {
                // IMAGE to PDF
                Image img = Image.getInstance((byte[]) data);
                img.setAbsolutePosition(0, 0);
                document.setPageSize(img);
                PdfWriter.getInstance(document, fileOutputStream);
                document.open();
                document = setDocumentProperties(document);
                document.add(img);
            } else return false;

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Document setDocumentProperties(Document document) {
        document.addCreationDate();
        document.addAuthor(PreferenceHelper.getAgentSession(activity).AGENT_NAME);
        document.addSubject(title);
        return document;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        PhoneFunctionality.makeToast(activity, activity.getString(R.string.save_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Boolean feedback) {
        super.onPostExecute(feedback);
        dialog.dismiss();
        try {
            if (fragment instanceof FlightReservationFragment) {
                FlightReservationFragment frag = (FlightReservationFragment) fragment;
                if (feedback) {
                    frag.flag_pnr_downloaded = true;
                    if (frag.selected_btn.equals(frag.btn_pnr_print)) showGCPDialog();
                    else createConfirmDialog(activity.getString(R.string.pnr_saved));
                } else
                    PhoneFunctionality.makeToast(activity, activity.getString(R.string.ticket_save_fail));
            } else if (fragment instanceof UmrahDesignFragment) {
                UmrahDesignFragment frag = (UmrahDesignFragment) fragment;
                if (feedback) {
                    if (frag.selected_btn.equals(frag.btn_umrah_print)) showGCPDialog();
                    else createConfirmDialog(activity.getString(R.string.umrah_form_saved));
                } else
                    PhoneFunctionality.makeToast(activity, activity.getString(R.string.umrah_save_fail));
            } else if (fragment instanceof FranchiseRecordFragment) {
                FranchiseRecordFragment frag = (FranchiseRecordFragment) fragment;
                if (feedback) {
                    if (frag.selected_btn.equals(frag.btn_fran_print)) showGCPDialog();
                    else createConfirmDialog(activity.getString(R.string.franchise_form_saved));
                } else
                    PhoneFunctionality.makeToast(activity, activity.getString(R.string.franchise_save_fail));
            } else if (fragment instanceof HajjPackagesFragment) {
                if (feedback) {
                    showGCPDialog();
                } else
                    PhoneFunctionality.makeToast(activity, activity.getString(R.string.hajj_save_fail));
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }

    private void showGCPDialog() {
        new GoogleCloudPrintHelper(activity, outputFile, docName).showGCPDialog();
    }

    private void createConfirmDialog(String title) {
        DialogHelper.createConfirmDialog(activity, title, activity.getString(R.string.view_file_msg),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileHelper.viewFile(activity, outputFile, FileHelper.APPLICATION_PDF);
                    }
                }, null);
    }
}
