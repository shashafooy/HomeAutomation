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
    private ArrayList<LightController> list; //list of light names
    private Context context;
    // private LightController light;

    public LightingAdapter(ArrayList<LightController> list, Context context){
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
    public void updateList(ArrayList<LightController> list){
        this.list=list;
    }

    public ArrayList<LightController> getList(){
        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.light_bar,null);
        }

        TextView listItemText = (TextView)view.findViewById(R.id.lightName);
        listItemText.setText(list.get(position).getLightName());
        //light = new LightController(listItemText.getText().toString());

        ImageButton settingsBtn = (ImageButton)view.findViewById(R.id.lightSettingsBtn);
        final Switch lightSwitch = (Switch)view.findViewById(R.id.lightSwitch);

        settingsBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),LightingOptionsActivity.class);
                intent.putExtra(Constants.LightKey,list.get(position));
                ((Activity) context).startActivityForResult(intent, Constants.RequestLightCode);
                notifyDataSetChanged();

            }
        });
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) list.get(position).turnOn();
                else list.get(position).turnOff();
                notifyDataSetChanged();
            }
        });

        ImageView timerIcon = view.findViewById(R.id.lightTimer);
        timerIcon.setVisibility(list.get(position).getTimerActive() ? View.VISIBLE : View.INVISIBLE);

        return view;
    }

    public void onActivityResult(int requestCode,int resultCode, Intent data){
        //Log.d("LightingAdapter","onActivityResult");
        LightController light, oldLight;

        switch(requestCode){
            case Constants.RequestLightCode:
                if(resultCode == Activity.RESULT_OK){
                    light = data.getParcelableExtra(Constants.LightKey);
                    oldLight = data.getParcelableExtra(Constants.OldLightKey);
                    int pos = 0;
                    for (LightController l : list){
                        if(l.getLightName().equals(oldLight.getLightName())){
                            break;
                        }
                        pos++;
                    }
                    if(pos==list.size()) throw new ArrayIndexOutOfBoundsException();
                    list.set(pos, light);
                }
                notifyDataSetChanged();

                break;
        }

    }
}
