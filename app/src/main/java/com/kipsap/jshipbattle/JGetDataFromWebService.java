package com.kipsap.jshipbattle;

import com.kipsap.commonsource.JConstants;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

public class JGetDataFromWebService 
{			
	private static final String METHOD_LOGIN_NEW = "clientLogInNew";
	private static final String METHOD_SIGNUP_NEW = "clientSignUpNew";
	private static final String METHOD_ADD_FRIEND_LIST = "addToFriendsList";
	private static final String METHOD_REMOVE_FRIEND_LIST = "removeFromFriendsList";
	private static final String METHOD_SEND_GAME_INVITE = "sendGameInvite";
	private static final String METHOD_SEND_GAME_INVITE_TO_BOT = "sendGameInviteToBot";
	private static final String METHOD_SEND_RANDOM_GAME_INVITE = "sendRandomGameInvite";
	private static final String METHOD_REQUEST_ID_OPP_STATE = "requestLatestIDOpponentAndState";
	private static final String METHOD_REQUEST_GAME_ARRAY_VERSIONED_WITH_CHAT_IDS = "requestGameArray_Versioned_With_Chat";
	private static final String METHOD_REQUEST_GAME_BY_ID_WITH_CHAT_ID = "requestGameByID_With_Chat";
	private static final String METHOD_ACCEPT_GAME = "acceptGame";
	private static final String METHOD_SET_INITIAL_BOARD_NEW = "setInitialBoardNEW";
	private static final String METHOD_SEND_FIRE_UPDATE = "sendFireUpdate";
	private static final String METHOD_GET_FRIENDS_LIST = "getFriendsList";	
	private static final String METHOD_GET_WORLD_TOP_X_NEW = "getWorldTopXNew";
	private static final String METHOD_GET_NATIONAL_TOP_X_NEW = "getNationalTopXNew";
	private static final String METHOD_REQUEST_RESIGN = "requestResign";
	private static final String METHOD_GET_PLAYER_STATISTICS = "getStatsNew";
	private static final String METHOD_GET_PLAYER_RATING_UPDATE = "getRatingUpdate";
	private static final String METHOD_GET_FRIENDS_SEARCH = "searchFriends";
	private static final String METHOD_REQUEST_SCORE_AND_COUNTRY_CODES = "requestScoreCC";
	private static final String METHOD_REQUEST_CHANGE_PASSWORD = "changePassword";
	private static final String METHOD_ASK_SETTING = "askSetting";
	private static final String METHOD_GIVE_SETTING = "giveSetting";
	//private static final String METHOD_ADD_CHAT_MESSAGE = "addChatMessage";
	private static final String METHOD_ADD_CHAT_MESSAGE_PAID_CHECK = "addChatMessagePaidCheck";
	private static final String METHOD_REQUEST_CHAT_MESSAGES = "getChatsForGameID";
	private static final String METHOD_REQUEST_NEW_CHAT_MESSAGES = "checkNewChatMessages";
	private static final String METHOD_GET_RATING_AND_COUNTRY_FOR_PLAYER = "getRatingAndCountryForPlayer";
	private static final String METHOD_GET_LAST_X_BOARDS = "getLastXBoards";
	
	
	
	private static final String NAMESPACE = "http://dynamictest.ws.com";
    //String URL = "http://192.168.1.76:8085/Dynamic_test/services/JSeaBattleWS?wsdl";
	//String URL = "http://192.168.1.76:8085/JSea_WebService/services/JSeaBattleWS?wsdl";

	//String URL = "http://82.73.223.219:8085/JSea_WebService/services/JSeaBattleWS?wsdl";
	//String URL = "http://10.0.0.15:8080/Dynamic_test/services/JSeaBattleWS?wsdl";
	String URL = "http://ship-battle.com/JSea_WebService/services/JSeaBattleWS?wsdl";	
	
	private static Activity _parentActivity;
	private int _iresult = -1;
	private long longresult = -1;
	String _resultStr;
	private ArrayList<GameInstance> _returnedGameArray;
	private GameInstance _returnedGameInstance;
	private ArrayList<String> _returnedFriendsList;	
	long startTime, endTime;
	private boolean _appNeedsToBeUpgraded = false;
	private boolean _timeoutExceededWhileRequestingGamesList = false;
	
	public void sendLogInRequest(final JLoginActivity activity, final String us, final String pwd, final int versionCode)
	{		
	  startTime = System.currentTimeMillis();
	  _parentActivity = activity;	  

	  // allows non-"edt" thread to be re-inserted into the "edt" queue
	  final Handler uiThreadCallback = new Handler();

	  // performs rendering in the "edt" thread, after background operation is complete
	  final Runnable runInUIThread = new Runnable() 
	  {
	    public void run() 
	    {
	    	_showLogInResultInUI(activity);
	    }
	  };

	  new Thread() {
	    @Override public void run() 
	    {
	    	_iresult = soapLogInNew(us, pwd, versionCode);
	    	//_iresult = soapLogIn(us, pwd, versionCode);
	    	uiThreadCallback.post(runInUIThread);
	    }
	  }.start();

	  Toast.makeText(_parentActivity, _parentActivity.getString(R.string.toast_loggingin), Toast.LENGTH_SHORT).show();

	}
	
	
	
	public void sendChangePasswordRequest(final ResetPassword activity, final String usr, final String oldPW, final String newPW) 
	{
		  startTime = System.currentTimeMillis();			
		  _parentActivity = activity;

		  final Handler uiThreadCallback = new Handler();
		  
		  final Runnable runInUIThread = new Runnable() 
		  {
		    public void run() 
		    {
		    	_showChangePasswordResultInUI(activity);
		    }
		  };

		  new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapChangePassword(usr, oldPW, newPW);
		    	uiThreadCallback.post(runInUIThread);
		    }
		  }.start();

		  //Toast.makeText(_parentActivity, _parentActivity.getString(R.string.toast_changingpassword), Toast.LENGTH_SHORT).show();

	}
	
	public void sendSignUpRequest(final JSignupActivity activity, final String us, final String pwd, final String email, final int versionCode, final String countryCode) 
	{
		  startTime = System.currentTimeMillis();			
		  _parentActivity = activity;

		  final Handler uiThreadCallback = new Handler();
		  
		  final Runnable runInUIThread = new Runnable() 
		  {
		    public void run() {
		    	_showSignUpResultInUI(activity);
		    }
		  };

		  new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapSignUp(us, pwd, email, versionCode, countryCode, 1); // clientCode = 1 for Android
		    	uiThreadCallback.post(runInUIThread);
		    }
		  }.start();

		  Toast.makeText(_parentActivity, _parentActivity.getString(R.string.toast_signingup), Toast.LENGTH_SHORT).show();

	}
	
	public void addToFriendsList(final JGamePicker activity, final String inviter, final String friend)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showFriendInviteUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapAddToFriendsList(inviter, friend);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
		
	}	
	
	public void addToFriendsList(final ShootActivity activity, final String inviter, final String friend)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showFriendInviteUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapAddToFriendsList(inviter, friend);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
		
	}	
	
	public void addToFriendsList(final StatsActivity activity, final String inviter, final String friend)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showFriendInviteUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapAddToFriendsList(inviter, friend);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
		
	}	
	
	public void sendRemoveFriendFromList(final JGamePicker activity, final String inviter, final String friend)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		      _showFriendRemovalInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapRemoveFromFriendsList(inviter, friend);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
		
	}
	
	public void sendGameInvite(final JGamePicker activity, final String inviter, final String invitee, final boolean random) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		      _showGameInviteInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	if (random)
		    		_resultStr = soapSendRandomGameInvite(inviter);
		    	else
		    		_resultStr = soapSendGameInvite(inviter, invitee);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
		 		 
	}
	
	public void sendGameInviteToBot(final JGamePicker activity, final String inviter) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		      _showGameInviteInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapSendGameInviteToBot(inviter);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
		 		 
	}	
	
	public void sendGameInvite(final StatsActivity activity, final String inviter, final String invitee, final boolean random) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		      _showGameInviteInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	if (random)
		    		_resultStr = soapSendRandomGameInvite(inviter);
		    	else
		    		_resultStr = soapSendGameInvite(inviter, invitee);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
		 		 
	}
	

	public void sendGameInvite(final ShootActivity activity, final String inviter, final String invitee) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showGameInviteInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapSendGameInvite(inviter, invitee);
		    	String resArr[] = _resultStr.split("&");
		    	_iresult = Integer.parseInt(resArr[0]);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
	}
	
	public void sendChatMessage(final ShootActivity activity, final long gameID, final String talker, final String txt, final boolean paidVersion)
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showChatReceivedInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapAddChatMessage(gameID, talker, txt, paidVersion);
		    	//String resArr[] = _resultStr.split("&");
		    	//_iresult = Integer.parseInt(resArr[0]);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
	}	
	
	public void requestPastChatMessages(final ShootActivity activity, final long gameID) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showPastChatMessagesInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapRequestPastChatMessages(gameID);
		    	//String resArr[] = _resultStr.split("&");
		    	//_iresult = Integer.parseInt(resArr[0]);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
	}
	
	public void requestLastXBoards(final ShootActivity activity, final String playerName) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showLastXBoardsInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapRequestLastXBoards(playerName, 5); //vijf
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
	}
	
	public void requestLastXBoards(final PlayFieldActivity activity, final String playerName) 
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		      _showLastXBoardsInUI(activity);
		    }
		};
		  
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapRequestLastXBoards(playerName, 5); //vijf
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	 
	}
	
	
	public void requestRunningGames(final JGamePicker activity, final String username, final int appVersion)
	{
		startTime = System.currentTimeMillis();	
		_parentActivity = activity;	
		
		
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showRunningGamesInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_returnedGameArray = soapRequestGameArrayWithAppVersionCheck(username, 0, appVersion); // 0 = no limit
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
	}
	
	public void requestPlayerStatistics(final StatsActivity activity, final String username)
	{
		startTime = System.currentTimeMillis();	
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		      _showPlayerStatisticsInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapRequestPlayerStatistics(username);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
	}	
	
	public void requestMostRecentGame(final NotifyService service, final String username)
	{		
		startTime = System.currentTimeMillis();	
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_updateLatestGameInfo(service);
		    }
		};
		
		new Thread() 
		{
		    @Override public void run() 
		    {
		    	_resultStr = soapRequestLatestIDOpponentAndState(username);
		    	//_returnedGameArray = soapRequestGameArray(username, 1);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
	}
	
	public void requestCheckNewChatMessages(final NotifyService service, final String username)
	{		
		startTime = System.currentTimeMillis();	
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_updateLatestChatInfo(service);
		    }
		};
		
		new Thread() 
		{
		    @Override public void run() 
		    {
		    	longresult = soapRequestCheckNewChatMessages(username);		    	
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
	}	
	
	public void requestCheckGameInstance(final ShootActivity activity, final long gameID)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showCheckGameInstanceInShootActivity(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_returnedGameInstance = soapRequestGameByID(gameID);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
	}
	
	public void requestScoreAndCountryCodes(final ShootActivity activity, final long gameID)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		      _showScoreAndCountryCodesInShootActivity(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapRequestScoreAndCountryCodes(gameID);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
	}
	
	
	public void getRatingAndCountryForPlayer(final JGamePicker activity, final String opponentName, final String me)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		    	_showPlayerRatingAndCountryResultInUI(activity, opponentName, me);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapGetRatingAndCountryForPlayer(opponentName);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
			
	}
	
	public void sendGameAccepted(final JGamePicker activity, final String curUsr, final String curOpp)
	{
		startTime = System.currentTimeMillis();
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		    	_showGameAcceptationResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapSendGameAccepted(curUsr, curOpp, true);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
			
	}
	
	public void sendGameDeclined(final JGamePicker activity, final String curUsr, final String curOpp)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() {
		    	_showGameAcceptationResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapSendGameAccepted(curUsr, curOpp, false);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		 
		
	}	
	
	public void sendBoardUpdateNEW(final PlayFieldActivity activity, final long gameID, final long shipboard, final int shipdirections, final int player)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showGameUpdateResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapSetInitialBoardNEW(gameID, shipboard, shipdirections, player);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();	
	}	
	
	public void sendFireUpdateUserX(final ShootActivity activity, final long gameID, final int crossHair, final int user)
	{		
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showFireUpdateResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapFireUpdateForUser(gameID, crossHair, user);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();		
	}	
	
	public void sendResignRequest(final ShootActivity activity, final long gameID, final int player)
	{
		startTime = System.currentTimeMillis();	
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showResignUpdateResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_iresult = soapSendResign(gameID, player);
			   	uiThreadCallback.post(runInUIThread);
			}
		}.start();			
	}	
	
	public void requestFriendsList(final JGamePicker activity, final String user)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showFriendsListInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_returnedFriendsList = soapGetFriendsList(user);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}	
	
	public void requestNationalTopX(final StatsActivity activity, final int limiet, final String countryCode)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showTopXInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_returnedFriendsList = soapGetNationalTopX(countryCode, limiet);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}
	
	public void requestWorldTopX(final StatsActivity activity, final int limiet)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showTopXInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_returnedFriendsList = soapGetWorldTopX(limiet);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}	
	
	public void sendFriendsSearch(final JGamePicker activity, final String searchString)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showSearchResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_returnedFriendsList = soapGetFriendsSearch(searchString);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}	
	
	public void sendRatingUpdateRequest(final ShootActivity activity, final String currentUsr, final String currentOpp)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showRatingUpdateResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapRatingUpdate(currentUsr, currentOpp);
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}
	
	public void askSettingRandomInvitations(final SettingsActivity activity, final String currentUsr)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showAskSettingRandomInvitationsUpdateResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapAskSetting(1, currentUsr); // settingID = 1 -> wants random invitations or not
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}	
	
	public void giveSettingRandomInvitations(final SettingsActivity activity, final String currentUsr, final int value)
	{
		_parentActivity = activity;
		final Handler uiThreadCallback = new Handler();
		final Runnable runInUIThread = new Runnable() 
		{
		    public void run() 
		    {
		    	_showGiveSettingRandomInvitationsUpdateResultInUI(activity);
		    }
		};
		
		new Thread() {
		    @Override public void run() 
		    {
		    	_resultStr = soapGiveSetting(1, currentUsr, value); // settingID = 1 -> wants random invitations or not
		    	uiThreadCallback.post(runInUIThread);
			}
		}.start();
	}	
	
	
	
	
	
	private int soapLogInNew(String usr, String pwd, int versionCode)
	{
				
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_LOGIN_NEW;		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_LOGIN_NEW);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("usr");//Define the variable name in the web service method
		pi.setValue(usr);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("pwd");
		pi2.setValue(pwd);
		pi2.setType(String.class);
        request.addProperty(pi2);
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("versionCode");
		pi3.setValue(versionCode);
		pi3.setType(Integer.class);
        request.addProperty(pi3);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        //HttpTransportSE ht = new HttpTransportSE(URL);
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString());   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return retval;
	}		
		
	private int soapChangePassword(String usr, String oldPW, String newPW)
	{				
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_CHANGE_PASSWORD;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_CHANGE_PASSWORD);
		
		PropertyInfo pi = new PropertyInfo();
        pi.setName("usr");
		pi.setValue(usr);
		pi.setType(String.class);
        request.addProperty(pi);
		
		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("oldPW");//Define the variable name in the web service method
		pi2.setValue(oldPW);//Define value for fname variable
		pi2.setType(String.class);//Define the type of the variable
        request.addProperty(pi2);//Pass properties to the variable
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("newPW");
		pi3.setValue(newPW);
		pi3.setType(String.class);
        request.addProperty(pi3);
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString()); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return retval;
	}
	
	private int soapSignUp(String usr, String pwd, String email, int versionCode, String countryCode, int clientCode)
	{				
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_SIGNUP_NEW;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_SIGNUP_NEW);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("usr");//Define the variable name in the web service method
		pi.setValue(usr);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("pwd");
		pi2.setValue(pwd);
		pi2.setType(String.class);
        request.addProperty(pi2);   
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("email");
		pi3.setValue(email);
		pi3.setType(String.class);
        request.addProperty(pi3); 
        
        PropertyInfo pi4 = new PropertyInfo();
        pi4.setName("versionCode");
		pi4.setValue(versionCode);
		pi4.setType(Integer.class);
        request.addProperty(pi4);
        
        PropertyInfo pi5 = new PropertyInfo();
        pi5.setName("countryCode");
		pi5.setValue(countryCode);
		pi5.setType(String.class);
        request.addProperty(pi5);   
        
        PropertyInfo pi6 = new PropertyInfo();
        pi6.setName("clientCode");
		pi6.setValue(clientCode);
		pi6.setType(Integer.class);
        request.addProperty(pi6);
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString()); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return retval;
	}
		
	private int soapAddToFriendsList(String inviter, String friend)
	{				
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_ADD_FRIEND_LIST;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_ADD_FRIEND_LIST);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("inviter");//Define the variable name in the web service method
		pi.setValue(inviter);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("friend");
		pi2.setValue(friend);
		pi2.setType(String.class);
        request.addProperty(pi2);
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();           
            retval = Integer.parseInt(response.toString()); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return retval;
	}
	
	private int soapRemoveFromFriendsList(String inviter, String friend)
	{				
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_REMOVE_FRIEND_LIST;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REMOVE_FRIEND_LIST);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("inviter");//Define the variable name in the web service method
		pi.setValue(inviter);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("friend");
		pi2.setValue(friend);
		pi2.setType(String.class);
        request.addProperty(pi2);
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();           
            retval = Integer.parseInt(response.toString()); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return retval;
	}
	
	private String soapSendGameInvite(String inviter, String invitee)
	{				
		String responseStr = JConstants.RESULT_NO_RESPONSE + "&";
		if (!inviter.toUpperCase().equals(invitee.toUpperCase()))
		{
			String soapAction = NAMESPACE + '/' + METHOD_SEND_GAME_INVITE;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_SEND_GAME_INVITE);
			
			PropertyInfo pi = new PropertyInfo();
			pi.setName("inviter");//Define the variable name in the web service method
			pi.setValue(inviter);//Define value for fname variable
			pi.setType(String.class);//Define the type of the variable
	        request.addProperty(pi);//Pass properties to the variable
	        
	        PropertyInfo pi2 = new PropertyInfo();
	        pi2.setName("invitee");
			pi2.setValue(invitee);
			pi2.setType(String.class);
	        request.addProperty(pi2);
			
	        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	        envelope.setOutputSoapObject(request);		        
	        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
	        try 
	        {
	            ht.call(soapAction, envelope);
	            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();           
	            responseStr = response.toString();
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
		}
		else
		{
			responseStr = JConstants.RESULT_CANNOT_PLAY_AGAINST_YOURSELF + "&";
		}
		return responseStr;
	}	
	
	private int soapAddChatMessage(long gameID, String talker, String txt, boolean paidVersion)
	{				
		int responseInt = JConstants.RESULT_NO_RESPONSE;
				
		String soapAction = NAMESPACE + '/' + METHOD_ADD_CHAT_MESSAGE_PAID_CHECK;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_ADD_CHAT_MESSAGE_PAID_CHECK);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("gameID");//Define the variable name in the web service method
		pi.setValue(gameID);//Define value for fname variable
		pi.setType(Long.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("talker");
		pi2.setValue(talker);
		pi2.setType(String.class);
        request.addProperty(pi2);
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("tekst");
		pi3.setValue(txt);
		pi3.setType(String.class);
        request.addProperty(pi3);
        
        PropertyInfo pi4 = new PropertyInfo();
        pi4.setName("isPaid");
		pi4.setValue(paidVersion);
		pi4.setType(Boolean.class);
        request.addProperty(pi4);
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse(); 
            responseInt = Integer.parseInt(response.toString());             
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return responseInt;
	}	
	
	private String soapRequestPastChatMessages(long gameID)
	{				
		String responseString = JConstants.RESULT_NO_RESPONSE + "#";
		
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_CHAT_MESSAGES;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_CHAT_MESSAGES);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("gameID");//Define the variable name in the web service method
		pi.setValue(gameID);//Define value for fname variable
		pi.setType(Long.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse(); 
            responseString = response.toString();   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return responseString;
	}
	
	private String soapRequestLastXBoards(String playerName, int X)
	{				
		String responseString = JConstants.RESULT_NO_RESPONSE + "#";
		
		String soapAction = NAMESPACE + '/' + METHOD_GET_LAST_X_BOARDS;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_LAST_X_BOARDS);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("playerName");//Define the variable name in the web service method
		pi.setValue(playerName);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("X");
		pi2.setValue(X);
		pi2.setType(Integer.class);
        request.addProperty(pi2);
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse(); 
            responseString = response.toString();   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return responseString;
	}
	
	private String soapSendGameInviteToBot(String inviter)
	{				
		String responseStr = JConstants.RESULT_NO_RESPONSE + "&";
		
		String soapAction = NAMESPACE + '/' + METHOD_SEND_GAME_INVITE_TO_BOT;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_SEND_GAME_INVITE_TO_BOT);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("inviter");//Define the variable name in the web service method
		pi.setValue(inviter);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();           
            responseStr = response.toString();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }	
		
		return responseStr;
	}
	
	
	
	private String soapSendRandomGameInvite(String inviter)
	{				
		String responseStr = JConstants.RESULT_NO_RESPONSE + "&";
		
		String soapAction = NAMESPACE + '/' + METHOD_SEND_RANDOM_GAME_INVITE;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_SEND_RANDOM_GAME_INVITE);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("inviter");//Define the variable name in the web service method
		pi.setValue(inviter);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable      
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            responseStr = response.toString();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return responseStr;
	}	
	
	public ArrayList<GameInstance> soapRequestGameArrayWithAppVersionCheck(String username, int limit, int appVersion)
	{
		_timeoutExceededWhileRequestingGamesList = false;
		int resultCode = JConstants.RESULT_INVALID_REQUEST;
		ArrayList<GameInstance> results = new ArrayList<GameInstance>();
		
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_GAME_ARRAY_VERSIONED_WITH_CHAT_IDS;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_GAME_ARRAY_VERSIONED_WITH_CHAT_IDS);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("username");//Define the variable name in the web service method
		pi.setValue(username);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("limit");
		pi2.setValue(limit);
		pi2.setType(Integer.class);
        request.addProperty(pi2);
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("appVersion");
		pi3.setValue(appVersion);
		pi3.setType(Integer.class);
        request.addProperty(pi3);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
        	ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            
            String str = response.toString();
            String resultArr[] = str.split("#");//Result string will split & store in an array
            // first extract the resultCode (0, 5 or 13)
            resultCode = Integer.parseInt(resultArr[0]);
            _appNeedsToBeUpgraded = (resultCode == JConstants.RESULT_INCORRECT_VERSION);
            String g;
            int secondsAgo= -1, state = -1;
            if (resultCode == JConstants.RESULT_OK)
            {
	            for (int i = 1; i < resultArr.length; i++)
	            {
	            	g = resultArr[i];
	            	String gameArr[] = g.split("&");
	            	long id = Long.parseLong(gameArr[0]);
	            	String inviter = gameArr[1];
	            	String invitee = gameArr[2];
	            	state = Integer.parseInt(gameArr[3]);
	            	secondsAgo = Integer.parseInt(gameArr[4]);
	            	long h1u1 = Long.parseLong(gameArr[5]);
	            	long h1u2 = Long.parseLong(gameArr[6]);
	            	long h2u1 = Long.parseLong(gameArr[7]);
	            	long h2u2 = Long.parseLong(gameArr[8]);
	            	long shipu1 = Long.parseLong(gameArr[9]);
	            	long shipu2 = Long.parseLong(gameArr[10]);
	            	int shipdir1 = Integer.parseInt(gameArr[11]);
	            	int shipdir2 = Integer.parseInt(gameArr[12]);
	            	long maxChat = Long.parseLong(gameArr[13]);
	            	GameInstance gi = new GameInstance(inviter, invitee, state, 
	            			shipu1, shipu2, shipdir1, shipdir2, secondsAgo, h1u1, h1u2, h2u1, h2u2, id, maxChat);
					results.add(gi);
	            }   
            }
            
        } 
        catch (ConnectException ce)
        {
        	_timeoutExceededWhileRequestingGamesList = true;
        	resultCode = JConstants.RESULT_NO_RESPONSE;
        }
        catch (SocketTimeoutException ste)
        {
        	_timeoutExceededWhileRequestingGamesList = true;
        	resultCode = JConstants.RESULT_NO_RESPONSE;
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return results;	
	}	
	
	public String soapRequestLatestIDOpponentAndState(String username)
	{
		String result = JConstants.RESULT_NO_RESPONSE + "&";
		
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_ID_OPP_STATE;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_ID_OPP_STATE);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("username");//Define the variable name in the web service method
		pi.setValue(username);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable      
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();           
        } 
        catch (ConnectException ce)
        {
        	// DO SOMETHING!
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return result;	
	}
	
	public long soapRequestCheckNewChatMessages(String username)
	{
		long result = JConstants.RESULT_NO_RESPONSE;
		
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_NEW_CHAT_MESSAGES;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_NEW_CHAT_MESSAGES);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("username");//Define the variable name in the web service method
		pi.setValue(username);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable      
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Long.parseLong(response.toString());        
        } 
        catch (ConnectException ce)
        {
        	// DO SOMETHING!
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return result;	
	}
	
	
	
	public String soapRequestScoreAndCountryCodes(long gameID)
	{
		
		String retString = "5";
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_SCORE_AND_COUNTRY_CODES;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_SCORE_AND_COUNTRY_CODES);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("gameID");//Define the variable name in the web service method
		pi.setValue(gameID);//Define value for fname variable
		pi.setType(Long.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retString = response.toString();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }        
		return retString;
		
	}
	
	public GameInstance soapRequestGameByID(long gameID)
	{
		GameInstance result = null;
		
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_GAME_BY_ID_WITH_CHAT_ID;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_GAME_BY_ID_WITH_CHAT_ID);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("gameID");//Define the variable name in the web service method
		pi.setValue(gameID);//Define value for fname variable
		pi.setType(Long.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();           
            
            String str = response.toString();
            String gameArr[] = str.split("&");//Result string will split & store in an array
            int state = -1, secondsAgo = -1;
        	long id = Long.parseLong(gameArr[0]);
        	String inviter = gameArr[1];
        	String invitee = gameArr[2];
        	state = Integer.parseInt(gameArr[3]);
        	secondsAgo = Integer.parseInt(gameArr[4]);
        	long h1u1 = Long.parseLong(gameArr[5]);
        	long h1u2 = Long.parseLong(gameArr[6]);
        	long h2u1 = Long.parseLong(gameArr[7]);
        	long h2u2 = Long.parseLong(gameArr[8]);
        	long shipu1 = Long.parseLong(gameArr[9]);
        	long shipu2 = Long.parseLong(gameArr[10]);
        	int shipdir1 = Integer.parseInt(gameArr[11]);
        	int shipdir2 = Integer.parseInt(gameArr[12]);
        	long maxChat = Long.parseLong(gameArr[13]);
        	GameInstance gi = new GameInstance(inviter, invitee, state, 
        			shipu1, shipu2, shipdir1, shipdir2, secondsAgo, h1u1, h1u2, h2u1, h2u2, id, maxChat);
			result = gi;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return result;
	}
	
	private String soapGetRatingAndCountryForPlayer(String opponentName)
	{				
		String responseString = JConstants.RESULT_NO_RESPONSE + "&";
		
		String soapAction = NAMESPACE + '/' + METHOD_GET_RATING_AND_COUNTRY_FOR_PLAYER;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_RATING_AND_COUNTRY_FOR_PLAYER);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("playerName");//Define the variable name in the web service method
		pi.setValue(opponentName);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
		
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse(); 
            responseString = response.toString();   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
		return responseString;
	}	
	
	public int soapSendGameAccepted(String inviter, String invitee, boolean yesno)
	{		
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_ACCEPT_GAME;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_ACCEPT_GAME);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("inviter");//Define the variable name in the web service method
		pi.setValue(inviter);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("invitee");
		pi2.setValue(invitee);
		pi2.setType(String.class);
        request.addProperty(pi2);
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("yesno");
		pi3.setValue(yesno);
		pi3.setType(Boolean.class);
        request.addProperty(pi3);        
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }        
		return retval;
	}
		
	public int soapSetInitialBoardNEW(long gameID, long shipboard, int shipdirections, int player)
	{		
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_SET_INITIAL_BOARD_NEW;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_SET_INITIAL_BOARD_NEW);
		
		PropertyInfo pi = new PropertyInfo();
        pi.setName("gameID");
		pi.setValue(gameID);
		pi.setType(Long.class);
        request.addProperty(pi);		
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("shipboard");
		pi2.setValue(shipboard);
		pi2.setType(Long.class);
        request.addProperty(pi2);
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("shipdirections");
		pi3.setValue(shipdirections);
		pi3.setType(Integer.class);
        request.addProperty(pi3);
        
        PropertyInfo pi4 = new PropertyInfo();
        pi4.setName("player");
		pi4.setValue(player);
		pi4.setType(Integer.class);
        request.addProperty(pi4);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }        
		return retval;
	}
	/*
	public int soapSetInitialBoardForUser(String inviter, String invitee, long shipboard, int shipdirections, int player)
	{		
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_SET_INITIAL_BOARD;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_SET_INITIAL_BOARD);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("inviter");//Define the variable name in the web service method
		pi.setValue(inviter);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("invitee");
		pi2.setValue(invitee);
		pi2.setType(String.class);
        request.addProperty(pi2);
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("shipboard");
		pi3.setValue(shipboard);
		pi3.setType(Long.class);
        request.addProperty(pi3);
        
        PropertyInfo pi4 = new PropertyInfo();
        pi4.setName("shipdirections");
		pi4.setValue(shipdirections);
		pi4.setType(Integer.class);
        request.addProperty(pi4);
        
        PropertyInfo pi5 = new PropertyInfo();
        pi5.setName("player");
		pi5.setValue(player);
		pi5.setType(Integer.class);
        request.addProperty(pi5);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }        
		return retval;
	}
	*/
	public String soapFireUpdateForUser(long gameID, int position, int player)
	{
		String retString = "5";
		String soapAction = NAMESPACE + '/' + METHOD_SEND_FIRE_UPDATE;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_SEND_FIRE_UPDATE);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("gameID");//Define the variable name in the web service method
		pi.setValue(gameID);//Define value for fname variable
		pi.setType(Long.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable		
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("position");
		pi2.setValue(position);
		pi2.setType(Integer.class);
        request.addProperty(pi2);       
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("player");
		pi3.setValue(player);
		pi3.setType(Integer.class);
        request.addProperty(pi3);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retString = response.toString();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }        
		return retString;
	}
	
	public int soapSendResign(long gameID, int player)
	{		
		int retval = 5;
		String soapAction = NAMESPACE + '/' + METHOD_REQUEST_RESIGN;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_REQUEST_RESIGN);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("gameID");//Define the variable name in the web service method
		pi.setValue(gameID);//Define value for fname variable
		pi.setType(Long.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable	      
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("player");
		pi2.setValue(player);
		pi2.setType(Integer.class);
        request.addProperty(pi2);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            retval = Integer.parseInt(response.toString());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }        
		return retval;
	}
	
	public ArrayList<String> soapGetNationalTopX(String countryCode, int limiet)
	{
		ArrayList<String> toppers = new ArrayList<String>();
		
		String soapAction = NAMESPACE + '/' + METHOD_GET_NATIONAL_TOP_X_NEW;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_NATIONAL_TOP_X_NEW);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("limiet");//Define the variable name in the web service method
		pi.setValue(limiet);//Define value for fname variable
		pi.setType(Integer.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("countryCode");
		pi2.setValue(countryCode);
		pi2.setType(String.class);
        request.addProperty(pi2);
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            
            String str = response.toString();
            String resultArr[] = str.split("#");//Result string will split & store in an array
            
            for (int i = 0; i < resultArr.length; i++)
            {
            	toppers.add(resultArr[i]);
            }
                    
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return toppers;
	}		
	
	public ArrayList<String> soapGetWorldTopX(int limiet)
	{
		ArrayList<String> toppers = new ArrayList<String>();
		
		String soapAction = NAMESPACE + '/' + METHOD_GET_WORLD_TOP_X_NEW;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_WORLD_TOP_X_NEW);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("limiet");//Define the variable name in the web service method
		pi.setValue(limiet);//Define value for fname variable
		pi.setType(Integer.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            
            String str = response.toString();
            String resultArr[] = str.split("#");//Result string will split & store in an array
            
            for (int i = 0; i < resultArr.length; i++)
            {
            	toppers.add(resultArr[i]);
            }
                    
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return toppers;
	}		
			
	public ArrayList<String> soapGetFriendsList(String user)
	{
		ArrayList<String> friends = new ArrayList<String>();
		
		String soapAction = NAMESPACE + '/' + METHOD_GET_FRIENDS_LIST;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_FRIENDS_LIST);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("user");//Define the variable name in the web service method
		pi.setValue(user);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            
            String str = response.toString();
            String resultArr[] = str.split("#");//Result string will split & store in an array
            
            for (int i = 0; i < resultArr.length; i++)
            {
            	friends.add(resultArr[i]);
            }
                    
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return friends;
	}		
	
	public ArrayList<String> soapGetFriendsSearch(String searchString)
	{
		ArrayList<String> friends = new ArrayList<String>();
		
		String soapAction = NAMESPACE + '/' + METHOD_GET_FRIENDS_SEARCH;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_FRIENDS_SEARCH);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("searchString");//Define the variable name in the web service method
		pi.setValue(searchString);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
            ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            
            String str = response.toString();
            String frArr[] = str.split("#");//Result string will split & store in an array
            for (int i = 0; i < frArr.length; i++)
            {
            	friends.add(frArr[i]);
            }           
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return friends;
	}
	
	public String soapRequestPlayerStatistics(String user)
	{
		String resultStr = "";
		String soapAction = NAMESPACE + '/' + METHOD_GET_PLAYER_STATISTICS;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PLAYER_STATISTICS);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("user");//Define the variable name in the web service method
		pi.setValue(user);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
        	ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            resultStr = response.toString();                 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return resultStr;
	}
	
	public String soapRatingUpdate(String currentUsr, String currentOpp)
	{
		String resultStr = "5";
		String soapAction = NAMESPACE + '/' + METHOD_GET_PLAYER_RATING_UPDATE;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PLAYER_RATING_UPDATE);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("currentUsr");//Define the variable name in the web service method
		pi.setValue(currentUsr);//Define value for fname variable
		pi.setType(String.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("currentOpp");
		pi2.setValue(currentOpp);
		pi2.setType(String.class);
        request.addProperty(pi2);
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
        	ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            resultStr = response.toString();                 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return resultStr;
	}
	
	public String soapAskSetting(int settingID, String currentUsr)
	{
		String resultStr = "5";
		String soapAction = NAMESPACE + '/' + METHOD_ASK_SETTING;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_ASK_SETTING);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("settingID");//Define the variable name in the web service method
		pi.setValue(settingID);//Define value for fname variable
		pi.setType(Integer.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable      
        
        PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("player");//Define the variable name in the web service method
		pi2.setValue(currentUsr);//Define value for fname variable
		pi2.setType(String.class);//Define the type of the variable
        request.addProperty(pi2);//Pass properties to the variable      
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
        	ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            resultStr = response.toString();                 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return resultStr;
	}
	
	public String soapGiveSetting(int settingID, String currentUsr, int settingValue)
	{
		String resultStr = "5";
		String soapAction = NAMESPACE + '/' + METHOD_GIVE_SETTING;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_GIVE_SETTING);
		
		PropertyInfo pi = new PropertyInfo();
		pi.setName("settingID");//Define the variable name in the web service method
		pi.setValue(settingID);//Define value for fname variable
		pi.setType(Integer.class);//Define the type of the variable
        request.addProperty(pi);//Pass properties to the variable      
        
        PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("player");//Define the variable name in the web service method
		pi2.setValue(currentUsr);//Define value for fname variable
		pi2.setType(String.class);//Define the type of the variable
        request.addProperty(pi2);//Pass properties to the variable      
        
        PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("settingValue");//Define the variable name in the web service method
		pi3.setValue(settingValue);//Define value for fname variable
		pi3.setType(Integer.class);//Define the type of the variable
        request.addProperty(pi3);//Pass properties to the variable     
               
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);		        
        HttpTransportSE ht = new HttpTransportSE(URL, 10000);
        try 
        {
        	ht.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            resultStr = response.toString();                 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return resultStr;
	}
	
	
	
	
	
	
	
	
	private void _showLogInResultInUI(JLoginActivity a) 
	{
		  endTime = System.currentTimeMillis();		  

		  a.receiveLogInResult(_iresult);	  

		}
	
	private void _showSignUpResultInUI(JSignupActivity a) 
	{
		  endTime = System.currentTimeMillis();		 
		
		  a.receiveSignUpResult(_iresult);
	}
	
	private void _showChangePasswordResultInUI(ResetPassword a) 
	{
		  endTime = System.currentTimeMillis();		 
		
		  a.receiveChangePasswordResult(_iresult);
	}
	
	private void _showGameAcceptationResultInUI(JGamePicker a) 
	{
		  endTime = System.currentTimeMillis();
		 		
		  a.receiveGameAcceptationResult(_iresult);
	}
	
	private void _showRunningGamesInUI(JGamePicker a) 
	{
		endTime = System.currentTimeMillis();		
		if (a != null)
		{
			if (!_appNeedsToBeUpgraded)
			{
				if (!_timeoutExceededWhileRequestingGamesList)
					a.receiveGamesListResult(_returnedGameArray, (int) (endTime-startTime), true);
				else
					a.receiveGamesListResult(_returnedGameArray, (int) (endTime-startTime), false);
			}
			else
				a.appNeedsToBeUpgraded();
		}
		
	}
	
	private void _showPlayerStatisticsInUI(StatsActivity a) 
	{
		endTime = System.currentTimeMillis();				
		a.receivePlayerStatistics(_resultStr);
	}
		
	private void _updateLatestGameInfo(NotifyService ns) 
	{			
		if (_resultStr != null)
		{		  
		  ns.receiveLatestGameInfo_LEAN(_resultStr);
		}
	}
	
	private void _updateLatestChatInfo(NotifyService ns) 
	{			
		ns.receiveLatestChatInfo(longresult);
	}
	
	private void _showSearchResultInUI(JGamePicker a)
	{
		a.receiveSearchResult(_returnedFriendsList);
	}
	
	private void _showRatingUpdateResultInUI(ShootActivity a)
	{
		a.receiveRatingUpdateResult(_resultStr);
	}
	
	private void _showFriendsListInUI(JGamePicker a) 
	{
		a.populateNewGameDialog(_returnedFriendsList);
	}
	
	private void _showTopXInUI(StatsActivity a) 
	{
		a.populateTopXDialog(_returnedFriendsList);
	}
	
	private void _showGameInviteInUI(JGamePicker a) 
	{
		a.receiveGameInviteResult(_resultStr);
	}
	
	private void _showGameInviteInUI(StatsActivity a) 
	{
		a.receiveGameInviteResult(_resultStr);
	}	
	
	private void _showChatReceivedInUI(ShootActivity a) 
	{
		a.receiveChatResult(_iresult);
	}
	
	private void _showPastChatMessagesInUI(ShootActivity a) 
	{
		a.receivePastChatMessages(_resultStr);
	}
	
	private void _showLastXBoardsInUI(ShootActivity a) 
	{
		a.receiveLastXBoards(_resultStr);
	}
	
	private void _showLastXBoardsInUI(PlayFieldActivity a) 
	{
		a.receiveLastXBoards(_resultStr);
	}
	
	private void _showGameInviteInUI(ShootActivity a) 
	{
		a.receiveGameInviteResult(_iresult);
	}
	
	private void _showFriendInviteUI(JGamePicker a)
	{
		a.receiveFriendInviteResult(_iresult);
	}
	
	private void _showFriendInviteUI(StatsActivity a)
	{
		a.receiveFriendInviteResult(_iresult);
	}
	
	private void _showFriendInviteUI(ShootActivity a)
	{
		a.receiveFriendInviteResult(_iresult);
	}
	
	private void _showFriendRemovalInUI(JGamePicker a)
	{
		a.receiveFriendRemovalResult(_iresult);
	}
	
	private void _showGameUpdateResultInUI(PlayFieldActivity a)
	{		
		a.receiveGameUpdateResult(_iresult);
	}
	
	private void _showFireUpdateResultInUI(ShootActivity a)
	{		
		endTime = System.currentTimeMillis();	
		if (a != null)
			a.receiveFireUpdateResult(_resultStr, (int) (endTime-startTime));
		else
			System.out.println("Would've crashed here");
	}
	
	private void _showResignUpdateResultInUI(ShootActivity a)
	{
		a.receiveResignResult(_iresult);
	}
	
	private void _showCheckGameInstanceInShootActivity(ShootActivity a) 
	{
		if (a != null)
			a.receiveCheckGameInstanceResult(_returnedGameInstance);
		else
			System.out.println("Would've crashed here");
	}	
	
	private void _showScoreAndCountryCodesInShootActivity(ShootActivity a)
	{		
		a.receiveScoreAndCountryCodesResult(_resultStr);
	}
	
	private void _showPlayerRatingAndCountryResultInUI(JGamePicker a, String opponentName, String me)
	{		
		a.receivePlayerRatingAndCountryResult(_resultStr, opponentName, me);
	}
	
	
	
	private void _showAskSettingRandomInvitationsUpdateResultInUI(SettingsActivity a)
	{		
		a.receiveAskSettingRandomInvitationsResult(_resultStr);
	}
	
	private void _showGiveSettingRandomInvitationsUpdateResultInUI(SettingsActivity a)
	{		
		a.receiveAskSettingRandomInvitationsResult(_resultStr);
	}
	
	
}