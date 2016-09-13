package com.mat_brandao.saudeapp.view.remedy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class RemedyAdapter extends RecyclerView.Adapter<RemedyAdapter.RemedyViewHolder> {
    private Context mContext;
    private List<Remedy> mList;

    public RemedyAdapter(Context context, List<Remedy> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RemedyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_remedy_layout, viewGroup, false);
        return new RemedyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RemedyViewHolder holder, int position) {
        Remedy remedy = mList.get(position);
        holder.itemRemedyName.setText(GenericUtil.capitalize(remedy.getProduto()));
        holder.itemRemedyActivePrincipal.setText(GenericUtil.capitalize(remedy.getPrincipioAtivo()));
        holder.itemRemedyLaboratory.setText(GenericUtil.capitalize(remedy.getLaboratorio()));
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public class RemedyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_remedy_name)
        TextView itemRemedyName;
        @Bind(R.id.item_remedy_active_principal)
        TextView itemRemedyActivePrincipal;
        @Bind(R.id.item_remedy_laboratory)
        TextView itemRemedyLaboratory;

        public RemedyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
