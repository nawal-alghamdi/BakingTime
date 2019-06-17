package com.example.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakingtime.R;
import com.example.bakingtime.data.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private Context context;
    private List<Step> steps;
    private View.OnClickListener onItemClickListener;


    public ListAdapter(Context context, List<Step> steps) {
        this.context = context;
        this.steps = steps;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
        ListViewHolder viewHolder = new ListViewHolder(view);
        viewHolder.constraintLayout.setPadding(18, 18, 18, 18);
        viewHolder.itemNameTextView.setTextSize(24);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int i) {
        String itemName;
        if (i == 0) {
            itemName = context.getString(R.string.ingredients);
        } else if (i == 1) {
            itemName = context.getString(R.string.recipe_introduction);
        } else itemName = context.getString(R.string.step) + (i - 1);
        holder.itemNameTextView.setText(itemName);
    }

    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        return steps.size() + 1;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
    }


    public class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name_textView)
        TextView itemNameTextView;
        @BindView(R.id.list_item_constraint_layout)
        ConstraintLayout constraintLayout;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // SetTag() as current view holder along with
            // setOnClickListener() as your local View.OnClickListener variable.
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

}
