package ads.fan.adslib.Format;




import android.app.Activity;
import android.util.Log;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.ironsource.mediationsdk.IronSource;

import com.unity3d.mediation.IInitializationListener;
import com.unity3d.mediation.InitializationConfiguration;
import com.unity3d.mediation.UnityMediation;
import com.unity3d.mediation.errors.SdkInitializationError;

import java.util.Map;

import ads.fan.adslib.Config.Config;
import ads.fan.adslib.Helper.AudienceNetworkInitializeHelper;

public class MyAdNetwork {

    public static class Initialize {

        private static final String TAG = "AdNetwork";
        Activity activity;
        private String unityGameId = "";
        private String ironSourceAppKey = "";

        public Initialize(Activity activity) {
            this.activity = activity;
        }

        public Initialize build() {
            initAds();
            return this;
        }




        public Initialize setUnityGameId(String unityGameId) {
            this.unityGameId = unityGameId;
            return this;
        }



        public Initialize setIronSourceAppKey(String ironSourceAppKey) {
            this.ironSourceAppKey = ironSourceAppKey;
            return this;
        }


        public void initAds() {
            if (Config.myAds.isShowAds()) {

                MobileAds.initialize(activity, initializationStatus -> {
                    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                    for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus adapterStatus = statusMap.get(adapterClass);
                        assert adapterStatus != null;
                        Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                    }
                });
                AudienceNetworkInitializeHelper.initializeAd(activity);


                InitializationConfiguration configuration = InitializationConfiguration.builder()
                        .setGameId(unityGameId)
                        .setInitializationListener(new IInitializationListener() {
                            @Override
                            public void onInitializationComplete() {
                                Log.d(TAG, "Unity Mediation is successfully initialized. with ID : " + unityGameId);
                            }

                            @Override
                            public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
                                Log.d(TAG, "Unity Mediation Failed to Initialize : " + msg);
                            }
                        }).build();
                UnityMediation.initialize(configuration);


                AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                AppLovinSdk.getInstance(activity).initializeSdk(config -> {
                });

                String advertisingId = IronSource.getAdvertiserId(activity);
                IronSource.setUserId(advertisingId);
                IronSource.init(activity, ironSourceAppKey);

            }
        }


    }

}
