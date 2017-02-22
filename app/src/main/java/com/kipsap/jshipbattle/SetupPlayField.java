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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SetupPlayField extends View implements OnTouchListener 
{
	public static final int PAUSE = 0;
    public static final int RUNNING = 1;
    private Context _context;
    private int running = RUNNING;    
    private ConfigureField _field1;    
    private long _moveDelay = 100;
    private long doubleTapFirstTap;
    private boolean bvalidBoard, _touchable, _hasShips;
    
    private GestureDetector mGestureDetector;
    int width, height, rand, zijde;
    int startX, startY, touchedBoatIndex, lastStartX, lastStartY, themeID;
    float cellsize;
    Bitmap bsea, ship5_hor, ship5_ver, 
    		ship4_hor, ship4_ver, 
    		ship3_hor, ship3_ver, 
    		ship2_hor, ship2_ver;
    
    Rect bat_hor, bat_ver, cru_hor, cru_ver, fri_hor, fri_ver, rms_hor, rms_ver, searect;
    
    private RefreshHandler _redrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
        	SetupPlayField.this.update();
        	SetupPlayField.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);            
        }
    };
    
    public void setInitialBoard(long shipboard, int shipdirections)
    {
    	if (_hasShips)
    		_field1.setInitialBoard(shipboard, shipdirections);
    }    
    
    public int setRandomBoard()
    {
    	return _field1.setRandomBoard(); 
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
    
    public void init(int h, int w, boolean touchable, boolean hasShips)
    {
    	_touchable = touchable;
    	_hasShips = hasShips;
    	 width = w;
         height = h;         
         zijde = Math.min(width, height);          
         rand = (int) (0.076 * zijde);
         cellsize = (float)((float)(zijde - 2 * rand) / 10.f);
         _field1.setCellSize(cellsize);
         if (touchable)
         	this.setOnTouchListener(this);
         
         
         switch (themeID)
 		{
 			case 0:				
 				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_caribbean, (_touchable ? width : width/4), (_touchable ? height : height/4));
 				break;
 			case 1:
 				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_swamp, (_touchable ? width : width/4), (_touchable ? height : height/4));
 				break;
 			case 2:
 				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_polar, (_touchable ? width : width/4), (_touchable ? height : height/4));
 				break;
 			case 3:
 				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_volcano, (_touchable ? width : width/4), (_touchable ? height : height/4));
 				break;
 			case 4:
 				bsea = decodeSampledBitmapFromResource(getResources(), R.drawable.map_poisoned_sea, (_touchable ? width : width/4), (_touchable ? height : height/4));
 				break;
 		}
 						
         
         if (_hasShips)
         {        	
 	        ship5_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b5_hor, (int)(5*cellsize), (int)cellsize);
 	        ship5_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b5_ver, (int)cellsize, (int)(5*cellsize));
 	        ship4_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b4_hor, (int)(4*cellsize), (int)cellsize);
 	        ship4_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b4_ver, (int)cellsize, (int)(4*cellsize));
 	        ship3_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b3_hor, (int)(3*cellsize), (int)cellsize);
 	        ship3_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b3_ver, (int)cellsize, (int)(3*cellsize));
 	        ship2_hor = decodeSampledBitmapFromResource(getResources(), R.drawable.b2_hor, (int)(2*cellsize), (int)cellsize);
 	        ship2_ver = decodeSampledBitmapFromResource(getResources(), R.drawable.b2_ver, (int)cellsize, (int)(2*cellsize));
 	        
 	        bat_hor = new Rect(0, 0, ship5_hor.getWidth(), ship5_hor.getHeight());
 	        bat_ver = new Rect(0, 0, ship5_ver.getWidth(), ship5_ver.getHeight());
 	        cru_hor = new Rect(0, 0, ship4_hor.getWidth(), ship4_hor.getHeight());
 	        cru_ver = new Rect(0, 0, ship4_ver.getWidth(), ship4_ver.getHeight());
 	        fri_hor = new Rect(0, 0, ship3_hor.getWidth(), ship3_hor.getHeight());
 	        fri_ver = new Rect(0, 0, ship3_ver.getWidth(), ship3_ver.getHeight());
 	        rms_hor = new Rect(0, 0, ship2_hor.getWidth(), ship2_hor.getHeight());
 	        rms_ver = new Rect(0, 0, ship2_ver.getWidth(), ship2_ver.getHeight());
         }
         if (_touchable)
         	searect = new Rect(0, 0, bsea.getWidth(), bsea.getHeight());        
        
         initPlayField();
    }
    
    public SetupPlayField(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        
        _context = context;
        _field1 = new ConfigureField(context);        
    }   
    
    public boolean onTouch(View view, MotionEvent event) 
    {
    	float x, y;
        int tx, ty; 
        boolean doubletapped;
        
        if (event.getAction() == MotionEvent.ACTION_UP)
        {          	
        	boolean succ; 
        	x = Math.min(event.getX(), (float)(zijde - rand - 1));
        	x = Math.max(x, (float)(rand));
        	y = Math.min(event.getY(), (float)(zijde - rand - 1));	
        	y = Math.max(y, (float)(rand));	        	
        	
        	tx = (int) ((x - rand) / (_field1.getCellSize()));
        	ty = (int) ((y - rand) / (_field1.getCellSize()));
        	
        	doubletapped = (System.currentTimeMillis() - doubleTapFirstTap < 100);        	
        	  
        	if (doubletapped) // flip a boat when doubletapped
        	{        		 				
				_field1.flip(touchedBoatIndex);				
        	}
        	else	// put a boat when drag released
        	{
        		succ = _field1.putBoatIfPossible(touchedBoatIndex, (tx + ty*10));
            	if (!succ) // when invalid board:
            	{
            		//System.out.println("WTF");
            	} 
        	}
        	bvalidBoard = _field1.getValidity();
        	((PlayFieldActivity) getContext()).enableGoAndFavoriteButton(bvalidBoard);
        }
        
        mGestureDetector.onTouchEvent(event);
       
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {     	
        	x = event.getX();
        	y = event.getY();        	
        	if ((x <= (float)(zijde)) && (y <= (float)(zijde)) && (x >= (float)(rand)) && (y >= (float)(rand)))
        	{        		
        		startX = (int) ((x - rand) / _field1.getCellSize());
	    		startY = (int) ((y - rand) / _field1.getCellSize());
	    		touchedBoatIndex = _field1.getTouchedBoat(startX, startY);	    		
        	}
        } 
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
        	x = Math.min(event.getX(), (float)(zijde - rand - 1));
        	x = Math.max(x, (float)(rand));
        	y = Math.min(event.getY(), (float)(zijde - rand - 1));	
        	y = Math.max(y, (float)(rand));
        	tx = (int) ((x - rand) / (_field1.getCellSize()));
        	ty = (int) ((y - rand) / (_field1.getCellSize()));        	
        	
        	if ((x < (float)(zijde)) && 
        		(y < (float)(zijde)) && 
        		(x > (float)(rand)) && 
        		(y > (float)(rand)))
        	{
        		_field1.updateBoatPosition(touchedBoatIndex, tx, ty);
        		bvalidBoard = _field1.getValidity();
            	((PlayFieldActivity) getContext()).enableGoAndFavoriteButton(bvalidBoard);
        	}
        }        	
    	return true;    	        
    }
    
    public void setMode(int mode) 
    {
    	running = mode;
        if (mode == RUNNING) 
        {
        	update();
            return;
        }
        if (mode == PAUSE) 
        {            
        	//System.out.println("Paused");
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) 
    {
    	
    	canvas.drawBitmap(bsea, searect, new Rect(0, 0, zijde, zijde), null); 
                
        int so, sl, x, y, i;
        boolean sd;
        if (_hasShips)
        {
	        for (i = 6; i >= 0; i--)
	        {
		        so = _field1.getShipOrigin(i);
		        sd = _field1.getShipDirection(i);
		        sl = _field1.getShipLength(i);
		        x = so%10;
		        y = so/10;
		        
		        if (sl == 5) // battleship
		        {
			        if (!sd)
			        {
			        	canvas.drawBitmap(ship5_hor, bat_hor, 
			        			new Rect(rand + (int) (x * cellsize), 
			        					 rand + (int) (y * cellsize), 
			        					 rand + (int) ((x + sl) *cellsize), 
			        					 rand + (int) (y * cellsize) + (int) (cellsize)),				
			        					 null);
			        }
			        else
			        {    		        	
			        	canvas.drawBitmap(ship5_ver, bat_ver, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) (x * cellsize) + (int) cellsize, rand + (int) ((y + sl) * (int) cellsize)),
								null);		        	
			        }
		        }
		        else if (sl == 4) // cruiser
		        {
			        if (!sd)
			        {
			        	canvas.drawBitmap(ship4_hor, cru_hor, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) ((x + sl) *cellsize), rand + (int) (y * cellsize) + (int) (cellsize)),				
							null);
			        }
			        else
			        {    		        	
			        	canvas.drawBitmap(ship4_ver, cru_ver, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) (x * cellsize) + (int) cellsize, rand + (int) ((y + sl) * (int) cellsize)),				
								null);		        	
			        }
		        }        
		        else if (sl == 3) // frigate
		        {
		        	if (!sd)
			        {
			        	canvas.drawBitmap(ship3_hor, fri_hor, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) ((x + sl) *cellsize), rand + (int) (y * cellsize) + (int) (cellsize)),				
							null);
			        }
			        else
			        {    
			        	canvas.drawBitmap(ship3_ver, fri_ver, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) (x * cellsize) + (int) cellsize, rand + (int) ((y + sl) * cellsize)),				
								null);
			        }
		        }
		        else if (sl == 2) // m.s.
		        {
		        	if (!sd)
			        {
			        	canvas.drawBitmap(ship2_hor, rms_hor, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) ((x + sl) *cellsize), rand + (int) (y * cellsize) + (int) (cellsize)),				
							null);
			        }
			        else
			        {    
			        	canvas.drawBitmap(ship2_ver, rms_ver, new Rect(rand + (int) (x * cellsize), rand + (int) (y * cellsize), rand + (int) (x * cellsize) + (int) cellsize, rand + (int) ((y + sl) * cellsize)),				
								null);			        	
			        }
		        }
	        }
        }
    }    
    
    private void update() 
    {
    	if (running == RUNNING) 
    		_redrawHandler.sleep(_moveDelay);    	
    }
    
    private void initPlayField() 
    {
        setFocusable(true);
        
        mGestureDetector = new GestureDetector(_context, new GestureDetector.SimpleOnGestureListener() 
        {            
 
            @Override
            public boolean onDoubleTap(MotionEvent e) 
            {
            	doubleTapFirstTap = System.currentTimeMillis();
            	return true;
            }
            
        });
        mGestureDetector.setIsLongpressEnabled(true);
    }
   
	public long getBoard() 
	{
		return _field1.getBoard();
	}	
	
	public int getDirections()
	{
		return _field1.getDirections();
	}	
	
	public void gameUpdateResultSuccessful(boolean success)
	{
		_field1.gameUpdateResultSuccessful(success);
	}

	public boolean getValidity()
	{
		return _field1.getValidity();
	}
	
	public void recycleBitmaps()
    {
		if (bsea != null) bsea.recycle();
		if (ship5_hor != null) ship5_hor.recycle();
		if (ship5_ver != null) ship5_ver.recycle();
		if (ship4_hor != null) ship4_hor.recycle();
		if (ship4_ver != null) ship4_ver.recycle();
		if (ship3_hor != null) ship3_hor.recycle();
		if (ship3_ver != null) ship3_ver.recycle();
		if (ship2_hor != null) ship2_hor.recycle();
		if (ship2_ver != null) ship2_ver.recycle();
	   
	    bsea = null;
	    ship5_hor = null;
	    ship5_ver = null;
	    ship4_hor = null;
	   	ship4_ver = null;
	   	ship3_hor = null;
	   	ship3_ver = null;
	   	ship2_hor = null;
	   	ship2_ver = null;	   	
    }
	
}
