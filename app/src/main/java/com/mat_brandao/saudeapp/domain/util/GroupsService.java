package com.mat_brandao.saudeapp.domain.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.view.chat.ChatActivity;
import com.mat_brandao.saudeapp.view.main.MainActivity;
import com.mat_brandao.saudeapp.view.my_groups.MyGroupsInteractorImpl;

import java.util.Random;

import static com.mat_brandao.saudeapp.view.chat.ChatPresenterImpl.MESSAGES_CHILD;
import static com.mat_brandao.saudeapp.view.group.GroupPresenterImpl.GROUP_KEY;

/**
 * Created by Mateus Brandão on 14/10/2016.
 */
public class GroupsService extends IntentService {
    private static final String TAG = "GroupsService";
    private MyGroupsInteractorImpl mInteractor;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mFirebaseGroupReference;

    public GroupsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called with: intent = [" + intent + "]");
        mInteractor = new MyGroupsInteractorImpl(this);
        if (mInteractor.getUser() != null && mInteractor.getUser().getId() != 0) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mInteractor.requestMyGroups()
                    .onErrorReturn(throwable -> null)
                    .retry(3)
                    .subscribe(listResponse -> {
                        if (listResponse != null) {
                            for (Grupo grupo : listResponse.body()) {
                                Log.d(TAG, "onHandleIntent: group loop");
                                mFirebaseGroupReference = mFirebaseDatabaseReference.child(String.valueOf(grupo.getCodGrupo()));
                                mFirebaseGroupReference.child(MESSAGES_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            int itemCount = mInteractor.getChatItemCountByGroupId(grupo.getCodGrupo());
                                            Log.d(TAG, "onDataChange: group: " + grupo.getDescricao());
                                            Log.d(TAG, "onDataChange: itemCount: " + itemCount);
                                            Log.d(TAG, "onDataChange: snapshotChildrenCount: " + dataSnapshot.getChildrenCount());
                                            if (itemCount < dataSnapshot.getChildrenCount()) {
                                                mInteractor.saveItemCount(grupo.getCodGrupo(), (int) dataSnapshot.getChildrenCount());
                                                showNotification(grupo);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void showNotification(Grupo grupo) {
        Intent intent = new Intent(this, ChatActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        intent.putExtra(GROUP_KEY, grupo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int notId = new Random().nextInt();

        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(notId, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Você possui mensagens não lidas no grupo: " + grupo.getDescricao())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notId, notificationBuilder.build());
    }
}
