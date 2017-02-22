package com.kipsap.jshipbattle;

import java.util.Arrays;
import android.content.Context;

public class ConfigureField {
			
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    private int[][] _gameGrid = new int[HEIGHT][WIDTH];
     
    private float fcell_size = 0;    
    private int squareTouched = -1;    
    
    private boolean bvalidBoard;
    private boolean[] _bDirecs;
    private long _shipboard, _originalShipBoard;
    private int _shipdirections, _originalShipDirections;
    
    private int[] battleship; //5
    private int[] cruiser; //4
    private int[] frigate1; //3
    private int[] frigate2; //3
    private int[] minesweeper1; //2
    private int[] minesweeper2; //2
    private int[] minesweeper3; //2

    public ConfigureField(Context context) 
    {    	
    	battleship = new int[5];
    	cruiser = new int[4];
    	frigate1 = new int[3];
    	frigate2 = new int[3];
    	minesweeper1 = new int[2];
    	minesweeper2 = new int[2];
    	minesweeper3 = new int[2];
    	_bDirecs = new boolean[7];    
    	bvalidBoard = false;
    }
    
    public float getCellSize()
    {
    	return fcell_size;
    }
    
    public void setCellSize(float cz)
    {
    	fcell_size = cz;
    }
    
    public boolean getValidity()
    {
    	return bvalidBoard;
    }

    public int getGridValue(int y, int x) 
    {
        return _gameGrid[y][x];
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

    public void setInitialBoard(long shipboard, int shipdirections)
    {
    	_shipboard = shipboard;
    	_shipdirections = shipdirections;
    	
    	_originalShipBoard = shipboard;
    	_originalShipDirections = shipdirections;
    	    	
    	setAllBoatPositions();    	
    	updateGrid(true, true);    	
    }
    
    public int setRandomBoard()
    {
    	int nTries = 0;
    	boolean valide = false;
    	while (!valide && nTries < 100)
    	{
    		battleship[0] = (int) Math.floor(100 * Math.random());    	
	    	cruiser[0] = (int) Math.floor(100 * Math.random());
	    	frigate1[0] = (int) Math.floor(100 * Math.random());
	    	frigate2[0] = (int) Math.floor(100 * Math.random());
	    	minesweeper1[0] = (int) Math.floor(100 * Math.random());
	    	minesweeper2[0] = (int) Math.floor(100 * Math.random());
	    	minesweeper3[0] = (int) Math.floor(100 * Math.random());
	    	
	    	_bDirecs[0] = (Math.random() < 0.5);
	    	_bDirecs[1] = (Math.random() < 0.5);
	    	_bDirecs[2] = (Math.random() < 0.5);
	    	_bDirecs[3] = (Math.random() < 0.5);
	    	_bDirecs[4] = (Math.random() < 0.5);
	    	_bDirecs[5] = (Math.random() < 0.5);
	    	_bDirecs[6] = (Math.random() < 0.5);
	    	
	    	encodeField();			// sets new _shipboard
	    	encodeDirections();		// sets new _shipdirections
	    	setAllBoatPositions();
	    	
	    	valide = checkGridValidity(true);
	    	nTries++;
    	}
	    return nTries;    	
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

	private boolean updateGrid(boolean invalidGridAllowed, boolean overlappingBoatsAreInvalid)
    {
		boolean bvalid = checkGridValidity(overlappingBoatsAreInvalid);
		
    	if (bvalid || invalidGridAllowed)
    	{
    		int i;
	    	int[][] tempGrid = new int[HEIGHT][WIDTH];
	    	
	    	for (i = 0; i < battleship.length; i++)
	    	{
	    		if ((battleship[i] >= 0) && (battleship[i] < 100))
	    			tempGrid[battleship[i]/10][battleship[i]%10] = 1;
	    	}
	    	
	    	for (i = 0; i < cruiser.length; i++)
	    	{
	    		if ((cruiser[i] >= 0) && (cruiser[i] < 100))
	    			tempGrid[cruiser[i]/10][cruiser[i]%10] = 1;
	    	}
	    	
	    	for (i = 0; i < frigate1.length; i++)
	    	{
	    		if ((frigate1[i] >= 0) && (frigate1[i] < 100))
	    			tempGrid[frigate1[i]/10][frigate1[i]%10] = 1;
	    		if ((frigate2[i] >= 0) && (frigate2[i] < 100))
	    			tempGrid[frigate2[i]/10][frigate2[i]%10] = 1;
	    	}
	    	for (i = 0; i < minesweeper1.length; i++)
	    	{
	    		if ((minesweeper1[i] >= 0) && (minesweeper1[i] < 100))
	    			tempGrid[minesweeper1[i]/10][minesweeper1[i]%10] = 1;
	    		if ((minesweeper2[i] >= 0) && (minesweeper2[i] < 100))
	    			tempGrid[minesweeper2[i]/10][minesweeper2[i]%10] = 1;
	    		if ((minesweeper3[i] >= 0) && (minesweeper3[i] < 100))
	    			tempGrid[minesweeper3[i]/10][minesweeper3[i]%10] = 1;
	    	}
	    	_gameGrid = tempGrid;	    	
    	}
    	else
    	{
    		//System.out.println("Invalid Grid!");
    	}
    	bvalidBoard = bvalid;
    	return bvalid;
    }
    
    private boolean checkGridValidity(boolean overlappingBoatsAreInvalid)
    {
    	boolean bvalid = true;
    	int shiplinenr = -1;
    	int i; // check if no ship is partly off the board
    	for (i = 0; i < battleship.length; i++)
    	{
    		shiplinenr = battleship[0] / 10;
    		bvalid &= ((battleship[i] >= 0) && (battleship[i] < 100));
    		if (!_bDirecs[0]) // if horizontal, the whole ship should be positioned on the same line (of course)
    			bvalid &= (battleship[i] / 10 == shiplinenr);
    	}
    	for (i = 0; i < cruiser.length; i++)
    	{
    		shiplinenr = cruiser[0] / 10;
    		bvalid &= ((cruiser[i] >= 0) && (cruiser[i] < 100));
    		if (!_bDirecs[1]) // if horizontal, the whole ship should be positioned on the same line (of course)
    			bvalid &= (cruiser[i] / 10 == shiplinenr);
    	}
    	for (i = 0; i < frigate1.length; i++)
    	{
    		shiplinenr = frigate1[0] / 10;
    		bvalid &= ((frigate1[i] >= 0) && (frigate1[i] < 100));
    		if (!_bDirecs[2]) // if horizontal, the whole ship should be positioned on the same line (of course)
    			bvalid &= (frigate1[i] / 10 == shiplinenr);
    	}
    	for (i = 0; i < frigate2.length; i++)
    	{
    		shiplinenr = frigate2[0] / 10;
    		bvalid &= ((frigate2[i] >= 0) && (frigate2[i] < 100));
    		if (!_bDirecs[3]) // if horizontal, the whole ship should be positioned on the same line (of course)
    			bvalid &= (frigate2[i] / 10 == shiplinenr);
    	}
    	
    	bvalid &= ((minesweeper1[0] >= 0) && (minesweeper1[0] < 100));
		bvalid &= ((minesweeper1[1] >= 0) && (minesweeper1[1] < 100));
		if (!_bDirecs[4]) bvalid &= (minesweeper1[1] / 10 == minesweeper1[0] / 10);
		
		bvalid &= ((minesweeper2[0] >= 0) && (minesweeper2[0] < 100));
		bvalid &= ((minesweeper2[1] >= 0) && (minesweeper2[1] < 100));
		if (!_bDirecs[5]) bvalid &= (minesweeper2[1] / 10 == minesweeper2[0] / 10);
		
		bvalid &= ((minesweeper3[0] >= 0) && (minesweeper3[0] < 100));
		bvalid &= ((minesweeper3[1] >= 0) && (minesweeper3[1] < 100));
		if (!_bDirecs[6]) bvalid &= (minesweeper3[1] / 10 == minesweeper3[0] / 10);
    	
    	if (!bvalid)
    		return false;
    	
    	if (!overlappingBoatsAreInvalid)
    		return bvalid;
    	
	   // check if no ships are overlapping
    	int[] tempFlatGrid = new int[HEIGHT*WIDTH];
       	tempFlatGrid[battleship[0]]++;
    	tempFlatGrid[battleship[1]]++;
    	tempFlatGrid[battleship[2]]++;
    	tempFlatGrid[battleship[3]]++;
    	tempFlatGrid[battleship[4]]++;
    	tempFlatGrid[cruiser[0]]++;
    	tempFlatGrid[cruiser[1]]++;
    	tempFlatGrid[cruiser[2]]++;
    	tempFlatGrid[cruiser[3]]++;
    	tempFlatGrid[frigate1[0]]++;
    	tempFlatGrid[frigate1[1]]++;
    	tempFlatGrid[frigate1[2]]++;
    	tempFlatGrid[frigate2[0]]++;
    	tempFlatGrid[frigate2[1]]++;
    	tempFlatGrid[frigate2[2]]++;
    	tempFlatGrid[minesweeper1[0]]++;
    	tempFlatGrid[minesweeper1[1]]++;
    	tempFlatGrid[minesweeper2[0]]++;
    	tempFlatGrid[minesweeper2[1]]++;
    	tempFlatGrid[minesweeper3[0]]++;
    	tempFlatGrid[minesweeper3[1]]++;
    	Arrays.sort(tempFlatGrid); // sort the array, lower to higher; if there is a value that is higher than 1, it means that multiple ships are overlapping... so no valid board
    	return (tempFlatGrid[tempFlatGrid.length - 1] <= 1);
    	
    }	
    
    public int getTouchedBoat(int x, int y)
    {
    	//returnvalue is a unique number, indicating a specific part of a specific ship: (0 = battleship[0], 5 = cruiser[0], etc..)
    	int retval = -1;
    	squareTouched = y*10 + x;
    	retval = touchedBattleship(squareTouched);
    	if (retval >= 0) return retval;
    	retval = touchedCruiser(squareTouched);
    	if (retval >= 0) return retval;
    	retval = touchedFrigate1(squareTouched);
    	if (retval >= 0) return retval;
    	retval = touchedFrigate2(squareTouched);
    	if (retval >= 0) return retval;
    	retval = touchedMineSweeper1(squareTouched);
    	if (retval >= 0) return retval;
    	retval = touchedMineSweeper2(squareTouched);
    	if (retval >= 0) return retval;
    	retval = touchedMineSweeper3(squareTouched);
    	return retval;
    }
    
    public void updateBoatPosition(int touchedBoatIdx, int x, int y)
    {
    	int i, squaretouched = 10*y + x;
    	int boatOrigin = -1;
    	int boatEnd = -1;
    	if ((touchedBoatIdx >= 0) && (touchedBoatIdx < 5))
    	{
    		boatOrigin = squaretouched - (_bDirecs[0] ? (touchedBoatIdx * 10) : (touchedBoatIdx));
    		boatEnd = squaretouched + (_bDirecs[0] ? (4 - touchedBoatIdx) * 10 : (4 - touchedBoatIdx));
    		while (boatOrigin < 0) 
    		{
    			boatOrigin += 10;
    			boatEnd += 10;
    		}
    		while (_bDirecs[0] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[0] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[0] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		//System.out.println("boatOrigin: " + boatOrigin);
    		battleship[0] = boatOrigin;
    		for (i = 1; i < battleship.length; i++)
	    		battleship[i] = battleship[0] + ((_bDirecs[0] ? 10 : 1) * i);
    	} 
    	else if ((touchedBoatIdx >= 5) && (touchedBoatIdx < 9))
    	{
    		boatOrigin = squaretouched - (_bDirecs[1] ? ((touchedBoatIdx - 5) * 10) : (touchedBoatIdx - 5));
    		boatEnd = squaretouched + (_bDirecs[1] ? (3 - (touchedBoatIdx - 5)) * 10 : (3 - (touchedBoatIdx - 5)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[1] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[1] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[1] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		cruiser[0] = boatOrigin;
    		for (i = 1; i < cruiser.length; i++) 	
        		cruiser[i] = cruiser[0] + ((_bDirecs[1] ? 10 : 1) * i);
    	}
    	else if ((touchedBoatIdx >= 9) && (touchedBoatIdx < 12))
    	{
    		boatOrigin = squaretouched - (_bDirecs[2] ? ((touchedBoatIdx - 9) * 10) : (touchedBoatIdx - 9));
    		boatEnd = squaretouched + (_bDirecs[2] ? (2 - (touchedBoatIdx - 9)) * 10 : (2 - (touchedBoatIdx - 9)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[2] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[2] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[2] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		frigate1[0] = boatOrigin;
    		for (i = 1; i < frigate1.length; i++) 	
    			frigate1[i] = frigate1[0] + ((_bDirecs[2] ? 10 : 1) * i);
    	}
    	else if ((touchedBoatIdx >= 12) && (touchedBoatIdx < 15))
    	{
    		boatOrigin = squaretouched - (_bDirecs[3] ? ((touchedBoatIdx - 12) * 10) : (touchedBoatIdx - 12));
    		boatEnd = squaretouched + (_bDirecs[3] ? (2 - (touchedBoatIdx - 12)) * 10 : (2 - (touchedBoatIdx - 12)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[3] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[3] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[3] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		frigate2[0] = boatOrigin;
    		for (i = 1; i < frigate2.length; i++) 	
    			frigate2[i] = frigate2[0] + ((_bDirecs[3] ? 10 : 1) * i);
    	}
    	else if ((touchedBoatIdx >= 15) && (touchedBoatIdx < 17))
    	{
    		boatOrigin = squaretouched - (_bDirecs[4] ? ((touchedBoatIdx - 15) * 10) : (touchedBoatIdx - 15));
    		boatEnd = squaretouched + (_bDirecs[4] ? (1 - (touchedBoatIdx - 15)) * 10 : (1 - (touchedBoatIdx - 15)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[4] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[4] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[4] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		minesweeper1[0] = boatOrigin;
    		minesweeper1[1] = minesweeper1[0] + (_bDirecs[4] ? 10 : 1);
    	}
    	else if ((touchedBoatIdx >= 17) && (touchedBoatIdx < 19))
    	{
    		boatOrigin = squaretouched - (_bDirecs[5] ? ((touchedBoatIdx - 17) * 10) : (touchedBoatIdx - 17));
    		boatEnd = squaretouched + (_bDirecs[5] ? (1 - (touchedBoatIdx - 17)) * 10 : (1 - (touchedBoatIdx - 17)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[5] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[5] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[5] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		minesweeper2[0] = boatOrigin;
    		minesweeper2[1] = minesweeper2[0] + (_bDirecs[5] ? 10 : 1);
    	}
    	else if ((touchedBoatIdx >= 19) && (touchedBoatIdx < 21))
    	{
    		boatOrigin = squaretouched - (_bDirecs[6] ? ((touchedBoatIdx - 19) * 10) : (touchedBoatIdx - 19));
    		boatEnd = squaretouched + (_bDirecs[6] ? (1 - (touchedBoatIdx - 19)) * 10 : (1 - (touchedBoatIdx - 19)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[6] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[6] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[6] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		minesweeper3[0] = boatOrigin;
    		minesweeper3[1] = minesweeper3[0] + (_bDirecs[6] ? 10 : 1);
    	}
    	updateGrid(true, true);
    }
    
    public boolean putBoatIfPossible(int touchedBoatIdx, int squaretouched)
    {
    	int i;    	
    	int boatOrigin = -1;
    	int boatEnd = -1;
    	boolean retval = true;
    	if ((touchedBoatIdx >= 0) && (touchedBoatIdx < 5))
    	{
    		boatOrigin = squaretouched - (_bDirecs[0] ? (touchedBoatIdx * 10) : (touchedBoatIdx)); 
    		boatEnd = squaretouched + (_bDirecs[0] ? (4 - touchedBoatIdx) * 10 : (4 - touchedBoatIdx));
    		while (boatOrigin < 0) 
    		{
    			boatOrigin += 10;
    			boatEnd += 10;
    		}
    		while (_bDirecs[0] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[0] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;
    			boatEnd += 1;
    		}
    		while (!_bDirecs[0] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		battleship[0] = boatOrigin;
    		for (i = 1; i < battleship.length; i++)
    			battleship[i] = battleship[0] + ((_bDirecs[0] ? 10 : 1) * i);    		
    		retval = updateGrid(true, true);    		
    	}
    	else if ((touchedBoatIdx >= 5) && (touchedBoatIdx < 9))
    	{
    		boatOrigin = squaretouched - (_bDirecs[1] ? ((touchedBoatIdx - 5) * 10) : (touchedBoatIdx - 5));
    		boatEnd = squaretouched + (_bDirecs[1] ? (3 - (touchedBoatIdx - 5)) * 10 : (3 - (touchedBoatIdx - 5)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[1] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[1] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[1] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		cruiser[0] = boatOrigin;
    		for (i = 1; i < cruiser.length; i++)
    			cruiser[i] = cruiser[0] + ((_bDirecs[1] ? 10 : 1) * i);    		
    		retval = updateGrid(true, true);    		    		
    	}
    	else if ((touchedBoatIdx >= 9) && (touchedBoatIdx < 12))
    	{
    		boatOrigin = squaretouched - (_bDirecs[2] ? ((touchedBoatIdx - 9) * 10) : (touchedBoatIdx - 9));
    		boatEnd = squaretouched + (_bDirecs[2] ? (2 - (touchedBoatIdx - 9)) * 10 : (2 - (touchedBoatIdx - 9)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[2] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[2] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[2] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		frigate1[0] = boatOrigin;
    		for (i = 1; i < frigate1.length; i++)
    			frigate1[i] = frigate1[0] + ((_bDirecs[2] ? 10 : 1) * i);    		
    		retval = updateGrid(true, true);  		
    	}
    	else if ((touchedBoatIdx >= 12) && (touchedBoatIdx < 15))
    	{
    		boatOrigin = squaretouched - (_bDirecs[3] ? ((touchedBoatIdx - 12) * 10) : (touchedBoatIdx - 12)); 
    		boatEnd = squaretouched + (_bDirecs[3] ? (2 - (touchedBoatIdx - 12)) * 10 : (2 - (touchedBoatIdx - 12)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[3] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[3] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[3] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		frigate2[0] = boatOrigin;   		
    		for (i = 1; i < frigate2.length; i++)    		
    			frigate2[i] = frigate2[0] + ((_bDirecs[3] ? 10 : 1) * i);    			
    		retval = updateGrid(true, true);  		
    	}
    	else if ((touchedBoatIdx >= 15) && (touchedBoatIdx < 17))
    	{
    		boatOrigin = squaretouched - (_bDirecs[4] ? ((touchedBoatIdx - 15) * 10) : (touchedBoatIdx - 15)); 
    		boatEnd = squaretouched + (_bDirecs[4] ? (1 - (touchedBoatIdx - 15)) * 10 : (1 - (touchedBoatIdx - 15)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[4] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[4] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[4] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		minesweeper1[0] = boatOrigin;    			
    		minesweeper1[1] = minesweeper1[0] + (_bDirecs[4] ? 10 : 1);    		
    		retval = updateGrid(true, true);   		
    	}
    	else if ((touchedBoatIdx >= 17) && (touchedBoatIdx < 19))
    	{
    		boatOrigin = squaretouched - (_bDirecs[5] ? ((touchedBoatIdx - 17) * 10) : (touchedBoatIdx - 17)); 
    		boatEnd = squaretouched + (_bDirecs[5] ? (1 - (touchedBoatIdx - 17)) * 10 : (1 - (touchedBoatIdx - 17)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[5] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[5] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[5] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		minesweeper2[0] = boatOrigin;    			
    		minesweeper2[1] = minesweeper2[0] + (_bDirecs[5] ? 10 : 1);
    		retval = updateGrid(true, true);
    	}
    	else if ((touchedBoatIdx >= 19) && (touchedBoatIdx < 21))
    	{
    		boatOrigin = squaretouched - (_bDirecs[6] ? ((touchedBoatIdx - 19) * 10) : (touchedBoatIdx - 19)); 
    		boatEnd = squaretouched + (_bDirecs[6] ? (1 - (touchedBoatIdx - 19)) * 10 : (1 - (touchedBoatIdx - 19)));
    		while (boatOrigin < 0) 
    			boatOrigin += 10;
    		while (_bDirecs[6] && boatEnd > 99)
    		{
    			boatOrigin -= 10;
    			boatEnd -= 10;
    		}
    		while (!_bDirecs[6] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 < 5))
    		{
    			boatOrigin += 1;    			
    			boatEnd += 1;
    		}
    		while (!_bDirecs[6] && (boatOrigin/10 != boatEnd/10) && (squaretouched%10 >= 5))
    		{
    			boatOrigin -= 1;
    			boatEnd -= 1;
    		}
    		minesweeper3[0] = boatOrigin;    		
    		minesweeper3[1] = minesweeper3[0] + (_bDirecs[6] ? 10 : 1);
    		retval = updateGrid(true, true);
    	}
    	return retval;
    }
    
    private void encodeField()
    {    	
    	// encode the current distribution of ships into the _shipboard long value    	
    	long board = 0L;
    	board += battleship[0];
    	board += cruiser[0] * 98;
    	board += frigate1[0] * Math.pow(98, 2);
    	board += frigate2[0] * Math.pow(98, 3);
    	board += minesweeper1[0] * Math.pow(98, 4);
    	board += minesweeper2[0] * Math.pow(98, 5);
    	board += minesweeper3[0] * Math.pow(98, 6);
    	_shipboard = board;
    }
    
    private void encodeDirections()
    {
    	int i, sd = 0;
    	for (i = 0; i < _bDirecs.length; i++)
    		sd += (_bDirecs[i] ? Math.pow(2, i) : 0); 
    	_shipdirections = sd;
    }
    
    private int touchedBattleship(int sq)
    {
    	int retval = -1;
    	if (battleship[0] == sq) retval = 0;
    	if (battleship[1] == sq) retval = 1;
    	if (battleship[2] == sq) retval = 2;
    	if (battleship[3] == sq) retval = 3;
    	if (battleship[4] == sq) retval = 4;
    	return retval;
    }
    
    private int touchedCruiser(int sq)
    {
    	int retval = -1;
    	if (cruiser[0] == sq) retval = 5;
    	if (cruiser[1] == sq) retval = 6;
    	if (cruiser[2] == sq) retval = 7;
    	if (cruiser[3] == sq) retval = 8;
    	return retval;    	
    }
    
    private int touchedFrigate1(int sq)
    {
    	int retval = -1;
    	if (frigate1[0] == sq) retval = 9;
    	if (frigate1[1] == sq) retval = 10;
    	if (frigate1[2] == sq) retval = 11;
    	return retval;    	
    }
    
    private int touchedFrigate2(int sq)
    {
    	int retval = -1;
    	if (frigate2[0] == sq) retval = 12;
    	if (frigate2[1] == sq) retval = 13;
    	if (frigate2[2] == sq) retval = 14;
    	return retval;    	
    }
    
    private int touchedMineSweeper1(int sq)
    {
    	int retval = -1;
    	if (minesweeper1[0] == sq) retval = 15;
    	if (minesweeper1[1] == sq) retval = 16;
    	return retval;    	
    }
    
    private int touchedMineSweeper2(int sq)
    {
    	int retval = -1;
    	if (minesweeper2[0] == sq) retval = 17;
    	if (minesweeper2[1] == sq) retval = 18;
    	return retval;    	
    }
    
    private int touchedMineSweeper3(int sq)
    {
    	int retval = -1;
    	if (minesweeper3[0] == sq) retval = 19;
    	if (minesweeper3[1] == sq) retval = 20;
    	return retval;    	
    }
    
	public long getBoard() 
	{
		encodeField();
		return _shipboard;
	}
	
	public int getDirections()
	{
		encodeDirections();
		return _shipdirections;
	}
	
	public void gameUpdateResultSuccessful(boolean success)
	{
		if (!success) // game update did not reach the server: put back the original board configuration
		{
			_shipboard = _originalShipBoard;
	    	_shipdirections = _originalShipDirections;
	    	    	
	    	setAllBoatPositions();    	
	    	updateGrid(false, true); 
		}
	}

	public void flip(int touchedBoat) 
	{
		if ((touchedBoat >= 0) && (touchedBoat < 5))
			_bDirecs[0] = !_bDirecs[0];
		else if ((touchedBoat >= 5) && (touchedBoat < 9))
			_bDirecs[1] = !_bDirecs[1];
		else if ((touchedBoat >= 9) && (touchedBoat < 12))
			_bDirecs[2] = !_bDirecs[2];
		else if ((touchedBoat >= 12) && (touchedBoat < 15))
			_bDirecs[3] = !_bDirecs[3];
		else if ((touchedBoat >= 15) && (touchedBoat < 17))
			_bDirecs[4] = !_bDirecs[4];
		else if ((touchedBoat >= 17) && (touchedBoat < 19))
			_bDirecs[5] = !_bDirecs[5];
		else if ((touchedBoat >= 19) && (touchedBoat < 21))
			_bDirecs[6] = !_bDirecs[6];
		
		encodeField();
    	encodeDirections();
		setAllBoatPositions();
			
		while (!updateGrid(true, false)) 
		{
			if ((touchedBoat >= 0) && (touchedBoat < 5))
				battleship[0] -= (_bDirecs[0] ? 10 : 1);
			else if ((touchedBoat >= 5) && (touchedBoat < 9))
				cruiser[0] -= (_bDirecs[1] ? 10 : 1);
			else if ((touchedBoat >= 9) && (touchedBoat < 12))
				frigate1[0] -= (_bDirecs[2] ? 10 : 1);
			else if ((touchedBoat >= 12) && (touchedBoat < 15))
				frigate2[0] -= (_bDirecs[3] ? 10 : 1);
			else if ((touchedBoat >= 15) && (touchedBoat < 17))
				minesweeper1[0] -= (_bDirecs[4] ? 10 : 1);	
			else if ((touchedBoat >= 17) && (touchedBoat < 19))
				minesweeper2[0] -= (_bDirecs[5] ? 10 : 1);		
			else if ((touchedBoat >= 19) && (touchedBoat < 21))
				minesweeper3[0] -= (_bDirecs[6] ? 10 : 1);
			encodeField();
	    	encodeDirections();
			setAllBoatPositions();
		}	
		updateGrid(true, true);  // one more time to set the bValidGrid boolean correctly
	}	
	
}
