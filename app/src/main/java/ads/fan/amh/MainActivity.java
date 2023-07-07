package ads.fan.amh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.BuildConfig;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import ads.fan.adslib.Config.Config;
import ads.fan.adslib.Format.AppOpenAd;
import ads.fan.adslib.Format.BannerAd;
import ads.fan.adslib.Format.InitListener;
import ads.fan.adslib.Format.InterstitialAd;
import ads.fan.adslib.Format.JavInterstitialCloseListener;
import ads.fan.adslib.Format.MyAdNetwork;
import ads.fan.adslib.Format.NativeAd;
import ads.fan.adslib.Format.NativeAdView;
import ads.fan.adslib.Format.OnShowAdCompleteListener;
import ads.fan.adslib.JavAds;
import ads.fan.amh.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    MyAdNetwork.Initialize adNetwork;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    NativeAd.Builder nativeAd;
    NativeAdView.Builder nativeAdView;
    Button btnInterstitial;
    Button btnSelectAds;
    Button btnNativeAdStyle;
    LinearLayout nativeAdViewContainer;
    LinearLayout bannerAdView;
    AppOpenAd.Builder appOpenAdBuilder;
    ActivityMainBinding binding;
    String NativeStyle = "medium";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);

        JavAds.FetchAdsData(this, "https://api.npoint.io/50f0dcd3070e0bee1ca0", new InitListener() {
            @Override
            public void onInitComplete() {
                if (Config.PurchaseCode.equals("QEYW63767jhjnscv")){
                    binding.bannerAdView2.addView(View.inflate(MainActivity.this, ads.fan.adslib.R.layout.view_banner_ad, null));
                    initAds();
                    loadOpenAds();
                    loadBannerAd();
                    loadInterstitialAd();

                    nativeAdViewContainer = findViewById(R.id.native_ad);
                    setNativeAdStyle(nativeAdViewContainer);
                    loadNativeAd();
                    binding.ShowInter.setEnabled(true);
                    binding.ShowInter.setOnClickListener(v -> {
                        showInterstitialAd();
                    });
                }else {
                    //crash app
                    //throw new RuntimeException("This is a crash");
                    new Throwable().printStackTrace();
                }

            }

            @Override
            public void onInitError(String error) {

            }
        });
    }
    LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (AppOpenAd.isAppOpenAdLoaded) {
                        appOpenAdBuilder.show(new OnShowAdCompleteListener() {
                            @Override
                            public void onShowAdComplete() {
                                AppOpenAd.isAppOpenAdLoaded = false;
                            }
                        });
                    }
            }, 100);
        }
    };
    private void initAds() {
        adNetwork = new MyAdNetwork.Initialize(this)
                .setUnityGameId(Config.myAds.getUniityGameId())
                .setIronSourceAppKey(Config.myAds.getIronAppKey())
                .build();
    }
    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd.Builder(this)
                .setAdMobInterstitialId(Config.myAds.getAdmobInterstitial())
                .setFanInterstitialId(Config.myAds.getFanInterstitial())
                .setUnityInterstitialId(Config.myAds.getUnityInterstitial())
                .setAppLovinInterstitialId(Config.myAds.getMaxInterstitial())
                .setIronSourceInterstitialId(Config.myAds.getIronInterstitial())
                .build();
    }

    private void showInterstitialAd() {
        interstitialAd.show(new JavInterstitialCloseListener() {
            @Override
            public void onShowAdComplete() {
                Toast.makeText(MainActivity.this, "Interstitial Ad Closed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadOpenAds() {
            appOpenAdBuilder = new AppOpenAd.Builder(this)
                    .setAdMobAppOpenId(Config.myAds.getAdmobAppOpen())
                    .setApplovinAppOpenId(Config.myAds.getMaxOpenAd())
                    .build(new OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                            AppOpenAd.isAppOpenAdLoaded = false;
                        }
                    });

    }



    private void loadBannerAd() {
        bannerAd = new BannerAd.Builder(this)
                .setAdMobBannerId(Config.myAds.getAdmobBanner())
                .setFanBannerId(Config.myAds.getFanBanner())
                .setUnityBannerId(Config.myAds.getUnityBanner())
                .setAppLovinBannerId(Config.myAds.getMaxBanner())
                .setIronSourceBannerId(Config.myAds.getIronBanner())
                .build();
    }




    private void loadNativeAd() {
        nativeAd = new NativeAd.Builder(this)
                .setBackupAdNetwork("Constant.BACKUP_AD_NETWORK")
                .setAdMobNativeId(Config.myAds.getAdmobNative())
                .setFanNativeId(Config.myAds.getFanNative())
                .setAppLovinNativeId(Config.myAds.getMaxNative())
                .setNativeAdStyle("medium")
                .setPadding(0, 0, 0, 0)
                .build();
    }

    private void loadNativeAdView(View view) {
        nativeAdView = new NativeAdView.Builder(this)
                .setAdMobNativeId(Config.myAds.getAdmobNative())
                .setFanNativeId(Config.myAds.getFanNative())
                .setAppLovinNativeId(Config.myAds.getMaxNative())
                .setNativeAdStyle("medium")
                .setView(view)
                .build();

        nativeAdView.setPadding(0, 0, 0, 0);
    }
    private void setNativeAdStyle(LinearLayout nativeAdView) {
        switch (NativeStyle) {
            case "medium":
                nativeAdView.addView(View.inflate(this, ads.fan.adslib.R.layout.view_native_ad_news, null));
                break;
            case "small":
                nativeAdView.addView(View.inflate(this, ads.fan.adslib.R.layout.view_native_ad_radio, null));
                break;
            default:
                nativeAdView.addView(View.inflate(this, ads.fan.adslib.R.layout.view_native_ad_medium, null));
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appOpenAdBuilder.destroyOpenAd();
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(lifecycleObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }




}