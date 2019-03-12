package com.example.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class LightingAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list; //list of light names
    private Context context;
    private LightController light;

    public LightingAdapter(ArrayList<String> list, Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return list.get(position).getId();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.light_bar,null);
        }

        TextView listItemText = (TextView)view.findViewById(R.id.lightName);
        listItemText.setText(list.get(position));
        light = new LightController(listItemText.toString());

        ImageButton settingsBtn = (ImageButton)view.findViewById(R.id.lightSettingsBtn);
        final Switch lightSwitch = (Switch)view.findViewById(R.id.lightSwitch);

        settingsBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LightingOptionsActivity.class);
                intent.putExtra(Constants.LightKey,light.toString());
                context.startActivity(intent);
                notifyDataSetChanged();

            }
        });
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) light.turnOn();
                else light.turnOff();
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
