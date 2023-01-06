package com.visionxoft.abacus.rehmantravel.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.CalendarFragment;
import com.visionxoft.abacus.rehmantravel.model.Constants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Device related and commonly used functions
 */
public class PhoneFunctionality {

    /**
     * Send Text message to specific phone and register receivers to check sms sent and delivered status
     *
     * @param receiver Number of receiver
     * @param message  Text message to be send
     */

    public static void sendMessage(final String receiver, final String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendMultipartTextMessage(receiver, null, sms.divideMessage(message), null, null);
    }

    /**
     * Copy text to Clipboard
     *
     * @param context Context
     * @param label   Label of data to clip
     * @param text    Actual text to clip
     */
    public static void copyToClipboard(Context context, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Send Email using other email application
     *
     * @param context Context
     * @param mailTo  Email address of receiver
     * @param subject Email subject
     * @param body    Email body
     */
    public static void sendEmail(Context context, String mailTo, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo});
        emailIntent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.select_email_app)));
    }

    /**
     * Hide visible keyboard
     *
     * @param activity Parent activity context
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Vibrate device
     *
     * @param context Context
     */
    public static void vibrateMobile(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(Constants.VIBRATE_TIME);
    }

    public static void showNotification(Class<?> parentClass, Context context, int notifID,
                                        String title, String msg, boolean auto_cancel) {
        showNotification(parentClass, context, notifID,
                R.drawable.ic_stat_r_logo_white, title, msg, auto_cancel);
    }

    /**
     * Display notification
     *
     * @param parentClass Parent Class
     * @param context     Context
     * @param notifID     Unique notification ID
     * @param icon        Notification icon
     * @param title       Notification title text
     * @param msg         Notification msg text
     * @param auto_cancel True if notification is cancelable, else not
     */
    private static void showNotification(Class<?> parentClass, Context context, int notifID, int icon,
                                         String title, String msg, boolean auto_cancel) {
        Intent intent = new Intent(context, parentClass);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notif;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notif = new Notification.Builder(context).setContentTitle(title)
                    .setContentText(msg).setSmallIcon(icon).setContentIntent(pi).build();
        } else {
            notif = new Notification.Builder(context)
                    .setContentTitle(title).setContentText(msg).setSmallIcon(icon).getNotification();
        }
        NotificationManager notifMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notif.flags |= auto_cancel ? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_NO_CLEAR;
        notifMgr.notify(notifID, notif);
    }

    /**
     * Hide notification
     *
     * @param context Context
     * @param notifID Unique notification ID
     */
    public static void hideNotification(Context context, int notifID) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notifID);
    }

    /**
     * Animate button
     *
     * @param view View to animate
     */
    public static void buttonAnim(View view) {
        //view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.anim_click));
        YoYo.with(Techniques.Flash).duration(400).playOn(view);
    }

    /**
     * Shake animation on error
     *
     * @param view View to animate
     */
    public static void errorAnimation(View view) {
        YoYo.with(Techniques.Shake).duration(500).playOn(view);
    }

    /**
     * Display toast message
     *
     * @param activity Parent activity class
     * @param msg      Message to display
     */
    public static void makeToast(Activity activity, String msg) {
        makeToast(activity, msg, false);
    }

    /**
     * Display toast message
     *
     * @param activity     Parent activity class
     * @param msg          Message to display
     * @param longDuration True if display duration is long, else false
     */
    public static void makeToast(Activity activity, String msg, boolean longDuration) {
        if (activity != null && !msg.equals("")) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                View layout = activity.getLayoutInflater().inflate(R.layout.layout_toast,
                        (ViewGroup) activity.findViewById(R.id.toast_linear_layout));
                ((TextView) layout.findViewById(R.id.toast_msg)).setText(msg);
                Toast toast = new Toast(activity.getApplicationContext());
                if (longDuration) toast.setDuration(Toast.LENGTH_LONG);
                else toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setView(layout);
                toast.show();
            } else {
                Toast.makeText(activity, msg, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void showCalendar(final Fragment fragment, final View selectedView,
                                    final Calendar calendar_date, Calendar min_date, Calendar max_date) {
        showCalendar(fragment, selectedView, calendar_date, min_date, max_date, false);
    }

    /**
     * Display full screen Calendar in fragment
     *
     * @param fragment      Parent fragment
     * @param selectedView  TextView to display selected date text
     * @param calendar_date Current date to display on Calendar
     * @param min_date      Minimum date to display on Calendar
     * @param max_date      Maximum date to display on Calendar
     */
    public static void showCalendar(final Fragment fragment, final View selectedView, final Calendar calendar_date,
                                    Calendar min_date, Calendar max_date, boolean do_focus) {
        if (calendar_date != null && min_date != null && max_date != null) {
            PhoneFunctionality.hideKeyboard(fragment.getActivity());
            IntentHelper.addObjectForKey(selectedView, "selected_view");
            IntentHelper.addObjectForKey(do_focus, "do_focus");
            IntentHelper.addObjectForKey(calendar_date, "calendar_date");
            IntentHelper.addObjectForKey(min_date, "min_date");
            IntentHelper.addObjectForKey(max_date, "max_date");
            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.show(fragment.getFragmentManager(), "dialog");
        }
    }

    /**
     * Toggle visibility of text normally on passwords
     *
     * @param selectedView TextView to toggle visibility
     * @param button       Button view to change background
     */
    public static void toggleVisibility(EditText selectedView, View button) {
        if (selectedView.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            selectedView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            button.setBackgroundResource(R.drawable.ic_visibility_off_black_18dp);
        } else {
            selectedView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            button.setBackgroundResource(R.drawable.ic_visibility_black_18dp);
        }
        selectedView.setSelection(selectedView.getText().length());
    }

    /**
     * Convert drawable to bitmap
     *
     * @param pictureDrawable Picture to convert
     * @return Picture in bitmap format
     * @throws IllegalArgumentException
     */
    public static Bitmap pictureDrawableToBitmap(PictureDrawable pictureDrawable) throws
            IllegalArgumentException {
        Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),
                pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bitmap;
    }

    /**
     * Get drawable from URL image
     *
     * @param url URL to get image
     * @return Drawable object
     */
    public static Drawable loadImageFromURL(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert bitmap to Array of bytes
     *
     * @param bitmap Bitmap object to convert
     * @return Array of bytes
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Convert pixel to density independent pixels
     *
     * @param context Context
     * @param px      pixel value
     * @return dp value
     */
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * Convert density independent pixels to pixel
     *
     * @param context Context
     * @param dp      dp value
     * @return pixel value
     */
    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    /**
     * Get all available currencies
     *
     * @return Set of Currency object
     */
    public static Set<Currency> getAllCurrencies() {
        Set<Currency> currencySet = new HashSet<>();
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale loc : locales) {
            try {
                currencySet.add(Currency.getInstance(loc));
            } catch (Exception ex) {
                // Locale not found
            }
        }
        return currencySet;
    }

    /**
     * Set Web View settings
     *
     * @param webView WebView object
     * @return Same object with settings
     */
    public static WebView setWebViewSettings(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        return webView;
    }
}
