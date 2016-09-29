package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class FavEstablishmentAdapter extends RecyclerView.Adapter<FavEstablishmentAdapter.FavEstablishmentViewHolder> {
    private final GenericObjectClickListener<Establishment> mListener;
    private Context mContext;
    private List<Establishment> mList;

    public FavEstablishmentAdapter(Context context, List<Establishment> list,
                                   GenericObjectClickListener<Establishment> listener) {
        mContext = context;
        mList = list;
        mListener = listener;
    }

    @Override
    public FavEstablishmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fav_establishment, viewGroup, false);
        return new FavEstablishmentViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(FavEstablishmentViewHolder holder, int position) {
        Establishment establishment = mList.get(position);

        Picasso.with(mContext)
                .load(getDrawableForCategory(establishment.getCategoriaUnidade().toLowerCase()))
                .into(holder.establishmentImage);

        String address = getAddressText(establishment.getLogradouro(),
                establishment.getNumero(), establishment.getBairro(), establishment.getCidade(),
                establishment.getUf(), establishment.getCep());

        holder.establishmentName.setText(GenericUtil.capitalize(establishment.getNomeFantasia()));
        holder.establishmentAddress.setText(address == null ?
                "Endereço não informado" : address);
        holder.establishmentPhone.setText(establishment.getTelefone() == null ?
                "Telefone não informado" : establishment.getTelefone());
    }

    private int getDrawableForCategory(String categoria) {
        int drawable;
        if (categoria.contains("consultório")) {
            drawable = R.drawable.ic_consultorio;
        } else if (categoria.contains("clínica")) {
            drawable = R.drawable.ic_clinica;
        } else if (categoria.contains("laboratório")) {
            drawable = R.drawable.ic_laboratorio;
        } else if (categoria.contains("urgência")) {
            drawable = R.drawable.ic_urgente;
        } else if (categoria.contains("hospital")) {
            drawable = R.drawable.ic_hospital;
        } else if (categoria.contains("atendimento domiciliar")) {
            drawable = R.drawable.ic_domiciliar;
        } else if (categoria.contains("posto de saúde")) {
            drawable = R.drawable.ic_urgente;
        } else if (categoria.contains("samu")) {
            drawable = R.drawable.ic_samu;
        } else {
            drawable = R.drawable.ic_urgente;
        }
        return drawable;
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    private String getAddressText(String logradouro, String numero, String bairro, String cidade, String uf, String cep) {
        String address = logradouro + ", Número: " + numero + ". " + GenericUtil.capitalize(bairro) + ", " +
                GenericUtil.capitalize(cidade) + ", " + uf + " - " + MaskUtil.mask("#####-###", cep);
        if (address.contains("null")) {
            return null;
        } else {
            return address;
        }
    }

    class FavEstablishmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final GenericObjectClickListener<Establishment> vListener;

        @Bind(R.id.establishment_image)
        ImageView establishmentImage;
        @Bind(R.id.item_establishment_name)
        TextView establishmentName;
        @Bind(R.id.item_establishment_address)
        TextView establishmentAddress;
        @Bind(R.id.item_establishment_phone)
        TextView establishmentPhone;

        public FavEstablishmentViewHolder(View itemView, GenericObjectClickListener<Establishment> listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            vListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (vListener != null) {
                vListener.onItemClick(mList.get(getAdapterPosition()));
            }
        }
    }
}
