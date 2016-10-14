package com.mat_brandao.saudeapp.view.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.MembroGrupo;
import com.mat_brandao.saudeapp.domain.util.DateUtil;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.GroupMemberViewHolder> {

    private final GenericObjectClickListener<MembroGrupo> mListener;
    private final GroupInteractorImpl mIteractor;
    private Context mContext;
    private List<MembroGrupo> mList;

    public GroupMembersAdapter(Context context, List<MembroGrupo> list, GenericObjectClickListener<MembroGrupo> listener) {
        mContext = context;
        mList = list;
        mListener = listener;
        mIteractor = new GroupInteractorImpl(mContext);


    }

    @Override
    public GroupMemberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group_member_layout, viewGroup, false);
        return new GroupMemberViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(GroupMemberViewHolder holder, int position) {
        MembroGrupo membroGrupo = mList.get(position);

        Picasso.with(mContext)
                .load("http://mobile-aceite.tcu.gov.br/appCivicoRS/rest/pessoas/" + membroGrupo.getUsuarioId() + "/fotoPerfil.png")
                .error(R.drawable.avatar_placeholder)
                .into(holder.memberAvatarImg);
        holder.lastInteractionDateText.setText("Entrou em: " + DateUtil.getFormattedDate(membroGrupo.getDataHoraAtivo()));
        if (membroGrupo.getUsuarioId() == mIteractor.getUser().getId()) {
            holder.memberNameText.setText("Você");
        } else {
            mIteractor.requestUser(membroGrupo.getUsuarioId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(throwable -> null)
                    .retry(3)
                    .subscribe(userResponse -> {
                        if (userResponse != null) {
                            if (userResponse.isSuccessful()) {
                                holder.memberNameText.setText(userResponse.body().getName());
                            } else {
                                holder.memberNameText.setText("Membro");
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    class GroupMemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final GenericObjectClickListener<MembroGrupo> vListener;

        @Bind(R.id.member_avatar_img)
        CircleImageView memberAvatarImg;
        @Bind(R.id.member_name_text)
        TextView memberNameText;
        @Bind(R.id.last_interaction_date_text)
        TextView lastInteractionDateText;

        GroupMemberViewHolder(View itemView, GenericObjectClickListener<MembroGrupo> listener) {
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
