package com.example.homeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class LightingAdapter extends BaseAdapter implements ListAdapter {
    private Context context;
    // private LightController light;

    public LightingAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return myDatabase.getLightList().size();
    }

    @Override
    public Object getItem(int position) {
        return myDatabase.getLightList().get(position);
    }

    @Override
    public long getItemId(int position) {
        //return myDatabase.getLightList().get(position).getId();
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.light_bar,null);
        }

        TextView listItemText = view.findViewById(R.id.lightName);
        listItemText.setText(myDatabase.getLightList().get(position).getLightName());

        ImageButton settingsBtn = (ImageButton)view.findViewById(R.id.lightSettingsBtn);
        final Switch lightSwitch = (Switch)view.findViewById(R.id.lightSwitch);

        settingsBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),LightingOptionsActivity.class);
                intent.putExtra(Constants.LightKey,myDatabase.getLightList().get(position).getLightName()); //TODO check this
                ((Activity) context).startActivityForResult(intent, Constants.RequestLightCode);
                notifyDataSetChanged();

            }
        });
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) myDatabase.getLightList().get(position).turnOn();
                else myDatabase.getLightList().get(position).turnOff();
                notifyDataSetChanged();
            }
        });

        ImageView timerIcon = view.findViewById(R.id.lightTimer);
        timerIcon.setVisibility(myDatabase.getLightList().get(position).getTimerActive() ? View.VISIBLE : View.INVISIBLE);

        return view;
    }

    public void onActivityResult(int requestCode,int resultCode, Intent data){
        //Log.d("LightingAdapter","onActivityResult");
        LightController light, oldLight;

        switch(requestCode){
            case Constants.RequestLightCode:
                notifyDataSetChanged();

                break;
        }

    }
}
