package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.AllPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.EuropeToursFragment;
import com.visionxoft.abacus.rehmantravel.fragment.HajjPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.VisaStudyCountryFragment;
import com.visionxoft.abacus.rehmantravel.fragment.VisaStudyPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.VisaVisitPackagesFragment;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GetPackagesFromURL extends AsyncTask<Void, Void, Integer> {

    private Fragment fragment;
    private Activity activity;
    private Dialog dialog;
    private String url;
    private List<ImageTag> imageTags;

    public GetPackagesFromURL(Fragment fragment, String url) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.url = url;
        imageTags = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.fetch_packages,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!GetPackagesFromURL.this.isCancelled())
                    GetPackagesFromURL.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        ImageTag imageTag;
        try {
            Document doc = Jsoup.connect(url).get();
            if (url.equals(activity.getString(R.string.hajj_package_url))) {
                Elements top_destinations = doc.select("div[class='row top-destinations'");
                for (Element top_destination : top_destinations) {
                    Elements els = top_destination.select("div[class='col-sm-4']");
                    for (Element el : els) {
                        imageTag = new ImageTag();
                        imageTag.alt = el.select("b").text();
                        imageTag.src = el.select("img[src^='atq_images/hajj-images/']").attr("src");
                        imageTag.extra = el.select("span").text();
                        imageTags.add(imageTag);
                    }
                }
            } else if (url.equals(activity.getString(R.string.umrah_package_url))) {
                Elements els = doc.select(".panel-body").select("img");
                for (Element el : els) {
                    imageTag = new ImageTag();
                    imageTag.alt = el.attr("alt") != null ? el.attr("alt") : "Umrah Package";
                    imageTag.src = el.attr("src");
                    imageTags.add(imageTag);
                }
            } else if (url.equals(activity.getString(R.string.visa_visit_url))) {
                Elements els = doc.select(".panel-body").select("a[href='#visit_visa']");
                for (Element el : els) {
                    imageTag = new ImageTag();
                    imageTag.alt = el.select("p").text();
                    imageTag.src = el.select("img").attr("src");
                    imageTag.extra = el.attr("onclick").split("\\(")[1];
                    imageTags.add(imageTag);
                }
            } else if (url.equals(activity.getString(R.string.visa_study_country_url))) {
                Elements els = doc.select(".panel-body").select("a[href^=visa/countryInstitutes/]");
                for (Element el : els) {
                    imageTag = new ImageTag();
                    imageTag.alt = el.select("b").text();
                    imageTag.src = el.select("img").attr("src");
                    imageTag.extra = el.attr("href");
                    imageTags.add(imageTag);
                }
            } else if (url.contains("countryInstitutes")) {
                Elements els = doc.select(".panel-body").select("div[class='thumbnail institute']");
                for (Element el : els) {
                    imageTag = new ImageTag();
                    imageTag.alt = el.select("div[class^=uni-title]").text();
                    imageTag.src = el.select("img[src^=studyImages").attr("src");
                    imageTag.extra = el.select("a[onclick^='Details'").attr("id");
                    imageTags.add(imageTag);
                }
            } else if (url.equals(activity.getString(R.string.all_package_url))) {
                Elements els = doc.select("div[class='visa-destinations']")
                        .select("div[class='col-lg-4 col-md-4 col-sm-4 col-xs-6']");
                for (Element el : els) {
                    imageTag = new ImageTag();
                    imageTag.alt = el.select("img[src^=pkgImages").attr("alt");
                    imageTag.src = el.select("img[src^=pkgImages").attr("src");
                    imageTag.extra = el.select("a[onclick^='ViewPkgPopups'").attr("id");
                    imageTags.add(imageTag);
                }
            } else if (url.equals(activity.getString(R.string.eur_tour_1_url))) {
                Elements primary_panels = doc.select("div[class='panel panel-primary']");
                for (Element primary_panel : primary_panels) {
                    if (primary_panel.select("div[class='panel-heading'").text().equals("Europe Tours")) {
                        Elements els = primary_panel.select("div[class='col-sm-3']");
                        for (Element el : els) {
                            imageTag = new ImageTag();
                            imageTag.alt = "";
                            imageTag.src = el.select("img[src^=EuropeToursImages").attr("src");
                            imageTag.extra = el.select("a[onclick^='showCampDetail'").attr("id");
                            imageTags.add(imageTag);
                        }
                    }
                }
            } else if (url.equals(activity.getString(R.string.eur_tour_2_url))) {
                Elements primary_panels = doc.select("div[class='panel panel-primary']");
                for (Element primary_panel : primary_panels) {
                    if (primary_panel.select("div[class='panel-heading'").text().equals("Poland Tours")) {
                        Elements els = primary_panel.select("div[class='col-sm-3']");
                        for (Element el : els) {
                            imageTag = new ImageTag();
                            imageTag.alt = "";
                            imageTag.src = el.select("img[src^=EuropeToursImages").attr("src");
                            imageTag.extra = el.select("a[onclick^='showPolandTourDetail'").attr("id");
                            imageTags.add(imageTag);
                        }
                    }
                }
            } else if (url.equals(activity.getString(R.string.eur_tour_3_url))) {
                Elements primary_panels = doc.select("div[class='panel panel-primary']");
                for (Element primary_panel : primary_panels) {
                    if (primary_panel.select("div[class='panel-heading'").text().equals("Poland Tours")) {
                        Elements els = primary_panel.select("div[class='col-sm-3']");
                        for (Element el : els) {
                            imageTag = new ImageTag();
                            imageTag.alt = "";
                            imageTag.src = el.select("img[src^=EuropeToursImages").attr("src");
                            imageTag.extra = el.select("a[onclick^='showPolandTourPackageDetail'").attr("id");
                            imageTags.add(imageTag);
                        }
                    }
                }
            } else if (url.equals(activity.getString(R.string.eur_tour_4_url))) {
                Elements primary_panels = doc.select("div[class='panel panel-primary']");
                for (Element primary_panel : primary_panels) {
                    if (primary_panel.select("div[class='panel-heading'").text().equals("Austria Tours")) {
                        Elements els = primary_panel.select("div[class='col-sm-3']");
                        for (Element el : els) {
                            imageTag = new ImageTag();
                            imageTag.alt = "";
                            imageTag.src = el.select("img[src^=EuropeToursImages").attr("src");
                            imageTag.extra = el.select("a[onclick^='showAustriaTourPackageDetail'").attr("id");
                            imageTags.add(imageTag);
                        }
                    }
                }
            } else if (url.equals(activity.getString(R.string.eur_tour_5_url))) {
                Elements primary_panels = doc.select("div[class='panel panel-primary']");
                for (Element primary_panel : primary_panels) {
                    if (primary_panel.select("div[class='panel-heading'").text().equals("Germany Tours")) {
                        Elements els = primary_panel.select("div[class='col-sm-3']");
                        for (Element el : els) {
                            imageTag = new ImageTag();
                            imageTag.alt = "";
                            imageTag.src = el.select("img[src^=EuropeToursImages").attr("src");
                            imageTag.extra = el.select("a[onclick^='showCampDetail'").attr("id");
                            imageTags.add(imageTag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
        return 1;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        PhoneFunctionality.makeToast(activity, activity.getString(R.string.fetch_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        dialog.dismiss();
        if (feedback == 1) {
            if (imageTags.size() > 0) {
                IntentHelper.addObjectForKey(imageTags, "imageTags");
                if (url.equals(activity.getString(R.string.hajj_package_url))) {
                    FragmentHelper.replaceFragment(fragment, new HajjPackagesFragment(),
                            activity.getString(R.string.hajj_package_tag));
                } else if (url.equals(activity.getString(R.string.umrah_package_url))) {
                    FragmentHelper.replaceFragment(fragment, new UmrahPackagesFragment(),
                            activity.getString(R.string.umrah_package_tag));
                } else if (url.equals(activity.getString(R.string.visa_visit_url))) {
                    FragmentHelper.replaceFragment(fragment, new VisaVisitPackagesFragment(),
                            activity.getString(R.string.visa_visit_tag));
                } else if (url.equals(activity.getString(R.string.visa_study_country_url))) {
                    FragmentHelper.replaceFragment(fragment, new VisaStudyCountryFragment(),
                            activity.getString(R.string.visa_study_tag));
                } else if (url.contains("countryInstitutes")) {
                    FragmentHelper.replaceFragment(fragment, new VisaStudyPackagesFragment(),
                            activity.getString(R.string.visa_study_packages_tag));
                } else if (url.equals(activity.getString(R.string.all_package_url))) {
                    FragmentHelper.replaceFragment(fragment, new AllPackagesFragment(),
                            activity.getString(R.string.all_package_tag));
                } else if (url.equals(activity.getString(R.string.eur_tour_1_url))) {
                    IntentHelper.addObjectForKey(EuropeToursFragment.SWISS_STD_CAMP, "europeTourType");
                    FragmentHelper.replaceFragment(fragment, new EuropeToursFragment(),
                            activity.getString(R.string.europe_tours_tag));
                } else if (url.equals(activity.getString(R.string.eur_tour_2_url))) {
                    IntentHelper.addObjectForKey(EuropeToursFragment.POLAND_TOUR, "europeTourType");
                    FragmentHelper.replaceFragment(fragment, new EuropeToursFragment(),
                            activity.getString(R.string.europe_tours_tag));
                } else if (url.equals(activity.getString(R.string.eur_tour_3_url))) {
                    IntentHelper.addObjectForKey(EuropeToursFragment.POLAND_TOUR_PKG, "europeTourType");
                    FragmentHelper.replaceFragment(fragment, new EuropeToursFragment(),
                            activity.getString(R.string.europe_tours_tag));
                } else if (url.equals(activity.getString(R.string.eur_tour_4_url))) {
                    IntentHelper.addObjectForKey(EuropeToursFragment.AUSTRIA_TOUR_PKG, "europeTourType");
                    FragmentHelper.replaceFragment(fragment, new EuropeToursFragment(),
                            activity.getString(R.string.europe_tours_tag));
                } else if (url.equals(activity.getString(R.string.eur_tour_5_url))) {
                    IntentHelper.addObjectForKey(EuropeToursFragment.GERMANY_TOUR, "europeTourType");
                    FragmentHelper.replaceFragment(fragment, new EuropeToursFragment(),
                            activity.getString(R.string.europe_tours_tag));
                }
            } else
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_package_found));
        } else
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));

    }
}
