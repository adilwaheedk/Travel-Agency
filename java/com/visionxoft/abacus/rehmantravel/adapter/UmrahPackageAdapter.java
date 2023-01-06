package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.List;

public class UmrahPackageAdapter extends PagerAdapter {

    private List<ImageTag> imageTags;
    private LayoutInflater inflater;
    private String path;

    public UmrahPackageAdapter(MainActivity mainActivity, String url, List<ImageTag> imageTags) {
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

        View view = inflater.inflate(R.layout.page_umrah_package, null);
        ViewHolder holder = new ViewHolder(view);
        ImageTag imageTag = imageTags.get(position);

        holder.webView.setVisibility(View.GONE);
        holder.tv_img_alt.setVisibility(View.GONE);
        holder.tv_img_alt.setText(imageTag.alt);

        holder.webView.setWebViewClient(new ImageWebViewClient(holder));
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

        public ImageWebViewClient(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            holder.webView.setBackgroundColor(Color.TRANSPARENT);
            holder.progress_package.setVisibility(View.GONE);
            holder.tv_loading_img.setVisibility(View.GONE);
            holder.tv_img_alt.setVisibility(View.VISIBLE);
            holder.webView.setVisibility(View.VISIBLE);
        }
    }

    private class ViewHolder {

        protected TextView tv_img_alt, tv_loading_img;
        protected WebView webView;
        protected View progress_package;

        ViewHolder(View v) {
            tv_img_alt = (TextView) v.findViewById(R.id.tv_img_alt);
            tv_loading_img = (TextView) v.findViewById(R.id.tv_loading_img);
            webView = (WebView) v.findViewById(R.id.web_view_package);
            progress_package = v.findViewById(R.id.progress_package);
            webView = PhoneFunctionality.setWebViewSettings(webView);
        }
    }
}
