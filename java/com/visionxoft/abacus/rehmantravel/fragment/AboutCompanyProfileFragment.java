package com.visionxoft.abacus.rehmantravel.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;

/**
 * Display pdf document About Company Profile
 */
public class AboutCompanyProfileFragment extends Fragment {

    private static final String googleDocs = "https://docs.google.com/viewer?url=";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_about_company_profile, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle("Company Profile");
        mainActivity.setToolbarSubTitle(null);

        // Setup webView
        WebView webView_company_profile = (WebView) rootView.findViewById(R.id.webView_company_profile);
        final View loading_progress_bar = rootView.findViewById(R.id.loading_progress_bar);
        final View loading_title = rootView.findViewById(R.id.loading_title);

        webView_company_profile.setVisibility(View.GONE);
        loading_progress_bar.setVisibility(View.VISIBLE);
        loading_title.setVisibility(View.VISIBLE);

        webView_company_profile.getSettings().setJavaScriptEnabled(true);
        webView_company_profile.getSettings().setLoadWithOverviewMode(true);
        webView_company_profile.getSettings().setBuiltInZoomControls(true);
        webView_company_profile.getSettings().setDisplayZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webView_company_profile.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            webView_company_profile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView_company_profile.setWebViewClient(
                new CompanyProfileWebClient(webView_company_profile, loading_progress_bar, loading_title));

        webView_company_profile.loadUrl(googleDocs + getString(R.string.rt_profile_pdf_path));

        return rootView;
    }

    /**
     * WebView client class to control behavior of webView
     */
    private final class CompanyProfileWebClient extends WebViewClient {

        private WebView webView_company_profile;
        private View loading_progress_bar, loading_title;

        public CompanyProfileWebClient(WebView webView_company_profile, View loading_progress_bar, View loading_title) {
            this.webView_company_profile = webView_company_profile;
            this.loading_progress_bar = loading_progress_bar;
            this.loading_title = loading_title;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webView_company_profile.setVisibility(View.VISIBLE);
            loading_progress_bar.setVisibility(View.GONE);
            loading_title.setVisibility(View.GONE);
        }
    }
}
