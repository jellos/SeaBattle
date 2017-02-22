package com.kipsap.jshipbattle;

import java.util.List;

import com.kipsap.commonsource.JConstants;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectedAdapter extends ArrayAdapter {

	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	private List<?> names;
	private List<?> ratings;
	private List<?> countrys;
	Context _context;
	
	@SuppressWarnings("unchecked")
	public SelectedAdapter(Context context, int textViewResourceId, List<?> theNames, List<?> theRatings, List<?> theCountrys)
	{		
		super(context, textViewResourceId, theNames);
		_context = context;
		names = theNames;
		ratings = theRatings;
		countrys = theCountrys;
	}

	public void setSelectedPosition(int pos)
	{
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public int getSelectedPosition()
	{
		return selectedPos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
	    View v = convertView;

	    // only inflate the view if it's null
	    if (v == null) 
	    {
	        LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = vi.inflate(R.layout.listitem_flag_friend_rating, null);
	    }

	    // get text view
	    //TextView label = (TextView)v.findViewById(R.id.friendTextItem);
	    TextView rank = (TextView) v.findViewById(R.id.txtRank);
	    TextView friendName = (TextView) v.findViewById(R.id.txtFriendName);
	    TextView friendRating = (TextView) v.findViewById(R.id.txtFriendRating);
	    ImageView imFlag = (ImageView) v.findViewById(R.id.flagimage);

        // change the row color based on selected state
        if (selectedPos == position)
        {
        	//friendName.setBackgroundColor(Color.BLUE);
        	//friendRating.setBackgroundColor(Color.BLUE);
        	rank.setBackgroundColor(_context.getResources().getColor(R.color.background_blue));
        	friendName.setBackgroundColor(_context.getResources().getColor(R.color.background_blue));
        	imFlag.setBackgroundColor(_context.getResources().getColor(R.color.background_blue));
        	friendRating.setBackgroundColor(_context.getResources().getColor(R.color.background_blue));
        }
        else
        {
        	rank.setBackgroundColor(Color.BLACK);
        	friendName.setBackgroundColor(Color.BLACK);
        	imFlag.setBackgroundColor(Color.BLACK);
        	friendRating.setBackgroundColor(Color.BLACK);
        }
        
        rank.setText("" + (position+1));
        friendName.setText(names.get(position).toString());
        friendRating.setText(ratings.get(position).toString());
        imFlag.setImageResource(JConstants.getCountryFlagResourceID(countrys.get(position).toString()));
        
        return(v);
	}	
}