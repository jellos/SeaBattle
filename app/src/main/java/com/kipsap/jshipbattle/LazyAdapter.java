package com.kipsap.jshipbattle;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<String> arls, udts;    
    private static LayoutInflater inflater = null;
    private ArrayList<Integer> colors;
    private ArrayList<Boolean> chatNotifs;
    
    public LazyAdapter(Activity a, ArrayList<Integer> css, ArrayList<String> gss, ArrayList<String> udt, ArrayList<Boolean> chn) 
    {
        activity = a;
        arls = gss;
        udts = udt;
        colors = css;
        chatNotifs = chn;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() 
    {
        return arls.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View vi=convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.custom_list_item, null);

        TextView text= (TextView) vi.findViewById(R.id.detext);
        TextView updateTime = (TextView) vi.findViewById(R.id.updated);
        ImageView image= (ImageView) vi.findViewById(R.id.image);
        ImageView chatnotification = (ImageView) vi.findViewById(R.id.chatnoti);
        text.setText(arls.get(position));
        updateTime.setText(udts.get(position));
        
        if (colors.get(position) == 3)
        	image.setImageResource(R.drawable.green_won);
        else if (colors.get(position) == 4)
        	image.setImageResource(R.drawable.red_lost);
        else if (colors.get(position) == 1)
        	image.setImageResource(R.drawable.green_light);
        else if (colors.get(position) == 2)
        	image.setImageResource(R.drawable.yellow_light);
        
        if (chatNotifs.get(position))
        	chatnotification.setVisibility(View.VISIBLE);
        else
        	chatnotification.setVisibility(View.INVISIBLE);
        
        return vi;
    }
}