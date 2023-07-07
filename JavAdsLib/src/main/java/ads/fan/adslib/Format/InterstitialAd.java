package ads.fan.adslib.Format;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;

import com.ironsource.mediationsdk.sdk.f;
import com.unity3d.mediation.IInterstitialAdLoadListener;
import com.unity3d.mediation.IInterstitialAdShowListener;
import com.unity3d.mediation.errors.LoadError;
import com.unity3d.mediation.errors.ShowError;
import com.vungle.warren.AdEventListener;

import java.util.concurrent.TimeUnit;

import ads.fan.adslib.Config.Config;
import ads.fan.adslib.Config.Tools;
import ads.fan.adslib.Helper.LoadingAds;

public class InterstitialAd {

    public static class Builder {
        public  String ADMOB = "admob";
        public  String MAX = "max";
        public  String IRON = "iron";
        public  String UNITY = "unity";
        public  String FAN = "fan";
        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private com.google.android.gms.ads.interstitial.InterstitialAd adMobInterstitialAd;
        private com.facebook.ads.InterstitialAd fanInterstitialAd;
        private com.unity3d.mediation.InterstitialAd unityInterstitialAd;
        private MaxInterstitialAd maxInterstitialAd;

        private int retryAttempt;
        private int counter = 1;

        private String adMobInterstitialId = "";
        private String fanInterstitialId = "";
        private String unityInterstitialId = "";
        private String appLovinInterstitialId = "";
        private String ironSourceInterstitialId = "";
        private int placementStatus = 1;

        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build() {
            loadInterstitialAd();
            return this;
        }

        public void show(JavInterstitialCloseListener listener) {
            showInterstitialAd(listener);
        }




        public Builder setAdMobInterstitialId(String adMobInterstitialId) {
            this.adMobInterstitialId = adMobInterstitialId;
            return this;
        }



        public Builder setFanInterstitialId(String fanInterstitialId) {
            this.fanInterstitialId = fanInterstitialId;
            return this;
        }

        public Builder setUnityInterstitialId(String unityInterstitialId) {
            this.unityInterstitialId = unityInterstitialId;
            return this;
        }

        public Builder setAppLovinInterstitialId(String appLovinInterstitialId) {
            this.appLovinInterstitialId = appLovinInterstitialId;
            return this;
        }



        public Builder setIronSourceInterstitialId(String ironSourceInterstitialId) {
            this.ironSourceInterstitialId = ironSourceInterstitialId;
            return this;
        }



        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadInterstitialAd() {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if (Config.myAds.getPriorityInterstitial().equals(ADMOB)){
                    com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId, Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                            adMobInterstitialAd = interstitialAd;
                            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitialAd();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    Log.d(TAG, "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    adMobInterstitialAd = null;
                                    Log.d(TAG, "The ad was shown.");
                                }
                            });
                            Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i(TAG, loadAdError.getMessage());
                            adMobInterstitialAd = null;
                            loadBackupInterstitialAd();
                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                        }
                    });

                }else if (Config.myAds.getPriorityInterstitial().equals(FAN)){
                    fanInterstitialAd = new com.facebook.ads.InterstitialAd(activity, fanInterstitialId);
                    InterstitialAdListener adListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                            fanInterstitialAd.loadAd();
                        }

                        @Override
                        public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                            loadBackupInterstitialAd();
                        }

                        @Override
                        public void onAdLoaded(com.facebook.ads.Ad ad) {
                            Log.d(TAG, "FAN Interstitial is loaded");
                        }

                        @Override
                        public void onAdClicked(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(com.facebook.ads.Ad ad) {

                        }
                    };

                    com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd.buildLoadAdConfig().withAdListener(adListener).build();
                    fanInterstitialAd.loadAd(loadAdConfig);
                }else if (Config.myAds.getPriorityInterstitial().equals(UNITY)){
                    unityInterstitialAd = new com.unity3d.mediation.InterstitialAd(activity, unityInterstitialId);
                    final IInterstitialAdLoadListener unityAdLoadListener = new IInterstitialAdLoadListener() {
                        @Override
                        public void onInterstitialLoaded(com.unity3d.mediation.InterstitialAd interstitialAd) {
                            Log.d(TAG, "unity interstitial ad loaded");
                        }

                        @Override
                        public void onInterstitialFailedLoad(com.unity3d.mediation.InterstitialAd interstitialAd, LoadError loadError, String s) {
                            Log.e(TAG, "Unity Ads failed to load ad : " + unityInterstitialId + " : error : " + s);
                            loadBackupInterstitialAd();
                        }

                    };
                    unityInterstitialAd.load(unityAdLoadListener);
                }else if (Config.myAds.getPriorityInterstitial().equals(MAX)){
                    maxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                    maxInterstitialAd.setListener(new MaxAdListener() {
                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            retryAttempt = 0;
                            Log.d(TAG, "AppLovin Interstitial Ad loaded...");
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {
                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {
                            maxInterstitialAd.loadAd();
                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            retryAttempt++;
                            long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                            new Handler().postDelayed(() -> maxInterstitialAd.loadAd(), delayMillis);
                            loadBackupInterstitialAd();
                            Log.d(TAG, "failed to load AppLovin Interstitial");
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            maxInterstitialAd.loadAd();
                        }
                    });

                    // Load the first ad
                    maxInterstitialAd.loadAd();
                }else if (Config.myAds.getPriorityInterstitial().equals(IRON)){
                    IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                        @Override
                        public void onAdReady(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdReady");
                        }

                        @Override
                        public void onAdLoadFailed(IronSourceError ironSourceError) {
                            Log.d(TAG, "onInterstitialAdLoadFailed" + " " + ironSourceError);
                            loadBackupInterstitialAd();
                        }

                        @Override
                        public void onAdOpened(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdOpened");
                        }

                        @Override
                        public void onAdShowSucceeded(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdShowSucceeded");
                        }

                        @Override
                        public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdShowFailed" + " " + ironSourceError);
                            loadBackupInterstitialAd();
                        }

                        @Override
                        public void onAdClicked(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdClicked");
                        }

                        @Override
                        public void onAdClosed(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdClosed");
                            loadInterstitialAd();
                        }
                    });
                    IronSource.loadInterstitial();
                }
            }
        }

        public void loadBackupInterstitialAd() {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if (Config.myAds.getBackupInterstitial().equals(ADMOB)){
                    com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId, Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                            adMobInterstitialAd = interstitialAd;
                            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitialAd();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    Log.d(TAG, "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    adMobInterstitialAd = null;
                                    Log.d(TAG, "The ad was shown.");
                                }
                            });
                            Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i(TAG, loadAdError.getMessage());
                            adMobInterstitialAd = null;
                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                        }
                    });
                }else if (Config.myAds.getBackupInterstitial().equals(FAN)){
                    fanInterstitialAd = new com.facebook.ads.InterstitialAd(activity, fanInterstitialId);
                    InterstitialAdListener adListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                            fanInterstitialAd.loadAd();
                        }

                        @Override
                        public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(com.facebook.ads.Ad ad) {
                            Log.d(TAG, "FAN Interstitial is loaded");
                        }

                        @Override
                        public void onAdClicked(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(com.facebook.ads.Ad ad) {

                        }
                    };

                    com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd.buildLoadAdConfig().withAdListener(adListener).build();
                    fanInterstitialAd.loadAd(loadAdConfig);
                }else if (Config.myAds.getBackupInterstitial().equals(UNITY)){
                    unityInterstitialAd = new com.unity3d.mediation.InterstitialAd(activity, unityInterstitialId);
                    final IInterstitialAdLoadListener unityAdLoadListener = new IInterstitialAdLoadListener() {
                        @Override
                        public void onInterstitialLoaded(com.unity3d.mediation.InterstitialAd interstitialAd) {
                            Log.d(TAG, "unity interstitial ad loaded");
                        }

                        @Override
                        public void onInterstitialFailedLoad(com.unity3d.mediation.InterstitialAd interstitialAd, LoadError loadError, String s) {
                            Log.e(TAG, "Unity Ads failed to load ad : " + unityInterstitialId + " : error : " + s);
                        }

                    };
                    unityInterstitialAd.load(unityAdLoadListener);
                }else if (Config.myAds.getBackupInterstitial().equals(MAX)){
                    maxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                    maxInterstitialAd.setListener(new MaxAdListener() {
                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            retryAttempt = 0;
                            Log.d(TAG, "AppLovin Interstitial Ad loaded...");
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {
                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {
                            maxInterstitialAd.loadAd();
                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            retryAttempt++;
                            long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                            new Handler().postDelayed(() -> maxInterstitialAd.loadAd(), delayMillis);
                            Log.d(TAG, "failed to load AppLovin Interstitial");
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            maxInterstitialAd.loadAd();
                        }
                    });

                    // Load the first ad
                    maxInterstitialAd.loadAd();
                }else if (Config.myAds.getBackupInterstitial().equals(IRON)){
                    IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                        @Override
                        public void onAdReady(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdReady");
                        }

                        @Override
                        public void onAdLoadFailed(IronSourceError ironSourceError) {
                            Log.d(TAG, "onInterstitialAdLoadFailed" + " " + ironSourceError);
                        }

                        @Override
                        public void onAdOpened(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdOpened");
                        }

                        @Override
                        public void onAdShowSucceeded(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdShowSucceeded");
                        }

                        @Override
                        public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdShowFailed" + " " + ironSourceError);
                        }

                        @Override
                        public void onAdClicked(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdClicked");
                        }

                        @Override
                        public void onAdClosed(AdInfo adInfo) {
                            Log.d(TAG, "onInterstitialAdClosed");
                            loadInterstitialAd();
                        }
                    });
                    IronSource.loadInterstitial();
                }


            }
        }

        public void showInterstitialAd(JavInterstitialCloseListener listener) {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if(Config.myAds.isShowLoadingAds()){
                    LoadingAds.Show(activity);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LoadingAds.Dismiss();
                            switch (Config.myAds.getPriorityInterstitial()){
                                case "admob":
                                    if (Config.myAds.getAdmobClickCount()==counter){
                                        counter = 1;
                                        if (adMobInterstitialAd != null) {
                                            adMobInterstitialAd.show(activity);
                                            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                @Override
                                                public void onAdDismissedFullScreenContent() {
                                                    super.onAdDismissedFullScreenContent();
                                                    listener.onShowAdComplete();
                                                }

                                                @Override
                                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                                    super.onAdFailedToShowFullScreenContent(adError);
                                                    showBackupInterstitialAd(listener);
                                                }
                                            });
                                        }
                                        else {
                                            showBackupInterstitialAd(listener);
                                            Log.d(TAG, "admob interstitial null");
                                        }
                                    }else {
                                        showBackupInterstitialAd(listener);
                                        counter++;
                                    }
                                    break;
                                case "fan":
                                    if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                                        fanInterstitialAd.show();
                                        fanInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                                            @Override
                                            public void onInterstitialDisplayed(Ad ad) {

                                            }

                                            @Override
                                            public void onInterstitialDismissed(Ad ad) {
                                                listener.onShowAdComplete();
                                            }

                                            @Override
                                            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                                showBackupInterstitialAd(listener);
                                            }

                                            @Override
                                            public void onAdLoaded(Ad ad) {

                                            }

                                            @Override
                                            public void onAdClicked(Ad ad) {

                                            }

                                            @Override
                                            public void onLoggingImpression(Ad ad) {

                                            }
                                        });
                                    } else {
                                        showBackupInterstitialAd(listener);
                                    }
                                    break;
                                case "iron":
                                    if (IronSource.isInterstitialReady()) {
                                        IronSource.showInterstitial(ironSourceInterstitialId);
                                        IronSource.setInterstitialListener(new InterstitialListener() {
                                            @Override
                                            public void onInterstitialAdReady() {

                                            }

                                            @Override
                                            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                                            }

                                            @Override
                                            public void onInterstitialAdOpened() {

                                            }

                                            @Override
                                            public void onInterstitialAdClosed() {
                                                listener.onShowAdComplete();
                                            }

                                            @Override
                                            public void onInterstitialAdShowSucceeded() {

                                            }

                                            @Override
                                            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                                                showBackupInterstitialAd(listener);
                                            }

                                            @Override
                                            public void onInterstitialAdClicked() {

                                            }
                                        });
                                    } else {
                                        showBackupInterstitialAd(listener);
                                    }
                                    break;
                                case "max":
                                    if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
                                        maxInterstitialAd.showAd();
                                        maxInterstitialAd.setListener(new MaxAdListener() {
                                            @Override
                                            public void onAdLoaded(MaxAd maxAd) {

                                            }

                                            @Override
                                            public void onAdDisplayed(MaxAd maxAd) {
                                            }

                                            @Override
                                            public void onAdHidden(MaxAd maxAd) {
                                                listener.onShowAdComplete();

                                            }

                                            @Override
                                            public void onAdClicked(MaxAd maxAd) {

                                            }

                                            @Override
                                            public void onAdLoadFailed(String s, MaxError maxError) {

                                            }

                                            @Override
                                            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                                                showBackupInterstitialAd(listener);
                                            }
                                        });
                                        Log.d(TAG, "show ad");
                                    } else {
                                        showBackupInterstitialAd(listener);
                                    }
                                    break;
                                case "unity":
                                    IInterstitialAdShowListener showListener = new IInterstitialAdShowListener() {
                                        @Override
                                        public void onInterstitialShowed(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                        }

                                        @Override
                                        public void onInterstitialClicked(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                        }

                                        @Override
                                        public void onInterstitialClosed(com.unity3d.mediation.InterstitialAd interstitialAd) {
                                            listener.onShowAdComplete();
                                        }

                                        @Override
                                        public void onInterstitialFailedShow(com.unity3d.mediation.InterstitialAd interstitialAd, ShowError showError, String s) {
                                            showBackupInterstitialAd(listener);
                                        }
                                    };
                                    unityInterstitialAd.show(showListener);
                                    break;

                            }
                        }
                    },4000);
                }else {
                    switch (Config.myAds.getPriorityInterstitial()){
                        case "admob":
                            if (Config.myAds.getAdmobClickCount()==counter){
                                counter = 1;
                                if (adMobInterstitialAd != null) {
                                    adMobInterstitialAd.show(activity);
                                    adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            super.onAdDismissedFullScreenContent();
                                            listener.onShowAdComplete();
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                            super.onAdFailedToShowFullScreenContent(adError);
                                            showBackupInterstitialAd(listener);
                                        }
                                    });
                                }
                                else {
                                    showBackupInterstitialAd(listener);
                                    Log.d(TAG, "admob interstitial null");
                                }
                            }else {
                                showBackupInterstitialAd(listener);
                                counter++;
                            }
                            break;
                        case "fan":
                            if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                                fanInterstitialAd.show();
                                fanInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                                    @Override
                                    public void onInterstitialDisplayed(Ad ad) {

                                    }

                                    @Override
                                    public void onInterstitialDismissed(Ad ad) {
                                        listener.onShowAdComplete();
                                    }

                                    @Override
                                    public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                        showBackupInterstitialAd(listener);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {

                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                });
                            } else {
                                showBackupInterstitialAd(listener);
                            }
                            break;
                        case "iron":
                            if (IronSource.isInterstitialReady()) {
                                IronSource.showInterstitial(ironSourceInterstitialId);
                                IronSource.setInterstitialListener(new InterstitialListener() {
                                    @Override
                                    public void onInterstitialAdReady() {

                                    }

                                    @Override
                                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                                    }

                                    @Override
                                    public void onInterstitialAdOpened() {

                                    }

                                    @Override
                                    public void onInterstitialAdClosed() {
                                        listener.onShowAdComplete();
                                    }

                                    @Override
                                    public void onInterstitialAdShowSucceeded() {

                                    }

                                    @Override
                                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                                        showBackupInterstitialAd(listener);
                                    }

                                    @Override
                                    public void onInterstitialAdClicked() {

                                    }
                                });
                            } else {
                                showBackupInterstitialAd(listener);
                            }
                            break;
                        case "max":
                            if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
                                maxInterstitialAd.showAd();
                                maxInterstitialAd.setListener(new MaxAdListener() {
                                    @Override
                                    public void onAdLoaded(MaxAd maxAd) {

                                    }

                                    @Override
                                    public void onAdDisplayed(MaxAd maxAd) {
                                    }

                                    @Override
                                    public void onAdHidden(MaxAd maxAd) {
                                        listener.onShowAdComplete();

                                    }

                                    @Override
                                    public void onAdClicked(MaxAd maxAd) {

                                    }

                                    @Override
                                    public void onAdLoadFailed(String s, MaxError maxError) {

                                    }

                                    @Override
                                    public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                                        showBackupInterstitialAd(listener);
                                    }
                                });
                                Log.d(TAG, "show ad");
                            } else {
                                showBackupInterstitialAd(listener);
                            }
                            break;
                        case "unity":
                            IInterstitialAdShowListener showListener = new IInterstitialAdShowListener() {
                                @Override
                                public void onInterstitialShowed(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialClicked(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialClosed(com.unity3d.mediation.InterstitialAd interstitialAd) {
                                    listener.onShowAdComplete();
                                }

                                @Override
                                public void onInterstitialFailedShow(com.unity3d.mediation.InterstitialAd interstitialAd, ShowError showError, String s) {
                                    showBackupInterstitialAd(listener);
                                }
                            };
                            unityInterstitialAd.show(showListener);
                            break;
                    }
                }

            }else {
                listener.onShowAdComplete();
            }
        }

        public void showBackupInterstitialAd(JavInterstitialCloseListener listener) {
            if (Config.myAds.isShowAds() && placementStatus != 0) {
                if(Config.myAds.isShowLoadingAds()){
                    LoadingAds.Show(activity);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LoadingAds.Dismiss();
                            switch (Config.myAds.getBackupInterstitial()){
                                case "admob":
                                    if (Config.myAds.getAdmobClickCount()==counter){
                                        counter = 1;
                                        if (adMobInterstitialAd != null) {
                                            adMobInterstitialAd.show(activity);
                                            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                @Override
                                                public void onAdDismissedFullScreenContent() {
                                                    super.onAdDismissedFullScreenContent();
                                                    listener.onShowAdComplete();
                                                }

                                                @Override
                                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                                    super.onAdFailedToShowFullScreenContent(adError);
                                                    listener.onShowAdComplete();
                                                }
                                            });
                                        }
                                        else {
                                            showBackupInterstitialAd(listener);
                                            Log.d(TAG, "admob interstitial null");
                                        }
                                    }else {
                                        listener.onShowAdComplete();
                                        counter++;
                                    }
                                    break;
                                case "fan":
                                    if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                                        fanInterstitialAd.show();
                                        fanInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                                            @Override
                                            public void onInterstitialDisplayed(Ad ad) {

                                            }

                                            @Override
                                            public void onInterstitialDismissed(Ad ad) {
                                                listener.onShowAdComplete();
                                            }

                                            @Override
                                            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                                listener.onShowAdComplete();
                                            }

                                            @Override
                                            public void onAdLoaded(Ad ad) {

                                            }

                                            @Override
                                            public void onAdClicked(Ad ad) {

                                            }

                                            @Override
                                            public void onLoggingImpression(Ad ad) {

                                            }
                                        });
                                    } else {
                                        listener.onShowAdComplete();
                                    }
                                    break;
                                case "iron":
                                    if (IronSource.isInterstitialReady()) {
                                        IronSource.showInterstitial(ironSourceInterstitialId);
                                        IronSource.setInterstitialListener(new InterstitialListener() {
                                            @Override
                                            public void onInterstitialAdReady() {

                                            }

                                            @Override
                                            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                                            }

                                            @Override
                                            public void onInterstitialAdOpened() {

                                            }

                                            @Override
                                            public void onInterstitialAdClosed() {
                                                listener.onShowAdComplete();
                                            }

                                            @Override
                                            public void onInterstitialAdShowSucceeded() {

                                            }

                                            @Override
                                            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                                                listener.onShowAdComplete();
                                            }

                                            @Override
                                            public void onInterstitialAdClicked() {

                                            }
                                        });
                                    } else {
                                        listener.onShowAdComplete();
                                    }
                                    break;
                                case "max":
                                    if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
                                        maxInterstitialAd.showAd();
                                        maxInterstitialAd.setListener(new MaxAdListener() {
                                            @Override
                                            public void onAdLoaded(MaxAd maxAd) {

                                            }

                                            @Override
                                            public void onAdDisplayed(MaxAd maxAd) {
                                            }

                                            @Override
                                            public void onAdHidden(MaxAd maxAd) {
                                                listener.onShowAdComplete();

                                            }

                                            @Override
                                            public void onAdClicked(MaxAd maxAd) {

                                            }

                                            @Override
                                            public void onAdLoadFailed(String s, MaxError maxError) {

                                            }

                                            @Override
                                            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                                                listener.onShowAdComplete();
                                            }
                                        });
                                        Log.d(TAG, "show ad");
                                    } else {
                                        listener.onShowAdComplete();
                                    }
                                    break;
                                case "unity":
                                    IInterstitialAdShowListener showListener = new IInterstitialAdShowListener() {
                                        @Override
                                        public void onInterstitialShowed(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                        }

                                        @Override
                                        public void onInterstitialClicked(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                        }

                                        @Override
                                        public void onInterstitialClosed(com.unity3d.mediation.InterstitialAd interstitialAd) {
                                            listener.onShowAdComplete();
                                        }

                                        @Override
                                        public void onInterstitialFailedShow(com.unity3d.mediation.InterstitialAd interstitialAd, ShowError showError, String s) {
                                            listener.onShowAdComplete();
                                        }
                                    };
                                    unityInterstitialAd.show(showListener);
                                    break;

                            }
                        }
                    },4000);
                }else {
                    switch (Config.myAds.getBackupInterstitial()){
                        case "admob":
                            if (Config.myAds.getAdmobClickCount()==counter){
                                counter = 1;
                                if (adMobInterstitialAd != null) {
                                    adMobInterstitialAd.show(activity);
                                    adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            super.onAdDismissedFullScreenContent();
                                            listener.onShowAdComplete();
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                            super.onAdFailedToShowFullScreenContent(adError);
                                            listener.onShowAdComplete();
                                        }
                                    });
                                }
                                else {
                                    listener.onShowAdComplete();
                                    Log.d(TAG, "admob interstitial null");
                                }
                            }else {
                                listener.onShowAdComplete();
                                counter++;
                            }
                            break;
                        case "fan":
                            if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                                fanInterstitialAd.show();
                                fanInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                                    @Override
                                    public void onInterstitialDisplayed(Ad ad) {

                                    }

                                    @Override
                                    public void onInterstitialDismissed(Ad ad) {
                                        listener.onShowAdComplete();
                                    }

                                    @Override
                                    public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                        listener.onShowAdComplete();
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {

                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                });
                            } else {
                                listener.onShowAdComplete();
                            }
                            break;
                        case "iron":
                            if (IronSource.isInterstitialReady()) {
                                IronSource.showInterstitial(ironSourceInterstitialId);
                                IronSource.setInterstitialListener(new InterstitialListener() {
                                    @Override
                                    public void onInterstitialAdReady() {

                                    }

                                    @Override
                                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                                    }

                                    @Override
                                    public void onInterstitialAdOpened() {

                                    }

                                    @Override
                                    public void onInterstitialAdClosed() {
                                        listener.onShowAdComplete();
                                    }

                                    @Override
                                    public void onInterstitialAdShowSucceeded() {

                                    }

                                    @Override
                                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                                        listener.onShowAdComplete();
                                    }

                                    @Override
                                    public void onInterstitialAdClicked() {

                                    }
                                });
                            } else {
                                listener.onShowAdComplete();
                            }
                            break;
                        case "max":
                            if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
                                maxInterstitialAd.showAd();
                                maxInterstitialAd.setListener(new MaxAdListener() {
                                    @Override
                                    public void onAdLoaded(MaxAd maxAd) {

                                    }

                                    @Override
                                    public void onAdDisplayed(MaxAd maxAd) {
                                    }

                                    @Override
                                    public void onAdHidden(MaxAd maxAd) {
                                        listener.onShowAdComplete();

                                    }

                                    @Override
                                    public void onAdClicked(MaxAd maxAd) {

                                    }

                                    @Override
                                    public void onAdLoadFailed(String s, MaxError maxError) {

                                    }

                                    @Override
                                    public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                                        listener.onShowAdComplete();
                                    }
                                });
                                Log.d(TAG, "show ad");
                            } else {
                                listener.onShowAdComplete();
                            }
                            break;
                        case "unity":
                            IInterstitialAdShowListener showListener = new IInterstitialAdShowListener() {
                                @Override
                                public void onInterstitialShowed(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialClicked(com.unity3d.mediation.InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialClosed(com.unity3d.mediation.InterstitialAd interstitialAd) {
                                    listener.onShowAdComplete();
                                }

                                @Override
                                public void onInterstitialFailedShow(com.unity3d.mediation.InterstitialAd interstitialAd, ShowError showError, String s) {
                                    listener.onShowAdComplete();
                                }
                            };
                            unityInterstitialAd.show(showListener);
                            break;
                    }
                }

            }else {
                listener.onShowAdComplete();
            }
        }

    }

}
