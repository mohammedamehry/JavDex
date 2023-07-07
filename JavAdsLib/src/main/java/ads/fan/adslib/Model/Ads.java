package ads.fan.adslib.Model;

import com.google.gson.annotations.SerializedName;

public class Ads{

	@SerializedName("MaxNative")
	private String maxNative;


	@SerializedName("FanBanner")
	private String fanBanner;


	@SerializedName("FanInterstitial")
	private String fanInterstitial;


	@SerializedName("FanNative")
	private String fanNative;
	@SerializedName("ShowAds")
	private boolean showAds;

	@SerializedName("PriorityAppOpen")
	private String priorityAppOpen;

	@SerializedName("AdmobNative")
	private String admobNative;

	@SerializedName("IronAppKey")
	private String ironAppKey;

	@SerializedName("AdmobClickCount")
	private int admobClickCount;

	@SerializedName("PriorityBanner")
	private String priorityBanner;

	@SerializedName("MaxOpenAd")
	private String maxOpenAd;

	@SerializedName("BackupBanner")
	private String backupBanner;

	@SerializedName("MaxBanner")
	private String maxBanner;

	@SerializedName("PriorityNative")
	private String priorityNative;

	@SerializedName("ShowLoadingAds")
	private boolean showLoadingAds;

	@SerializedName("IronInterstitial")
	private String ironInterstitial;

	@SerializedName("BackupInterstitial")
	private String backupInterstitial;

	@SerializedName("IronBanner")
	private String ironBanner;

	@SerializedName("UniityGameId")
	private String uniityGameId;

	@SerializedName("UnityBanner")
	private String unityBanner;

	@SerializedName("AdmobAppOpen")
	private String admobAppOpen;

	@SerializedName("PriorityInterstitial")
	private String priorityInterstitial;

	@SerializedName("UnityInterstitial")
	private String unityInterstitial;

	@SerializedName("AdmobInterstitial")
	private String admobInterstitial;

	@SerializedName("BackupNative")
	private String backupNative;

	@SerializedName("MaxInterstitial")
	private String maxInterstitial;

	@SerializedName("AdmobBanner")
	private String admobBanner;

	public String getMaxNative(){
		return maxNative;
	}

	public boolean isShowAds(){
		return showAds;
	}

	public String getPriorityAppOpen(){
		return priorityAppOpen;
	}

	public String getAdmobNative(){
		return admobNative;
	}

	public String getIronAppKey(){
		return ironAppKey;
	}

	public int getAdmobClickCount(){
		return admobClickCount;
	}

	public String getPriorityBanner(){
		return priorityBanner;
	}

	public String getMaxOpenAd(){
		return maxOpenAd;
	}

	public String getBackupBanner(){
		return backupBanner;
	}

	public String getMaxBanner(){
		return maxBanner;
	}

	public String getPriorityNative(){
		return priorityNative;
	}

	public boolean isShowLoadingAds(){
		return showLoadingAds;
	}

	public String getIronInterstitial(){
		return ironInterstitial;
	}

	public String getBackupInterstitial(){
		return backupInterstitial;
	}

	public String getIronBanner(){
		return ironBanner;
	}

	public String getUniityGameId(){
		return uniityGameId;
	}

	public String getUnityBanner(){
		return unityBanner;
	}

	public String getAdmobAppOpen(){
		return admobAppOpen;
	}

	public String getPriorityInterstitial(){
		return priorityInterstitial;
	}

	public String getUnityInterstitial(){
		return unityInterstitial;
	}

	public String getAdmobInterstitial(){
		return admobInterstitial;
	}

	public String getBackupNative(){
		return backupNative;
	}

	public String getMaxInterstitial(){
		return maxInterstitial;
	}

	public String getAdmobBanner(){
		return admobBanner;
	}

	public String getFanBanner() {
		return fanBanner;
	}

	public String getFanInterstitial() {
		return fanInterstitial;
	}

	public String getFanNative() {
		return fanNative;
	}
}