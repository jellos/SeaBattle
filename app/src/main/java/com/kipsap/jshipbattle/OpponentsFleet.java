package com.kipsap.jshipbattle;

import android.content.Context;
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

public class OpponentsFleet extends View implements OnTouchListener 
{
	public static final int PAUSE = 0;
    public static final int RUNNING = 1;
    private Context _context;
    private int running = RUNNING;    
    private ShootField _oppFleet;    
    private long _moveDelay = 100; // was 50
    
    boolean crosshairenabled, gamefinished;
    int crosshair = 0, lastcrosshair;
    float crosshairovershoot = 0.4f;
    float cellsize;
    int width, height, rand, zijde, themeID;
    private Bitmap bsea, croha,
		    ship5_hor, ship5_ver, ship5_hor_sunk, ship5_ver_sunk,
		    ship4_hor, ship4_ver, ship4_hor_sunk, ship4_ver_sunk, 
		    ship3_hor, ship3_ver, ship3_hor_sunk, ship3_ver_sunk, 
		    ship2_hor, ship2_ver, ship2_hor_sunk, ship2_ver_sunk,
		    miss, explo, explo_gray;
    
    private Rect bat_hor_dest, bat_ver_dest, cru_hor_dest, cru_ver_dest, fri1_hor_dest, fri1_ver_dest,
			 fri2_hor_dest, fri2_ver_dest, rms1_hor_dest, rms1_ver_dest,
			 rms2_hor_dest, rms2_ver_dest,
			 rms3_hor_dest, rms3_ver_dest, searect_dest;
		    
    Rect bat_hor, bat_ver, cru_hor, cru_ver, fri_hor, fri_ver, rms_hor, rms_ver, searect;    
        
	
    private RefreshHandler _redrawHandler = new RefreshHandler();
    
	class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message message) 
        {
        	OpponentsFleet.this.update();
        	OpponentsFleet.this.invalidate();
        }

        public void sleep(long delayMillis) 
        {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);            
        }
    };
	
    public OpponentsFleet(Context context, AttributeSet attrs) 
    {
        super(context, attrs);        
        this.setOnTouchListener(this);
        _context = context;
        _oppFleet = new ShootField(context);
        
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
    	croha.recycle();
	    ship5_hor.recycle();
	    ship5_ver.recycle();
	    ship5_hor_sunk.recycle();
	    ship5_ver_sunk.recycle();	    
	   	ship4_hor.recycle();
	   	ship4_ver.recycle();
	   	ship4_hor_sunk.recycle();
	   	ship4_ver_sunk.recycle(); 
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
	    croha = null;
	    ship5_hor = null;
	    ship5_ver = null;
	    ship5_hor_sunk = null;
	    ship5_ver_sunk = null;  
	   	ship4_hor = null;
	   	ship4_ver = null;
	   	ship4_hor_sunk = null;
	   	ship4_ver_sunk = null;
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
         rand = (int) (0.076 * zijde);
         cellsize = (float)((float)(zijde - 2*rand) / 10.f);
         _oppFleet.setCellSize(cellsize);
         crosshairenabled = true;
         
         bat_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(0)%10 * cellsize),
        		 				 rand + (int)(_oppFleet.getShipOrigin(0)/10 * cellsize), 
        		 				 rand + (int)((_oppFleet.getShipOrigin(0)%10 + 5) * cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(0)/10 * cellsize) + (int)(cellsize));
         bat_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(0)%10 * cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(0)/10 * cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(0)%10 * cellsize) + (int)cellsize, 
        		 				 rand + (int)((_oppFleet.getShipOrigin(0)/10 + 5) * cellsize));
         cru_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(1)%10 * cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(1)/10 * cellsize), 
        		 				 rand + (int)((_oppFleet.getShipOrigin(1)%10 + 4) *cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(1)/10 * cellsize) + (int)(cellsize));
         cru_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(1)%10 * cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(1)/10 * cellsize), 
        		 				 rand + (int)(_oppFleet.getShipOrigin(1)%10 * cellsize) + (int)(cellsize), 
        		 				 rand + (int)((_oppFleet.getShipOrigin(1)/10 + 4) * cellsize));
         fri1_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(2)%10 * cellsize), 
				 				  rand + (int)(_oppFleet.getShipOrigin(2)/10 * cellsize), 
				 				  rand + (int)((_oppFleet.getShipOrigin(2)%10 + 3) *cellsize), 
				 				  rand + (int)(_oppFleet.getShipOrigin(2)/10 * cellsize) + (int)(cellsize));
         fri1_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(2)%10 * cellsize), 
				 				  rand + (int)(_oppFleet.getShipOrigin(2)/10 * cellsize), 
				 				  rand + (int)(_oppFleet.getShipOrigin(2)%10 * cellsize) + (int)(cellsize), 
				 				  rand + ((int)(_oppFleet.getShipOrigin(2)/10 + 3) * (int)(cellsize)));
		fri2_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(3)%10 * cellsize), 
								  rand + (int)(_oppFleet.getShipOrigin(3)/10 * cellsize), 
								  rand + (int)((_oppFleet.getShipOrigin(3)%10 + 3) *cellsize), 
								  rand + (int)(_oppFleet.getShipOrigin(3)/10 * cellsize) + (int)(cellsize));
		fri2_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(3)%10 * cellsize), 
								  rand + (int)(_oppFleet.getShipOrigin(3)/10 * cellsize), 
								  rand + (int)(_oppFleet.getShipOrigin(3)%10 * cellsize) + (int)(cellsize), 
								  rand + ((int)(_oppFleet.getShipOrigin(3)/10 + 3) * (int)(cellsize)));
		rms1_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(4)%10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(4)/10 * cellsize), 
								 rand + (int)((_oppFleet.getShipOrigin(4)%10 + 2) *cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(4)/10 * cellsize) + (int)(cellsize));
		rms1_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(4)%10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(4)/10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(4)%10 * cellsize) + (int)(cellsize), 
								 rand + (int)((_oppFleet.getShipOrigin(4)/10 + 2) * cellsize));
		rms2_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(5)%10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(5)/10 * cellsize), 
								 rand + (int)((_oppFleet.getShipOrigin(5)%10 + 2) *cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(5)/10 * cellsize) + (int)(cellsize));
		rms2_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(5)%10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(5)/10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(5)%10 * cellsize) + (int)(cellsize), 
								 rand + (int)((_oppFleet.getShipOrigin(5)/10 + 2) * cellsize));
		rms3_hor_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(6)%10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(6)/10 * cellsize), 
								 rand + (int)((_oppFleet.getShipOrigin(6)%10 + 2) *cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(6)/10 * cellsize) + (int)(cellsize));
		rms3_ver_dest = new Rect(rand + (int)(_oppFleet.getShipOrigin(6)%10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(6)/10 * cellsize), 
								 rand + (int)(_oppFleet.getShipOrigin(6)%10 * cellsize) + (int)(cellsize), 
								 rand + (int)((_oppFleet.getShipOrigin(6)/10 + 2) * cellsize));

		searect_dest = new Rect(0, 0, zijde, zijde);
		
		
		
		croha = decodeSampledBitmapFromResource(getResources(), R.drawable.crosshair3, (int)((1 + 2*crosshairovershoot) * cellsize), (int)((1 + 2*crosshairovershoot) * cellsize));
	      
        ship5_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b5_hor, (int)(5*cellsize), (int)cellsize);
        ship5_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b5_ver, (int)cellsize, (int)(5*cellsize));
        ship5_hor_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b5_hor_sunk, (int)(5*cellsize), (int)cellsize);
        ship5_ver_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b5_ver_sunk, (int)cellsize, (int)(5*cellsize));
        ship4_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b4_hor, (int)(4*cellsize), (int)cellsize);
        ship4_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b4_ver, (int)cellsize, (int)(4*cellsize));
        ship4_hor_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b4_hor_sunk, (int)(4*cellsize), (int)cellsize);
        ship4_ver_sunk = decodeSampledBitmapFromResource(getResources(), R.drawable.b4_ver_sunk, (int)cellsize, (int)(4*cellsize));
        
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
	    	
             
        bat_hor = new Rect(0, 0, ship5_hor.getWidth(), ship5_hor.getHeight());
        bat_ver = new Rect(0, 0, ship5_ver.getWidth(), ship5_ver.getHeight());
        cru_hor = new Rect(0, 0, ship4_hor.getWidth(), ship4_hor.getHeight());
        cru_ver = new Rect(0, 0, ship4_ver.getWidth(), ship4_ver.getHeight());
        fri_hor = new Rect(0, 0, ship3_hor.getWidth(), ship3_hor.getHeight());
        fri_ver = new Rect(0, 0, ship3_ver.getWidth(), ship3_ver.getHeight());
        rms_hor = new Rect(0, 0, ship2_hor.getWidth(), ship2_hor.getHeight());
        rms_ver = new Rect(0, 0, ship2_ver.getWidth(), ship2_ver.getHeight());        
        searect = new Rect(0, 0, bsea.getWidth(), bsea.getHeight());
    }

    public boolean onTouch(View view, MotionEvent event) 
    {
    	float x, y;
        int tx, ty;
    	
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        { 
        	x = event.getX();
        	y = event.getY();
        	if ((x <= (float)(zijde)) && (y <= (float)(zijde)) && (x >= (float)(rand)) && (y >= (float)(rand)))
        	{
        		//niks
        	}
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
        	lastcrosshair = crosshair;
        	crosshairovershoot = 1.0f;
        	x = Math.min(event.getX(), (float)(zijde - rand - 1));
        	x = Math.max(x, (float)(rand));
        	y = Math.min(event.getY(), (float)(zijde - rand - 1));	
        	y = Math.max(y, (float)(rand));	
        	tx = (int) ((x - rand) / (_oppFleet.getCellSize()));
        	ty = (int) ((y - rand) / (_oppFleet.getCellSize()));
        	if (crosshairenabled)
        	{
        		crosshair = Math.max(0, Math.min(10*ty + tx, 99));
        		if (lastcrosshair != crosshair && !gamefinished)
        		{
        			((ShootActivity) getContext()).playTickSound();
        		}
        	}
        	//System.out.println("crosshair is at: " + crosshair);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        { 
        	lastcrosshair = crosshair;        	
        	crosshairovershoot = 0.4f;
        	x = Math.min(event.getX(), (float)(zijde - rand - 1));
        	x = Math.max(x, (float)(rand));
        	y = Math.min(event.getY(), (float)(zijde - rand - 1));
        	y = Math.max(y, (float)(rand));
        	tx = (int) ((x - rand) / (_oppFleet.getCellSize()));
        	ty = (int) ((y - rand) / (_oppFleet.getCellSize()));
        	if (crosshairenabled)
        	{
        		crosshair = Math.max(0, 10*ty + tx);
        		if (lastcrosshair != crosshair && !gamefinished)
        		{
        			((ShootActivity) getContext()).playTickSound();
        		}
        	}
        }
        return true;
    }
    
    public void disableCrossHair()
    {
    	crosshairenabled = false;
    }
    
    public void enableCrossHair()
    {
    	crosshairenabled = true;
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
    	canvas.drawBitmap(bsea, searect, searect_dest, null);        
        float cz = _oppFleet.getCellSize();
        int i;
        boolean sd, ss;
        for (i = 0; i < 7; i++)
        {
        	sd = _oppFleet.getShipDirection(i);	        
	        ss = _oppFleet.getShipState(i); //sunken or not	        
	        	        
	        if (i == 0) // battleship
	        {
	        	if (!sd)
		        {
		    	   if (ss || gamefinished) // show all ships when it is a finished game
		    		   canvas.drawBitmap((ss? ship5_hor_sunk : ship5_hor), bat_hor, bat_hor_dest, null);
		        }
		        else
		        {   
		        	if (ss || gamefinished)
		        		canvas.drawBitmap((ss? ship5_ver_sunk : ship5_ver), bat_ver, bat_ver_dest, null);
		        }
	        }
	        else if (i == 1) // cruiser
	        {
	            if (!sd)
		        {
		        	if (ss || gamefinished)
		        		canvas.drawBitmap((ss? ship4_hor_sunk : ship4_hor), cru_hor, cru_hor_dest, null);
		        }
		       else
		        {   
		    	    if (ss || gamefinished)
		    	    	canvas.drawBitmap((ss? ship4_ver_sunk : ship4_ver), cru_ver, cru_ver_dest, null);
		        }
	        }   
	        else if (i == 2) // frigate 1
	        {
	        	if (!sd)
		        {
	        		if (ss || gamefinished)
	        			canvas.drawBitmap((ss? ship3_hor_sunk : ship3_hor), fri_hor, fri1_hor_dest, null);
		        }
		        else
		        {    
		        	if (ss || gamefinished)
		        		canvas.drawBitmap((ss? ship3_ver_sunk : ship3_ver), fri_ver, fri1_ver_dest, null);
		        }
	        }
	        else if (i == 3) // frigate 2
	        {
	        	if (!sd)
		        {
	        		if (ss || gamefinished)
	        			canvas.drawBitmap((ss? ship3_hor_sunk : ship3_hor), fri_hor, fri2_hor_dest, null);
		        }
		        else
		        {    
		        	if (ss || gamefinished)
		        		canvas.drawBitmap((ss? ship3_ver_sunk : ship3_ver), fri_ver, fri2_ver_dest, null);
		        }
	        }
	        else if (i == 4) // minswiep. 1
	        {
	        	if (!sd)
		        {
	        		if (ss || gamefinished)
	        			canvas.drawBitmap((ss? ship2_hor_sunk : ship2_hor), rms_hor, rms1_hor_dest, null);
		        }
		        else
		        {    
		        	if (ss || gamefinished)
		        		canvas.drawBitmap((ss? ship2_ver_sunk : ship2_ver), rms_ver, rms1_ver_dest, null);
		        }
	        }
	        else if (i == 5) // minswiep. 2
	        {
	        	if (!sd)
		        {
	        		if (ss || gamefinished)
	        			canvas.drawBitmap((ss? ship2_hor_sunk : ship2_hor), rms_hor, rms2_hor_dest, null);
		        }
		        else
		        {    
		        	if (ss || gamefinished)
		        		canvas.drawBitmap((ss? ship2_ver_sunk : ship2_ver), rms_ver, rms2_ver_dest, null);
		        }
	        }
	        else if (i == 6) // minswiep. 3
	        {
	        	if (!sd)
		        {
	        		if (ss || gamefinished)
	        			canvas.drawBitmap((ss? ship2_hor_sunk : ship2_hor), rms_hor, rms3_hor_dest, null);
		        }
		        else
		        {    
		        	if (ss || gamefinished)
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
            	if (_oppFleet.getGridValue(h, w) == 3) // for ship hit
                {                   
                    canvas.drawBitmap(explo, 
            				new Rect(0, 0, explo.getWidth(), explo.getHeight()), 
            				new Rect(rand + (int) (w * cz) - (int) (0.05f*cz),
            						rand + (int) (h * cz) - (int) (0.05f*cz), 
            						rand + (int) (w * cz) + (int)((1.05f)*cz), 
            						rand + (int) (h * cz) + (int)((1.05f)*cz)), 
            				null); 
                    
                } 
            	else if (_oppFleet.getGridValue(h, w) == 1) // for ship miss
                {           		
            		canvas.drawBitmap(miss, 
            				new Rect(0, 0, miss.getWidth(), miss.getHeight()), 
            				new Rect(rand + (int) (w * cz), 
            						rand + (int) (h * cz), 
            						rand + (int) (w * cz) + (int) (cz -1), 
            						rand + (int) (h * cz) + (int) (cz -1)), 
            				null);            		                
                } 
            	else if (_oppFleet.getGridValue(h, w) == 4) // for ship sunken!
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
        
        //draw crosshair only when game is not finished
        if (!gamefinished && crosshair >= 0 && crosshair < 100)
        {
        	canvas.drawBitmap(croha, new Rect(0, 0, croha.getWidth(), croha.getHeight()), 
        					new Rect(rand + (int) ((crosshair % 10) * cz) - (int) (crosshairovershoot*cz),
        	        		rand + (int) ((crosshair / 10) * cz) - (int) (crosshairovershoot*cz), 
        	                rand + (int) ((crosshair % 10) * cz) + ((int)((1.f + crosshairovershoot)*cz) -1),
        	                rand + (int) ((crosshair / 10) * cz) + ((int)((1.f + crosshairovershoot)*cz) -1)), null);
        	
        }
        
    }
    
    private void update() 
    {
    	if (running == RUNNING) 
    	{
    		_redrawHandler.sleep(_moveDelay);
    	}
    	//else
    		//System.out.println("Paused ..");
    }
    
    public void setOppboard(long myShipboard, int myShipdirections, long h1, long h2)
    {
    	_oppFleet.setInitialBoard(myShipboard, myShipdirections, h1, h2);
    }    
    
    public int fire()
    {
    	return _oppFleet.fire(crosshair);    	
    }
    
    public int getShotPositionGridValue()
    {
    	return _oppFleet.getGridValue((crosshair/10), (crosshair%10));
    }
    
    public int checkIfFiringIsPossibleHere()
    {
    	return _oppFleet.getFireGridValue(crosshair);		
    }
    
    public long getHits1()
    {
    	return _oppFleet.getHits1();
    }
    
    public long getHits2()
    {
    	return _oppFleet.getHits2();
    }
    
    public int getCrossHair()
    {
    	return crosshair;
    }
 
    public void setCrossHair(int ch)
    {
    	crosshair = ch;
    }
       
    public int getNumberOfShots()
	{
		return _oppFleet.getNumberOfShots();
	}
	
	public int getNumberOfHits()
	{
		return _oppFleet.getNumberOfHits();
	}
	
	public void setFinished(boolean finished)
	{
		gamefinished = finished;
	}
	
	public int howManyBattleshipsSunk()
	{
		return _oppFleet.howManyBattleshipsSunk();
	}
	
	public int howManyCruisersSunk()
	{
		return _oppFleet.howManyCruisersSunk();
	}
	
	public int howManyFrigatesSunk()
	{
		return _oppFleet.howManyFrigatesSunk();
	}
	
	public int howManyMinesweepersSunk()
	{
		return _oppFleet.howManyMinesweepersSunk();
	}
}
