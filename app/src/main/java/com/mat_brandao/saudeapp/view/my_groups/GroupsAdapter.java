package com.mat_brandao.saudeapp.view.my_groups;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupMemberViewHolder> {
    private final GenericObjectClickListener<Grupo> mListener;

    private Context mContext;
    private List<Grupo> mList;

    public GroupsAdapter(Context context, List<Grupo> list, GenericObjectClickListener<Grupo> listener) {
        mContext = context;
        mList = list;
        mListener = listener;
    }

    @Override
    public GroupMemberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group_layout, viewGroup, false);
        return new GroupMemberViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(GroupMemberViewHolder holder, int position) {
        Grupo grupo = mList.get(position);
        holder.itemGroupName.setText(grupo.getDescricao());
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    class GroupMemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final GenericObjectClickListener<Grupo> vListener;
        @Bind(R.id.item_group_name)
        TextView itemGroupName;

        GroupMemberViewHolder(View itemView, GenericObjectClickListener<Grupo> listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            vListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (vListener != null)
                vListener.onItemClick(mList.get(getAdapterPosition()));
        }
    }
}
