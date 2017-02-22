package com.kipsap.jshipbattle;

import android.content.Context;

public class ShootField {
		
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    
    private int[][] _gameGrid = new int[HEIGHT][WIDTH];
    private int[][] _fireGrid = new int[HEIGHT][WIDTH];
    private int[][] _gameGridTEST = new int[HEIGHT][WIDTH];
    private int[][] _fireGridTEST = new int[HEIGHT][WIDTH];
    
    private float fcell_size = 0.f; //48;  
    
    
    private boolean[] _bDirecs;
    private long _shipboard;
    private int _shipdirections;
    
    private long _hits1, _hits2, _testhits1, _testhits2;
    private int _numberOfShots, _numberOfHits, _numberOfTestHits;
    
    private int[] battleship; //5
    private int[] cruiser; //4
    private int[] frigate1; //3
    private int[] frigate2; //3
    private int[] minesweeper1; //2
    private int[] minesweeper2; //2
    private int[] minesweeper3; //2   
    
    private boolean bSunkenBattleship = false,
    				bSunkenCruiser = false,
    				bSunkenFrigate1 = false, 
    				bSunkenFrigate2 = false, 
    				bSunkenMineSweeper1 = false, 
    				bSunkenMineSweeper2 = false, 
    				bSunkenMineSweeper3 = false;
    
    private boolean blastSunkenBattleship = false,
			blastSunkenCruiser = false,
			blastSunkenFrigate1 = false, 
			blastSunkenFrigate2 = false, 
			blastSunkenMineSweeper1 = false, 
			blastSunkenMineSweeper2 = false, 
			blastSunkenMineSweeper3 = false;
    
    private boolean bTestSunkenBattleship = false,
			bTestSunkenCruiser = false,
			bTestSunkenFrigate1 = false, 
			bTestSunkenFrigate2 = false, 
			bTestSunkenMineSweeper1 = false, 
			bTestSunkenMineSweeper2 = false, 
			bTestSunkenMineSweeper3 = false;
    
    private boolean bTestLastSunkenBattleship = false,
			bTestLastSunkenCruiser = false,
			bTestLastSunkenFrigate1 = false, 
			bTestLastSunkenFrigate2 = false, 
			bTestLastSunkenMineSweeper1 = false, 
			bTestLastSunkenMineSweeper2 = false, 
			bTestLastSunkenMineSweeper3 = false;

    public ShootField(Context context) 
    {
    	battleship = new int[5];
    	cruiser = new int[4];
    	frigate1 = new int[3];
    	frigate2 = new int[3];
    	minesweeper1 = new int[2];
    	minesweeper2 = new int[2];
    	minesweeper3 = new int[2];
    	_bDirecs = new boolean[7];  
    	_numberOfShots = 0;
    	_numberOfHits = 0;
    	_numberOfTestHits = 0;
    }
    
    public float getCellSize()
    {
    	return fcell_size;
    }
    
    public void setCellSize(float cz)
    {
    	fcell_size = cz;
    }

    public int getFireGridValue(int pos)
    {
    	if (pos >= 0 && pos < 100)
    		return _fireGrid[pos/10][pos%10];	// 1 or 0
    	else
    		return -1;
    }
    
    public int getGridValue(int y, int x) 
    {
    	// 0: no boat, no shot
    	// 1: no boat, missed shot
    	// 2: boat, no shot
    	// 3: boat, shot
    	// 4: boat, sunken
    	
    	int retval = -1;
    	if (_gameGrid[y][x] == 0) // no boat
    	{
    		retval = _fireGrid[y][x];
    	}
    	else // boat
    	{
    		retval = _gameGrid[y][x] + 1;
    	}
    	return retval;    	
    }
    
    public int getShipOrigin(int shipID)
    {
    	switch (shipID)
    	{
    	case 0:
    		return battleship[0];	    		
    	case 1:
    	   	return cruiser[0];
    	case 2:
    		return frigate1[0];
    	case 3:
    		return frigate2[0];
    	case 4:
    		return minesweeper1[0];
    	case 5:
    		return minesweeper2[0];
    	case 6:
    		return minesweeper3[0];    	   	
    	default:
    	   	return -1;
    	}
    }
    
    public boolean getShipDirection(int shipID)
    {    	
    	return _bDirecs[shipID];
    }
    
    public int getShipLength(int shipID)
    {
    	switch (shipID)
    	{
    	case 0:
    		return battleship.length;	
    		
    	case 1:
    	   	return cruiser.length;
    	   	
    	case 2:
    	case 3:
    		return frigate1.length;
    		
    	case 4:
    	case 5:
    	case 6:
    		return minesweeper1.length;
    	   	
    	default:
    	   	return -1;
    	}
    }
    
    public boolean getShipState(int shipID)
    {
    	switch (shipID)
    	{
    	case 0:
    		return bSunkenBattleship;    		
    	case 1:
    	   	return bSunkenCruiser;   	   	
    	case 2:
    		return bSunkenFrigate1;
    	case 3:
    		return bSunkenFrigate2;   		
    	case 4:
    		return bSunkenMineSweeper1;
    	case 5:
    		return bSunkenMineSweeper2;
    	case 6:
    		return bSunkenMineSweeper3;
    	default:
    		return false;
    	}
    	
    }
    
    private void updateTotalNumberOfShots()
    {
    	int i, n = 0;
    	for (i = 0; i < 100; i++)
    	{
    		n += _fireGrid[i/10][i%10];    				
    	}
    	_numberOfShots = n;
    }

    public void setInitialBoard(long shipboard, int shipdirections, long hits1, long hits2)
    {
    	_shipboard = shipboard;
    	_shipdirections = shipdirections;
    	
    	//_originalShipBoard = shipboard;
    	//_originalShipDirections = shipdirections;
    	
    	_hits1 = hits1;
    	_hits2 = hits2;    	
    	_testhits1 = hits1;
		_testhits2 = hits2;
    	    	    	
    	setHitDistribution();
    	setHitDistributionTEST();
    	setAllBoatPositions();    	
    	updateGrid();
    	updateGridTEST();
    }
    
    private void setHitDistribution()
    {
    	int i;
    	for (i = 0; i < 60; i++)
    	{    		
    		_fireGrid[i/10][i%10] = ((_hits1 & (1L << i)) > 0 ? 1 : 0);
    	}
    	for (i = 60; i < 100; i++)
    	{
    		_fireGrid[i/10][i%10] = ((_hits2 & (1L << (i - 60))) > 0 ? 1 : 0);
    	}    	
    }
    
    private void setHitDistributionTEST()
    {
    	int i;
    	for (i = 0; i < 60; i++)
    	{    		
    		_fireGridTEST[i/10][i%10] = ((_testhits1 & (1L << i)) > 0 ? 1 : 0);
    	}
    	for (i = 60; i < 100; i++)
    	{
    		_fireGridTEST[i/10][i%10] = ((_testhits2 & (1L << (i - 60))) > 0 ? 1 : 0);
    	}    	
    }
    
    private void setAllBoatPositions() 
    {    	
    	int i;    	
    	for (i = 0; i < _bDirecs.length; i++)
    	   	_bDirecs[i] = (_shipdirections & (int) (Math.pow(2, i))) >> (i) == 1;
    	
    	// set the origin position ( = top or left most part) of every ship
    	battleship[0] = (int) Math.floor((_shipboard % Math.pow(98, 1)) / Math.pow(98, 0));
    	cruiser[0] = (int) Math.floor((_shipboard % Math.pow(98, 2)) / Math.pow(98, 1));
    	frigate1[0] = (int) Math.floor((_shipboard % Math.pow(98, 3)) / Math.pow(98, 2));
    	frigate2[0] = (int) Math.floor((_shipboard % Math.pow(98, 4)) / Math.pow(98, 3));
    	minesweeper1[0] = (int) Math.floor((_shipboard % Math.pow(98, 5)) / Math.pow(98, 4));
    	minesweeper2[0] = (int) Math.floor((_shipboard % Math.pow(98, 6)) / Math.pow(98, 5));
    	minesweeper3[0] = (int) Math.floor((_shipboard % Math.pow(98, 7)) / Math.pow(98, 6));
    	
    	// set the rest of the ship, based on origin position
    	for (i = 1; i < battleship.length; i++)
    		battleship[i] = battleship[0] + ((_bDirecs[0] ? 10 : 1) * i);
    		
    	for (i = 1; i < cruiser.length; i++) 	
    		cruiser[i] = cruiser[0] + ((_bDirecs[1] ? 10 : 1) * i);
    		
    	for (i = 1; i < frigate1.length; i++)
    		frigate1[i] = frigate1[0] + ((_bDirecs[2] ? 10 : 1) * i);
    		
    	for (i = 1; i < frigate2.length; i++)
    		frigate2[i] = frigate2[0] + ((_bDirecs[3] ? 10 : 1) * i);
    		
    	minesweeper1[1] = minesweeper1[0] + (_bDirecs[4] ? 10 : 1);
    	minesweeper2[1] = minesweeper2[0] + (_bDirecs[5] ? 10 : 1);
    	minesweeper3[1] = minesweeper3[0] + (_bDirecs[6] ? 10 : 1);		
	}

    
	private void updateGrid()
    {
		int nhit = 0, ntotalHits = 0;
    		
    	int i;
    	int[][] tempGrid = new int[HEIGHT][WIDTH];
    	
    	for (i = 0; i < battleship.length; i++)
    	{
    		if ((battleship[i] >= 0) && (battleship[i] < 100))
    		{
    			tempGrid[battleship[i]/10][battleship[i]%10] = 1 + _fireGrid[battleship[i]/10][battleship[i]%10];
    			nhit += _fireGrid[battleship[i]/10][battleship[i]%10];
    		}    			
    	}
    	if (nhit == 5) bSunkenBattleship = true;
    	ntotalHits += nhit;
    	nhit = 0;	    	
    	for (i = 0; i < cruiser.length; i++)
    	{
    		if ((cruiser[i] >= 0) && (cruiser[i] < 100))
    		{
    			tempGrid[cruiser[i]/10][cruiser[i]%10] = 1 + _fireGrid[cruiser[i]/10][cruiser[i]%10];
    			nhit += _fireGrid[cruiser[i]/10][cruiser[i]%10];
    		}    		
    	}
    	if (nhit == 4) bSunkenCruiser = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < frigate1.length; i++)
    	{
    		if ((frigate1[i] >= 0) && (frigate1[i] < 100))
    		{
    			tempGrid[frigate1[i]/10][frigate1[i]%10] = 1 + _fireGrid[frigate1[i]/10][frigate1[i]%10];
    			nhit += _fireGrid[frigate1[i]/10][frigate1[i]%10];
    		}    		
    	}
    	if (nhit == 3) bSunkenFrigate1 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < frigate2.length; i++)
    	{
    		if ((frigate2[i] >= 0) && (frigate2[i] < 100))
    		{
    			tempGrid[frigate2[i]/10][frigate2[i]%10] = 1 + _fireGrid[frigate2[i]/10][frigate2[i]%10];
    			nhit += _fireGrid[frigate2[i]/10][frigate2[i]%10];
    		}    		
    	}
    	if (nhit == 3) bSunkenFrigate2 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < minesweeper1.length; i++)
    	{
    		if ((minesweeper1[i] >= 0) && (minesweeper1[i] < 100))
    		{
    			tempGrid[minesweeper1[i]/10][minesweeper1[i]%10] = 1 + _fireGrid[minesweeper1[i]/10][minesweeper1[i]%10];
    			nhit += _fireGrid[minesweeper1[i]/10][minesweeper1[i]%10];
    		}    		
    	}
    	if (nhit == 2) bSunkenMineSweeper1 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < minesweeper2.length; i++)
    	{
    		if ((minesweeper2[i] >= 0) && (minesweeper2[i] < 100))
    		{
    			tempGrid[minesweeper2[i]/10][minesweeper2[i]%10] = 1 + _fireGrid[minesweeper2[i]/10][minesweeper2[i]%10];
    			nhit += _fireGrid[minesweeper2[i]/10][minesweeper2[i]%10];
    		}   		
    	}
    	if (nhit == 2) bSunkenMineSweeper2 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < minesweeper3.length; i++)
    	{
    		if ((minesweeper3[i] >= 0) && (minesweeper3[i] < 100))
    		{
    			tempGrid[minesweeper3[i]/10][minesweeper3[i]%10] = 1 + _fireGrid[minesweeper3[i]/10][minesweeper3[i]%10];
    			nhit += _fireGrid[minesweeper3[i]/10][minesweeper3[i]%10];
    		}    		
    	}    
    	if (nhit == 2) bSunkenMineSweeper3 = true;
    	ntotalHits += nhit;
    	
    	_numberOfHits = ntotalHits;    	
    	_gameGrid = tempGrid;	
    	updateTotalNumberOfShots();
    	
    	if (bSunkenBattleship)
    		for (i = 0; i < battleship.length; i++)
    			_gameGrid[battleship[i]/10][battleship[i]%10]++;	    	
    	if (bSunkenCruiser)
    		for (i = 0; i < cruiser.length; i++)
    			_gameGrid[cruiser[i]/10][cruiser[i]%10]++;
    	if (bSunkenFrigate1)
    		for (i = 0; i < frigate1.length; i++)
    			_gameGrid[frigate1[i]/10][frigate1[i]%10]++;
    	if (bSunkenFrigate2)
    		for (i = 0; i < frigate2.length; i++)
    			_gameGrid[frigate2[i]/10][frigate2[i]%10]++;
    	if (bSunkenMineSweeper1)
    		for (i = 0; i < minesweeper1.length; i++)
    			_gameGrid[minesweeper1[i]/10][minesweeper1[i]%10]++;
    	if (bSunkenMineSweeper2)
    		for (i = 0; i < minesweeper2.length; i++)
    			_gameGrid[minesweeper2[i]/10][minesweeper2[i]%10]++;
    	if (bSunkenMineSweeper3)
    		for (i = 0; i < minesweeper3.length; i++)
    			_gameGrid[minesweeper3[i]/10][minesweeper3[i]%10]++;
    	
    	
    }
    
	public long getHits1()
	{		
		return _hits1;
	}
	
	public long getHits2()
	{
		return _hits2;
	}
	
	public int fire(int pos)
	{		
		if (_fireGrid[pos/10][pos%10] == 1)
		{
			return -2; //firing is not possible on an already fired on spot
		}
		else
		{			
			_fireGrid[pos/10][pos%10] = 1;
			encodeHits();
			blastSunkenBattleship = bSunkenBattleship;
			blastSunkenCruiser = bSunkenCruiser;
			blastSunkenFrigate1 = bSunkenFrigate1;
			blastSunkenFrigate2 = bSunkenFrigate2;
			blastSunkenMineSweeper1 = bSunkenMineSweeper1;
			blastSunkenMineSweeper2 = bSunkenMineSweeper2;
			blastSunkenMineSweeper3 = bSunkenMineSweeper3;
			updateGrid();
			return checkNewSunkenShips(); // returns -1 if no ships have sunken, otherwise the shipID	
		}
	}
	
	public void encodeHits()
	{
		int i;
		long h1 = 0L, h2 = 0L;
    	for (i = 59; i >= 0; i--)
    	{    		
    		h1 += (_fireGrid[i/10][i%10] == 1 ? (long) Math.pow(2, i) : 0); //hit or miss
    	}
    	for (i = 99; i >= 60; i--)
    	{
    		h2 += (_fireGrid[i/10][i%10] == 1 ? (long) Math.pow(2, (i - 60)) : 0); //hit or miss
    	}
    	_hits1 = h1;
    	_hits2 = h2;
	}
	
	private int checkNewSunkenShips()
	{
		if (blastSunkenBattleship != bSunkenBattleship)
			return 0;
		if (blastSunkenCruiser != bSunkenCruiser)
			return 1;
		if (blastSunkenFrigate1 != bSunkenFrigate1)
			return 2;
		if (blastSunkenFrigate2 != bSunkenFrigate2)
			return 3;
		if (blastSunkenMineSweeper1 != bSunkenMineSweeper1)
			return 4;
		if (blastSunkenMineSweeper2 != bSunkenMineSweeper2)
			return 5;
		if (blastSunkenMineSweeper3 != bSunkenMineSweeper3)
			return 6;
		
		return -2;
		
	}
	
	public int updateFireShots(long h1, long h2)
	{	
		int retval = -1;
		int lastNumberOfHits = _numberOfHits;
		_hits1 = h1;
		_hits2 = h2;
		blastSunkenBattleship = bSunkenBattleship;
		blastSunkenCruiser = bSunkenCruiser;
		blastSunkenFrigate1 = bSunkenFrigate1;
		blastSunkenFrigate2 = bSunkenFrigate2;
		blastSunkenMineSweeper1 = bSunkenMineSweeper1;
		blastSunkenMineSweeper2 = bSunkenMineSweeper2;
		blastSunkenMineSweeper3 = bSunkenMineSweeper3;
		setHitDistribution();
		setAllBoatPositions();    //niet nodig, volgens mij		//wel dus
    	updateGrid(); 
    	retval = checkNewSunkenShips(); // returns -2 if no ships have sunken, otherwise the shipID	
    	
    	if (retval < 0)
    	{
    		if (lastNumberOfHits != _numberOfHits) // nieuwe voltreffer && geen schip gezonken
    			retval = 7;    		
    		else
    			retval = -1; // plonsj
    	}    		
    	return retval;   	
	}
	
	public int updateFireShotsTEST(long h1, long h2) // checks the result of a fire shot, without updating the actual gamefield
	{	
		int retval = -1;
		int lastNumberOfHits = _numberOfTestHits;
		_testhits1 = h1;
		_testhits2 = h2;
		bTestLastSunkenBattleship = bTestSunkenBattleship;
		bTestLastSunkenCruiser = bTestSunkenCruiser;
		bTestLastSunkenFrigate1 = bTestSunkenFrigate1;
		bTestLastSunkenFrigate2 = bTestSunkenFrigate2;
		bTestLastSunkenMineSweeper1 = bTestSunkenMineSweeper1;
		bTestLastSunkenMineSweeper2 = bTestSunkenMineSweeper2;
		bTestLastSunkenMineSweeper3 = bTestSunkenMineSweeper3;
		setHitDistributionTEST();		
    	updateGridTEST(); 
    	retval = checkNewSunkenShipsTest(); // returns -2 if no ships have sunken, otherwise the shipID	
    	
    	if (retval < 0)
    	{
    		if (lastNumberOfHits != _numberOfTestHits) // nieuwe voltreffer && geen schip gezonken
    			retval = 7;    		
    		else
    			retval = -1; // plonsj
    	}    		
    	return retval;   	
	}
	
	public int getNumberOfShots()
	{
		return _numberOfShots;
	}
	
	public int getNumberOfHits()
	{
		return _numberOfHits;
	}
	
	
	
	
	private void updateGridTEST()
    {
		int nhit = 0, ntotalHits = 0;
    		
    	int i;
    	int[][] tempGrid = new int[HEIGHT][WIDTH];
    	
    	for (i = 0; i < battleship.length; i++)
    	{
    		if ((battleship[i] >= 0) && (battleship[i] < 100))
    		{
    			tempGrid[battleship[i]/10][battleship[i]%10] = 1 + _fireGridTEST[battleship[i]/10][battleship[i]%10];
    			nhit += _fireGridTEST[battleship[i]/10][battleship[i]%10];
    		}    			
    	}
    	if (nhit == 5) bTestSunkenBattleship = true;
    	ntotalHits += nhit;
    	nhit = 0;	    	
    	for (i = 0; i < cruiser.length; i++)
    	{
    		if ((cruiser[i] >= 0) && (cruiser[i] < 100))
    		{
    			tempGrid[cruiser[i]/10][cruiser[i]%10] = 1 + _fireGridTEST[cruiser[i]/10][cruiser[i]%10];
    			nhit += _fireGridTEST[cruiser[i]/10][cruiser[i]%10];
    		}    		
    	}
    	if (nhit == 4) bTestSunkenCruiser = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < frigate1.length; i++)
    	{
    		if ((frigate1[i] >= 0) && (frigate1[i] < 100))
    		{
    			tempGrid[frigate1[i]/10][frigate1[i]%10] = 1 + _fireGridTEST[frigate1[i]/10][frigate1[i]%10];
    			nhit += _fireGridTEST[frigate1[i]/10][frigate1[i]%10];
    		}    		
    	}
    	if (nhit == 3) bTestSunkenFrigate1 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < frigate2.length; i++)
    	{
    		if ((frigate2[i] >= 0) && (frigate2[i] < 100))
    		{
    			tempGrid[frigate2[i]/10][frigate2[i]%10] = 1 + _fireGridTEST[frigate2[i]/10][frigate2[i]%10];
    			nhit += _fireGridTEST[frigate2[i]/10][frigate2[i]%10];
    		}    		
    	}
    	if (nhit == 3) bTestSunkenFrigate2 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < minesweeper1.length; i++)
    	{
    		if ((minesweeper1[i] >= 0) && (minesweeper1[i] < 100))
    		{
    			tempGrid[minesweeper1[i]/10][minesweeper1[i]%10] = 1 + _fireGridTEST[minesweeper1[i]/10][minesweeper1[i]%10];
    			nhit += _fireGridTEST[minesweeper1[i]/10][minesweeper1[i]%10];
    		}    		
    	}
    	if (nhit == 2) bTestSunkenMineSweeper1 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < minesweeper2.length; i++)
    	{
    		if ((minesweeper2[i] >= 0) && (minesweeper2[i] < 100))
    		{
    			tempGrid[minesweeper2[i]/10][minesweeper2[i]%10] = 1 + _fireGridTEST[minesweeper2[i]/10][minesweeper2[i]%10];
    			nhit += _fireGridTEST[minesweeper2[i]/10][minesweeper2[i]%10];
    		}   		
    	}
    	if (nhit == 2) bTestSunkenMineSweeper2 = true;
    	ntotalHits += nhit;
    	nhit = 0;
    	for (i = 0; i < minesweeper3.length; i++)
    	{
    		if ((minesweeper3[i] >= 0) && (minesweeper3[i] < 100))
    		{
    			tempGrid[minesweeper3[i]/10][minesweeper3[i]%10] = 1 + _fireGridTEST[minesweeper3[i]/10][minesweeper3[i]%10];
    			nhit += _fireGridTEST[minesweeper3[i]/10][minesweeper3[i]%10];
    		}    		
    	}    
    	if (nhit == 2) bTestSunkenMineSweeper3 = true;
    	ntotalHits += nhit;
    	
    	_gameGridTEST = tempGrid;	
    	_numberOfTestHits = ntotalHits;
    	    	
    	if (bTestSunkenBattleship)
    		for (i = 0; i < battleship.length; i++)
    			_gameGridTEST[battleship[i]/10][battleship[i]%10]++;	    	
    	if (bTestSunkenCruiser)
    		for (i = 0; i < cruiser.length; i++)
    			_gameGridTEST[cruiser[i]/10][cruiser[i]%10]++;
    	if (bTestSunkenFrigate1)
    		for (i = 0; i < frigate1.length; i++)
    			_gameGridTEST[frigate1[i]/10][frigate1[i]%10]++;
    	if (bTestSunkenFrigate2)
    		for (i = 0; i < frigate2.length; i++)
    			_gameGridTEST[frigate2[i]/10][frigate2[i]%10]++;
    	if (bTestSunkenMineSweeper1)
    		for (i = 0; i < minesweeper1.length; i++)
    			_gameGridTEST[minesweeper1[i]/10][minesweeper1[i]%10]++;
    	if (bTestSunkenMineSweeper2)
    		for (i = 0; i < minesweeper2.length; i++)
    			_gameGridTEST[minesweeper2[i]/10][minesweeper2[i]%10]++;
    	if (bTestSunkenMineSweeper3)
    		for (i = 0; i < minesweeper3.length; i++)
    			_gameGridTEST[minesweeper3[i]/10][minesweeper3[i]%10]++;    	
    	
    }
	
	private int checkNewSunkenShipsTest()
	{
		if (bTestLastSunkenBattleship != bTestSunkenBattleship)
			return 0;
		if (bTestLastSunkenCruiser != bTestSunkenCruiser)
			return 1;
		if (bTestLastSunkenFrigate1 != bTestSunkenFrigate1)
			return 2;
		if (bTestLastSunkenFrigate2 != bTestSunkenFrigate2)
			return 3;
		if (bTestLastSunkenMineSweeper1 != bTestSunkenMineSweeper1)
			return 4;
		if (bTestLastSunkenMineSweeper2 != bTestSunkenMineSweeper2)
			return 5;
		if (bTestLastSunkenMineSweeper3 != bTestSunkenMineSweeper3)
			return 6;
		
		return -2;		
	}
	
	public int howManyBattleshipsSunk()
	{
		return (bSunkenBattleship ? 1 : 0);
	}
	
	public int howManyCruisersSunk()
	{
		return (bSunkenCruiser ? 1 : 0);
	}
	
	public int howManyFrigatesSunk()
	{
		int ret = 0;
		if (bSunkenFrigate1) ret++;
		if (bSunkenFrigate2) ret++;
		return ret;
	}
	
	public int howManyMinesweepersSunk()
	{
		int ret = 0;
		if (bSunkenMineSweeper1) ret++;
		if (bSunkenMineSweeper2) ret++;
		if (bSunkenMineSweeper3) ret++;
		return ret;
	}
}