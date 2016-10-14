package com.mat_brandao.saudeapp.view.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.FriendlyMessage;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.model.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPresenterImpl implements ChatPresenter {
    private static final String TAG = "ChatPresenterImpl";
    public static final String MESSAGES_CHILD = "messages";

    private Grupo mGroup;

    private ChatInteractorImpl mInteractor;
    private Context mContext;
    private ChatView mView;

    private User mUser;

    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseGroupReference;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
//        mView = null;
    }

    @Override
    public void onRetryClicked() {
    }

    public ChatPresenterImpl(ChatView view, Context context) {
        mInteractor = new ChatInteractorImpl(context);
        mContext = context;
        mView = view;

        mGroup = mView.getGroupFromIntent();
        mView.setToolbarTitle("Chat: " + mGroup.getDescricao());

        mUser = mInteractor.getUser();
        configureFirebaseAdapter();
    }

    private void configureFirebaseAdapter() {
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseGroupReference = mFirebaseDatabaseReference.child(String.valueOf(mGroup.getCodGrupo()));
        checkIfGroupHasMessages();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseGroupReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                mView.setProgressBarVisibility(View.GONE);
                mView.setEmptyViewVisibility(View.GONE);

                if (friendlyMessage.getEmail().equals(mUser.getEmail())) {
                    viewHolder.messageLayout.setVisibility(View.GONE);
                    viewHolder.ounMessageLayout.setVisibility(View.VISIBLE);
                    viewHolder.ounMessageTextView.setText(friendlyMessage.getText());
                } else {
                    viewHolder.messageLayout.setVisibility(View.VISIBLE);
                    viewHolder.ounMessageLayout.setVisibility(View.GONE);
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messengerTextView.setText(friendlyMessage.getName());
                }
                Picasso.with(mContext)
                        .load(friendlyMessage.getPhotoUrl())
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder)
                        .into(viewHolder.messengerImageView);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mView.getLastPositionVisible();

                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    mView.scrollRecyclerToPosition(positionStart);
                }
            }
        });

        mView.setMessageAdapter(mFirebaseAdapter);
    }

    private void checkIfGroupHasMessages() {
        mFirebaseGroupReference.child(MESSAGES_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mView.setProgressBarVisibility(View.GONE);
                if (dataSnapshot.getValue() == null) {
                    mView.setEmptyViewVisibility(View.VISIBLE);
                } else {
                    mView.setEmptyViewVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mView.setProgressBarVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSendButtonClick(String message) {
        if (mInteractor.isMessageValid(message)) {
            mView.clearMessageText();
            FriendlyMessage friendlyMessage = new FriendlyMessage(message, mUser.getName(), mUser.getEmail(),
                    mInteractor.getPhotoUrl(mUser.getId()));
            mFirebaseGroupReference.child(MESSAGES_CHILD)
                    .push().setValue(friendlyMessage);
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView ounMessageTextView;
        TextView messengerTextView;
        CircleImageView messengerImageView;
        LinearLayout messageLayout;
        RelativeLayout ounMessageLayout;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            ounMessageTextView = (TextView) itemView.findViewById(R.id.ounMessageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            messageLayout = (LinearLayout) itemView.findViewById(R.id.message_layout);
            ounMessageLayout = (RelativeLayout) itemView.findViewById(R.id.oun_message_layout);
        }
    }
}