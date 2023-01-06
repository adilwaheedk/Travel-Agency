package com.visionxoft.abacus.rehmantravel.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.HotelPrice;
import com.visionxoft.abacus.rehmantravel.model.MadinaHotel;
import com.visionxoft.abacus.rehmantravel.model.MakkahHotel;
import com.visionxoft.abacus.rehmantravel.model.UmrahFormDetail;
import com.visionxoft.abacus.rehmantravel.task.PDFConverterHelper;
import com.visionxoft.abacus.rehmantravel.task.SendEmail;
import com.visionxoft.abacus.rehmantravel.task.GetUmrahHotelDetail;
import com.visionxoft.abacus.rehmantravel.task.GetUmrahHotelPrice;
import com.visionxoft.abacus.rehmantravel.task.GetUmrahTravelSector;
import com.visionxoft.abacus.rehmantravel.task.GetUmrahVisaPrice;
import com.visionxoft.abacus.rehmantravel.task.SubmitUmrahForm;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.GoogleCloudPrintHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.views.RippleView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Design custom package for Umrah
 */
public class UmrahDesignFragment extends Fragment {

    // region Class member instances
    public View selected_btn;
    public RippleView btn_umrah_print;
    public final int MAKKAH1 = 1, MADINA = 2, MAKKAH2 = 3;
    public List<MakkahHotel> makkah1Hotels, makkah2Hotels;
    public List<MadinaHotel> madinaHotels;
    public MakkahHotel selected_makkah1Hotel, selected_makkah2Hotel;
    public MadinaHotel selected_madinaHotel;
    public HotelPrice makkah1Price, madinaPrice, makkah2Price;
    public int visaPrice = 0, totalTravelPrice = 0;

    private String docName;
    private File outputFile;
    private MainActivity mainActivity;
    private GetUmrahHotelPrice getUmrahHotelPrice;
    private GetUmrahHotelDetail makkah1HotelDetails, madinaHotelDetails, makkah2HotelDetails;
    private SimpleDateFormat simpleDateFormat, simpleDateFormat_param;
    private Calendar current_date, max_date;
    private EditText umrah_name, umrah_email, umrah_contact, umrah_city, umrah_remarks,
            umrah_makkah1_checkin, umrah_makkah1_checkout, umrah_madina_checkin,
            umrah_madina_checkout, umrah_makkah2_checkin, umrah_makkah2_checkout;
    private TextView umrah_makkah1_rooms, umrah_madina_rooms, umrah_makkah2_rooms,
            umrah_makkah1_nights, umrah_madina_nights, umrah_makkah2_nights,
            umrah_makkah1_price, umrah_madina_price, umrah_makkah2_price, umrah_total_rooms,
            umrah_total_nights, umrah_travel_price, umrah_total_price, umrah_grand_total_sr, umrah_grand_total_pkr;
    private Spinner umrah_makkah1_2rooms, umrah_makkah1_3rooms, umrah_makkah1_4rooms,
            umrah_madina_2rooms, umrah_madina_3rooms, umrah_madina_4rooms,
            umrah_makkah2_2rooms, umrah_makkah2_3rooms, umrah_makkah2_4rooms,
            umrah_sp_adult, umrah_sp_child, umrah_sp_infant;
    private RadioButton umrah_visa_yes;
    private CheckBox umrah_travel_toggle;
    private int adultVisaPrice = 0, childVisaPrice = 0, infantVisaPrice = 0, totalHotelPrice = 0;
    private String vehicleId, sectorId;
    private byte[] form_img_bytes;
    private boolean buttonClicked = false;
    private Picture umrahPicture;
    // endregion

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_umrah_design, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.umrah_design));
        mainActivity.setToolbarSubTitle(null);

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        simpleDateFormat_param = new SimpleDateFormat(getString(R.string.date_format_param), Locale.getDefault());
        current_date = Calendar.getInstance(Locale.getDefault());
        max_date = Calendar.getInstance(Locale.getDefault());
        max_date.add(Calendar.YEAR, 1);

        // region Find All Views
        final View umrah_makkah1_ll = rootView.findViewById(R.id.umrah_makkah1_ll);
        final View umrah_madina_ll = rootView.findViewById(R.id.umrah_madina_ll);
        final View umrah_makkah2_ll = rootView.findViewById(R.id.umrah_makkah2_ll);
        final View umrah_travel_ll = rootView.findViewById(R.id.umrah_travel_ll);

        // Client Info
        umrah_name = (EditText) rootView.findViewById(R.id.umrah_name);
        umrah_email = (EditText) rootView.findViewById(R.id.umrah_email);
        umrah_contact = (EditText) rootView.findViewById(R.id.umrah_contact);
        umrah_city = (EditText) rootView.findViewById(R.id.umrah_city);
        umrah_remarks = (EditText) rootView.findViewById(R.id.umrah_remarks);
        View umrah_name_clear = rootView.findViewById(R.id.umrah_name_clear);
        View umrah_email_clear = rootView.findViewById(R.id.umrah_email_clear);
        View umrah_contact_clear = rootView.findViewById(R.id.umrah_contact_clear);
        View umrah_city_clear = rootView.findViewById(R.id.umrah_city_clear);
        View umrah_remarks_clear = rootView.findViewById(R.id.umrah_remarks_clear);
        umrah_sp_adult = (Spinner) rootView.findViewById(R.id.umrah_sp_adult);
        umrah_sp_child = (Spinner) rootView.findViewById(R.id.umrah_sp_child);
        umrah_sp_infant = (Spinner) rootView.findViewById(R.id.umrah_sp_infant);

        // Makkah1 Info
        final View umrah_makkah1_toggle = rootView.findViewById(R.id.umrah_makkah1_toggle);
        final ImageView umrah_makkah1_toggle_img = (ImageView) rootView.findViewById(R.id.umrah_makkah1_toggle_img);
        final MultiAutoCompleteTextView umrah_makkah1_hotel = (MultiAutoCompleteTextView) rootView.findViewById(R.id.umrah_makkah1_hotel);
        View umrah_makkah1_clear = rootView.findViewById(R.id.umrah_makkah1_clear);
        umrah_makkah1_checkin = (EditText) rootView.findViewById(R.id.umrah_makkah1_checkin);
        umrah_makkah1_checkout = (EditText) rootView.findViewById(R.id.umrah_makkah1_checkout);
        umrah_makkah1_2rooms = (Spinner) rootView.findViewById(R.id.umrah_makkah1_2rooms);
        umrah_makkah1_3rooms = (Spinner) rootView.findViewById(R.id.umrah_makkah1_3rooms);
        umrah_makkah1_4rooms = (Spinner) rootView.findViewById(R.id.umrah_makkah1_4rooms);
        umrah_makkah1_rooms = (TextView) rootView.findViewById(R.id.umrah_makkah1_rooms);
        umrah_makkah1_nights = (TextView) rootView.findViewById(R.id.umrah_makkah1_nights);
        umrah_makkah1_price = (TextView) rootView.findViewById(R.id.umrah_makkah1_price);

        // Madina Info
        final View umrah_madina_toggle = rootView.findViewById(R.id.umrah_madina_toggle);
        final ImageView umrah_madina_toggle_img = (ImageView) rootView.findViewById(R.id.umrah_madina_toggle_img);
        final MultiAutoCompleteTextView umrah_madina_hotel = (MultiAutoCompleteTextView) rootView.findViewById(R.id.umrah_madina_hotel);
        View umrah_madina_clear = rootView.findViewById(R.id.umrah_madina_clear);
        umrah_madina_checkin = (EditText) rootView.findViewById(R.id.umrah_madina_checkin);
        umrah_madina_checkout = (EditText) rootView.findViewById(R.id.umrah_madina_checkout);
        umrah_madina_2rooms = (Spinner) rootView.findViewById(R.id.umrah_madina_2rooms);
        umrah_madina_3rooms = (Spinner) rootView.findViewById(R.id.umrah_madina_3rooms);
        umrah_madina_4rooms = (Spinner) rootView.findViewById(R.id.umrah_madina_4rooms);
        umrah_madina_rooms = (TextView) rootView.findViewById(R.id.umrah_madina_rooms);
        umrah_madina_nights = (TextView) rootView.findViewById(R.id.umrah_madina_nights);
        umrah_madina_price = (TextView) rootView.findViewById(R.id.umrah_madina_price);

        // Makkah2 Info
        final View umrah_makkah2_toggle = rootView.findViewById(R.id.umrah_makkah2_toggle);
        final ImageView umrah_makkah2_toggle_img = (ImageView) rootView.findViewById(R.id.umrah_makkah2_toggle_img);
        final MultiAutoCompleteTextView umrah_makkah2_hotel = (MultiAutoCompleteTextView) rootView.findViewById(R.id.umrah_makkah2_hotel);
        View umrah_makkah2_clear = rootView.findViewById(R.id.umrah_makkah2_clear);
        umrah_makkah2_checkin = (EditText) rootView.findViewById(R.id.umrah_makkah2_checkin);
        umrah_makkah2_checkout = (EditText) rootView.findViewById(R.id.umrah_makkah2_checkout);
        umrah_makkah2_2rooms = (Spinner) rootView.findViewById(R.id.umrah_makkah2_2rooms);
        umrah_makkah2_3rooms = (Spinner) rootView.findViewById(R.id.umrah_makkah2_3rooms);
        umrah_makkah2_4rooms = (Spinner) rootView.findViewById(R.id.umrah_makkah2_4rooms);
        umrah_makkah2_rooms = (TextView) rootView.findViewById(R.id.umrah_makkah2_rooms);
        umrah_makkah2_nights = (TextView) rootView.findViewById(R.id.umrah_makkah2_nights);
        umrah_makkah2_price = (TextView) rootView.findViewById(R.id.umrah_makkah2_price);

        // Travel Info
        umrah_travel_toggle = (CheckBox) rootView.findViewById(R.id.umrah_travel_toggle);
        final Spinner umrah_travel_sector = (Spinner) rootView.findViewById(R.id.umrah_travel_sector);
        final Spinner umrah_travel_vehicle = (Spinner) rootView.findViewById(R.id.umrah_travel_vehicle);
        umrah_travel_price = (TextView) rootView.findViewById(R.id.umrah_travel_price);

        umrah_visa_yes = (RadioButton) rootView.findViewById(R.id.umrah_visa_yes);
        //umrah_sp_visa = (Spinner) rootView.findViewById(R.id.umrah_sp_visa);
        umrah_grand_total_sr = (TextView) rootView.findViewById(R.id.umrah_grand_total_sr);
        umrah_grand_total_pkr = (TextView) rootView.findViewById(R.id.umrah_grand_total_pkr);

        // Hotels Expense Info
        umrah_total_rooms = (TextView) rootView.findViewById(R.id.umrah_total_rooms);
        umrah_total_nights = (TextView) rootView.findViewById(R.id.umrah_total_nights);
        umrah_total_price = (TextView) rootView.findViewById(R.id.umrah_total_price);
        // endregion

        // region Init Views
        //ArrayList<String> visasCounter = new ArrayList<>();
        //for (int i = 1; i <= 30; i++) visasCounter.add(String.valueOf(i));
        //umrah_sp_visa.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, visasCounter));
        //umrah_sp_visa.setEnabled(false);

        //umrah_makkah1_ll.setVisibility(View.GONE);
        //umrah_madina_ll.setVisibility(View.GONE);
        //umrah_makkah2_ll.setVisibility(View.GONE);
        umrah_travel_ll.setVisibility(View.GONE);
        umrah_makkah1_2rooms.setEnabled(false);
        umrah_makkah1_3rooms.setEnabled(false);
        umrah_makkah1_4rooms.setEnabled(false);
        umrah_madina_2rooms.setEnabled(false);
        umrah_madina_3rooms.setEnabled(false);
        umrah_madina_4rooms.setEnabled(false);
        umrah_makkah2_2rooms.setEnabled(false);
        umrah_makkah2_3rooms.setEnabled(false);
        umrah_makkah2_4rooms.setEnabled(false);
        // endregion

        docName = getString(R.string.umrah_file_prefix) + umrah_contact.getText().toString();
        outputFile = new File(FileHelper.getDirectory(mainActivity), docName + FileHelper.fileExtPDF);

        // region Passenger Visa Calculation
        umrah_sp_adult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (umrah_visa_yes.isChecked())
                    adultVisaPrice = visaPrice * Integer.parseInt(umrah_sp_adult.getSelectedItem().toString());
                else adultVisaPrice = 0;
                calculateGrandTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_sp_child.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (umrah_visa_yes.isChecked())
                    childVisaPrice = visaPrice * Integer.parseInt(umrah_sp_child.getSelectedItem().toString());
                else childVisaPrice = 0;
                calculateGrandTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_sp_infant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (umrah_visa_yes.isChecked())
                    infantVisaPrice = visaPrice * Integer.parseInt(umrah_sp_infant.getSelectedItem().toString());
                else infantVisaPrice = 0;
                calculateGrandTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_visa_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adultVisaPrice = visaPrice * Integer.parseInt(umrah_sp_adult.getSelectedItem().toString());
                    childVisaPrice = visaPrice * Integer.parseInt(umrah_sp_child.getSelectedItem().toString());
                    infantVisaPrice = visaPrice * Integer.parseInt(umrah_sp_infant.getSelectedItem().toString());
                } else {
                    adultVisaPrice = 0;
                    childVisaPrice = 0;
                    infantVisaPrice = 0;
                }
                calculateGrandTotal();
            }
        });
        // endregion

        // region Passenger Visa Calculation Commented
//        umrah_visa_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                umrah_sp_visa.setEnabled(isChecked);
//                if (isChecked)
//                    noOfVisas = Integer.parseInt(umrah_sp_visa.getSelectedItem().toString());
//                else noOfVisas = 0;
//                calculateGrandTotal();
//            }
//        });
//
//        umrah_sp_visa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
//                noOfVisas = Integer.parseInt(umrah_sp_visa.getSelectedItem().toString());
//                calculateGrandTotal();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        // endregion

        // region Toggle OnClick listeners
        umrah_makkah1_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umrah_makkah1_ll.isShown()) {
                    YoYo.with(Techniques.FadeOutLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_makkah1_ll.setVisibility(View.GONE);
                            umrah_makkah1_toggle_img.setImageResource(R.drawable.ic_add_white_18dp);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_makkah1_ll);
                } else {
                    YoYo.with(Techniques.FadeInLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            umrah_makkah1_ll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_makkah1_toggle_img.setImageResource(R.drawable.ic_remove_white_18dp);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_makkah1_ll);
                }
            }
        });

        umrah_madina_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umrah_madina_ll.isShown()) {
                    YoYo.with(Techniques.FadeOutLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_madina_ll.setVisibility(View.GONE);
                            umrah_madina_toggle_img.setImageResource(R.drawable.ic_add_white_18dp);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_madina_ll);
                } else {
                    YoYo.with(Techniques.FadeInLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            umrah_madina_ll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_madina_toggle_img.setImageResource(R.drawable.ic_remove_white_18dp);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_madina_ll);
                }
            }
        });

        umrah_makkah2_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umrah_makkah2_ll.isShown()) {
                    YoYo.with(Techniques.FadeOutLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_makkah2_ll.setVisibility(View.GONE);
                            umrah_makkah2_toggle_img.setImageResource(R.drawable.ic_add_white_18dp);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_makkah2_ll);
                } else {
                    YoYo.with(Techniques.FadeInLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            umrah_makkah2_ll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_makkah2_toggle_img.setImageResource(R.drawable.ic_remove_white_18dp);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_makkah2_ll);
                }
            }
        });

        umrah_travel_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    YoYo.with(Techniques.FadeInLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            umrah_travel_ll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_travel_ll);
                } else {
                    YoYo.with(Techniques.FadeOutLeft).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            umrah_travel_ll.setVisibility(View.GONE);
                            totalTravelPrice = 0;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    }).duration(200).playOn(umrah_travel_ll);
                }
                calculateGrandTotal();
            }
        });
        // endregion

        // region Clear EditTexts
        umrah_name_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_name.setText("");
                umrah_name.requestFocus();
            }
        });


        umrah_email_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_email.setText("");
                umrah_email.requestFocus();
            }
        });

        umrah_contact_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_contact.setText("");
                umrah_contact.requestFocus();
            }
        });

        umrah_city_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_city.setText("");
                umrah_city.requestFocus();
            }
        });

        umrah_remarks_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_remarks.setText("");
                umrah_remarks.requestFocus();
            }
        });

        umrah_makkah1_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_makkah1_hotel.setText("");
                umrah_makkah1_hotel.setEnabled(true);
                umrah_makkah1_checkin.setEnabled(false);
                umrah_makkah1_checkout.setEnabled(false);
                umrah_makkah1_2rooms.setEnabled(false);
                umrah_makkah1_2rooms.setSelection(0);
                umrah_makkah1_3rooms.setEnabled(false);
                umrah_makkah1_3rooms.setSelection(0);
                umrah_makkah1_4rooms.setEnabled(false);
                umrah_makkah1_4rooms.setSelection(0);
                umrah_makkah1_hotel.requestFocus();
                umrah_makkah1_checkin.setText("");
                umrah_makkah1_checkout.setText("");
                makkah1Price = null;
                selected_makkah1Hotel = null;
                calculateRoomsAndPrices(MAKKAH1);
            }
        });

        umrah_madina_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_madina_hotel.setText("");
                umrah_madina_hotel.setEnabled(true);
                umrah_madina_checkin.setEnabled(false);
                umrah_madina_checkout.setEnabled(false);
                umrah_madina_2rooms.setEnabled(false);
                umrah_madina_2rooms.setSelection(0);
                umrah_madina_3rooms.setEnabled(false);
                umrah_madina_3rooms.setSelection(0);
                umrah_madina_4rooms.setEnabled(false);
                umrah_madina_4rooms.setSelection(0);
                umrah_madina_hotel.requestFocus();
                umrah_madina_checkin.setText("");
                umrah_madina_checkout.setText("");
                madinaPrice = null;
                selected_madinaHotel = null;
                calculateRoomsAndPrices(MADINA);
            }
        });

        umrah_makkah2_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umrah_makkah2_hotel.setText("");
                umrah_makkah2_hotel.setEnabled(true);
                umrah_makkah2_checkin.setEnabled(false);
                umrah_makkah2_checkout.setEnabled(false);
                umrah_makkah2_2rooms.setEnabled(false);
                umrah_makkah2_2rooms.setSelection(0);
                umrah_makkah2_3rooms.setEnabled(false);
                umrah_makkah2_3rooms.setSelection(0);
                umrah_makkah2_4rooms.setEnabled(false);
                umrah_makkah2_4rooms.setSelection(0);
                umrah_makkah2_hotel.requestFocus();
                umrah_makkah2_checkin.setText("");
                umrah_makkah2_checkout.setText("");
                makkah2Price = null;
                selected_makkah2Hotel = null;
                calculateRoomsAndPrices(MAKKAH2);
            }
        });
        //endregion

        // region Hotels OnClick Listeners
        umrah_makkah1_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makkah1HotelDetails = new GetUmrahHotelDetail(UmrahDesignFragment.this, umrah_makkah1_hotel, MAKKAH1);
                makkah1HotelDetails.execute(umrah_makkah1_hotel.getText().toString());
            }
        });

        umrah_madina_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                madinaHotelDetails = new GetUmrahHotelDetail(UmrahDesignFragment.this, umrah_madina_hotel, MADINA);
                madinaHotelDetails.execute(umrah_madina_hotel.getText().toString());
            }
        });

        umrah_makkah2_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makkah2HotelDetails = new GetUmrahHotelDetail(UmrahDesignFragment.this, umrah_makkah2_hotel, MAKKAH2);
                makkah2HotelDetails.execute(umrah_makkah2_hotel.getText().toString());
            }
        });
        // endregion

        // region TextWatcher listeners
        umrah_makkah1_hotel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (umrah_makkah1_hotel.length() > 0) {
                    makkah1HotelDetails = new GetUmrahHotelDetail(UmrahDesignFragment.this, umrah_makkah1_hotel, MAKKAH1);
                    makkah1HotelDetails.execute(umrah_makkah1_hotel.getText().toString());
                }
            }
        });

        umrah_madina_hotel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (umrah_madina_hotel.length() > 0) {
                    madinaHotelDetails = new GetUmrahHotelDetail(UmrahDesignFragment.this, umrah_madina_hotel, MADINA);
                    madinaHotelDetails.execute(umrah_madina_hotel.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        umrah_makkah2_hotel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (umrah_makkah2_hotel.length() > 0) {
                    makkah2HotelDetails = new GetUmrahHotelDetail(UmrahDesignFragment.this, umrah_makkah2_hotel, MAKKAH2);
                    makkah2HotelDetails.execute(umrah_makkah2_hotel.getText().toString());
                }
            }
        });
        // endregion

        // region Item Click Listeners
        umrah_makkah1_hotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                umrah_makkah1_hotel.setText(parent.getItemAtPosition(position).toString());
                umrah_makkah1_hotel.setEnabled(false);
                umrah_makkah1_checkin.setEnabled(true);
                umrah_makkah1_checkout.setEnabled(true);
                umrah_makkah1_2rooms.setEnabled(true);
                umrah_makkah1_3rooms.setEnabled(true);
                umrah_makkah1_4rooms.setEnabled(true);
                selected_makkah1Hotel = makkah1Hotels.get(position);
            }
        });

        umrah_madina_hotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                umrah_madina_hotel.setText(parent.getItemAtPosition(position).toString());
                umrah_madina_hotel.setEnabled(false);
                umrah_madina_checkin.setEnabled(true);
                umrah_madina_checkout.setEnabled(true);
                umrah_madina_2rooms.setEnabled(true);
                umrah_madina_3rooms.setEnabled(true);
                umrah_madina_4rooms.setEnabled(true);
                selected_madinaHotel = madinaHotels.get(position);
            }
        });

        umrah_makkah2_hotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                umrah_makkah2_hotel.setText(parent.getItemAtPosition(position).toString());
                umrah_makkah2_hotel.setEnabled(false);
                umrah_makkah2_checkin.setEnabled(true);
                umrah_makkah2_checkout.setEnabled(true);
                umrah_makkah2_2rooms.setEnabled(true);
                umrah_makkah2_3rooms.setEnabled(true);
                umrah_makkah2_4rooms.setEnabled(true);
                selected_makkah2Hotel = makkah2Hotels.get(position);
            }
        });
        // endregion

        // region Check In/Out Click Listeners
        umrah_makkah1_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    PhoneFunctionality.showCalendar(UmrahDesignFragment.this, v, current_date, min_date, max_date);
                    buttonClicked = false;
                }
            }
        });

        umrah_makkah1_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    try {

                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(umrah_makkah1_checkin.getText().toString()));
                        PhoneFunctionality.showCalendar(UmrahDesignFragment.this, v, current_date, min_date, max_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.select_check_in));
                    }
                    buttonClicked = false;
                }
            }
        });

        umrah_madina_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    if (umrah_makkah1_checkout.length() > 0) {
                        try {
                            min_date.setTime(simpleDateFormat.parse(umrah_makkah1_checkout.getText().toString()));
                        } catch (ParseException ignored) {
                            // Ignore exception
                        }
                    }
                    PhoneFunctionality.showCalendar(UmrahDesignFragment.this, v, current_date, min_date, max_date);
                    buttonClicked = false;
                }
            }
        });

        umrah_madina_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(umrah_madina_checkin.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(UmrahDesignFragment.this, v, current_date, min_date, max_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.select_check_in));
                    }
                    buttonClicked = false;
                }
            }
        });

        umrah_makkah2_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    if (umrah_madina_checkout.length() > 0) {
                        try {
                            min_date.setTime(simpleDateFormat.parse(umrah_madina_checkout.getText().toString()));
                        } catch (ParseException ignored) {
                            // Ignore exception
                        }
                    }
                    PhoneFunctionality.showCalendar(UmrahDesignFragment.this, v, current_date, min_date, max_date);
                    buttonClicked = false;
                }
            }
        });

        umrah_makkah2_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(umrah_makkah2_checkin.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(UmrahDesignFragment.this, v, current_date, min_date, max_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.select_check_in));
                    }
                    buttonClicked = false;
                }
            }
        });
        // endregion

        // region Check In/Out TextWatchers
        umrah_makkah1_checkin.addTextChangedListener(new HotelPriceTextWatcher(MAKKAH1));
        umrah_makkah1_checkout.addTextChangedListener(new HotelPriceTextWatcher(MAKKAH1));
        umrah_madina_checkout.addTextChangedListener(new HotelPriceTextWatcher(MADINA));
        umrah_madina_checkout.addTextChangedListener(new HotelPriceTextWatcher(MADINA));
        umrah_makkah2_checkout.addTextChangedListener(new HotelPriceTextWatcher(MAKKAH2));
        umrah_makkah2_checkout.addTextChangedListener(new HotelPriceTextWatcher(MAKKAH2));
        // endregion

        // region Calculate Hotel Rooms
        umrah_makkah1_2rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MAKKAH1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_makkah1_3rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MAKKAH1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_makkah1_4rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MAKKAH1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_madina_2rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MADINA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_madina_3rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MADINA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_madina_4rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MADINA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_makkah2_2rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MAKKAH2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_makkah2_3rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MAKKAH2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_makkah2_4rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRoomsAndPrices(MAKKAH2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // endregion

        // region Calculate Travel Expense
        umrah_travel_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sectorId = getResources().getStringArray(R.array.sectorUmrahId)[position];
                if (sectorId != null && vehicleId != null) {
                    new GetUmrahTravelSector(UmrahDesignFragment.this).execute(vehicleId, sectorId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        umrah_travel_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleId = getResources().getStringArray(R.array.vehicleTypeUmrahId)[position];
                if (sectorId != null && vehicleId != null) {
                    new GetUmrahTravelSector(UmrahDesignFragment.this).execute(vehicleId, sectorId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // endregion

        mainActivity.action_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields())
                    new SubmitUmrahForm(UmrahDesignFragment.this, getUmrahFormDetails()).execute();
                else PhoneFunctionality.errorAnimation(v);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity != null && mainActivity.action_submit != null)
            mainActivity.action_submit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mainActivity != null && mainActivity.action_submit != null)
            mainActivity.action_submit.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        if (getUmrahHotelPrice != null && getUmrahHotelPrice.getStatus() == AsyncTask.Status.RUNNING)
            getUmrahHotelPrice.cancel(true);
        if (makkah1HotelDetails != null && makkah1HotelDetails.getStatus() == AsyncTask.Status.RUNNING)
            makkah1HotelDetails.cancel(true);
        if (madinaHotelDetails != null && madinaHotelDetails.getStatus() == AsyncTask.Status.RUNNING)
            madinaHotelDetails.cancel(true);
        if (makkah2HotelDetails != null && makkah2HotelDetails.getStatus() == AsyncTask.Status.RUNNING)
            makkah2HotelDetails.cancel(true);
        super.onStop();
    }

    private class HotelPriceTextWatcher implements TextWatcher {
        int hotel_type;

        HotelPriceTextWatcher(int hotel_type) {
            this.hotel_type = hotel_type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (getUmrahHotelPrice != null && getUmrahHotelPrice.getStatus() == AsyncTask.Status.RUNNING)
                getUmrahHotelPrice.cancel(true);
            getUmrahHotelPrice = new GetUmrahHotelPrice(UmrahDesignFragment.this, hotel_type);
            switch (hotel_type) {
                case MAKKAH1:
                    if (umrah_makkah1_checkin.length() > 0 && umrah_makkah1_checkout.length() > 0) {
                        try {
                            Date checkIn = simpleDateFormat.parse(umrah_makkah1_checkin.getText().toString());
                            Date checkOut = simpleDateFormat.parse(umrah_makkah1_checkout.getText().toString());
                            long elapsedDays = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
                            if (elapsedDays > 21) {
                                umrah_makkah1_nights.setText("0");
                                umrah_makkah1_checkout.setText("");
                                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_validity));
                            } else {
                                umrah_makkah1_nights.setText(String.valueOf(elapsedDays));
                                new GetUmrahVisaPrice(UmrahDesignFragment.this).execute(
                                        simpleDateFormat_param.format(checkIn), simpleDateFormat_param.format(checkOut));
                                if (selected_makkah1Hotel != null) {
                                    getUmrahHotelPrice.execute(selected_makkah1Hotel.id,
                                            simpleDateFormat_param.format(checkIn), simpleDateFormat_param.format(checkOut));
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else return;
                    break;
                case MADINA:
                    if (umrah_madina_checkin.length() > 0 && umrah_madina_checkout.length() > 0) {
                        try {
                            Date checkIn = simpleDateFormat.parse(umrah_madina_checkin.getText().toString());
                            Date checkOut = simpleDateFormat.parse(umrah_madina_checkout.getText().toString());
                            long elapsedDays = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
                            if (elapsedDays > 21) {
                                umrah_madina_nights.setText("0");
                                umrah_madina_checkout.setText("");
                                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_validity));
                            } else {
                                umrah_madina_nights.setText(String.valueOf(elapsedDays));
                                if (selected_madinaHotel != null) {
                                    getUmrahHotelPrice.execute(selected_madinaHotel.id,
                                            simpleDateFormat_param.format(checkIn), simpleDateFormat_param.format(checkOut));
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else return;
                    break;
                case MAKKAH2:
                    if (umrah_makkah2_checkin.length() > 0 && umrah_makkah2_checkout.length() > 0) {
                        try {
                            Date checkIn = simpleDateFormat.parse(umrah_makkah2_checkin.getText().toString());
                            Date checkOut = simpleDateFormat.parse(umrah_makkah2_checkout.getText().toString());
                            long elapsedDays = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
                            if (elapsedDays > 21) {
                                umrah_makkah2_nights.setText("0");
                                umrah_makkah2_checkout.setText("");
                                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_validity));
                            } else {
                                umrah_makkah2_nights.setText(String.valueOf(elapsedDays));
                                if (selected_makkah2Hotel != null) {
                                    getUmrahHotelPrice.execute(selected_makkah2Hotel.id,
                                            simpleDateFormat_param.format(checkIn), simpleDateFormat_param.format(checkOut));
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else return;
                    break;
            }

            int total_nights = Integer.parseInt(umrah_makkah1_nights.getText().toString()) +
                    Integer.parseInt(umrah_madina_nights.getText().toString()) +
                    Integer.parseInt(umrah_makkah2_nights.getText().toString());
            umrah_total_nights.setText(String.valueOf(total_nights));
        }
    }

    public void calculateRoomsAndPrices(int hotel_type) {
        int no_rooms2, no_rooms3, no_rooms4, rooms2Price, rooms3Price, rooms4Price, totalRoomPrice, totalPrice;
        switch (hotel_type) {
            case MAKKAH1:
                if (makkah1Price != null) {
                    if (makkah1Price.mkdblrprice.equals("0")) {
                        umrah_makkah1_2rooms.setEnabled(false);
                        no_rooms2 = 0;
                    } else {
                        umrah_makkah1_2rooms.setEnabled(true);
                        no_rooms2 = Integer.parseInt(umrah_makkah1_2rooms.getSelectedItem().toString());
                    }
                    rooms2Price = Integer.parseInt(makkah1Price.mkdblrprice) * no_rooms2;
                    if (makkah1Price.mktrplrprice.equals("0")) {
                        umrah_makkah1_3rooms.setEnabled(false);
                        no_rooms3 = 0;
                    } else {
                        umrah_makkah1_3rooms.setEnabled(true);
                        no_rooms3 = Integer.parseInt(umrah_makkah1_3rooms.getSelectedItem().toString());
                    }
                    rooms3Price = Integer.parseInt(makkah1Price.mktrplrprice) * no_rooms3;
                    if (makkah1Price.mkqudrprice.equals("0")) {
                        umrah_makkah1_4rooms.setEnabled(false);
                        no_rooms4 = 0;
                    } else {
                        umrah_makkah1_4rooms.setEnabled(true);
                        no_rooms4 = Integer.parseInt(umrah_makkah1_4rooms.getSelectedItem().toString());
                    }
                    rooms4Price = Integer.parseInt(makkah1Price.mkqudrprice) * no_rooms4;
                    umrah_makkah1_rooms.setText(String.valueOf(no_rooms2 + no_rooms3 + no_rooms4));
                    totalRoomPrice = rooms2Price + rooms3Price + rooms4Price;
                    totalPrice = (Integer.parseInt(umrah_makkah1_nights.getText().toString()) * totalRoomPrice);
                    umrah_makkah1_price.setText(String.valueOf(totalPrice));
                } else {
                    umrah_makkah1_rooms.setText("0");
                    umrah_makkah1_nights.setText("0");
                    umrah_makkah1_price.setText("0");
                    return;
                }
                break;
            case MADINA:
                if (madinaPrice != null) {
                    if (madinaPrice.mddblrprice.equals("0")) {
                        umrah_madina_2rooms.setEnabled(false);
                        no_rooms2 = 0;
                    } else {
                        umrah_madina_2rooms.setEnabled(true);
                        no_rooms2 = Integer.parseInt(umrah_madina_2rooms.getSelectedItem().toString());
                    }
                    rooms2Price = Integer.parseInt(madinaPrice.mddblrprice) * no_rooms2;
                    if (madinaPrice.mdtrplrprice.equals("0")) {
                        umrah_madina_3rooms.setEnabled(false);
                        no_rooms3 = 0;
                    } else {
                        umrah_madina_3rooms.setEnabled(true);
                        no_rooms3 = Integer.parseInt(umrah_madina_3rooms.getSelectedItem().toString());
                    }
                    rooms3Price = Integer.parseInt(madinaPrice.mdtrplrprice) * no_rooms3;
                    if (madinaPrice.mdqudrprice.equals("0")) {
                        umrah_madina_4rooms.setEnabled(false);
                        no_rooms4 = 0;
                    } else {
                        umrah_madina_4rooms.setEnabled(true);
                        no_rooms4 = Integer.parseInt(umrah_madina_4rooms.getSelectedItem().toString());
                    }
                    rooms4Price = Integer.parseInt(madinaPrice.mdqudrprice) * no_rooms4;
                    umrah_madina_rooms.setText(String.valueOf(no_rooms2 + no_rooms3 + no_rooms4));
                    totalRoomPrice = rooms2Price + rooms3Price + rooms4Price;
                    totalPrice = (Integer.parseInt(umrah_madina_nights.getText().toString()) * totalRoomPrice);
                    umrah_madina_price.setText(String.valueOf(totalPrice));
                } else {
                    umrah_madina_rooms.setText("0");
                    umrah_madina_nights.setText("0");
                    umrah_madina_price.setText("0");
                    return;
                }
                break;
            case MAKKAH2:
                if (makkah2Price != null) {
                    if (makkah2Price.mkdblrprice.equals("0")) {
                        umrah_makkah2_2rooms.setEnabled(false);
                        no_rooms2 = 0;
                    } else {
                        umrah_makkah2_2rooms.setEnabled(true);
                        no_rooms2 = Integer.parseInt(umrah_makkah2_2rooms.getSelectedItem().toString());
                    }
                    rooms2Price = Integer.parseInt(makkah2Price.mkdblrprice) * no_rooms2;
                    if (makkah2Price.mktrplrprice.equals("0")) {
                        umrah_makkah2_3rooms.setEnabled(false);
                        no_rooms3 = 0;
                    } else {
                        umrah_makkah2_3rooms.setEnabled(true);
                        no_rooms3 = Integer.parseInt(umrah_makkah2_3rooms.getSelectedItem().toString());
                    }
                    rooms3Price = Integer.parseInt(makkah2Price.mktrplrprice) * no_rooms3;
                    if (makkah2Price.mkqudrprice.equals("0")) {
                        umrah_makkah2_4rooms.setEnabled(false);
                        no_rooms4 = 0;
                    } else {
                        umrah_makkah2_4rooms.setEnabled(true);
                        no_rooms4 = Integer.parseInt(umrah_makkah2_4rooms.getSelectedItem().toString());
                    }
                    rooms4Price = Integer.parseInt(makkah2Price.mkqudrprice) * no_rooms4;
                    umrah_makkah2_rooms.setText(String.valueOf(no_rooms2 + no_rooms3 + no_rooms4));
                    totalRoomPrice = rooms2Price + rooms3Price + rooms4Price;
                    totalPrice = (Integer.parseInt(umrah_makkah2_nights.getText().toString()) * totalRoomPrice);
                    umrah_makkah2_price.setText(String.valueOf(totalPrice));
                } else {
                    umrah_makkah2_rooms.setText("0");
                    umrah_makkah2_nights.setText("0");
                    umrah_makkah2_price.setText("0");
                    return;
                }
                break;
        }

        int totalHotelRooms = Integer.parseInt(umrah_makkah1_rooms.getText().toString()) +
                Integer.parseInt(umrah_madina_rooms.getText().toString()) +
                Integer.parseInt(umrah_makkah2_rooms.getText().toString());

        totalHotelPrice = Integer.parseInt(umrah_makkah1_price.getText().toString()) +
                Integer.parseInt(umrah_madina_price.getText().toString()) +
                Integer.parseInt(umrah_makkah2_price.getText().toString());

        umrah_total_rooms.setText(String.valueOf(totalHotelRooms));
        int total_nights = Integer.parseInt(umrah_makkah1_nights.getText().toString()) +
                Integer.parseInt(umrah_madina_nights.getText().toString()) +
                Integer.parseInt(umrah_makkah2_nights.getText().toString());
        umrah_total_nights.setText(String.valueOf(total_nights));
        umrah_total_price.setText(String.valueOf(totalHotelPrice));
        calculateGrandTotal();
    }

    private void calculateGrandTotal() {
        int grandTotalPrice = totalHotelPrice + totalTravelPrice + (adultVisaPrice + childVisaPrice + infantVisaPrice);
        umrah_grand_total_sr.setText(String.valueOf(grandTotalPrice));
        umrah_grand_total_pkr.setText(String.valueOf(grandTotalPrice * 29));
    }

    public boolean checkFields() {
        if (umrah_name.getText().toString().equals("")) umrah_name.setText("Unknown");

        if (umrah_contact.getText().toString().equals("")) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_contact_required));
            return false;
        }

        if (umrah_contact.length() < 7 || umrah_contact.length() > 15) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_contact_invalid));
            return false;
        }

        if (umrah_sp_adult.getSelectedItem().equals("0")) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_select_adult));
            return false;
        }

        int no_of_adults = Integer.parseInt(umrah_sp_adult.getSelectedItem().toString());
        if (selected_makkah1Hotel != null) {
            int rooms_space = Integer.parseInt(umrah_makkah1_2rooms.getSelectedItem().toString()) * 2 +
                    Integer.parseInt(umrah_makkah1_3rooms.getSelectedItem().toString()) * 3 +
                    Integer.parseInt(umrah_makkah1_4rooms.getSelectedItem().toString()) * 4;
            if (rooms_space < no_of_adults) {
                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_increase_room) + "in makkah hotel");
                return false;
            }
        } else {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_select_makkah_hotel));
            return false;
        }

        if (selected_madinaHotel != null) {
            int rooms_space = Integer.parseInt(umrah_madina_2rooms.getSelectedItem().toString()) * 2 +
                    Integer.parseInt(umrah_madina_3rooms.getSelectedItem().toString()) * 3 +
                    Integer.parseInt(umrah_madina_4rooms.getSelectedItem().toString()) * 4;
            if (rooms_space < no_of_adults) {
                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_increase_room) + "in madinah hotel");
                return false;
            }
        } else {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_select_madina_hotel));
            return false;
        }

        if (selected_makkah2Hotel != null) {
            int rooms_space = Integer.parseInt(umrah_makkah2_2rooms.getSelectedItem().toString()) * 2 +
                    Integer.parseInt(umrah_makkah2_3rooms.getSelectedItem().toString()) * 3 +
                    Integer.parseInt(umrah_makkah2_4rooms.getSelectedItem().toString()) * 4;
            if (rooms_space < no_of_adults) {
                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_increase_room) + "in makkah hotel");
                return false;
            }
        }

        if (umrah_total_nights.getText().toString().equals("0")) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_select_one_night));
            return false;
        }

        if (Integer.parseInt(umrah_total_nights.getText().toString()) > 21) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_validity));
            return false;
        }

        //int no_of_childs = Integer.parseInt(umrah_sp_child.getSelectedItem().toString());
        //int no_of_infants = Integer.parseInt(umrah_sp_infant.getSelectedItem().toString());
        //if (noOfVisas > (no_of_adults + no_of_childs + no_of_infants)) {
        //    PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_visa_increases));
        //    return false;
        //}

        if (umrah_travel_toggle.isChecked() && umrah_travel_price.getText().toString().equals("0")) {
            umrah_travel_toggle.performClick();
        }

        return true;
    }

    public UmrahFormDetail getUmrahFormDetails() {
        UmrahFormDetail umrahFormDetail = new UmrahFormDetail();
        umrahFormDetail.user_name = umrah_name.getText().toString();
        umrahFormDetail.user_email = umrah_email.getText().toString();
        umrahFormDetail.user_contact_no = umrah_contact.getText().toString();
        umrahFormDetail.city_name = umrah_city.getText().toString();
        umrahFormDetail.remarks = umrah_remarks.getText().toString();

        if (selected_makkah1Hotel != null) {
            umrahFormDetail.mkHotelId = selected_makkah1Hotel.hotelId;
            umrahFormDetail.MKHootelTypeId = selected_makkah1Hotel.mkhotelTypeId;
            umrahFormDetail.mkhotelTypeId = selected_makkah1Hotel.mkhotelTypeId;
            umrahFormDetail.mkInputId = selected_makkah1Hotel.mkhotelName;
        }

        if (selected_madinaHotel != null) {
            umrahFormDetail.mdHotelId = selected_madinaHotel.hotelId;
            umrahFormDetail.MDhotelType = selected_madinaHotel.mdhotelTypeId;
            umrahFormDetail.mdInputId = selected_madinaHotel.mdhotelName;
        }

        if (selected_makkah2Hotel != null) {
            umrahFormDetail.mk_HotelId = selected_makkah2Hotel.hotelId;
            umrahFormDetail.MK_hotelType = selected_makkah2Hotel.mkhotelTypeId;
            umrahFormDetail.mk_InputId = selected_makkah2Hotel.mkhotelName;
        }

        umrahFormDetail.mkRoBB = umrah_makkah1_rooms.getText().toString();
        umrahFormDetail.mdRoBB = umrah_madina_rooms.getText().toString();
        umrahFormDetail.mk_RoBB = umrah_makkah2_rooms.getText().toString();
        umrahFormDetail.adults = umrah_sp_adult.getSelectedItem().toString();
        umrahFormDetail.children = umrah_sp_child.getSelectedItem().toString();
        umrahFormDetail.infant = umrah_sp_infant.getSelectedItem().toString();
        umrahFormDetail.mkcheckIn = umrah_makkah1_checkin.getText().toString();
        umrahFormDetail.mkcheckOut = umrah_makkah1_checkout.getText().toString();
        umrahFormDetail.md_checkIn = umrah_madina_checkin.getText().toString();
        umrahFormDetail.md_checkOut = umrah_madina_checkout.getText().toString();
        umrahFormDetail.mk_checkIn = umrah_makkah2_checkin.getText().toString();
        umrahFormDetail.mk_checkOut = umrah_makkah2_checkout.getText().toString();
        umrahFormDetail.mkDoubleroom = umrah_makkah1_2rooms.getSelectedItem().toString();
        umrahFormDetail.mdDoubleroom = umrah_madina_2rooms.getSelectedItem().toString();
        umrahFormDetail.mk_Double_room = umrah_makkah2_2rooms.getSelectedItem().toString();
        umrahFormDetail.mkTripleroom = umrah_makkah1_3rooms.getSelectedItem().toString();
        umrahFormDetail.mdTripleroom = umrah_madina_3rooms.getSelectedItem().toString();
        umrahFormDetail.mk_Triple_room = umrah_makkah2_3rooms.getSelectedItem().toString();
        umrahFormDetail.mkQuadroom = umrah_makkah1_4rooms.getSelectedItem().toString();
        umrahFormDetail.mdQuadroom = umrah_madina_4rooms.getSelectedItem().toString();
        umrahFormDetail.mk_Quad_room = umrah_makkah2_4rooms.getSelectedItem().toString();
        umrahFormDetail.mktotal_nights = umrah_makkah1_nights.getText().toString();
        umrahFormDetail.md_total_nights = umrah_madina_nights.getText().toString();
        umrahFormDetail.mk_total_nights = umrah_makkah2_nights.getText().toString();
        umrahFormDetail.TotalNRoom = umrah_total_rooms.getText().toString();
        umrahFormDetail.total_amount = umrah_total_price.getText().toString();
        umrahFormDetail.total_nights = umrah_total_nights.getText().toString();

        if (umrah_travel_toggle.isChecked()) {
            umrahFormDetail.sector = sectorId;
            umrahFormDetail.vehicleId = vehicleId;
            umrahFormDetail.total_grant_vehicle_price = umrah_travel_price.getText().toString();
        }

        if (umrah_visa_yes.isChecked()) {
            umrahFormDetail.visa = "1";
            umrahFormDetail.db_visa_price = String.valueOf(visaPrice);
        } else umrahFormDetail.visa = "0";

        umrahFormDetail.mktotal_grant_price = umrah_makkah1_price.getText().toString();
        umrahFormDetail.mdtotal_grant_price = umrah_madina_price.getText().toString();
        umrahFormDetail.mk_total_grant_price = umrah_makkah2_price.getText().toString();

        return umrahFormDetail;
    }

    public void setTravelerAmount(String travelPrice) {
        totalTravelPrice = Integer.parseInt(travelPrice);
        umrah_travel_price.setText(travelPrice);
        calculateGrandTotal();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showUmrahDialog(final String form_html) {
        final Dialog dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_umrah_form, Gravity.CENTER);
        dialog.show();

        // Find Views
        final WebView webView = (WebView) dialog.findViewById(R.id.webView_umrah_form);
        final View btn_back_umrah_form = dialog.findViewById(R.id.btn_back_umrah_form);
        btn_umrah_print = (RippleView) dialog.findViewById(R.id.btn_umrah_print);
        final RippleView btn_umrah_email = (RippleView) dialog.findViewById(R.id.btn_umrah_email);
        final RippleView btn_umrah_save = (RippleView) dialog.findViewById(R.id.btn_umrah_save);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            WebView.enableSlowWholeDocumentDraw();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setInitialScale(100);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setInitialScale(1);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.loadData(form_html, "text/html", null);

        // region Listener to capture image from webView
        webView.setPictureListener(new WebView.PictureListener() {
            @Override
            public void onNewPicture(WebView view, final Picture picture) {
                if (form_img_bytes != null) return;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (picture == null) umrahPicture = webView.capturePicture();
                        if (picture == null && umrahPicture == null) return;
                        try {
                            PictureDrawable drawable = picture != null ?
                                    new PictureDrawable(picture) : new PictureDrawable(umrahPicture);
                            Bitmap bitmap = PhoneFunctionality.pictureDrawableToBitmap(drawable);
                            form_img_bytes = PhoneFunctionality.bitmapToBytes(bitmap);
                            if (form_img_bytes != null && FileHelper.createMainDirectory(mainActivity)) {
                                FileHelper.writeToFile(FileHelper.getDirectory(mainActivity),
                                        docName + FileHelper.fileExtPNG, form_img_bytes);
                                PhoneFunctionality.makeToast(mainActivity, getString(R.string.umrah_form_saved));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);
            }
        });
        // endregion

        // Back Button
        btn_back_umrah_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (form_img_bytes != null) dialog.dismiss();
            }
        });


        // region Print Button
        btn_umrah_print.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                selected_btn = v;
                if (FileHelper.createMainDirectory(mainActivity)) {
                    if (outputFile.exists()) {
                        // Google Cloud Print
                        new GoogleCloudPrintHelper(mainActivity, outputFile, docName).showGCPDialog();
                    } else {
                        new PDFConverterHelper(UmrahDesignFragment.this, outputFile, form_html,
                                getString(R.string.umrah_form), docName).execute();
                    }
                } else
                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.directory_failed));
            }
        });
        // endregion

        // region Email Button
        btn_umrah_email.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                final Dialog dialog = DialogHelper.createCustomDialog(mainActivity,
                        R.layout.dialog_umrah_form_email, Gravity.CENTER);
                dialog.show();

                final EditText umrah_send_email = (EditText) dialog.findViewById(R.id.umrah_send_email);
                ImageButton umrah_send_email_clear = (ImageButton) dialog.findViewById(R.id.umrah_send_email_clear);
                LinearLayout btn_umrah_send_mail = (LinearLayout) dialog.findViewById(R.id.btn_umrah_send_mail);
                LinearLayout btn_umrah_cancel_mail = (LinearLayout) dialog.findViewById(R.id.btn_umrah_cancel_mail);

                // Clear Email EditText Button
                umrah_send_email_clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        umrah_send_email.setText("");
                        umrah_send_email.requestFocus();
                    }
                });

                // Back Button
                btn_umrah_cancel_mail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Send Email Button
                btn_umrah_send_mail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email_addresses = umrah_send_email.getText().toString();
                        if (!email_addresses.equals("")) {
                            dialog.dismiss();
                            sendEmail(form_html, email_addresses);
                        } else
                            umrah_send_email.setError(getString(R.string.error_field_required));
                    }
                });
            }
        });
        // endregion

        // region Save Button
        btn_umrah_save.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                selected_btn = v;
                if (FileHelper.createMainDirectory(mainActivity)) {
                    if (outputFile.exists()) {
                        DialogInterface.OnClickListener positive_listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new PDFConverterHelper(UmrahDesignFragment.this, outputFile,
                                        form_html, getString(R.string.umrah_form), docName).execute();
                            }
                        };
                        DialogHelper.createConfirmDialog(getContext(), getString(R.string.file_already_exist_title),
                                getString(R.string.file_already_exist_msg), positive_listener, null);
                    } else {
                        new PDFConverterHelper(UmrahDesignFragment.this, outputFile,
                                form_html, getString(R.string.umrah_form), docName).execute();
                    }
                } else
                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.directory_failed));
            }
        });
        // endregion
    }

    private void sendEmail(String msg, String email_addresses) {
        String sender, bcc;
        AgentSession agentSession = PreferenceHelper.getAgentSession(mainActivity);
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            sender = getString(R.string.rt_email_address);
            bcc = "";
        } else {
            sender = agentSession.AGENT_EMAIL;
            bcc = getString(R.string.rt_email_address);
        }

        new SendEmail(UmrahDesignFragment.this, sender, email_addresses, "", bcc,
                getString(R.string.mail_umrah_subject), msg, null).execute();
    }
}
