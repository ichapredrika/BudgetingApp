package com.icha.budgetingapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icha.budgetingapp.AddFragment;
import com.icha.budgetingapp.R;
import com.icha.budgetingapp.entity.Trans;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RepeatingTransAdapter extends RecyclerView.Adapter<RepeatingTransAdapter.TransViewHolder> {
    private DecimalFormat df = new DecimalFormat("#,###");
    private ArrayList<Trans> listTrans = new ArrayList<>();
    private Activity activity;

    public RepeatingTransAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Trans> getListTrans() {
        return listTrans;
    }

    public void setListTrans(ArrayList<Trans> listTrans) {
        if (listTrans.size() > 0) {
            this.listTrans.clear();
        }
        this.listTrans.addAll(listTrans);
        notifyDataSetChanged();
    }

    public void addItem(Trans trans) {
        this.listTrans.add(trans);
        notifyItemInserted(listTrans.size() - 1);
    }

    public void updateItem(int position, Trans trans) {
        this.listTrans.set(position, trans);
        notifyItemChanged(position, trans);
    }

    public void removeItem(int position) {
        this.listTrans.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listTrans.size());
    }

    @NonNull
    @Override
    public TransViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trans, parent, false);
        return new TransViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransViewHolder holder, int position) {
        String amount = holder.itemView.getContext().getString(R.string.format_amount, df.format(listTrans.get(position).getAmount()));
        String type = listTrans.get(position).getType();
        holder.tvDate.setText(listTrans.get(position).getDate());
        holder.tvDescription.setText(listTrans.get(position).getDescription());
        if (type.equals(AddFragment.EXTRA_INCOME)) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorAccent));
        } else
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorNegative));
        holder.tvAmount.setText(amount);

    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    class TransViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDate, tvDescription, tvAmount;

        TransViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.txt_amount);
            tvDescription = itemView.findViewById(R.id.txt_description);
            tvDate = itemView.findViewById(R.id.txt_date);
        }
    }
}
