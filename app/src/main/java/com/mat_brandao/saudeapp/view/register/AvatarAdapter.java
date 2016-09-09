package com.mat_brandao.saudeapp.view.register;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {
    private final GenericObjectClickListener<Integer> mListener;
    private Context mContext;
    private List<Integer> mList;

    public AvatarAdapter(Context context, GenericObjectClickListener<Integer> listener) {
        mContext = context;
        mListener = listener;
        mList = new ArrayList<>();
        mList.add(R.drawable.avatar_placeholder);
        mList.add(R.drawable.men1);
        mList.add(R.drawable.woman1);
        mList.add(R.drawable.men2);
        mList.add(R.drawable.woman2);
        mList.add(R.drawable.men3);
        mList.add(R.drawable.woman3);
        mList.add(R.drawable.men4);
        mList.add(R.drawable.woman4);
    }

    @Override
    public AvatarViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_avatar_layout, viewGroup, false);
        return new AvatarViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(AvatarViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mList.get(position))
                .into(holder.avatarImage);
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public class AvatarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final GenericObjectClickListener<Integer> vListener;
        @Bind(R.id.avatar_image)
        CircleImageView avatarImage;

        public AvatarViewHolder(View itemView, GenericObjectClickListener<Integer> listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            vListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            vListener.onItemClick(mList.get(getAdapterPosition()));
        }
    }
}
