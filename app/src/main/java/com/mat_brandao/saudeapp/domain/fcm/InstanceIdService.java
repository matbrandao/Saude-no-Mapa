package com.mat_brandao.saudeapp.domain.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Installation;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.DateUtil;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import io.realm.Realm;

/**
 * Created by Mateus Brandão on 21-Jul-16.
 */
public class InstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "InstanceIdService";
    public static final String TOKEN_ACTION = "token_return";
    public static final String TOKEN_EXTRA = "token";

    private User mUser;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser = realm.where(User.class).findFirst();
        });

        if (mUser != null) {
            if (mUser.getInstallationId() != null) {
                Log.d(TAG, "sendRegistrationToServer: requesting installation");
                RestClient.getHeader(mUser.getAppToken())
                        .getInstallation(mUser.getInstallationId())
                        .onErrorReturn(throwable -> null)
                        .subscribe(installationResponse -> {
                            if (installationResponse != null && installationResponse.isSuccessful()) {
                                if (!refreshedToken.equals(installationResponse.body().getDeviceToken())) {
                                    Log.d(TAG, "sendRegistrationToServer: token is different, trying to update");
                                    installationResponse.body().setDeviceToken(refreshedToken);

                                    RestClient.getHeader(mUser.getAppToken())
                                            .updateInstallation(installationResponse.body())
                                            .onErrorReturn(throwable -> null)
                                            .subscribe();
                                }
                            } else {
                                Log.d(TAG, "sendRegistrationToServer: error getting installation");
                            }
                        });
            } else {
                Log.d(TAG, "sendRegistrationToServer: creating installation");
                Installation installation = new Installation();
                installation.setAppId(Integer.valueOf(this.getString(R.string.app_id)));
                installation.setDateTime(DateUtil.getNowDate());
                installation.setDeviceOS(this.getString(R.string.device_os));
                installation.setDeviceToken(refreshedToken);
                installation.setUserId(mUser.getId());

                RestClient.getHeader(mUser.getAppToken())
                        .createInstallation(installation)
                        .onErrorReturn(throwable -> null)
                        .subscribe(installationResponse -> {
                            Realm.getDefaultInstance().executeTransaction(realm -> {
                                realm.where(User.class).findFirst()
                                        .setInstallationId(GenericUtil.getNumbersFromString(installationResponse.headers().get("location")));
                            });
                        });
            }
        } else {
            Log.d(TAG, "sendRegistrationToServer: user == null");
        }
    }
}
