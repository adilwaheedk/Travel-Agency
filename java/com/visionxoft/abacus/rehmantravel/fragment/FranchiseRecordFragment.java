package com.visionxoft.abacus.rehmantravel.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.FranchiseRecordAdapter;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.FranchisePersonal;
import com.visionxoft.abacus.rehmantravel.model.FranchiseReference;
import com.visionxoft.abacus.rehmantravel.task.SubmitFranchiseForm;
import com.visionxoft.abacus.rehmantravel.task.PDFConverterHelper;
import com.visionxoft.abacus.rehmantravel.task.SendEmail;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.GoogleCloudPrintHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.views.RippleView;
import com.visionxoft.abacus.rehmantravel.views.SimpleDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import paul.arian.fileselector.FileSelectionActivity;

public class FranchiseRecordFragment extends Fragment {

    public View selected_btn;
    public RippleView btn_fran_print;

    private MainActivity mainActivity;
    private ArrayList<File> attach_files;
    private TextView lbl_files_selected;
    private int REQUEST_CODE = 500;
    private View rootView;
    private byte[] form_img_bytes;
    private Picture franPicture;
    private String docTitle, docName;
    private WebView webView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.franchise_title));
        mainActivity.setToolbarSubTitle(getString(R.string.franchise_subtitle_rec));

        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_franchise_record, container, false);

        // Fetch required data
        Object obj1 = IntentHelper.getObjectForKey("franchise_personal");
        Object obj2 = IntentHelper.getObjectForKey("franchise_references");

        if (obj1 == null || obj2 == null) {
            PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.application_restarted));
            mainActivity.finish();
            return rootView;
        }

        final FranchisePersonal franPer = (FranchisePersonal) obj1;
        final FranchiseReference[] franRefArray = (FranchiseReference[]) obj2;

        docTitle = franPer.mobile;

        if (Constants.APP_TEST_MODE) {
            showFranchiseFormDialog(FileHelper.readAssetFileToString(mainActivity, "test_html.html"));
            return rootView;
        }

        // region Find Views
        final RecyclerView list_fran_rec = (RecyclerView) rootView.findViewById(R.id.list_fran_rec);
        final EditText btn_fran_rec_q1 = (EditText) rootView.findViewById(R.id.btn_fran_rec_q1);
        final EditText btn_fran_rec_q2 = (EditText) rootView.findViewById(R.id.btn_fran_rec_q2);
        final EditText btn_fran_rec_q3 = (EditText) rootView.findViewById(R.id.btn_fran_rec_q3);
        final EditText btn_fran_rec_invest = (EditText) rootView.findViewById(R.id.btn_fran_rec_invest);
        final EditText btn_fran_rec_turnover = (EditText) rootView.findViewById(R.id.btn_fran_rec_turnover);
        final EditText btn_fran_rec_gross = (EditText) rootView.findViewById(R.id.btn_fran_rec_gross);
        final EditText btn_fran_rec_noe = (EditText) rootView.findViewById(R.id.btn_fran_rec_noe);
        final RadioButton fran_rec_retail_exp = (RadioButton) rootView.findViewById(R.id.fran_rec_retail_exp);
        final RadioButton fran_rec_franchise_exp = (RadioButton) rootView.findViewById(R.id.fran_rec_franchise_exp);
        final View ll_fran_rec_retail_exp = rootView.findViewById(R.id.ll_fran_rec_retail_exp);
        final View ll_fran_rec_franchise_exp = rootView.findViewById(R.id.ll_fran_rec_franchise_exp);
        final EditText fran_rec_retail_exp_ans = (EditText) rootView.findViewById(R.id.fran_rec_retail_exp_ans);
        final EditText fran_rec_franchise_exp_ans = (EditText) rootView.findViewById(R.id.fran_rec_franchise_exp_ans);
        final View btn_fran_rec_q1_clear = rootView.findViewById(R.id.btn_fran_rec_q1_clear);
        final View btn_fran_rec_q2_clear = rootView.findViewById(R.id.btn_fran_rec_q2_clear);
        final View btn_fran_rec_q3_clear = rootView.findViewById(R.id.btn_fran_rec_q3_clear);
        final View btn_fran_rec_invest_clear = rootView.findViewById(R.id.btn_fran_rec_invest_clear);
        final View btn_fran_rec_turnover_clear = rootView.findViewById(R.id.btn_fran_rec_turnover_clear);
        final View btn_fran_rec_gross_clear = rootView.findViewById(R.id.btn_fran_rec_gross_clear);
        final View btn_fran_rec_noe_clear = rootView.findViewById(R.id.btn_fran_rec_noe_clear);
        final View fran_rec_retail_exp_ans_clear = rootView.findViewById(R.id.fran_rec_retail_exp_ans_clear);
        final View fran_rec_franchise_exp_ans_clear = rootView.findViewById(R.id.fran_rec_franchise_exp_ans_clear);
        final View btn_fran_rec_choose_files = rootView.findViewById(R.id.btn_fran_rec_choose_files);
        final View btn_fran_rec_back = rootView.findViewById(R.id.btn_fran_rec_back);
        final View btn_fran_rec_submit = rootView.findViewById(R.id.btn_fran_rec_submit);
        lbl_files_selected = (TextView) rootView.findViewById(R.id.lbl_fran_rec_files_selected);
        //endregion

        // Init Employee/Business records Adapter
        FranchiseRecordAdapter adapter = new FranchiseRecordAdapter(this);
        list_fran_rec.setLayoutManager(new LinearLayoutManager(mainActivity));
        list_fran_rec.setItemAnimator(new DefaultItemAnimator());
        list_fran_rec.addItemDecoration(new SimpleDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL, 2f));
        list_fran_rec.setAdapter(adapter);

        ll_fran_rec_retail_exp.setVisibility(View.GONE);
        ll_fran_rec_franchise_exp.setVisibility(View.GONE);

        // region Clear Edit Texts
        btn_fran_rec_q1_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_q1.setText("");
            }
        });

        btn_fran_rec_q2_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_q2.setText("");
            }
        });

        btn_fran_rec_q3_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_q3.setText("");
            }
        });

        btn_fran_rec_invest_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_invest.setText("");
            }
        });

        btn_fran_rec_turnover_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_turnover.setText("");
            }
        });

        btn_fran_rec_gross_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_gross.setText("");
            }
        });

        btn_fran_rec_noe_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fran_rec_noe.setText("");
            }
        });

        fran_rec_retail_exp_ans_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_rec_retail_exp_ans.setText("");
            }
        });

        fran_rec_franchise_exp_ans_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_rec_franchise_exp_ans.setText("");
            }
        });
        //endregion

        // region RadioButton listeners
        fran_rec_retail_exp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) ll_fran_rec_retail_exp.setVisibility(View.VISIBLE);
                else ll_fran_rec_retail_exp.setVisibility(View.GONE);
            }
        });

        fran_rec_franchise_exp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) ll_fran_rec_franchise_exp.setVisibility(View.VISIBLE);
                else ll_fran_rec_franchise_exp.setVisibility(View.GONE);
            }
        });
        // endregion

        //  Choose File Button
        btn_fran_rec_choose_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(mainActivity, FileSelectionActivity.class), REQUEST_CODE);
            }
        });

        // Back Button
        btn_fran_rec_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        // region Submit Button
        btn_fran_rec_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> bu_period = new ArrayList<>();
                List<String> bu_position = new ArrayList<>();
                List<String> bu_ename = new ArrayList<>();
                for (int i = 0; i < list_fran_rec.getChildCount(); i++) {
                    View view = list_fran_rec.getChildAt(i);
                    String fran_rec_period = ((EditText) view.findViewById(R.id.fran_rec_period)).getText().toString();
                    String fran_rec_position = ((EditText) view.findViewById(R.id.fran_rec_position)).getText().toString();
                    String fran_rec_business = ((EditText) view.findViewById(R.id.fran_rec_business)).getText().toString();
                    if (fran_rec_period.equals("") || fran_rec_position.equals("") || fran_rec_business.equals("")) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.emp_bus_field_required));
                        PhoneFunctionality.errorAnimation(view);
                        return;
                    }
                    bu_period.add(fran_rec_period);
                    bu_position.add(fran_rec_position);
                    bu_ename.add(fran_rec_business);
                }

                String rt_interest = btn_fran_rec_q1.getText().toString();
                String ideal_rt = btn_fran_rec_q2.getText().toString();
                String involve_family_member = btn_fran_rec_q3.getText().toString();

                if (rt_interest.equals("")) {
                    btn_fran_rec_q1.setError(getString(R.string.error_field_required));
                    btn_fran_rec_q1.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }

                if (ideal_rt.equals("")) {
                    btn_fran_rec_q2.setError(getString(R.string.error_field_required));
                    btn_fran_rec_q2.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }

                if (involve_family_member.equals("")) {
                    btn_fran_rec_q3.setError(getString(R.string.error_field_required));
                    btn_fran_rec_q3.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }

                String prev_retail_exp = "0", prev_retail_answer = "";
                if (fran_rec_retail_exp.isChecked()) {
                    prev_retail_exp = "1";
                    prev_retail_answer = fran_rec_retail_exp_ans.getText().toString();
                    if (prev_retail_answer.equals("")) {
                        fran_rec_retail_exp_ans.setError(getString(R.string.error_field_required));
                        fran_rec_retail_exp_ans.requestFocus();
                        PhoneFunctionality.errorAnimation(v);
                        return;
                    }
                }

                String prev_franchise_business_exp = "0", franchise_exp_answer = "";
                if (fran_rec_franchise_exp.isChecked()) {
                    prev_franchise_business_exp = "1";
                    franchise_exp_answer = fran_rec_franchise_exp_ans.getText().toString();
                    if (franchise_exp_answer.equals("")) {
                        fran_rec_franchise_exp_ans.setError(getString(R.string.error_field_required));
                        fran_rec_franchise_exp_ans.requestFocus();
                        PhoneFunctionality.errorAnimation(v);
                        return;
                    }
                }

                String invest_money = btn_fran_rec_invest.getText().toString();
                if (invest_money.equals("")) {
                    btn_fran_rec_invest.setError(getString(R.string.error_field_required));
                    btn_fran_rec_invest.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }

                String no_employee = btn_fran_rec_noe.getText().toString();
                if (no_employee.equals("")) {
                    btn_fran_rec_noe.setError(getString(R.string.error_field_required));
                    btn_fran_rec_noe.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                } else if (no_employee.equals("0")) {
                    btn_fran_rec_noe.setError(getString(R.string.error_field_invalid));
                    btn_fran_rec_noe.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }

                // region Prepare parameters
                final Hashtable<String, Object> params = new Hashtable<>();
                params.put("pe_name", franPer.name);
                params.put("pe_address", franPer.address);
                params.put("pe_telno", franPer.phone);
                params.put("pe_mno", franPer.mobile);
                params.put("pe_email", franPer.email);
                params.put("pe_nic", franPer.nic);
                params.put("pe_age", franPer.age);
                params.put("pe_status", franPer.marital_status);
                params.put("pe_kids", franPer.no_of_kids);
                params.put("pe_academic", franPer.qualification);

                ArrayList<String> pa_name = new ArrayList<>();
                ArrayList<String> pa_relation = new ArrayList<>();
                ArrayList<String> pa_telno = new ArrayList<>();
                ArrayList<String> pa_cellno = new ArrayList<>();
                ArrayList<String> pa_email = new ArrayList<>();
                ArrayList<String> pa_nic = new ArrayList<>();
                ArrayList<String> pa_age = new ArrayList<>();
                ArrayList<String> pa_business = new ArrayList<>();
                ArrayList<String> pa_academic = new ArrayList<>();
                for (FranchiseReference franRef : franRefArray) {
                    pa_name.add(franRef.name);
                    pa_relation.add(franRef.relation);
                    pa_telno.add(franRef.phone);
                    pa_cellno.add(franRef.mobile);
                    pa_email.add(franRef.email);
                    pa_nic.add(franRef.nic);
                    pa_age.add(franRef.age);
                    pa_business.add(franRef.business);
                    pa_academic.add(franRef.qualification);
                }
                params.put("pa_name[]", pa_name);
                params.put("pa_relation[]", pa_relation);
                params.put("pa_telno[]", pa_telno);
                params.put("pa_cellno[]", pa_cellno);
                params.put("pa_email[]", pa_email);
                params.put("pa_nic[]", pa_nic);
                params.put("pa_age[]", pa_age);
                params.put("pa_business[]", pa_business);
                params.put("pa_academic[]", pa_academic);

                params.put("bu_period[]", bu_period);
                params.put("bu_ename[]", bu_ename);
                params.put("bu_position[]", bu_position);
                params.put("rt_interest", rt_interest);
                params.put("ideal_rt", ideal_rt);
                params.put("involve_family_member", involve_family_member);
                params.put("prev_retail_exp", prev_retail_exp);
                params.put("prev_retail_answer", prev_retail_answer);
                params.put("prev_franchise_business_exp", prev_franchise_business_exp);
                params.put("franchise_exp_answer", franchise_exp_answer);
                params.put("invest_money", invest_money);
                params.put("annual_sales_turnover", btn_fran_rec_turnover.getText().toString());
                params.put("declare_gross_income", btn_fran_rec_gross.getText().toString());
                params.put("no_employee", no_employee);
                if (attach_files != null) params.put("franchiseUpload[]", attach_files);
                // endregion

                // Apply Franchise Registration
                new SubmitFranchiseForm(FranchiseRecordFragment.this, params).execute();
            }
        });
        //endregion

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                attach_files = (ArrayList<File>) data.getSerializableExtra(FileSelectionActivity.FILES_TO_UPLOAD);
                if (attach_files.size() == 0)
                    lbl_files_selected.setText(getString(R.string.no_file_selected));
                else if (attach_files.size() == 1)
                    lbl_files_selected.setText(attach_files.get(0).getName() + " selected");
                else if (attach_files.size() > 0)
                    lbl_files_selected.setText(attach_files.size() + " files selected");
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showFranchiseFormDialog(final String form_html) {

        docName = getString(R.string.franchise_file_prefix) + docTitle;
        final File outputFile = new File(FileHelper.getDirectory(mainActivity), docName + FileHelper.fileExtPDF);

        final Dialog dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_franchise_form, Gravity.CENTER);
        dialog.show();

        // Find Views
        final View btn_back_fran_form = dialog.findViewById(R.id.btn_back_fran_form);
        final RippleView btn_fran_save = (RippleView) dialog.findViewById(R.id.btn_fran_save);
        final RippleView btn_fran_email = (RippleView) dialog.findViewById(R.id.btn_fran_email);
        btn_fran_print = (RippleView) dialog.findViewById(R.id.btn_fran_print);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            WebView.enableSlowWholeDocumentDraw();

        webView = (WebView) dialog.findViewById(R.id.webView_fran_form);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.loadData(form_html, "text/html", null);

        // region Listener to capture image from webView commented
        //webView.setPictureListener(new WebView.PictureListener() {
        //    @Override
        //    public void onNewPicture(WebView view, final Picture picture) {
        //        saveFranchiseForm(false);
        //    }
        //});
        // endregion

        // region Back Button
        btn_back_fran_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // endregion

        // region Print Button
        btn_fran_print.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                selected_btn = v;
                // Google Cloud Print
                if (outputFile.exists()) {
                    new GoogleCloudPrintHelper(mainActivity, outputFile, docName).showGCPDialog();
                } else {
                    new PDFConverterHelper(FranchiseRecordFragment.this, outputFile, form_html,
                            getString(R.string.franchise_form), docName).execute();
                }
            }
        });
        // endregion

        // region Email Button
        btn_fran_email.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                final Dialog dialog = DialogHelper.createCustomDialog(mainActivity,
                        R.layout.dialog_franchise_form_email, Gravity.CENTER);
                dialog.show();

                // Find Views
                final EditText fran_email_addr = (EditText) dialog.findViewById(R.id.fran_email_addr);
                ImageButton fran_email_addr_clear = (ImageButton) dialog.findViewById(R.id.fran_email_addr_clear);
                LinearLayout btn_fran_send_mail = (LinearLayout) dialog.findViewById(R.id.btn_fran_send_mail);
                LinearLayout btn_fran_cancel_mail = (LinearLayout) dialog.findViewById(R.id.btn_fran_cancel_mail);

                // Clear EditText
                fran_email_addr_clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fran_email_addr.setText("");
                        fran_email_addr.requestFocus();
                    }
                });

                // Back Button
                btn_fran_cancel_mail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Send Email Button
                btn_fran_send_mail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email_addresses = fran_email_addr.getText().toString();
                        if (!email_addresses.equals("")) {
                            dialog.dismiss();
                            sendEmail(form_html, email_addresses);
                        } else
                            fran_email_addr.setError(getString(R.string.error_field_required));
                    }
                });
            }
        });
        // endregion

        // region Save Button
        btn_fran_save.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                selected_btn = v;
                saveFranchiseForm(true);
            }
        });
        // endregion
    }

    private void saveFranchiseForm(final boolean viewFile) {
        new AsyncTask<Void, Void, Boolean>() {
            Dialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = DialogHelper.createProgressDialog(mainActivity, R.string.image_saving,
                        R.string.please_wait, false);
                franPicture = webView.capturePicture();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (form_img_bytes != null) return true;
                try {
                    Bitmap bitmap = PhoneFunctionality.pictureDrawableToBitmap(new PictureDrawable(franPicture));
                    form_img_bytes = PhoneFunctionality.bitmapToBytes(bitmap);
                    if (form_img_bytes != null && FileHelper.createMainDirectory(mainActivity)) {
                        FileHelper.writeToFile(FileHelper.getDirectory(mainActivity),
                                docName + FileHelper.fileExtPNG, form_img_bytes);
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
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.franchise_form_not_saved));
                } else {
                    if (viewFile) {
                        DialogInterface.OnClickListener positive_listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File imgFile = new File(FileHelper.getDirectory(mainActivity), docName + FileHelper.fileExtPNG);
                                FileHelper.viewFile(mainActivity, imgFile, FileHelper.IMAGE_PNG);
                            }
                        };
                        DialogHelper.createConfirmDialog(getContext(), getString(R.string.view_file_msg),
                                getString(R.string.view_file_title), positive_listener, null);
                    } else
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.franchise_form_saved));
                }
            }
        }.execute();
    }

    private void sendEmail(String msg, String email_addresses) {
        String sender, bcc = "";
        AgentSession agentSession = PreferenceHelper.getAgentSession(mainActivity);
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            sender = getString(R.string.rt_email_address);
        } else {
            sender = agentSession.AGENT_EMAIL;
            bcc = getString(R.string.rt_email_address);
        }

        new SendEmail(this, sender, email_addresses, "", bcc,
                getString(R.string.mail_franchise_subject), msg, null).execute();
    }
}
