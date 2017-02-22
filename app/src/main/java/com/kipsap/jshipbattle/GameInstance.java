package com.kipsap.jshipbattle;

public class GameInstance 
{

	public final static int GS_UNINITIALIZED = -1;
	public final static int GS_UNACCEPTED = 0;
	public final static int GS_DECLINED = 1;
	public final static int GS_BOARD_BOTH_NOTREADY = 2;	
	public final static int GS_PLAYER1_BOARD_READY = 3;
	public final static int GS_PLAYER2_BOARD_READY = 4;
	public final static int GS_BOARD_BOTH_READY = 5;	
	public final static int GS_PLAYER1_TURN = 6;
	public final static int GS_PLAYER2_TURN = 7;
	public final static int GS_PLAYER1_WON = 8;
	public final static int GS_PLAYER2_WON = 9;
	public final static int GS_PLAYER1_RESIGNED = 10;
	public final static int GS_PLAYER2_RESIGNED = 11;	
	
	private String playerA;
	private String playerB;
	private int state;
	private long shipboardUser1;
	private long shipboardUser2;
	private int shipdirectionsUser1;
	private int shipdirectionsUser2;
	private int secondsAgo;	
	private long hits1user1, hits1user2, hits2user1, hits2user2;
	private long gameID;
	
	private long maxChatID;
	
	public GameInstance(String inviter, String invitee, int state2,
                        long shipB1, long shipB2, int shipDir1,
                        int shipDir2, int secago, long h1u1, long h1u2, long h2u1, long h2u2, long id,
                        long maxiChatID)
	
	{
        playerA = inviter;
        playerB = invitee;
        state = state2;
        shipboardUser1 = shipB1;
        shipboardUser2 = shipB2;
        shipdirectionsUser1 = shipDir1;
        shipdirectionsUser2 = shipDir2;
        secondsAgo = secago;
        hits1user1 = h1u1;
        hits1user2 = h1u2;
        hits2user1 = h2u1;
        hits2user2 = h2u2;
        gameID = id;
        maxChatID = maxiChatID;
    }

	public String getPlayerA() {
		return playerA;
	}
	
	public String getPlayerB() {
		return playerB;
	}
	
	public int getGameState() {
		return state;
	}
	
	public long getShipBoardUser1() {
		return shipboardUser1;
	}
	
	public int getShipDirectionsUser1() {
		return shipdirectionsUser1;
	}
	
	public long getShipBoardUser2() {
		return shipboardUser2;
	}
	
	public int getShipDirectionsUser2() {
		return shipdirectionsUser2;
	}
	
	public int getSecondsAgo() {
		return secondsAgo;
	}	
	
	public long getHits1User1() {
		return hits1user1;
	}
	public long getHits1User2() {
		return hits1user2;
	}
	public long getHits2User1() {
		return hits2user1;
	}
	public long getHits2User2() {
		return hits2user2;
	}
	public long getGameID() {
		return gameID;
	}
	public long getMaxChatID() {
		return maxChatID;
	}
}
