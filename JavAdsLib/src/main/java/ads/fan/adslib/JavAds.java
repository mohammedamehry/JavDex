package ads.fan.adslib;

import android.app.Activity;
import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.lang.reflect.Method;

import ads.fan.adslib.Config.Config;
import ads.fan.adslib.Format.InitListener;
import ads.fan.adslib.Format.MyAdNetwork;
import ads.fan.adslib.Model.AdsResponse;

public class JavAds {

    public static void FetchAdsData(Activity activity, String AdsJSonLink, InitListener listener){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AdsJSonLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Config.adsResponse = gson.fromJson(response, AdsResponse.class);
                Config.myAds = Config.adsResponse.getAds();
                MyAdNetwork.Initialize myAdNetwork = new MyAdNetwork.Initialize(activity)
                        .setUnityGameId(Config.myAds.getUniityGameId())
                        .setIronSourceAppKey(Config.myAds.getIronAppKey())
                        .build();
                myAdNetwork.initAds();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onInitComplete();
                    }
                },5000);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onInitError(error.getMessage());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);

    }
}
