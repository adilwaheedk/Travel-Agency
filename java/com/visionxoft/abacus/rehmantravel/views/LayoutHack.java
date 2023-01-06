package com.visionxoft.abacus.rehmantravel.views;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Layout Hack class to set layout values programmatically
 */
public class LayoutHack {

    /**
     * Set nested listView's height to match_parent
     *
     * @param listAdapter List adapter of child listView
     * @param listView    Desired child listView
     * @return ListView of new height params
     */
    public static ListView setListViewHeightBasedOnChildren(ListAdapter listAdapter, ListView listView) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        return listView;
    }

    /**
     * Get correct view of item (not visible item) in listView
     *
     * @param pos      Position of item in listView
     * @param listView ListView itself
     * @return Desired child view/item of listView
     */
    public static View getViewByPosition(int pos, ListView listView) {
        try {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                return listView.getChildAt(pos - firstListItemPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static int getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static void toggleAnimation(final View view, boolean upward) {
        //final int screenHeight = LayoutHack.getScreenHeight(activity);
        if (upward) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "TranslationY", 160f, 0);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "TranslationY", 0, 160f);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
    }
}
