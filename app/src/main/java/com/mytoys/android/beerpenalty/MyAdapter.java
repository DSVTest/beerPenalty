package com.mytoys.android.beerpenalty;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListPenaltysQuery;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListPenaltysQuery.Item> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private NameListener nameListener;


    // data is passed into the constructor
    MyAdapter(Context context, NameListener nameListener) {
        this.mInflater = LayoutInflater.from(context);
        this.nameListener = nameListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListPenaltysQuery.Item item = mData.get(position);
        holder.txt_name.setText(item.name());
        holder.txt_beers.setText(item.beers().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameListener.nameSelected(item.name());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // resets the list with a new set of data
    public void setItems(List<ListPenaltysQuery.Item> items) {
        mData = items;
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name;
        TextView txt_beers;

        ViewHolder(View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_title);
            txt_beers = itemView.findViewById(R.id.txt_description);
        }
    }
}
