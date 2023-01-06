package com.visionxoft.abacus.rehmantravel.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;

/**
 * Helper class to manage different types of dialog box
 */
public class DialogHelper {

    public static Dialog createCustomDialog(Context context, int contentViewId, int gravity) {
        return createCustomDialog(context, contentViewId, gravity, true);
    }

    /**
     * Create custom dialog box
     *
     * @param context       Context
     * @param contentViewId Id of layout view
     * @return Dialog object
     */
    public static Dialog createCustomDialog(Context context, int contentViewId, int gravity, boolean cancelAble) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelAble);
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomToTopAnimation;
        dialog.getWindow().setGravity(gravity);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(contentViewId);
        return dialog;
    }

    /**
     * Create progress/loading dialog box
     *
     * @param context    Context
     * @param titleId    String id for name of dialog
     * @param messageId  String id for message of dialog
     * @param cancelAble true if dialog is cancelable, else false
     * @return Dialog object
     */
    public static Dialog createProgressDialog(Context context, int titleId, int messageId,
                                              boolean cancelAble) {
        final Dialog dialog = createCustomDialog(context, R.layout.dialog_progress, Gravity.CENTER);
        TextView progress_title = (TextView) dialog.findViewById(R.id.progress_title);
        TextView progress_msg = (TextView) dialog.findViewById(R.id.progress_msg);
        View progress_cancel = dialog.findViewById(R.id.progress_cancel);
        dialog.setCancelable(cancelAble);
        if (!cancelAble) progress_cancel.setVisibility(View.GONE);
        else
            progress_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

        progress_title.setText(titleId);
        progress_msg.setText(messageId);
        dialog.show();
        return dialog;
    }

    /**
     * Create confirmation dialog box
     *
     * @param context           Context
     * @param title             String for name of dialog
     * @param pos_btn_title     Positive button name
     * @param neg_btn_title     Negative button name
     * @param message           String for message of dialog
     * @param positive_listener Positive listener of dialog
     * @param negative_listener Negative listener of dialog
     * @return Dialog object
     */
    public static Dialog createConfirmDialog(Context context, String title, String message,
                                             String pos_btn_title, String neg_btn_title,
                                             DialogInterface.OnClickListener positive_listener,
                                             DialogInterface.OnClickListener negative_listener) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
                .setCancelable(false).setMessage(message)
                .setPositiveButton(pos_btn_title, positive_listener)
                .setNegativeButton(neg_btn_title, negative_listener).show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomToTopAnimation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_form);
        return dialog;
    }


    /**
     * Create confirmation dialog box
     *
     * @param context           Context
     * @param title             String for name of dialog
     * @param message           String for message of dialog
     * @param positive_listener Positive listener of dialog
     * @param negative_listener Negative listener of dialog
     * @return Dialog object
     */
    public static Dialog createConfirmDialog(Context context, String title, String message,
                                             DialogInterface.OnClickListener positive_listener,
                                             DialogInterface.OnClickListener negative_listener) {
        return createConfirmDialog(context, title, message, "YES", "NO", positive_listener, negative_listener);
    }
}
