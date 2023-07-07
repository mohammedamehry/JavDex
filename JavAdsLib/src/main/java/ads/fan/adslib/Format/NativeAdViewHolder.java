package ads.fan.adslib.Format;



import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.adview.AppLovinAdView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.vungle.warren.AdEventListener;


import java.util.ArrayList;
import java.util.List;

import ads.fan.adslib.Config.Config;
import ads.fan.adslib.Config.NativeTemplateStyle;
import ads.fan.adslib.Config.TemplateView;
import ads.fan.adslib.Config.Tools;
import ads.fan.adslib.R;

public class NativeAdViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "AdNetwork";
    LinearLayout nativeAdViewContainer;

    //AdMob
    MediaView mediaView;
    TemplateView admobNativeAd;
    LinearLayout admobNativeBackground;


    //FAN
    com.facebook.ads.NativeAd fanNativeAd;
    NativeAdLayout fanNativeAdLayout;


    //AppLovin
    FrameLayout applovinNativeAd;
    MaxNativeAdLoader nativeAdLoader;
    MaxAd maxNativeAd;




    public NativeAdViewHolder(View view) {
        super(view);

        nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);

        //AdMob
        admobNativeAd = view.findViewById(R.id.admob_native_ad_container);
        mediaView = view.findViewById(R.id.media_view);
        admobNativeBackground = view.findViewById(R.id.background);


        //FAN
        fanNativeAdLayout = view.findViewById(R.id.fan_native_ad_container);


        //AppLovin
        applovinNativeAd = view.findViewById(R.id.applovin_native_ad_container);

    }

    public void loadNativeAd(Context context,String adMobNativeId, String fanNativeId, String appLovinNativeId, boolean legacyGDPR,String nativeAdStyle) {
        if (Config.myAds.isShowAds()) {
            switch (Config.myAds.getPriorityNative()){
                case "admob":
                    if (admobNativeAd.getVisibility() != View.VISIBLE) {
                        AdLoader adLoader = new AdLoader.Builder(context, adMobNativeId)
                                .forNativeAd(NativeAd -> {
                                    ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.color_native_background_dark));
                                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                    admobNativeAd.setStyles(styles);
                                    admobNativeBackground.setBackgroundResource(R.color.color_native_background_dark);
                                    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                    admobNativeAd.setNativeAd(NativeAd);
                                    admobNativeAd.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                })
                                .withAdListener(new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                                    }
                                })
                                .build();
                        adLoader.loadAd(Tools.getAdRequest((Activity) context, legacyGDPR));
                    } else {
                        Log.d(TAG, "AdMob native ads has been loaded");
                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                    }
                    break;
                case "max":
                    if (applovinNativeAd.getVisibility() != View.VISIBLE) {
                        nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, context);
                        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                            @Override
                            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                // Clean up any pre-existing native ad to prevent memory leaks.
                                if (maxNativeAd != null) {
                                    nativeAdLoader.destroy(maxNativeAd);
                                }

                                // Save ad for cleanup.
                                maxNativeAd = ad;

                                // Add ad view to view.
                                applovinNativeAd.removeAllViews();
                                applovinNativeAd.addView(nativeAdView);
                                applovinNativeAd.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);

                                LinearLayout applovinNativeBackground = nativeAdView.findViewById(R.id.applovin_native_background);
                                applovinNativeBackground.setBackgroundResource(R.color.color_native_background_dark);


                                Log.d(TAG, "Max Native Ad loaded successfully");
                            }

                            @Override
                            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                // We recommend retrying with exponentially higher delays up to a maximum delay
                                loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);

                                Log.d(TAG, "failed to load Max Native Ad with message : " + error.getMessage() + " and error code : " + error.getCode());
                            }

                            @Override
                            public void onNativeAdClicked(final MaxAd ad) {
                                // Optional click callback
                            }
                        });
                            nativeAdLoader.loadAd(createNativeAdViewDark(nativeAdStyle, context));

                    } else {
                        Log.d(TAG, "AppLovin Native ads has been loaded");
                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                    }
                    break;
                case "fan":
                    if (fanNativeAdLayout.getVisibility() != View.VISIBLE) {
                        fanNativeAd = new com.facebook.ads.NativeAd(context, fanNativeId);
                        NativeAdListener nativeAdListener = new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                // Race condition, load() called again before last ad was displayed
                                fanNativeAdLayout.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);
                                if (fanNativeAd != ad) {
                                    return;
                                }
                                // Inflate Native Ad into Container
                                //inflateAd(nativeAd);
                                fanNativeAd.unregisterView();
                                // Add the Ad view into the ad container.
                                LayoutInflater inflater = LayoutInflater.from(context);
                                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                                LinearLayout nativeAdView;

                                switch (nativeAdStyle) {
                                    case "medium":
                                        nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_news_template_view, fanNativeAdLayout, false);
                                        break;
                                    case "small":
                                        nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_radio_template_view, fanNativeAdLayout, false);
                                        break;
                                    default:
                                        nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_medium_template_view, fanNativeAdLayout, false);
                                        break;
                                }
                                fanNativeAdLayout.addView(nativeAdView);

                                // Add the AdOptionsView
                                LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                                AdOptionsView adOptionsView = new AdOptionsView(context, fanNativeAd, fanNativeAdLayout);
                                adChoicesContainer.removeAllViews();
                                adChoicesContainer.addView(adOptionsView, 0);

                                // Create native UI using the ad metadata.
                                TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
                                com.facebook.ads.MediaView nativeAdIcon = nativeAdView.findViewById(R.id.native_ad_icon);
                                TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
                                TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                                Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                                LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                nativeAdTitle.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_primary_text_color));
                                nativeAdSocialContext.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_primary_text_color));
                                sponsoredLabel.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_secondary_text_color));
                                nativeAdBody.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_secondary_text_color));
                                fanNativeBackground.setBackgroundResource(R.color.color_native_background_dark);

                                // Set the Text.
                                nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
                                nativeAdBody.setText(fanNativeAd.getAdBodyText());
                                nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
                                nativeAdCallToAction.setVisibility(fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction());
                                sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation());

                                // Create a list of clickable views
                                List<View> clickableViews = new ArrayList<>();
                                clickableViews.add(nativeAdTitle);
                                clickableViews.add(sponsoredLabel);
                                clickableViews.add(nativeAdIcon);
                                clickableViews.add(nativeAdMedia);
                                clickableViews.add(nativeAdBody);
                                clickableViews.add(nativeAdSocialContext);
                                clickableViews.add(nativeAdCallToAction);

                                // Register the Title and CTA button to listen for clicks.
                                fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews);

                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = fanNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build();
                        fanNativeAd.loadAd(loadAdConfig);
                    } else {
                        Log.d(TAG, "FAN Native Ad has been loaded");
                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                    }
                    break;

            }
        }
    }

    public void loadBackupNativeAd(Context context,String adMobNativeId, String fanNativeId, String appLovinNativeId, boolean legacyGDPR,String nativeAdStyle) {
        if (Config.myAds.isShowAds()) {
            switch (Config.myAds.getPriorityNative()){
                case "admob":
                    if (admobNativeAd.getVisibility() != View.VISIBLE) {
                        AdLoader adLoader = new AdLoader.Builder(context, adMobNativeId)
                                .forNativeAd(NativeAd -> {
                                    ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.color_native_background_dark));
                                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                    admobNativeAd.setStyles(styles);
                                    admobNativeBackground.setBackgroundResource(R.color.color_native_background_dark);
                                    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                    admobNativeAd.setNativeAd(NativeAd);
                                    admobNativeAd.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                })
                                .withAdListener(new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                                    }
                                })
                                .build();
                        adLoader.loadAd(Tools.getAdRequest((Activity) context, legacyGDPR));
                    } else {
                        Log.d(TAG, "AdMob native ads has been loaded");
                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                    }
                    break;
                case "max":
                    if (applovinNativeAd.getVisibility() != View.VISIBLE) {
                        nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, context);
                        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                            @Override
                            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                // Clean up any pre-existing native ad to prevent memory leaks.
                                if (maxNativeAd != null) {
                                    nativeAdLoader.destroy(maxNativeAd);
                                }

                                // Save ad for cleanup.
                                maxNativeAd = ad;

                                // Add ad view to view.
                                applovinNativeAd.removeAllViews();
                                applovinNativeAd.addView(nativeAdView);
                                applovinNativeAd.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);

                                LinearLayout applovinNativeBackground = nativeAdView.findViewById(R.id.applovin_native_background);
                                applovinNativeBackground.setBackgroundResource(R.color.color_native_background_dark);


                                Log.d(TAG, "Max Native Ad loaded successfully");
                            }

                            @Override
                            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                // We recommend retrying with exponentially higher delays up to a maximum delay
                                loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);

                                Log.d(TAG, "failed to load Max Native Ad with message : " + error.getMessage() + " and error code : " + error.getCode());
                            }

                            @Override
                            public void onNativeAdClicked(final MaxAd ad) {
                                // Optional click callback
                            }
                        });
                        nativeAdLoader.loadAd(createNativeAdViewDark(nativeAdStyle, context));

                    } else {
                        Log.d(TAG, "AppLovin Native ads has been loaded");
                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                    }
                    break;
                case "fan":
                    if (fanNativeAdLayout.getVisibility() != View.VISIBLE) {
                        fanNativeAd = new com.facebook.ads.NativeAd(context, fanNativeId);
                        NativeAdListener nativeAdListener = new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                // Race condition, load() called again before last ad was displayed
                                fanNativeAdLayout.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);
                                if (fanNativeAd != ad) {
                                    return;
                                }
                                // Inflate Native Ad into Container
                                //inflateAd(nativeAd);
                                fanNativeAd.unregisterView();
                                // Add the Ad view into the ad container.
                                LayoutInflater inflater = LayoutInflater.from(context);
                                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                                LinearLayout nativeAdView;

                                switch (nativeAdStyle) {
                                    case "medium":
                                        nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_news_template_view, fanNativeAdLayout, false);
                                        break;
                                    case "small":
                                        nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_radio_template_view, fanNativeAdLayout, false);
                                        break;
                                    default:
                                        nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_medium_template_view, fanNativeAdLayout, false);
                                        break;
                                }
                                fanNativeAdLayout.addView(nativeAdView);

                                // Add the AdOptionsView
                                LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                                AdOptionsView adOptionsView = new AdOptionsView(context, fanNativeAd, fanNativeAdLayout);
                                adChoicesContainer.removeAllViews();
                                adChoicesContainer.addView(adOptionsView, 0);

                                // Create native UI using the ad metadata.
                                TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
                                com.facebook.ads.MediaView nativeAdIcon = nativeAdView.findViewById(R.id.native_ad_icon);
                                TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
                                TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                                Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                                LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                nativeAdTitle.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_primary_text_color));
                                nativeAdSocialContext.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_primary_text_color));
                                sponsoredLabel.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_secondary_text_color));
                                nativeAdBody.setTextColor(ContextCompat.getColor(context, R.color.applovin_dark_secondary_text_color));
                                fanNativeBackground.setBackgroundResource(R.color.color_native_background_dark);

                                // Set the Text.
                                nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
                                nativeAdBody.setText(fanNativeAd.getAdBodyText());
                                nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
                                nativeAdCallToAction.setVisibility(fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction());
                                sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation());

                                // Create a list of clickable views
                                List<View> clickableViews = new ArrayList<>();
                                clickableViews.add(nativeAdTitle);
                                clickableViews.add(sponsoredLabel);
                                clickableViews.add(nativeAdIcon);
                                clickableViews.add(nativeAdMedia);
                                clickableViews.add(nativeAdBody);
                                clickableViews.add(nativeAdSocialContext);
                                clickableViews.add(nativeAdCallToAction);

                                // Register the Title and CTA button to listen for clicks.
                                fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews);

                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = fanNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build();
                        fanNativeAd.loadAd(loadAdConfig);
                    } else {
                        Log.d(TAG, "FAN Native Ad has been loaded");
                        loadBackupNativeAd(context, adMobNativeId, fanNativeId, appLovinNativeId, legacyGDPR,nativeAdStyle);
                    }
                    break;

            }
        }
    }


    public void setNativeAdPadding(int left, int top, int right, int bottom) {
        nativeAdViewContainer.setPadding(left, top, right, bottom);
    }

    public void setNativeAdMargin(int left, int top, int right, int bottom) {
        setMargins(nativeAdViewContainer, left, top, right, bottom);
    }

    public void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void setNativeAdBackgroundResource(int drawableBackground) {
        nativeAdViewContainer.setBackgroundResource(drawableBackground);
    }

    public void setNativeAdBackgroundColor(Context context, boolean darkTheme, int nativeBackgroundLight, int nativeBackgroundDark) {
        if (darkTheme) {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundDark));
        } else {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundLight));
        }
    }


    public MaxNativeAdView createNativeAdViewDark(String nativeAdStyle,Context context) {
        MaxNativeAdViewBinder binder;
        switch (nativeAdStyle) {
            case "medium":
                binder = new MaxNativeAdViewBinder.Builder(R.layout.gnt_applovin_news_template_view)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;
            case "small":
                binder = new MaxNativeAdViewBinder.Builder(R.layout.gnt_applovin_radio_template_view)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;

            default:
                binder = new MaxNativeAdViewBinder.Builder(R.layout.gnt_applovin_medium_template_view)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;
        }
        return new MaxNativeAdView(binder, context);
    }
    @SuppressWarnings("ConstantConditions")
    public void populateNativeAdView(Context context, com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView nativeAdView, boolean darkTheme, int nativeBackgroundDark, int nativeBackgroundLight) {

        if (darkTheme) {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundDark));
            nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundDark);
        } else {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundLight));
            nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundLight);
        }

        nativeAdView.setMediaView(nativeAdView.findViewById(R.id.media_view));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));

        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
        nativeAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        }

        nativeAdView.setNativeAd(nativeAd);
    }

}
