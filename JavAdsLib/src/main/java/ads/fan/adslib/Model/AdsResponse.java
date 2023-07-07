package ads.fan.adslib.Model;

import com.google.gson.annotations.SerializedName;

public class AdsResponse{

	@SerializedName("Ads")
	private Ads ads;

	public Ads getAds(){
		return ads;
	}
}