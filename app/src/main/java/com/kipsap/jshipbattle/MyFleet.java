package com.kipsap.jshipbattle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MyFleet extends View implements OnTouchListener
{
	public static final int PAUSE = 0;
    public static final int RUNNING = 1;
    
    private int running = RUNNING;    
    private ShootField _myFleet;    
    private long _moveDelay = 100; // was 50
    private Context _context;
        
    int crosshair = -1; // for showing opponent's last shot position
    int width, height, rand, zijde;
    float cellsize;
    float crosshairovershoot = 0.4f;
    Bitmap bsea, croha,
    ship3_hor, ship3_ver, ship3_hor_sunk, ship3_ver_sunk, 
    ship2_hor, ship2_ver, ship2_hor_sunk, ship2_ver_sunk,
    miss, explo, explo_gray,
    ship5, ship5_sunk,
    ship4, ship4_sunk;
    
    Rect bat_src, cru_src, fri_hor, fri_ver, rms_hor, rms_ver, searect;
    Rect bat_hor_dest, bat_ver_dest, cru_hor_dest, cru_ver_dest, fri1_hor_dest, fri1_ver_dest,
    	 fri2_hor_dest, fri2_ver_dest, rms1_hor_dest, rms1_ver_dest,
    	 rms2_hor_dest, rms2_ver_dest,
    	 rms3_hor_dest, rms3_ver_dest, searect_dest;
    
    SharedPreferences sharedPrefs;
    int themeID;
	
    private RefreshHandler _redrawHandler = new RefreshHandler();
    
	class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
        	MyFleet.this.update();
        	MyFleet.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
	
    public MyFleet(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        this.setOnTouchListener(this);
        _context = context;
        _myFleet = new ShootField(context);
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
    {
    	// Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) 
	    {	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) 
	        {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
    
    public static Bitmap decodeSampledBitmapFromResource(Resources reso, int resId, int reqWidth, int reqHeight) 
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(reso, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(reso, resId, options);
    }
    
    public void setTheme(int theme)
    {
    	themeID = theme;    	
        
        			
        
    }
    
    public void recycleBitmaps()
    {
    	bsea.recycle();    	
    	ship5.recycle();
    	ship5_sunk.recycle();
	    ship4.recycle();
	   	ship4_sunk.recycle();
	   	ship3_hor.recycle();
	   	ship3_ver.recycle();
	   	ship3_hor_sunk.recycle();
	   	ship3_ver_sunk.recycle();
	   	ship2_hor.recycle();
	   	ship2_ver.recycle();
	   	ship2_hor_sunk.recycle();
	   	ship2_ver_sunk.recycle();
	    miss.recycle();
	    explo.recycle();
	    explo_gray.recycle();
	    
	    bsea = null;
	    ship5 = null;	    
	    ship5_sunk = null;
	   	ship4 = null;
	   	ship4_sunk = null;
	    ship3_hor = null;
	   	ship3_ver = null;
	   	ship3_hor_sunk = null;
	   	ship3_ver_sunk = null;
	   	ship2_hor = null;
	   	ship2_ver = null;
	   	ship2_hor_sunk = null;
	   	ship2_ver_sunk = null;
	    miss = null;
	    explo = null;
	    explo_gray = null;
    }
    
    public void init(int h, int w)
    {
    	 width = w;
    	 height = h;    	 
    	 zijde = Math.min(width, height);
         rand = (int) (0.076 * zijde) - 1;
         cellsize = (float)((float)(zijde - 2*rand) / 10.f);
         _myFleet.setCellSize(cellsize);                  
         
         bat_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(0)%10 * cellsize),
        		 				 rand + (int)(_myFleet.getShipOrigin(0)/10 * cellsize), 
        		 				 rand + (int)((_myFleet.getShipOrigin(0)%10 + 5) * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(0)/10 * cellsize) + (int)(cellsize));
         bat_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(0)%10 * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(0)/10 * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(0)%10 * cellsize) + (int)cellsize, 
        		 				 rand + (int)((_myFleet.getShipOrigin(0)/10 + 5) * cellsize));
         cru_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(1)%10 * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(1)/10 * cellsize), 
        		 				 rand + (int)((_myFleet.getShipOrigin(1)%10 + 4) * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(1)/10 * cellsize) + (int)(cellsize));
         cru_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(1)%10 * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(1)/10 * cellsize), 
        		 				 rand + (int)(_myFleet.getShipOrigin(1)%10 * cellsize) + (int)(cellsize), 
        		 				 rand + (int)((_myFleet.getShipOrigin(1)/10 + 4) * cellsize));
         fri1_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(2)%10 * cellsize), 
				 				  rand + (int)(_myFleet.getShipOrigin(2)/10 * cellsize), 
				 				  rand + (int)((_myFleet.getShipOrigin(2)%10 + 3) *cellsize), 
				 				  rand + (int)(_myFleet.getShipOrigin(2)/10 * cellsize) + (int)(cellsize));
         fri1_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(2)%10 * cellsize), 
				 				  rand + (int)(_myFleet.getShipOrigin(2)/10 * cellsize), 
				 				  rand + (int)(_myFleet.getShipOrigin(2)%10 * cellsize) + (int)(cellsize), 
				 				  rand + ((int)(_myFleet.getShipOrigin(2)/10 + 3) * (int)(cellsize)));
		fri2_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(3)%10 * cellsize), 
								  rand + (int)(_myFleet.getShipOrigin(3)/10 * cellsize), 
								  rand + (int)((_myFleet.getShipOrigin(3)%10 + 3) * cellsize), 
								  rand + (int)(_myFleet.getShipOrigin(3)/10 * cellsize) + (int)(cellsize));
		fri2_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(3)%10 * cellsize), 
								  rand + (int)(_myFleet.getShipOrigin(3)/10 * cellsize), 
								  rand + (int)(_myFleet.getShipOrigin(3)%10 * cellsize) + (int)(cellsize), 
								  rand + ((int)(_myFleet.getShipOrigin(3)/10 + 3) * (int)(cellsize)));
		rms1_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(4)%10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(4)/10 * cellsize), 
								 rand + (int)((_myFleet.getShipOrigin(4)%10 + 2) * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(4)/10 * cellsize) + (int)(cellsize));
		rms1_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(4)%10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(4)/10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(4)%10 * cellsize) + (int)(cellsize), 
								 rand + (int)((_myFleet.getShipOrigin(4)/10 + 2) * cellsize));
		rms2_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(5)%10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(5)/10 * cellsize), 
								 rand + (int)((_myFleet.getShipOrigin(5)%10 + 2) * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(5)/10 * cellsize) + (int)(cellsize));
		rms2_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(5)%10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(5)/10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(5)%10 * cellsize) + (int)(cellsize), 
								 rand + (int)((_myFleet.getShipOrigin(5)/10 + 2) * cellsize));
		rms3_hor_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(6)%10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(6)/10 * cellsize), 
								 rand + (int)((_myFleet.getShipOrigin(6)%10 + 2) * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(6)/10 * cellsize) + (int)(cellsize));
		rms3_ver_dest = new Rect(rand + (int)(_myFleet.getShipOrigin(6)%10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(6)/10 * cellsize), 
								 rand + (int)(_myFleet.getShipOrigin(6)%10 * cellsize) + (int)(cellsize), 
								 rand + (int)((_myFleet.getShipOrigin(6)/10 + 2) * cellsize));
		
		
		
		
		ship5 = (_myFleet.getShipDirection(0) ? 
        		decodeSampledBitmapFromResource(getResources(), R.drawable.b5_ver, (int)(cellsize), (int)(5*cellsize)) :
        		decodeSampledBitmapFromResource(getResources(), R.drawable.b5_hor, (int)(5*cellsize), (int)cellsize));
        
        ship5_sunk = (_myFleet.getShipDirection(0) ? 
        		decodeSampledBitmapFromResource(getResources(), R.drawable.b5_ver_sunk, (int)(cellsize), (int)(5*cellsize)) :
	        	decodeSampledBitmapFromResource(getResources(), R.drawable.b5_hor_sunk, (int)(5*cellsize), (int)cellsize));
        
        croha = decodeSampledBitmapFromResource(getResources(), R.drawable.crosshair3, (int)((1 + 2*crosshairovershoot) * cellsize), (int)((1 + 2*crosshairovershoot) * cellsize));
                	               
        ship4 = (_myFleet.getShipDirection(1) ? 
        		decodeSampledBitmapFromResource(getResources(), R.drawable.b4_ver, (int)(cellsize), (int)(4*cellsize)) :
		        decodeSampledBitmapFromResource(getResources(), R.drawable.b4_hor, (int)(4*cellsize), (int)cellsize));
        
        ship4_sunk = (_myFleet.getShipDirection(1) ? 
        		decodeSampledBitmapFromResource(getResources(), R.drawable.b4_ver_sunk, (int)(cellsize), (int)(4*cellsize)) :
			    decodeSampledBitmapFromResource(getResources(), R.drawable.b4_hor_sunk, (int)(4*cellsize), (int)cellsize));
        
        ship3_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b3_hor, (int)(3*cellsize), (int)cellsize);
        ship3_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b3_ver, (int)(cellsize), (int)(3*cellsize));
        ship3_hor_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b3_hor_sunk, (int)(3*cellsize), (int)cellsize);
        ship3_ver_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b3_ver_sunk, (int)(cellsize), (int)(3*cellsize));
        ship2_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b2_hor, (int)(2*cellsize), (int)cellsize);
        ship2_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b2_ver, (int)(cellsize), (int)(2*cellsize));
        ship2_hor_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b2_hor_sunk, (int)(2*cellsize), (int)cellsize);
        ship2_ver_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b2_ver_sunk, (int)(cellsize), (int)(2*cellsize));
        
        miss = decodeSampledBitmapFromResource(getResources(), R.drawable.miss, (int)(cellsize), (int)(cellsize));
        explo = decodeSampledBitmapFromResource(getResources(), R.drawable.explo2, (int)(cellsize), (int)(cellsize));
        explo_gray = decodeSampledBitmapFromResource(getResources(), R.drawable.explo2_gray, (int)(cellsize), (int)(cellsize));	        
        
        switch (themeID)
		{
			case 0:				
				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_caribbean, width, height);
				break;
			case 1:
				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_swamp, width, height);
				miss = decodeSampledBitmapFromResource(getResources(), R.drawable.miss_brown, (int)(cellsize), (int)(cellsize));
				break;
			case 2:
				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_polar, width, height);
				miss = decodeSampledBitmapFromResource(getResources(), R.drawable.miss_white, (int)(cellsize), (int)(cellsize));
				break;
			case 3:
				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_volcano, width, height);
				explo = decodeSampledBitmapFromResource(getResources(), R.drawable.explo_green, (int)(cellsize), (int)(cellsize));
				miss = decodeSampledBitmapFromResource(getResources(), R.drawable.miss_black, (int)(cellsize), (int)(cellsize));
				break;
			case 4:
				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_poisoned_sea, width, height);
				miss = decodeSampledBitmapFromResource(getResources(), R.drawable.miss_black, (int)(cellsize), (int)(cellsize));
				break;
		}
        
        bat_src = new Rect(0, 0, ship5.getWidth(), ship5.getHeight());
        cru_src = new Rect(0, 0, ship4.getWidth(), ship4.getHeight());
        fri_hor = new Rect(0, 0, ship3_hor.getWidth(), ship3_hor.getHeight());
        fri_ver = new Rect(0, 0, ship3_ver.getWidth(), ship3_ver.getHeight());
        rms_hor = new Rect(0, 0, ship2_hor.getWidth(), ship2_hor.getHeight());
        rms_ver = new Rect(0, 0, ship2_ver.getWidth(), ship2_ver.getHeight());
        searect = new Rect(0, 0, bsea.getWidth(), bsea.getHeight());
	         
	   	        
    }

    public boolean onTouch(View view, MotionEvent event) 
    {
		return false;    	
    }
    
    public void setMode(int mode) {
    	running = mode;
        if (mode == RUNNING) 
        {
        	update();
            return;
        }
        if (mode == PAUSE) 
        {
            // TODO: implement.
        	//System.out.println("Paused");
        }
    }  
    
    @Override
    protected void onDraw(Canvas canvas) 
    {
    	canvas.drawBitmap(bsea, searect, new Rect(0, 0, zijde, zijde), null); 
    	float cz = _myFleet.getCellSize();
        
        int i;
        boolean sd, ss;
        for (i = 0; i < 7; i++)
        {
        	sd = _myFleet.getShipDirection(i);
	        ss = _myFleet.getShipState(i); //sunken or not	        
	        	        
	        if (i == 0) // battleship
	        {		       
		    	canvas.drawBitmap((ss? ship5_sunk : ship5), bat_src, (sd ? bat_ver_dest : bat_hor_dest), null);		        
	        }
	        else if (i == 1) // cruiser
	        {
		        canvas.drawBitmap((ss? ship4_sunk : ship4), cru_src, (sd ? cru_ver_dest : cru_hor_dest), null);
	        }        
	        else if (i == 2) // frigate 1
	        {
	        	if (!sd)
		        {
	        		canvas.drawBitmap((ss? ship3_hor_sunk : ship3_hor), fri_hor, fri1_hor_dest, null);
		        }
		        else
		        {    
		        	canvas.drawBitmap((ss? ship3_ver_sunk : ship3_ver), fri_ver, fri1_ver_dest, null);
		        }
	        }
	        else if (i == 3) // frigate 2
	        {
	        	if (!sd)
		        {
	        		canvas.drawBitmap((ss? ship3_hor_sunk : ship3_hor), fri_hor, fri2_hor_dest, null);
		        }
		        else
		        {    
		        	canvas.drawBitmap((ss? ship3_ver_sunk : ship3_ver), fri_ver, fri2_ver_dest, null);
		        }
	        }
	        else if (i == 4) // minesw. 1
	        {
	        	if (!sd)
		        {
	        		canvas.drawBitmap((ss? ship2_hor_sunk : ship2_hor), rms_hor, rms1_hor_dest, null);
		        }
		        else
		        {    
		        	canvas.drawBitmap((ss? ship2_ver_sunk : ship2_ver), rms_ver, rms1_ver_dest, null);
		        }
	        }
	        else if (i == 5) // minesw. 2
	        {
	        	if (!sd)
		        {
	        		canvas.drawBitmap((ss? ship2_hor_sunk : ship2_hor), rms_hor, rms2_hor_dest, null);
		        }
		        else
		        {    
		        	canvas.drawBitmap((ss? ship2_ver_sunk : ship2_ver), rms_ver, rms2_ver_dest, null);
		        }
	        }
	        else if (i == 6) // minesw. 3
	        {
	        	if (!sd)
		        {
	        		canvas.drawBitmap((ss? ship2_hor_sunk : ship2_hor), rms_hor, rms3_hor_dest, null);
		        }
		        else
		        {    
		        	canvas.drawBitmap((ss? ship2_ver_sunk : ship2_ver), rms_ver, rms3_ver_dest, null);
		        }
	        }
        }
        
     // draw shiphits, etc ..
        for (int h = 0; h < ConfigureField.HEIGHT; h++) 
        {
            for (int w = 0; w < ConfigureField.WIDTH; w++) 
            {
            	// 0: no boat, no shot
            	// 1: no boat, missed shot
            	// 2: boat, no shot
            	// 3: boat, shot
            	// 4: boat, sunken
            	if (_myFleet.getGridValue(h, w) == 1) // for a shot in the water
                {            		
            		canvas.drawBitmap(miss, new Rect(0, 0, miss.getWidth(), miss.getHeight()), 
        				new Rect(rand + (int)(w * cz), rand + (int)(h * cz), rand + (int)(w * cz) + (int)(cz -1), rand + (int)(h * cz) + (int)(cz -1)), null);           		            
                } 
            	else if (_myFleet.getGridValue(h, w) == 3) // a hit boat
                {                   
            		 canvas.drawBitmap(explo, 
             				new Rect(0, 0, explo.getWidth(), explo.getHeight()), 
             				new Rect(rand + (int) (w * cz) - (int) (0.05f*cz),
             						rand + (int) (h * cz) - (int) (0.05f*cz), 
             						rand + (int) (w * cz) + (int)((1.05f)*cz), 
             						rand + (int) (h * cz) + (int)((1.05f)*cz)), 
             				null); 
                }            	
            	else if (_myFleet.getGridValue(h, w) == 4) // for ship sunken!
                { 
            		canvas.drawBitmap(explo_gray,
            				new Rect(0, 0, explo_gray.getWidth(), explo_gray.getHeight()),
            				new Rect(rand + (int) (w * cz) - (int) (0.05f*cz),
            						rand + (int) (h * cz) - (int) (0.05f*cz), 
            						rand + (int) (w * cz) + (int)((1.05f)*cz), 
            						rand + (int) (h * cz) + (int)((1.05f)*cz)), 
            				null);
                } 
            }
        }  
        
        // draw crosshair to show opponent's last shot position
        if ((crosshair >= 0) && (crosshair <= 99))
        {
        	canvas.drawBitmap(croha, new Rect(0, 0, croha.getWidth(), croha.getHeight()), 
				new Rect(rand + (int) ((crosshair % 10) * cz) - (int) (crosshairovershoot*cz),
        		rand + (int) ((crosshair / 10) * cz) - (int) (crosshairovershoot*cz), 
                rand + (int) ((crosshair % 10) * cz) + ((int) ((1.f + crosshairovershoot)*cz) -1),
                rand + (int) ((crosshair / 10) * cz) + ((int)((1.f + crosshairovershoot)*cz) -1)), null);
        }
        else
        {
        	
        }
    }
    
    private void update() 
    {
    	if (running == RUNNING) 
    	{
    		//setDelay();   	
    		_redrawHandler.sleep(_moveDelay);
    	}
    	else
    	{
    		//System.out.println("Paused ..");
    	}
    }
    
    public void setConfiguredBoard(long myShipboard, int myShipdirections, long h1, long h2)
    {
    	_myFleet.setInitialBoard(myShipboard, myShipdirections, h1, h2);
    }
    
    public long getHits1()
    {
    	return _myFleet.getHits1();
    }
    
    public long getHits2()
    {
    	return _myFleet.getHits2();
    }
    
    public int updateFireShotsTEST(long h1, long h2)
    {
    	return _myFleet.updateFireShotsTEST(h1, h2);
    }
    
    public int updateFireShots(long h1, long h2)
    {
    	return _myFleet.updateFireShots(h1, h2);
    }
    
    public int getNumberOfShots()
	{
		return _myFleet.getNumberOfShots();
	}
	
	public int getNumberOfHits()
	{
		return _myFleet.getNumberOfHits();
	}
	
	public void setLastShotPosition(int lastShot)
	{
		crosshair = lastShot;
	}
}
