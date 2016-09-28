package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class FavRemedyAdapter extends RecyclerView.Adapter<FavRemedyAdapter.FavRemedyViewHolder> {

    private final GenericObjectClickListener<Remedy> mListener;
    private Context mContext;
    private List<Remedy> mList;

    public FavRemedyAdapter(Context context, List<Remedy> list, GenericObjectClickListener<Remedy> listener) {
        mContext = context;
        mList = list;
        mListener = listener;
    }

    @Override
    public FavRemedyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_remedy_layout, viewGroup, false);
        return new FavRemedyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(FavRemedyViewHolder holder, int position) {
        Remedy remedy = mList.get(position);
        holder.itemRemedyName.setText(GenericUtil.capitalize(remedy.getProduto().toLowerCase()));
        holder.itemRemedyActivePrincipal.setText(GenericUtil.capitalize(remedy.getPrincipioAtivo().toLowerCase()));
        holder.itemRemedyLaboratory.setText(GenericUtil.capitalize(remedy.getLaboratorio().toLowerCase()));
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public class FavRemedyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private GenericObjectClickListener<Remedy> vListener;

        @Bind(R.id.item_remedy_name)
        TextView itemRemedyName;
        @Bind(R.id.item_remedy_active_principal)
        TextView itemRemedyActivePrincipal;
        @Bind(R.id.item_remedy_laboratory)
        TextView itemRemedyLaboratory;

        public FavRemedyViewHolder(View itemView, GenericObjectClickListener<Remedy> listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            vListener = listener;
        }

        @Override
        public void onClick(View view) {
            if (vListener != null) {
                vListener.onItemClick(mList.get(getAdapterPosition()));
            }
        }
    }
}
