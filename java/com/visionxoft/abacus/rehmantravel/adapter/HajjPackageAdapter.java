package com.visionxoft.abacus.rehmantravel.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.HajjPackagesFragment;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.PDFConverterHelper;
import com.visionxoft.abacus.rehmantravel.task.SendEmail;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.GoogleCloudPrintHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.views.RippleView;

import java.io.File;
import java.util.List;

public class HajjPackageAdapter extends PagerAdapter {

    public View selected_btn;

    private HajjPackagesFragment fragment;
    private MainActivity mainActivity;
    private List<ImageTag> imageTags;
    private LayoutInflater inflater;
    private String path, docName;
    private File outputFile;

    public HajjPackageAdapter(HajjPackagesFragment fragment, String url, List<ImageTag> imageTags) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.path = url;
        this.imageTags = imageTags;
        this.inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageTags.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        docName = mainActivity.getString(R.string.hajj_file_prefix) + position;

        View view = inflater.inflate(R.layout.page_hajj_package, null);
        ViewHolder holder = new ViewHolder(view);
        ImageTag imageTag = imageTags.get(position);

        holder.webView.setVisibility(View.GONE);
        holder.btn_hajj_email.setVisibility(View.GONE);
        holder.btn_hajj_print.setVisibility(View.GONE);
        holder.btn_hajj_save.setVisibility(View.GONE);
        holder.tv_img_alt.setVisibility(View.GONE);
        holder.tv_img_alt.setText(imageTag.alt);

        holder.webView.setWebViewClient(new ImageWebViewClient(holder, imageTag));
        holder.webView.loadUrl(path + imageTag.src);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public class ImageWebViewClient extends WebViewClient {

        private ViewHolder holder;
        private byte[] ticket_img_bytes;
        private Picture hajjPicture;
        private ImageTag imageTag;

        public ImageWebViewClient(ViewHolder holder, ImageTag imageTag) {
            this.holder = holder;
            this.imageTag = imageTag;
        }

        @Override
        public void onPageFinished(final WebView webView, String url) {

            FileHelper.createMainDirectory(mainActivity);
            outputFile = new File(FileHelper.getDirectory(mainActivity), docName + FileHelper.fileExtPDF);

            holder.webView.setBackgroundColor(Color.TRANSPARENT);
            holder.progress_package.setVisibility(View.GONE);
            holder.tv_loading_img.setVisibility(View.GONE);
            holder.tv_img_alt.setVisibility(View.VISIBLE);
            holder.webView.setVisibility(View.VISIBLE);
            holder.btn_hajj_email.setVisibility(View.VISIBLE);
            holder.btn_hajj_print.setVisibility(View.VISIBLE);
            holder.btn_hajj_save.setVisibility(View.VISIBLE);

            // region Email Button
            holder.btn_hajj_email.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    final Dialog dialog = DialogHelper.createCustomDialog(mainActivity,
                            R.layout.dialog_hajj_package_email, Gravity.CENTER);
                    dialog.show();

                    final EditText send_email = (EditText) dialog.findViewById(R.id.send_email);
                    ImageButton send_email_clear = (ImageButton) dialog.findViewById(R.id.send_email_clear);
                    LinearLayout btn_send_email = (LinearLayout) dialog.findViewById(R.id.btn_send_email);
                    LinearLayout btn_send_email_back = (LinearLayout) dialog.findViewById(R.id.btn_send_email_back);

                    // Clear Email EditText Button
                    send_email_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            send_email.setText("");
                            send_email.requestFocus();
                        }
                    });

                    // Back Button
                    btn_send_email_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    // Send Email Button
                    btn_send_email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String email_addresses = send_email.getText().toString();
                            if (!email_addresses.equals("")) {
                                dialog.dismiss();
                                sendEmail(email_addresses);
                            } else
                                send_email.setError(mainActivity.getString(R.string.error_field_required));
                        }
                    });
                }
            });
            // endregion

            // region Print Button
            holder.btn_hajj_print.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (FileHelper.createMainDirectory(mainActivity)) {
                        selected_btn = v;
                        saveHajjImage(webView, true);
                    } else
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.directory_failed));

                }
            });
            // endregion

            // region Save Button
            holder.btn_hajj_save.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (FileHelper.createMainDirectory(mainActivity)) {
                        saveHajjImage(webView, false);
                    } else
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.directory_failed));
                }
            });
            // endregion
        }

        private void saveHajjImage(final WebView webView, final boolean isPrint) {
            new AsyncTask<Void, Void, Boolean>() {
                Dialog dialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = DialogHelper.createProgressDialog(mainActivity, R.string.image_saving,
                            R.string.please_wait, false);
                    hajjPicture = webView.capturePicture();
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        if (ticket_img_bytes != null) return true;
                        Bitmap bitmap = PhoneFunctionality.pictureDrawableToBitmap(new PictureDrawable(hajjPicture));
                        ticket_img_bytes = PhoneFunctionality.bitmapToBytes(bitmap);
                        if (ticket_img_bytes != null && FileHelper.createMainDirectory(mainActivity)) {
                            FileHelper.writeToFile(FileHelper.getDirectory(mainActivity),
                                    docName + FileHelper.fileExtPNG, ticket_img_bytes);
                            return true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean feedback) {
                    super.onPostExecute(feedback);
                    dialog.dismiss();
                    if (!feedback) {
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.hajj_image_not_saved));
                        return;
                    }
                    if (isPrint) {
                        if (outputFile.exists()) {
                            new GoogleCloudPrintHelper(mainActivity, outputFile, docName).showGCPDialog();
                        } else {
                            new PDFConverterHelper(fragment, outputFile, ticket_img_bytes,
                                    mainActivity.getString(R.string.hajj_form), docName).execute();
                        }
                    } else {
                        DialogHelper.createConfirmDialog(mainActivity, mainActivity.getString(R.string.view_file_title),
                                mainActivity.getString(R.string.view_file_msg), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FileHelper.viewFile(mainActivity, outputFile, FileHelper.IMAGE_PNG);
                                    }
                                }, null);
                    }

                }
            }.execute();
        }

        private void sendEmail(String email_addresses) {
            AgentSession agentSession = PreferenceHelper.getAgentSession(mainActivity);
            String sender, bcc;
            if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
                sender = mainActivity.getString(R.string.rt_email_address);
                bcc = "";
            } else {
                sender = agentSession.AGENT_EMAIL;
                bcc = mainActivity.getString(R.string.rt_email_address);
            }

            new SendEmail(fragment, sender, email_addresses, "", bcc,
                    mainActivity.getString(R.string.mail_pnr_subject), null,
                    "\n" + path + imageTag.src).execute();
        }
    }

    private class ViewHolder {

        protected TextView tv_img_alt, tv_loading_img;
        protected WebView webView;
        protected View progress_package;
        protected RippleView btn_hajj_email, btn_hajj_print, btn_hajj_save;

        ViewHolder(View v) {
            tv_img_alt = (TextView) v.findViewById(R.id.tv_img_alt);
            tv_loading_img = (TextView) v.findViewById(R.id.tv_loading_img);
            webView = (WebView) v.findViewById(R.id.web_view_package);
            progress_package = v.findViewById(R.id.progress_package);
            btn_hajj_email = (RippleView) v.findViewById(R.id.btn_hajj_email);
            btn_hajj_print = (RippleView) v.findViewById(R.id.btn_hajj_print);
            btn_hajj_save = (RippleView) v.findViewById(R.id.btn_hajj_save);
            webView = PhoneFunctionality.setWebViewSettings(webView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                WebView.enableSlowWholeDocumentDraw();
        }
    }
}
