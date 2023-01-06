package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.GetPackagesFromURL;
import com.visionxoft.abacus.rehmantravel.utils.BitmapCacheHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class VisaStudyCountryAdapter extends BaseAdapter {

    private Fragment fragment;
    private List<ImageTag> imageTags;
    private LayoutInflater inflater;
    private String path;
    private BitmapCacheHelper cacheHelper;
    //private int img_width, img_height, padding;
    //private BitmapFactory.Options options;


    public VisaStudyCountryAdapter(Fragment fragment, String url, List<ImageTag> imageTags) {
        this.fragment = fragment;
        this.path = url;
        this.imageTags = imageTags;
        this.inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cacheHelper = new BitmapCacheHelper();
        //DisplayMetrics display = new DisplayMetrics();
        //activity.getWindowManager().getDefaultDisplay().getMetrics(display);
        //img_width = display.widthPixels;
        //img_height = display.heightPixels - activity.getResources().getDimensionPixelSize(R.dimen.actionBarSize);
        //padding = (int) PhoneFunctionality.pxFromDp(activity, 6);
        //options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.RGB_565;
        //options.inDither = true;
    }

    @Override
    public int getCount() {
        return imageTags.size();
    }

    @Override
    public Object getItem(int position) {
        return imageTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            try {
                convertView = inflater.inflate(R.layout.row_visa_study_country, parent, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final ImageTag imageTag = (ImageTag) getItem(position);

        final Bitmap bitmap = cacheHelper.getBitmapFromMemCache(String.valueOf(position));
        if (bitmap != null) {
            holder.img_country_visa_study.setImageBitmap(bitmap);
            holder.progress_country_visa_study.setVisibility(View.GONE);
        } else {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return BitmapFactory.decodeStream((InputStream) new URL(path + imageTag.src).getContent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        cacheHelper.addBitmapToMemoryCache(String.valueOf(position), bitmap);
                        holder.img_country_visa_study.setImageBitmap(bitmap);
                        holder.progress_country_visa_study.setVisibility(View.GONE);
                    }
                }
            }.execute();
        }

        //Bitmap src_bitmap = BitmapFactory.decodeResource(activity.getResources(), pic_srcs[position], options);
        //Bitmap bitmap = Bitmap.createScaledBitmap(src_bitmap, img_width, img_height, true);

        holder.tv_country_visa_study.setText(imageTag.alt);
        holder.item_country_visa_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] splitter = imageTag.extra.split("/");
                String id = splitter[splitter.length - 1];
                IntentHelper.addObjectForKey(imageTag.alt, "visa_country_name");
                IntentHelper.addObjectForKey(id, "visa_country_id");
                new GetPackagesFromURL(fragment, path + imageTag.extra).execute();
            }
        });
        return convertView;
    }

    public class ViewHolder {
        protected final TextView tv_country_visa_study;
        protected final ImageView img_country_visa_study;
        protected final View item_country_visa_study, progress_country_visa_study;

        ViewHolder(View v) {
            tv_country_visa_study = (TextView) v.findViewById(R.id.tv_country_visa_study);
            img_country_visa_study = (ImageView) v.findViewById(R.id.img_country_visa_study);
            item_country_visa_study = v.findViewById(R.id.item_country_visa_study);
            progress_country_visa_study = v.findViewById(R.id.progress_country_visa_study);
        }
    }


}
