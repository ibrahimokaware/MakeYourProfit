package com.rivierasoft.makeyourprofit;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;


public class AdsCenter {
    public static InterstitialAd mInterstitialAd;
    public static RewardedAd rewardedAd;
    public static RewardedAd rewardedAd2;

    public static void loadInterstitialAd(Context context) {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public static void loadRewardedAd(final Context context) {
        rewardedAd = new RewardedAd(context, "");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    public static void loadRewardedAd2(Context context) {
        rewardedAd2 = new RewardedAd(context, "");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd2.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

//    public void showAd() {
//        if (rewardedAd.isLoaded()) {
//            Activity activityContext = (Activity) context;
//            RewardedAdCallback adCallback = new RewardedAdCallback() {
//                @Override
//                public void onRewardedAdOpened() {
//                    // Ad opened.
//                }
//
//                @Override
//                public void onRewardedAdClosed() {
//                    // Ad closed.
//                }
//
//                @Override
//                public void onUserEarnedReward(@NonNull RewardItem reward) {
//                    // User earned reward.
//                }
//
//                @Override
//                public void onRewardedAdFailedToShow(AdError adError) {
//                    // Ad failed to display.
//                }
//            };
//            rewardedAd.show(activityContext, adCallback);
//        } else {
//            Toast.makeText(context, "The rewarded ad wasn't loaded yet.", Toast.LENGTH_SHORT).show();
//            //Log.d("TAG", "The rewarded ad wasn't loaded yet.");
//        }
//    }

//    public RewardedAd createAndLoadRewardedAd() {
//        RewardedAd rewardedAd = new RewardedAd(context,
//                "");
//        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
//            @Override
//            public void onRewardedAdLoaded() {
//                // Ad successfully loaded.
//            }
//
//            @Override
//            public void onRewardedAdFailedToLoad(LoadAdError adError) {
//                // Ad failed to load.
//            }
//        };
//        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
//        return rewardedAd;
//    }

//    @Override
//    public void onRewardedAdClosed() {
//        this.rewardedAd = createAndLoadRewardedAd();
//    }


//    package ...
//
//            import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.rewarded.RewardedAd;
//
//    public class MainActivity extends Activity {
//
//        private RewardedAd gameOverRewardedAd;
//        private RewardedAd extraCoinsRewardedAd;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//        ...
//            gameOverRewardedAd = createAndLoadRewardedAd(
//                    "");
//            extraCoinsRewardedAd = createAndLoadRewardedAd(
//                    "");
//        }
//
//        public RewardedAd createAndLoadRewardedAd(String adUnitId) {
//            RewardedAd rewardedAd = new RewardedAd(this, adUnitId);
//            RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
//                @Override
//                public void onRewardedAdLoaded() {
//                    // Ad successfully loaded.
//                }
//
//                @Override
//                public void onRewardedAdFailedToLoad(LoadAdError adError) {
//                    // Ad failed to load.
//                }
//            };
//            rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
//            return rewardedAd;
//        }
//    }
}
