package ads.fan.adslib.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.WindowManager;

import ads.fan.adslib.databinding.LoadingDialogBinding;

public class LoadingAds {
    public static Dialog dialog;
    public static void Show(Activity activity){
        LoadingDialogBinding loadingDialog = LoadingDialogBinding.inflate(activity.getLayoutInflater());
        dialog = new Dialog(activity);
        dialog.setContentView(loadingDialog.getRoot());
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }
    public static void Dismiss(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
