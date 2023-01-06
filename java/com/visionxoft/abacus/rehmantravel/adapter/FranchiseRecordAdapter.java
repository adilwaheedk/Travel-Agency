package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.FranchiseRecordFragment;
import com.visionxoft.abacus.rehmantravel.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class FranchiseRecordAdapter extends RecyclerView.Adapter<FranchiseRecordAdapter.MyViewHolder> {

    private Context context;
    private int item_count = 1;
    //private List<String> stringList = new ArrayList<>();

    /**
     * Maintain view and list of Franchise Employee/Business records
     *
     * @param fragment Parent fragment class
     */
    public FranchiseRecordAdapter(FranchiseRecordFragment fragment) {
        context = fragment.getContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_franchise_record, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // Add/Remove Button
        if (position == 0) {
            ImageView img = (ImageView) holder.btn_rem_fran_rec.findViewById(R.id.btn_rem_fran_rec_img);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img.setImageDrawable(context.getDrawable(R.drawable.ic_add_white_18dp));
                holder.btn_rem_fran_rec.setBackground(context.getDrawable(R.drawable.selector_fab_primary));
            } else {
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_white_18dp));
                holder.btn_rem_fran_rec.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selector_fab_primary));
            }

            // ADD ROW
            holder.btn_rem_fran_rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_count++;
                    notifyItemInserted(item_count - 1);
                }
            });
        } else {

            // REMOVE ROW
            holder.btn_rem_fran_rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_count--;
                    notifyItemRemoved(position);
                }
            });
        }

        holder.fran_rec_period.setText("");
        holder.fran_rec_position.setText("");
        holder.fran_rec_business.setText("");
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return item_count;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        protected final EditText fran_rec_period, fran_rec_position, fran_rec_business;
        protected final View btn_rem_fran_rec;

        MyViewHolder(View v) {
            super(v);
            fran_rec_period = (EditText) v.findViewById(R.id.fran_rec_period);
            fran_rec_position = (EditText) v.findViewById(R.id.fran_rec_position);
            fran_rec_business = (EditText) v.findViewById(R.id.fran_rec_business);
            btn_rem_fran_rec = v.findViewById(R.id.btn_rem_fran_rec);
        }
    }

}
