package com.example.finalproject;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import static com.example.finalproject.R.layout.activity_recyclerviewitem;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    public ArrayList<String> list;
    Context context;

    public RecyclerViewAdapter(ArrayList<String> list) {
        this.list=list;
    }

    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(activity_recyclerviewitem,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        //set all the colours in the colour palette and their corresponding colour codes

        String[]  results = (list.get(position).toString()).split(",");
        holder.color1TextView.setText("#" + results[0]);
        holder.color1TextView.setBackgroundColor(Color.parseColor("#"+results[0]));
        holder.color2TextView.setText("#" + results[1]);
        holder.color2TextView.setBackgroundColor(Color.parseColor("#"+results[1]));
        holder.color3TextView.setText(" #" + results[2]);
        holder.color3TextView.setBackgroundColor(Color.parseColor("#"+results[2]));
        holder.color4TextView.setText("#" + results[3]);
        holder.color4TextView.setBackgroundColor(Color.parseColor("#"+results[3]));
        holder.color5TextView.setText("#" + results[4]);
        holder.color5TextView.setBackgroundColor(Color.parseColor("#"+results[4]));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView color1TextView;
        public TextView color2TextView;
        public TextView color3TextView;
        public TextView color4TextView;
        public TextView color5TextView;

        public LinearLayout myLayout;
        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            color1TextView = (TextView) itemView.findViewById(R.id.colorTextView1);
            color2TextView = (TextView) itemView.findViewById(R.id.colorTextView2);
            color3TextView = (TextView) itemView.findViewById(R.id.colorTextView3);
            color4TextView = (TextView) itemView.findViewById(R.id.colorTextView4);
            color5TextView = (TextView) itemView.findViewById(R.id.colorTextView5);

            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
        }
    }

}
