package ads.fan.adslib.Format;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;

import ads.fan.adslib.Config.Config;

@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    public  String ADMOB = "admob";
    public  String MAX = "max";


    public static com.google.android.gms.ads.appopen.AppOpenAd appOpenAd = null;
    public static MaxAppOpenAd maxAppOpenAd = null;
//    public static com.wortise.ads.appopen.AppOpenAd wortiseAppOpenAd = null;
    public static boolean isAppOpenAdLoaded = false;

    public static class Builder {

        private static final String TAG = "AppOpenAd";
        private final Activity activity;
        private String adMobAppOpenId = "";
        private String applovinAppOpenId = "";

        public Builder(Activity activity) {
            this.activity = activity;
        }


        public Builder build(OnShowAdCompleteListener onShowAdCompleteListener) {
            loadAppOpenAd(onShowAdCompleteListener);
            return this;
        }



        public Builder show(OnShowAdCompleteListener onShowAdCompleteListener) {
            showAppOpenAd(onShowAdCompleteListener);
            return this;
        }



        public Builder setAdMobAppOpenId(String adMobAppOpenId) {
            this.adMobAppOpenId = adMobAppOpenId;
            return this;
        }



        public Builder setApplovinAppOpenId(String applovinAppOpenId) {
            this.applovinAppOpenId = applovinAppOpenId;
            return this;
        }



        public void destroyOpenAd() {
            AppOpenAd.isAppOpenAdLoaded = false;
            if (Config.myAds.isShowAds()) {
                if (Config.myAds.getPriorityAppOpen().equals("admob")) {
                    if (appOpenAd != null) {
                        appOpenAd = null;
                    }
                } else if (Config.myAds.getPriorityAppOpen().equals("max")) {
                    if (maxAppOpenAd != null) {
                        maxAppOpenAd = null;
                    }
                }

            }
        }

        public void loadAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            if (Config.myAds.isShowAds()) {
                if (Config.myAds.getPriorityAppOpen().equals("admob")) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    com.google.android.gms.ads.appopen.AppOpenAd.load(activity, adMobAppOpenId, adRequest, new com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.appopen.AppOpenAd ad) {
                            appOpenAd = ad;
                            showAppOpenAd(onShowAdCompleteListener);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            appOpenAd = null;
                            Log.d(TAG, "[" + "" + "] " + "[on start] failed to load app open ad: " + loadAdError.getMessage());
                        }
                    });
                } else if (Config.myAds.getPriorityAppOpen().equals("max")) {
                    maxAppOpenAd = new MaxAppOpenAd(applovinAppOpenId, activity);
                    maxAppOpenAd.setListener(new MaxAdListener() {
                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            showAppOpenAd(onShowAdCompleteListener);
                            Log.d(TAG, "[" + "" + "] " + "[on start] app open ad loaded");
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {

                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {
                            maxAppOpenAd = null;
                            showAppOpenAd(onShowAdCompleteListener);
                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            maxAppOpenAd = null;
                            Log.d(TAG, "[" + "" + "] " + "[on start] failed to load app open ad: " + error.getMessage());
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            maxAppOpenAd = null;
                            Log.d(TAG, "[" + "" + "] " + "[on start] failed to display app open ad: " + error.getMessage());
                        }
                    });
                    maxAppOpenAd.loadAd();
                }

            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void showAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            if (Config.myAds.isShowAds()){
                if (Config.myAds.getPriorityAppOpen().equals("admob")) {
                    if (appOpenAd != null) {
                        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                appOpenAd = null;
                                onShowAdCompleteListener.onShowAdComplete();
                                Log.d(TAG, "[" + "" + "] " + "[on start] close app open ad");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                appOpenAd = null;
                                onShowAdCompleteListener.onShowAdComplete();
                                Log.d(TAG, "[" + "" + "] " + "[on start] failed to show app open ad: " + adError.getMessage());
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.d(TAG, "[" + "" + "] " + "[on start] show app open ad");
                            }
                        });
                        appOpenAd.show(activity);
                    } else {
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                } else if (Config.myAds.getPriorityAppOpen().equals("max")) {
                    if (maxAppOpenAd != null) {
                        maxAppOpenAd.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {

                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                                Log.d(TAG, "[" + "" + "] " + "[on start] show app open ad");
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                onShowAdCompleteListener.onShowAdComplete();
                                Log.d(TAG, "[" + "" + "] " + "[on start] close app open ad");
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                onShowAdCompleteListener.onShowAdComplete();
                                Log.d(TAG, "[" + "" + "] " + "[on start] app open ad load failed: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                onShowAdCompleteListener.onShowAdComplete();
                                Log.d(TAG, "[" + "" + "] " + "[on start] app open ad display failed: " + error.getMessage());
                            }
                        });
                        maxAppOpenAd.showAd();
                    } else {
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                }
            }

        }




    }

}
