package com.visionxoft.abacus.rehmantravel.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.ContactListAdapter;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.Contact;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.ReceivableClient;
import com.visionxoft.abacus.rehmantravel.task.CancelBooking;
import com.visionxoft.abacus.rehmantravel.task.GetPartyInfo;
import com.visionxoft.abacus.rehmantravel.task.IssueTicket;
import com.visionxoft.abacus.rehmantravel.task.PDFConverterHelper;
import com.visionxoft.abacus.rehmantravel.task.QueryContact;
import com.visionxoft.abacus.rehmantravel.task.SendEmail;
import com.visionxoft.abacus.rehmantravel.task.SendMessages;
import com.visionxoft.abacus.rehmantravel.task.VoidTickets;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.GoogleCloudPrintHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PermissionHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.views.RippleView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Display booked ticket receipt in webView.
 * Provides buttons to show print, email dialog or download file to storage
 */
public class FlightReservationFragment extends Fragment {

    public boolean flag_pnr_downloaded, flag_pnr_emailed, flag_ticket_issued, flag_booking_cancelled, flag_ticket_voided;
    public View selected_btn, send_sms_mactv_ll, send_sms_progress, send_sms_progress_title, send_sms_contact_rl;
    public RippleView btn_pnr_print, btn_pnr_save, btn_pnr_ticket, btn_pnr_book_cancel, btn_pnr_ticket_void;
    public WebView webView;
    public List<ReceivableClient> receivableClientList;
    public ContactListAdapter contactListAdapter;
    public ListView send_sms_contact_list;
    public AgentSession agentSession;
    public String PNR, webview_ticket_html;

    private boolean verified;
    private String docName;
    private byte[] ticket_img_bytes;
    private PricedItinerary pricedItinerary;
    private Picture ticketPicture;
    private File outputFile;
    private MainActivity mainActivity;
    private GetPartyInfo getPartyInfo;
    private ReceivableClient selected_receivableClient;
    private String markup_percentage = "0";

    @SuppressLint("SetJavaScriptEnabled")
    @Deprecated
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_flight_book_ticket, container, false);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.eticket_title));
        mainActivity.setToolbarSubTitle("");

        agentSession = PreferenceHelper.getAgentSession(mainActivity);

        // Get Objects
        Object obj0 = IntentHelper.getObjectForKey("view_pnr", true);
        Object obj1 = IntentHelper.getObjectForKey("webView_ticket_html");
        Object obj2 = IntentHelper.getObjectForKey("ticket_pnr");
        Object obj3 = IntentHelper.getObjectForKey("PricedItinerary");

        boolean view_pnr;
        if (obj0 == null && obj3 != null) {
            view_pnr = false;
            webview_ticket_html = (String) obj1;
            PNR = (String) obj2;
            pricedItinerary = (PricedItinerary) obj3;
        } else {
            if (obj1 != null && obj2 != null) {
                view_pnr = true;
                webview_ticket_html = (String) obj1;
                PNR = (String) obj2;
            } else {
                PhoneFunctionality.makeToast(mainActivity, getString(R.string.application_restarted));
                startActivity(new Intent(rootView.getContext(), MainActivity.class));
                return rootView;
            }
        }

        // region Find Views
        btn_pnr_book_cancel = (RippleView) rootView.findViewById(R.id.btn_book_cancel_ll);
        btn_pnr_ticket_void = (RippleView) rootView.findViewById(R.id.btn_ticket_void_ll);
        btn_pnr_print = (RippleView) rootView.findViewById(R.id.btn_print_ll);
        btn_pnr_save = (RippleView) rootView.findViewById(R.id.btn_save_ll);
        btn_pnr_ticket = (RippleView) rootView.findViewById(R.id.btn_ticket_ll);
        final RippleView btn_pnr_mail = (RippleView) rootView.findViewById(R.id.btn_mail_ll);
        final RippleView btn_pnr_sms = (RippleView) rootView.findViewById(R.id.btn_sms_ll);
        // endregion

        // region Setup WebView
        webView = (WebView) rootView.findViewById(R.id.webView_ticket);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            WebView.enableSlowWholeDocumentDraw();

        // Load WebView
        refreshWebView(webview_ticket_html);

        // WebView Picture Listener to get image from webView
        webView.setPictureListener(new WebView.PictureListener() {
            @Override
            public void onNewPicture(WebView view, final Picture picture) {
                if (ticket_img_bytes != null) return;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (picture == null) ticketPicture = webView.capturePicture();
                        if (picture == null && ticketPicture == null) return;
                        try {
                            PictureDrawable drawable = picture != null ?
                                    new PictureDrawable(picture) : new PictureDrawable(ticketPicture);
                            Bitmap bitmap = PhoneFunctionality.pictureDrawableToBitmap(drawable);
                            ticket_img_bytes = PhoneFunctionality.bitmapToBytes(bitmap);
                            if (ticket_img_bytes != null && FileHelper.createMainDirectory(mainActivity)) {
                                FileHelper.writeToFile(FileHelper.getDirectory(mainActivity),
                                        docName + FileHelper.fileExtPNG, ticket_img_bytes);
                                PhoneFunctionality.makeToast(mainActivity, getString(R.string.pnr_image_saved));
                                flag_pnr_downloaded = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }
        });
        // endregion

        mainActivity.setToolbarSubTitle(getString(R.string.booking_ref) + PNR);
        docName = getString(R.string.pnr_file_prefix) + PNR;
        outputFile = new File(FileHelper.getDirectory(mainActivity), docName + FileHelper.fileExtPDF);

        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            btn_pnr_ticket.setVisibility(View.GONE);
            btn_pnr_ticket_void.setVisibility(View.GONE);
            btn_pnr_book_cancel.setVisibility(View.GONE);
        } else {
            // region Issue Ticket Button
            btn_pnr_ticket.setVisibility(View.VISIBLE);
            btn_pnr_ticket_void.setVisibility(View.VISIBLE);
            btn_pnr_ticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog_issue_tkt = DialogHelper.createCustomDialog(mainActivity,
                            R.layout.dialog_flight_book_ticket_issue, Gravity.CENTER);

                    // region Find Views
                    final View issue_pass_ll = dialog_issue_tkt.findViewById(R.id.issue_pass_ll);
                    final View issue_ticket_ll = dialog_issue_tkt.findViewById(R.id.issue_ticket_ll);
                    final TextView issue_credit_current = (TextView) dialog_issue_tkt.findViewById(R.id.issue_credit_current);
                    final TextView issue_credit_used = (TextView) dialog_issue_tkt.findViewById(R.id.issue_credit_used);
                    final TextView issue_credit_total = (TextView) dialog_issue_tkt.findViewById(R.id.issue_credit_total);
                    final EditText issue_pass = (EditText) dialog_issue_tkt.findViewById(R.id.issue_pass);
                    final View issue_pass_clear = dialog_issue_tkt.findViewById(R.id.issue_pass_clear);
                    final MultiAutoCompleteTextView issue_rec_acc = (MultiAutoCompleteTextView) dialog_issue_tkt.findViewById(R.id.issue_rec_acc);
                    final View issue_rec_acc_clear = dialog_issue_tkt.findViewById(R.id.issue_rec_acc_clear);
                    final TextView issue_fop = (TextView) dialog_issue_tkt.findViewById(R.id.issue_fop);
                    final TextView issue_pay_amount_value = (TextView) dialog_issue_tkt.findViewById(R.id.issue_pay_amount_value);
                    final RadioButton issue_rb_percent = (RadioButton) dialog_issue_tkt.findViewById(R.id.issue_rb_percent);
                    final RadioButton issue_rb_amount = (RadioButton) dialog_issue_tkt.findViewById(R.id.issue_rb_amount);
                    final EditText issue_percent = (EditText) dialog_issue_tkt.findViewById(R.id.issue_percent);
                    final EditText issue_lumpsum = (EditText) dialog_issue_tkt.findViewById(R.id.issue_lumpsum);
                    final TextView issue_rec_amount_value = (TextView) dialog_issue_tkt.findViewById(R.id.issue_rec_amount_value);
                    final View btn_issue_verify = dialog_issue_tkt.findViewById(R.id.btn_issue_verify);
                    final View btn_issue_back = dialog_issue_tkt.findViewById(R.id.btn_issue_back);
                    final TextView btn_issue_verify_title = (TextView) dialog_issue_tkt.findViewById(R.id.btn_issue_verify_title);

                    // Init visibilities of views
                    issue_lumpsum.setVisibility(View.GONE);
                    if (!verified) {
                        issue_pass_ll.setVisibility(View.VISIBLE);
                        issue_ticket_ll.setVisibility(View.GONE);
                    } else {
                        issue_pass_ll.setVisibility(View.GONE);
                        issue_ticket_ll.setVisibility(View.VISIBLE);
                        btn_issue_verify_title.setText(R.string.issue_ticket);
                    }
                    // endregion

                    final String payable_amount, ValidatingCarrier;
                    if (Constants.APP_TEST_MODE) {
                        payable_amount = "5709";
                        ValidatingCarrier = "PK";
                    } else {
                        payable_amount = pricedItinerary._AirItineraryPricingInfo.get(0)._ItinTotalFare._TotalFare.Amount;
                        ValidatingCarrier = pricedItinerary._TPA_Extensions._ValidatingCarrier.Code;
                    }
                    final float pay_amount = Float.parseFloat(payable_amount);
                    issue_pay_amount_value.setText(payable_amount);
                    issue_rec_amount_value.setText(payable_amount);

                    issue_credit_current.setText(agentSession.CurrentCredit);
                    issue_credit_used.setText(agentSession.UsedCredit);
                    issue_credit_total.setText(agentSession.TotalCredit);

                    // region Ticket Markup Calculation
                    issue_percent.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (issue_rb_percent.isChecked()) {
                                String percentStr = issue_percent.getText().toString();
                                float percentFloat = percentStr.equals("") ? 0 : Float.parseFloat(percentStr);
                                double lumpsum = pay_amount * percentFloat / 100.0;
                                double rec_amount = lumpsum + pay_amount;
                                issue_rec_amount_value.setText(String.valueOf(Math.round(rec_amount)));
                                markup_percentage = String.valueOf(Math.round(percentFloat));
                            }
                        }
                    });

                    issue_lumpsum.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (issue_rb_amount.isChecked()) {
                                String lumpsum_str = issue_lumpsum.getText().toString();
                                long lumpsum = lumpsum_str.equals("") ? 0 : Long.parseLong(lumpsum_str);
                                double percentDouble = (lumpsum * 100.0) / pay_amount;
                                issue_rec_amount_value.setText(String.valueOf(pay_amount + Math.round(lumpsum)));
                                markup_percentage = String.valueOf(Math.round(percentDouble));
                            }
                        }
                    });

                    issue_rb_percent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                issue_percent.setVisibility(View.VISIBLE);
                                issue_lumpsum.setVisibility(View.GONE);
                                issue_percent.requestFocus();
                            } else {
                                issue_percent.setVisibility(View.GONE);
                                issue_lumpsum.setVisibility(View.VISIBLE);
                                issue_lumpsum.requestFocus();
                            }
                        }
                    });
                    // endregion

                    getPartyInfo = new GetPartyInfo(FlightReservationFragment.this, issue_rec_acc,
                            agentSession.AGENT_ID, agentSession.AGENT_USER_ID);
                    getPartyInfo.execute();

                    dialog_issue_tkt.show();

                    // region Receivable Client Auto Complete
                    issue_rec_acc.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (getPartyInfo != null) getPartyInfo.cancel(true);
                            if (issue_rec_acc.hasFocus() && issue_rec_acc.getText().toString().length() > 0) {
                                getPartyInfo = new GetPartyInfo(FlightReservationFragment.this, issue_rec_acc,
                                        agentSession.AGENT_ID, agentSession.AGENT_USER_ID);
                                getPartyInfo.execute();
                            }
                        }
                    });

                    issue_rec_acc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            issue_rec_acc.setText("");
                            issue_fop.setText(parent.getItemAtPosition(position).toString().trim());
                            if (receivableClientList != null)
                                selected_receivableClient = receivableClientList.get(position);
                        }
                    });
                    // endregion

                    // region Verify and Issue Button
                    btn_issue_verify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!verified) {
                                final String verify_pass = issue_pass.getText().toString();
                                if (verify_pass.equals("")) {
                                    issue_pass.setError(getString(R.string.error_field_required));
                                    return;
                                }
                                if (verify_pass.equals(PreferenceHelper.getString(mainActivity, PreferenceHelper.LOGGED_PASS))) {
                                    issue_pass_ll.setVisibility(View.GONE);
                                    btn_issue_verify_title.setText(R.string.issue_now);
                                    issue_ticket_ll.setVisibility(View.VISIBLE);
                                    issue_fop.setVisibility(View.VISIBLE);
                                    verified = true;
                                } else
                                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.error_incorrect_password));


                            } else {
                                String fop = issue_fop.getText().toString();
                                if (fop.equals(getString(R.string.not_selected))) {
                                    issue_fop.setError(getString(R.string.receivable_account_required));
                                    return;
                                }

                                if (payable_amount.equals("")) {
                                    issue_lumpsum.setError(getString(R.string.error_field_required));
                                    return;
                                }

                                if (selected_receivableClient == null) {
                                    issue_rec_acc.setError(getString(R.string.select_rec_account));
                                    return;
                                }

                                float credit = Float.parseFloat(agentSession.CurrentCredit);
                                float receivable = Float.parseFloat(issue_rec_amount_value.getText().toString());

                                if (credit < receivable) {
                                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.credit_limit_msg));
                                    return;
                                }

                                dialog_issue_tkt.dismiss();

                                // Issue Ticket
                                selected_btn = v;
                                new IssueTicket(FlightReservationFragment.this, PNR, ValidatingCarrier,
                                        agentSession, selected_receivableClient, markup_percentage,
                                        issue_rec_amount_value.getText().toString()).execute();
                                selected_receivableClient = null;
                            }
                        }
                    });
                    // endregion

                    btn_issue_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_issue_tkt.dismiss();
                        }
                    });

                    issue_pass_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            issue_pass.setText("");
                        }
                    });

                    issue_rec_acc_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            issue_rec_acc.setText("");
                        }
                    });
                }
            });
            // endregion
        }

        // region Print Button
        btn_pnr_print.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                selected_btn = v;
                // Google Cloud Print
                if (outputFile.exists()) {
                    new GoogleCloudPrintHelper(mainActivity, outputFile, docName).showGCPDialog();
                } else {
                    new PDFConverterHelper(FlightReservationFragment.this, outputFile,
                            ticket_img_bytes, getString(R.string.ticket_receipt), docName).execute();
                }
            }
        });
        // endregion

        // region E-mail Button
        btn_pnr_mail.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                final Dialog dialog = DialogHelper.createCustomDialog(mainActivity,
                        R.layout.dialog_flight_book_ticket_email, Gravity.CENTER);
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
                            sendEmail(agentSession, PNR, email_addresses, webview_ticket_html);
                        } else
                            send_email.setError(getString(R.string.error_field_required));
                    }
                });
            }
        });
        //endregion

        // region Save Button
        btn_pnr_save.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                selected_btn = v;
                if (FileHelper.createMainDirectory(mainActivity)) {
                    if (outputFile.exists()) {
                        DialogInterface.OnClickListener positive_listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new PDFConverterHelper(FlightReservationFragment.this, outputFile,
                                        ticket_img_bytes, getString(R.string.ticket_receipt), docName).execute();
                            }
                        };
                        DialogInterface.OnClickListener negative_listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DialogHelper.createConfirmDialog(mainActivity, getString(R.string.pnr_saved),
                                        getString(R.string.view_file_msg),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FileHelper.viewFile(mainActivity, outputFile, FileHelper.APPLICATION_PDF);
                                            }
                                        }, null);
                            }
                        };
                        DialogHelper.createConfirmDialog(getContext(), getString(R.string.file_already_exist_title),
                                getString(R.string.file_already_exist_msg), positive_listener, negative_listener);

                    } else {
                        new PDFConverterHelper(FlightReservationFragment.this, outputFile,
                                ticket_img_bytes, getString(R.string.ticket_receipt), docName).execute();
                    }
                } else
                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.directory_failed));
            }
        });
        // endregion

        // region Sms Button
        btn_pnr_sms.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                int GRANT_SEND_SMS = PreferenceHelper.getInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS);
                if (GRANT_SEND_SMS == -1) {
                    PermissionHelper.checkSMSPermission(mainActivity);
                } else if (GRANT_SEND_SMS == 1) {
                    createSMSDialog();
                } else {
                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.no_sms_permission));
                }
            }
        });
        // endregion

        // region Cancel Booking
        btn_pnr_book_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_btn = v;
                DialogHelper.createConfirmDialog(mainActivity, getString(R.string.confirm_book_cancel_title),
                        getString(R.string.confirm_book_cancel_msg) + PNR, "YES", "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!flag_booking_cancelled) {
                                    new CancelBooking(FlightReservationFragment.this, PNR, agentSession).execute();
                                } else {
                                    btn_pnr_book_cancel.setVisibility(View.GONE);
                                    PhoneFunctionality.makeToast(mainActivity, "Booking Ref# " + PNR + " cancelled already!");
                                }
                            }
                        }, null);
            }
        });
        // endregion

        // region Void Tickets
        btn_pnr_ticket_void.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_ticket_issued) {
                    if (!flag_ticket_voided) {
                        new VoidTickets(FlightReservationFragment.this, PNR, agentSession,
                                pricedItinerary._TPA_Extensions._ValidatingCarrier.Code).execute();
                    } else {
                        PhoneFunctionality.makeToast(mainActivity, "Ticket voided already!");
                    }
                } else {
                    PhoneFunctionality.makeToast(mainActivity, "Ticket not issued yet!");
                }
            }
        });
        // endregion

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.toggle.setHomeAsUpIndicator(R.drawable.ic_action_home_white);
        mainActivity.action_refresh_pnr.setVisible(true);
    }

    @Override
    public void onPause() {
        mainActivity.toggle.setHomeAsUpIndicator(null);
        mainActivity.action_refresh_pnr.setVisible(false);
        super.onPause();
    }

    public void createSMSDialog() {
        final Dialog dialog_ticket_sms = DialogHelper.createCustomDialog(mainActivity,
                R.layout.dialog_flight_book_ticket_sms, Gravity.CENTER);

        // region Find Views
        final EditText send_sms = (EditText) dialog_ticket_sms.findViewById(R.id.send_sms);
        final MultiAutoCompleteTextView send_sms_mactv = (MultiAutoCompleteTextView)
                dialog_ticket_sms.findViewById(R.id.send_sms_mactv);
        View send_sms_mactv_clear = dialog_ticket_sms.findViewById(R.id.send_sms_mactv_clear);
        send_sms_contact_rl = dialog_ticket_sms.findViewById(R.id.send_sms_contact_rl);
        send_sms_progress = dialog_ticket_sms.findViewById(R.id.send_sms_progress);
        send_sms_progress_title = dialog_ticket_sms.findViewById(R.id.send_sms_progress_title);
        send_sms_mactv_ll = dialog_ticket_sms.findViewById(R.id.send_sms_mactv_ll);
        send_sms_contact_list = (ListView) dialog_ticket_sms.findViewById(R.id.send_sms_contact_list);
        ImageButton send_sms_clear = (ImageButton) dialog_ticket_sms.findViewById(R.id.send_sms_clear);
        LinearLayout btn_send_sms = (LinearLayout) dialog_ticket_sms.findViewById(R.id.btn_send_sms);
        LinearLayout btn_send_sms_back = (LinearLayout) dialog_ticket_sms.findViewById(R.id.btn_send_sms_back);
        // endregion

        send_sms_contact_list.setVisibility(View.GONE);
        send_sms_mactv_ll.setVisibility(View.GONE);

        int GRANT_READ_CONTACTS = PreferenceHelper.getInt(mainActivity, PreferenceHelper.GRANT_READ_CONTACTS);
        if (GRANT_READ_CONTACTS == -1) {
            PermissionHelper.checkContactPermission(mainActivity);
        } else if (GRANT_READ_CONTACTS == 1) {
            new QueryContact(FlightReservationFragment.this).execute();
        }

        dialog_ticket_sms.show();

        // Text change listener
        send_sms_mactv.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (contactListAdapter != null)
                    contactListAdapter.getFilter().filter(send_sms_mactv.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Clear MAC textView Button
        send_sms_mactv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_sms_mactv.setText("");
                send_sms_mactv.requestFocus();
            }
        });

        // Clear number Button
        send_sms_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_sms.setText("");
                send_sms.requestFocus();
            }
        });

        // Back Button
        btn_send_sms_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_ticket_sms.dismiss();
            }
        });

        // Send Sms Button
        btn_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_btn = v;

                final String sms_no_to = send_sms.getText().toString();
                if ((contactListAdapter != null && contactListAdapter.selected_contacts.size() > 0)
                        || !sms_no_to.equals("")) {
                    dialog_ticket_sms.dismiss();
                    DialogInterface.OnClickListener positive_listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HashMap<String, Contact> contactList = null;
                            if (contactListAdapter != null)
                                contactList = contactListAdapter.selected_contacts;
                            new SendMessages(FlightReservationFragment.this, agentSession,
                                    contactList, sms_no_to, PNR, flag_ticket_issued).execute();
                        }
                    };

                    DialogHelper.createConfirmDialog(getContext(),
                            getString(R.string.sms_send_title),
                            getString(R.string.sms_send_msg),
                            positive_listener, null);
                } else {
                    PhoneFunctionality.makeToast(mainActivity, "Please select or type contact no");
                }
            }
        });
    }

    public void refreshWebView(String webview_ticket_html) {
        ticket_img_bytes = null;
        webView.loadData(webview_ticket_html, "text/html", null);
    }

    private void sendEmail(AgentSession agentSession, String PNR, String webview_ticket_html, String email_addresses) {
        String sender;
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            sender = getString(R.string.rt_email_address);
        } else {
            sender = agentSession.AGENT_EMAIL;
        }

        new SendEmail(this, PNR, sender, email_addresses, webview_ticket_html).execute();
    }
}
