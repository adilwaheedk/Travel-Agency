package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.GetEuropeTourInfo;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.List;

public class EuropeToursAdapter extends PagerAdapter {

    private Fragment fragment;
    private List<ImageTag> imageTags;
    private LayoutInflater inflater;
    private String path;
    private int tourType;

    public EuropeToursAdapter(Fragment fragment, String url, List<ImageTag> imageTags, int tourType) {
        this.fragment = fragment;
        this.path = url;
        this.imageTags = imageTags;
        this.inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tourType = tourType;
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

        View view = inflater.inflate(R.layout.page_europe_tours, null);
        ViewHolder holder = new ViewHolder(view);
        ImageTag imageTag = imageTags.get(position);

        holder.webView.setVisibility(View.GONE);
        holder.tv_img_description.setVisibility(View.GONE);
        holder.btn_eur_tour_detail.setVisibility(View.GONE);
        holder.tv_img_description.setText(imageTag.alt);

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
        private ImageTag imageTag;

        public ImageWebViewClient(ViewHolder holder, ImageTag imageTag) {
            this.holder = holder;
            this.imageTag = imageTag;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            holder.webView.setBackgroundColor(Color.TRANSPARENT);
            holder.progress_package.setVisibility(View.GONE);
            holder.tv_loading_img.setVisibility(View.GONE);
            holder.tv_img_description.setVisibility(View.VISIBLE);
            holder.webView.setVisibility(View.VISIBLE);
            holder.btn_eur_tour_detail.setVisibility(View.VISIBLE);

            holder.btn_eur_tour_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetEuropeTourInfo(fragment, imageTag, tourType).execute();
                }
            });
        }
    }

    private class ViewHolder {

        protected View tv_loading_img, progress_package, btn_eur_tour_detail;
        protected TextView tv_img_description;
        protected WebView webView;

        ViewHolder(View v) {
            tv_img_description = (TextView) v.findViewById(R.id.tv_img_alt);
            webView = (WebView) v.findViewById(R.id.web_view_package);
            tv_loading_img = v.findViewById(R.id.tv_loading_img);
            progress_package = v.findViewById(R.id.progress_package);
            btn_eur_tour_detail = v.findViewById(R.id.btn_eur_tour_detail);
            webView = PhoneFunctionality.setWebViewSettings(webView);
        }
    }
}
