package com.example.finalproject;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import static com.example.finalproject.R.layout.activity_recyclerviewitem;

import java.util.ArrayList;

//https://www.ethangardner.com/posts/color-theory-math/
//complementary (2): H1 = |(H0 + 180 degrees) - 360 degrees|

//triad (3):
//H1 = |(H0 + 120 degrees) - 360 degrees|
//H2 = |(H0 + 240 degrees) - 360 degrees|

//analogous (2+):
//formula: H1 = |(H0 + 30 degrees) - 360 degrees|
//formula: H2 = |(H0 + 60 degrees) - 360 degrees|
//formula: H3 = |(H0 + 90 degrees) - 360 degrees|

//int rgb = Color.HSBtoRGB(hue, saturation, brightness);
//        red = (rgb>>16)&0xFF;
//        green = (rgb>>8)&0xFF;
//        blue = rgb&0xFF;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    public ArrayList<String> list;
    Context context;
    static boolean expand=false;

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
//
//        if(!expand) holder.expandableLayout.setVisibility(View.INVISIBLE);
//        else holder.expandableLayout.setVisibility(View.VISIBLE);
        holder.expandableLayout.setVisibility(expand ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView color1TextView,color2TextView, color3TextView,color4TextView,color5TextView;

        public TextView aColor1TextView,aColor2TextView, aColor3TextView, aColor4TextView, aColor5TextView;
        public TextView cColor1TextView,cColor2TextView, cColor3TextView, cColor4TextView, cColor5TextView;
        public TextView tColor1TextView, tColor2TextView, tColor3TextView, tColor4TextView, tColor5TextView;


        public LinearLayout myLayout;
        public LinearLayout expandableLayout;
        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView.findViewById(R.id.OgPalletlayout);
            expandableLayout= (LinearLayout) itemView.findViewById(R.id.ExpandableLayout);

            color1TextView = (TextView) itemView.findViewById(R.id.colorTextView1);
            color2TextView = (TextView) itemView.findViewById(R.id.colorTextView2);
            color3TextView = (TextView) itemView.findViewById(R.id.colorTextView3);
            color4TextView = (TextView) itemView.findViewById(R.id.colorTextView4);
            color5TextView = (TextView) itemView.findViewById(R.id.colorTextView5);

            aColor1TextView = (TextView) itemView.findViewById(R.id.analogusColorTextView1);
            aColor2TextView = (TextView) itemView.findViewById(R.id.analogusColorTextView2);
            aColor3TextView = (TextView) itemView.findViewById(R.id.analogusColorTextView3);
            aColor4TextView = (TextView) itemView.findViewById(R.id.analogusColorTextView4);
            aColor5TextView = (TextView) itemView.findViewById(R.id.analogusColorTextView5);

            cColor1TextView = (TextView) itemView.findViewById(R.id.complemnataryColorTextView1);
            cColor2TextView = (TextView) itemView.findViewById(R.id.complemnataryColorTextView2);
            cColor3TextView = (TextView) itemView.findViewById(R.id.complemnataryColorTextView3);
            cColor4TextView = (TextView) itemView.findViewById(R.id.complemnataryColorTextView4);
            cColor5TextView = (TextView) itemView.findViewById(R.id.complemnataryColorTextView5);

            tColor1TextView = (TextView) itemView.findViewById(R.id.triadColorTextView1);
            tColor2TextView = (TextView) itemView.findViewById(R.id.triadColorTextView2);
            tColor3TextView = (TextView) itemView.findViewById(R.id.triadColorTextView3);
            tColor4TextView = (TextView) itemView.findViewById(R.id.triadColorTextView4);
            tColor5TextView = (TextView) itemView.findViewById(R.id.triadColorTextView5);


//            itemView.setOnClickListener(this);
            context = itemView.getContext();

            myLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!expand) expand=true;
                    else expand=false;
                    Toast.makeText(context, ""+expand, Toast.LENGTH_SHORT).show();
                    notifyItemChanged(getAdapterPosition());
                }
            });



        }



    }

}
