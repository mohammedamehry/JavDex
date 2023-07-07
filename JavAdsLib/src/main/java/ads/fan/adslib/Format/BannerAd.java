package ads.fan.adslib.Format;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.applovin.adview.AppLovinAdView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;

import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.unity3d.services.banners.api.Banner;

import ads.fan.adslib.Config.Config;
import ads.fan.adslib.Config.Tools;
import ads.fan.adslib.R;

public class BannerAd {

    public static class Builder {
        public  String ADMOB = "admob";
        public  String MAX = "max";
        public  String IRON = "iron";
        public  String UNITY = "unity";
        public  String FAN = "fan";
        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private AdView adView;
        private com.facebook.ads.AdView fanAdView;
        private AppLovinAdView appLovinAdView;
        FrameLayout ironSourceBannerView;
        private IronSourceBannerLayout ironSourceBannerLayout;

        private String backupAdNetwork = "";
        private String adMobBannerId = "";
        private String fanBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String ironSourceBannerId = "";
        private int placementStatus = 1;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build() {
            loadBannerAd();
            return this;
        }



        public Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Builder setAdMobBannerId(String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }



        public Builder setFanBannerId(String fanBannerId) {
            this.fanBannerId = fanBannerId;
            return this;
        }

        public Builder setUnityBannerId(String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        public Builder setAppLovinBannerId(String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }



        public Builder setIronSourceBannerId(String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }


        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }


        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadBannerAd() {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if (Config.myAds.getPriorityBanner().equals(ADMOB)) {
                    FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                    adContainerView.post(() -> {
                        adView = new AdView(activity);
                        adView.setAdUnitId(adMobBannerId);
                        adContainerView.removeAllViews();
                        adContainerView.addView(adView);
                        adView.setAdSize(Tools.getAdSize(activity));
                        adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        adView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                adContainerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                // Code to be executed when an ad request fails.
                                adContainerView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                            }
                        });
                    });
                }
                else if (Config.myAds.getPriorityBanner().equals(FAN)) {
                    fanAdView = new com.facebook.ads.AdView(activity, fanBannerId, AdSize.BANNER_HEIGHT_50);
                    RelativeLayout fanAdViewContainer = activity.findViewById(R.id.fan_banner_view_container);
                    fanAdViewContainer.addView(fanAdView);
                    com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            fanAdViewContainer.setVisibility(View.GONE);
                            loadBackupBannerAd();
                            Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            fanAdViewContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
                    fanAdView.loadAd(loadAdConfig);
                }
                else if (Config.myAds.getPriorityBanner().equals(UNITY)) {
                    RelativeLayout unityAdView = activity.findViewById(R.id.unity_banner_view_container);
                    BannerView bottomBanner = new BannerView(activity, unityBannerId, new UnityBannerSize(320, 50));
                    bottomBanner.setListener(new BannerView.IListener() {
                        @Override
                        public void onBannerLoaded(BannerView bannerView) {
                            unityAdView.setVisibility(View.VISIBLE);
                            Log.d("Unity_banner", "ready");
                        }

                        @Override
                        public void onBannerClick(BannerView bannerView) {

                        }

                        @Override
                        public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                            Log.d("SupportTest", "Banner Error" + bannerErrorInfo);
                            unityAdView.setVisibility(View.GONE);
                            loadBackupBannerAd();
                        }

                        @Override
                        public void onBannerLeftApplication(BannerView bannerView) {

                        }
                    });
                    unityAdView.addView(bottomBanner);
                    bottomBanner.load();
                } else if (Config.myAds.getPriorityBanner().equals(MAX)) {
                    RelativeLayout appLovinAdView = activity.findViewById(R.id.applovin_banner_view_container);
                    MaxAdView maxAdView = new MaxAdView(appLovinBannerId, activity);
                    maxAdView.setListener(new MaxAdViewAdListener() {
                        @Override
                        public void onAdExpanded(MaxAd ad) {

                        }

                        @Override
                        public void onAdCollapsed(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            appLovinAdView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {

                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {

                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            appLovinAdView.setVisibility(View.GONE);
                            loadBackupBannerAd();
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                        }
                    });

                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                    maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.color_native_background_dark));
                    appLovinAdView.addView(maxAdView);
                    maxAdView.loadAd();
                } else if (Config.myAds.getPriorityBanner().equals(IRON)) {
                    ironSourceBannerView = activity.findViewById(R.id.ironsource_banner_view_container);
                    ISBannerSize size = ISBannerSize.BANNER;
                    ironSourceBannerLayout = IronSource.createBanner(activity, size);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    ironSourceBannerView.addView(ironSourceBannerLayout, 0, layoutParams);
                    if (ironSourceBannerLayout != null) {
                        ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                            @Override
                            public void onAdLoaded(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdLoaded");
                                ironSourceBannerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdLoadFailed(IronSourceError ironSourceError) {
                                Log.d(TAG, "onBannerAdLoadFailed" + " " + ironSourceError.getErrorMessage());
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onAdClicked(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdClicked");
                            }

                            @Override
                            public void onAdLeftApplication(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdLeftApplication");
                            }

                            @Override
                            public void onAdScreenPresented(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdScreenPresented");
                            }

                            @Override
                            public void onAdScreenDismissed(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdScreenDismissed");
                            }
                        });
                        IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                    } else {
                        Log.d(TAG, "IronSource.createBanner returned null");
                    }
                }


                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        public void loadBackupBannerAd() {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if (Config.myAds.getBackupBanner().equals(ADMOB)) {
                    FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                    adContainerView.post(() -> {
                        adView = new AdView(activity);
                        adView.setAdUnitId(adMobBannerId);
                        adContainerView.removeAllViews();
                        adContainerView.addView(adView);
                        adView.setAdSize(Tools.getAdSize(activity));
                        adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        adView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                adContainerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                // Code to be executed when an ad request fails.
                                adContainerView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                            }
                        });
                    });
                }
                else if (Config.myAds.getBackupBanner().equals(FAN)) {
                    fanAdView = new com.facebook.ads.AdView(activity, fanBannerId, AdSize.BANNER_HEIGHT_50);
                    RelativeLayout fanAdViewContainer = activity.findViewById(R.id.fan_banner_view_container);
                    fanAdViewContainer.addView(fanAdView);
                    com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            fanAdViewContainer.setVisibility(View.GONE);
                            loadBackupBannerAd();
                            Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            fanAdViewContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
                    fanAdView.loadAd(loadAdConfig);
                }
                else if (Config.myAds.getBackupBanner().equals(UNITY)) {
                    RelativeLayout unityAdView = activity.findViewById(R.id.unity_banner_view_container);
                    BannerView bottomBanner = new BannerView(activity, unityBannerId, new UnityBannerSize(320, 50));
                    bottomBanner.setListener(new BannerView.IListener() {
                        @Override
                        public void onBannerLoaded(BannerView bannerView) {
                            unityAdView.setVisibility(View.VISIBLE);
                            Log.d("Unity_banner", "ready");
                        }

                        @Override
                        public void onBannerClick(BannerView bannerView) {

                        }

                        @Override
                        public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                            Log.d("SupportTest", "Banner Error" + bannerErrorInfo);
                            unityAdView.setVisibility(View.GONE);
                            loadBackupBannerAd();
                        }

                        @Override
                        public void onBannerLeftApplication(BannerView bannerView) {

                        }
                    });
                    unityAdView.addView(bottomBanner);
                    bottomBanner.load();
                } else if (Config.myAds.getBackupBanner().equals(MAX)) {
                    RelativeLayout appLovinAdView = activity.findViewById(R.id.applovin_banner_view_container);
                    MaxAdView maxAdView = new MaxAdView(appLovinBannerId, activity);
                    maxAdView.setListener(new MaxAdViewAdListener() {
                        @Override
                        public void onAdExpanded(MaxAd ad) {

                        }

                        @Override
                        public void onAdCollapsed(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            appLovinAdView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {

                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {

                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            appLovinAdView.setVisibility(View.GONE);
                            loadBackupBannerAd();
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                        }
                    });

                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                    maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.color_native_background_dark));
                    appLovinAdView.addView(maxAdView);
                    maxAdView.loadAd();
                } else if (Config.myAds.getBackupBanner().equals(IRON)) {
                    ironSourceBannerView = activity.findViewById(R.id.ironsource_banner_view_container);
                    ISBannerSize size = ISBannerSize.BANNER;
                    ironSourceBannerLayout = IronSource.createBanner(activity, size);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    ironSourceBannerView.addView(ironSourceBannerLayout, 0, layoutParams);
                    if (ironSourceBannerLayout != null) {
                        ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                            @Override
                            public void onAdLoaded(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdLoaded");
                                ironSourceBannerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdLoadFailed(IronSourceError ironSourceError) {
                                Log.d(TAG, "onBannerAdLoadFailed" + " " + ironSourceError.getErrorMessage());
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onAdClicked(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdClicked");
                            }

                            @Override
                            public void onAdLeftApplication(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdLeftApplication");
                            }

                            @Override
                            public void onAdScreenPresented(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdScreenPresented");
                            }

                            @Override
                            public void onAdScreenDismissed(AdInfo adInfo) {
                                Log.d(TAG, "onBannerAdScreenDismissed");
                            }
                        });
                        IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                    } else {
                        Log.d(TAG, "IronSource.createBanner returned null");
                    }
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        public void destroyAndDetachBanner() {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if (Config.myAds.getBackupBanner().equals(IRON) || Config.myAds.getPriorityBanner().equals(IRON)) {
                    if (ironSourceBannerView != null) {
                        Log.d(TAG, "ironSource banner is not null, ready to destroy");
                        IronSource.destroyBanner(ironSourceBannerLayout);
                        ironSourceBannerView.removeView(ironSourceBannerLayout);
                    } else {
                        Log.d(TAG, "ironSource banner is null");
                    }
                }
            }
        }

    }

}
