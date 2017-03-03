package com.kipsap.jshipbattle;

import com.kipsap.commonsource.JConstants;
import com.kipsap.jshipbattle.util.IabHelper;
import com.kipsap.jshipbattle.util.IabResult;
import com.kipsap.jshipbattle.util.Purchase;
import com.kipsap.jshipbattle.util.Inventory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JGamePicker extends FragmentActivity {

    AlertDialog alertDialog, menumenuDialog;
    ListView lvGamesList;
    LazyAdapter glAdapter;
    final Context context = this;
    String currentUsr, selectedFriend = null, selectedFoundUser = null;
    private ImageButton ibtnNewgame, ibtnFindfriends, ibtnStats, ibtnSettings, ibtnRefresh, buttonMenuMenu;
    private JGetDataFromWebService jgd;
    RadioButton rbOption0, rbOption1, rbOption2;
    WindowManager _wm;
    Display _display;
    RadioGroup radioGroup;
    Timer timer;
    Handler h;
    ArrayList<String> allGames, allFriends, updateTimes, allCountrys;
    ArrayList<Integer> colorStates, allRatings;
    ArrayList<Boolean> chatNotifs;
    boolean imPlayerA, bRequestingGamesList;
    SharedPreferences sharedPrefs;
    boolean newb, bInAppBilling, bPaidVersion;
    private int appVersion;
    int styleID, themeID;
    private static final int DIALOG1_KEY = 1; //waiting while getting friends list
    private static final int DIALOG2_KEY = 2; //waiting while getting search results
    private static final int DIALOG3_KEY = 3; //waiting while getting player's rating&country
    ProgressDialog dialog1, dialog2, dialog3;

    private RelativeLayout rootView;

    private static final String TAG_SHIPBATTLE = "com.kipsap.jshipbattle";
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPremiumAppBoughtListener;
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gamepick);

        final Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mHelper = new IabHelper(this, JConstants.base64EncodedPublicKey);
        sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);
        styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        themeID = Math.max(0, sharedPrefs.getInt("themeID", 0));
        newb = sharedPrefs.getBoolean("newb2", true);

        mPremiumAppBoughtListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isFailure()) {
                    return;
                } else if (purchase.getSku().equals(JConstants.SKU_PREMIUM_APP)) {
                    Log.d(TAG_SHIPBATTLE, "In-app Billing: purchase finished, premium app bought!");
                    mHelper.queryInventoryAsync(mReceivedInventoryListener);
                }
            }
        };

        mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener()
        {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory)
            {
                if (result.isFailure())
                {
                    System.out.println("query inventory is a failure ..");
                }
                else
                {
                    boolean bOldPaidVersion = bPaidVersion;
                    if (inventory.hasPurchase(JConstants.SKU_PREMIUM_APP))
                    {
                        bPaidVersion = true;
                    }
                    else
                    {
                        bPaidVersion = true;    //FALSE !
                        //styleID = 0;          //UNCOMMENT
                        //themeID = 0;          //UNCOMMENT
                    }

                    SharedPreferences.Editor editor;
                    editor = sharedPrefs.edit();
                    editor.putBoolean("bPaidVersion", bPaidVersion);
                    editor.putInt("themeID", themeID);
                    editor.putInt("styleID", styleID);
                    editor.commit();

                    if (bOldPaidVersion != bPaidVersion)
                        setupUi();
                }
            }
        };

        currentUsr = "";
        bRequestingGamesList = false;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
                currentUsr = bundle.getString("currentUser"); //may return null ?

        if (currentUsr == null || currentUsr.equals(""))
        {
            currentUsr = sharedPrefs.getString("username", "");
            if (currentUsr.equals("")) {
                // no known user, so go to the startscreen first
                Intent goToTheStart = new Intent(JGamePicker.this, LoginOrSignup.class);
                startActivity(goToTheStart);
                finish(); //klaar hier
            }
        }

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    bInAppBilling = false;
                    Log.d(TAG_SHIPBATTLE, "In-app Billing setup failed: " + result);
                } else {
                    bInAppBilling = true;
                    Log.d(TAG_SHIPBATTLE, "In-app Billing is set up OK");
                }
                mHelper.queryInventoryAsync(mReceivedInventoryListener);
            }
        });

        if (newb)
        {
            LayoutInflater li = LayoutInflater.from(context);
            View newbView = li.inflate(R.layout.newbie_gamepicker_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setView(newbView);
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor;
                    editor = sharedPrefs.edit();
                    editor.putBoolean("newb2", false); // not a newb2 anymore
                    editor.commit();

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            setCorrectBackGroundDrawableForButtons(alertDialog, false, false, true);
        }

        appVersion = -1;
        PackageInfo pinfo;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pinfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        rootView = (RelativeLayout) findViewById(R.id.container);
        lvGamesList = (ListView) findViewById(R.id.lvGamesList);
        buttonMenuMenu = (ImageButton) findViewById(R.id.menu_menu);
        ibtnNewgame = (ImageButton) findViewById(R.id.ibtnNewgame);
        ibtnFindfriends = (ImageButton) findViewById(R.id.ibtnFindfriends);
        ibtnSettings = (ImageButton) findViewById(R.id.ibtnSettings);
        ibtnStats = (ImageButton) findViewById(R.id.ibtnStats);
        ibtnRefresh = (ImageButton) findViewById(R.id.ibtnRefresh);

        _wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        _display = _wm.getDefaultDisplay();
        jgd = new JGetDataFromWebService();
        h = new Handler();

        Intent startTheService = new Intent(context, NotifyService.class);
        startService(startTheService);

        radioGroup = new RadioGroup(context);
        rbOption0 = new RadioButton(context);
        rbOption1 = new RadioButton(context);
        rbOption2 = new RadioButton(context);

        buttonMenuMenu.setEnabled(true);
        buttonMenuMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMenuMenu();
            }
        });

        ibtnNewgame.setEnabled(true);
        ibtnNewgame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG1_KEY);
                jgd.requestFriendsList(JGamePicker.this, currentUsr);
                ibtnNewgame.setEnabled(false);
            }

        });

        ibtnFindfriends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.find_friend_dialog, null);
                LinearLayout layout_root = (LinearLayout) promptsView.findViewById(R.id.layout_root);
                switch (themeID) {
                    case 0:
                        layout_root.setBackgroundResource(R.drawable.chatbackground_half);
                        break;
                    case 1:
                        layout_root.setBackgroundResource(R.drawable.chatbackground_half_green);
                        break;
                    case 2:
                        layout_root.setBackgroundResource(R.drawable.chatbackground_half_white);
                        break;
                    case 3:
                        layout_root.setBackgroundResource(R.drawable.chatbackground_half_orange);
                        break;
                    case 4:
                        layout_root.setBackgroundResource(R.drawable.chatbackground_half);
                        break;
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);

                final EditText searchString = (EditText) promptsView.findViewById(R.id.etSearchString);
                final TextView findPlayers = (TextView) promptsView.findViewById(R.id.tvFindFriends);
                findPlayers.setTypeface(army);

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.btn_search),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String stringSearch = searchString.getText().toString();
                                        if (stringSearch.length() >= 2) {
                                            showDialog(DIALOG2_KEY);
                                            jgd.sendFriendsSearch(JGamePicker.this, stringSearch);
                                        } else {
                                            Toast.makeText(JGamePicker.this, getString(R.string.toast_minimum2characters), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton(getString(R.string.btn_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
            }
        });

        ibtnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("currentUsr", currentUsr);

                Intent goStats = new Intent(JGamePicker.this, StatsActivity.class);
                goStats.putExtras(bundle);
                startActivity(goStats);
            }
        });

        ibtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("currentUsr", currentUsr);

                Intent goSetting = new Intent(JGamePicker.this, SettingsActivity.class);
                goSetting.putExtras(bundle);
                startActivity(goSetting);
            }
        });

        ibtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bRequestingGamesList) {
                    bRequestingGamesList = true;
                    setButtonRefreshOrRefreshing();
                    jgd.requestRunningGames(JGamePicker.this, currentUsr, appVersion);
                }
            }
        });


    }


    private void setupUi()
    {
        setButtonRefreshOrRefreshing(); //to set the correct styleID for the refresh button

        switch (styleID)
        {
            case 0:
                rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo);
                ibtnNewgame.setBackgroundResource(R.drawable.plus);
                ibtnFindfriends.setBackgroundResource(R.drawable.loep);
                ibtnStats.setBackgroundResource(R.drawable.stats);
                ibtnSettings.setBackgroundResource(R.drawable.settings);
                ibtnRefresh.setBackgroundResource(R.drawable.refresh);
                break;
            case 1:
                rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo_black);
                ibtnNewgame.setBackgroundResource(R.drawable.plus_black);
                ibtnFindfriends.setBackgroundResource(R.drawable.loep_black);
                ibtnStats.setBackgroundResource(R.drawable.stats_black);
                ibtnSettings.setBackgroundResource(R.drawable.settings_black);
                ibtnRefresh.setBackgroundResource(R.drawable.refresh_black);
                break;
            case 2:
                rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo_white);
                ibtnNewgame.setBackgroundResource(R.drawable.plus_white);
                ibtnFindfriends.setBackgroundResource(R.drawable.loep_white);
                ibtnStats.setBackgroundResource(R.drawable.stats_white);
                ibtnSettings.setBackgroundResource(R.drawable.settings_white);
                ibtnRefresh.setBackgroundResource(R.drawable.refresh_white);
                break;
            case 3:
                rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo_brush);
                ibtnNewgame.setBackgroundResource(R.drawable.plus_brush);
                ibtnFindfriends.setBackgroundResource(R.drawable.loep_brush);
                ibtnStats.setBackgroundResource(R.drawable.stats_brush);
                ibtnSettings.setBackgroundResource(R.drawable.settings_brush);
                ibtnRefresh.setBackgroundResource(R.drawable.refresh_brush);

                ibtnNewgame.setPadding(30, 30, 30, 30);
                break;
        }

        if (bPaidVersion)
        {
            FragmentManager fm = getSupportFragmentManager();
            Fragment adFragment = fm.findFragmentById(R.id.adFragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(adFragment);
            ft.commit();
        }
        else
        {
            LinearLayout empty = (LinearLayout) findViewById(R.id.empty);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
            empty.setLayoutParams(lp);
        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("new jgamepicker intent");
        super.onNewIntent(intent);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG1_KEY: {
                dialog1 = new ProgressDialog(this);
                dialog1.setIndeterminate(true);
                dialog1.setCancelable(true);
                return dialog1;
            }
            case DIALOG2_KEY: {
                dialog2 = new ProgressDialog(this);
                dialog2.setIndeterminate(true);
                dialog2.setCancelable(true);
                return dialog2;
            }
            case DIALOG3_KEY: {
                dialog3 = new ProgressDialog(this);
                dialog3.setIndeterminate(true);
                dialog3.setCancelable(true);
                return dialog3;
            }
        }
        return null;
    }

    public void appNeedsToBeUpgraded() {
        Toast.makeText(JGamePicker.this, getString(R.string.msg_incorrectversion), Toast.LENGTH_LONG).show();
        sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.commit();
        Intent goToTheStart = new Intent(JGamePicker.this, JLoginActivity.class);
        startActivity(goToTheStart);
        finish(); //klaar hier
    }

    public void populateNewGameDialog(ArrayList<String> friends) {
        if (dialog1 != null) {
            dialog1.cancel();
        }
        ibtnNewgame.setEnabled(true);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.invite_dialog, null);
        LinearLayout layout_root = (LinearLayout) promptsView.findViewById(R.id.layout_root);
        switch (themeID) {
            case 0:
                layout_root.setBackgroundResource(R.drawable.chatbackground);
                break;
            case 1:
                layout_root.setBackgroundResource(R.drawable.chatbackground_green);
                break;
            case 2:
                layout_root.setBackgroundResource(R.drawable.chatbackground_white);
                break;
            case 3:
                layout_root.setBackgroundResource(R.drawable.chatbackground_orange);
                break;
            case 4:
                layout_root.setBackgroundResource(R.drawable.chatbackground);
                break;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        TextView findPlayers = (TextView) promptsView.findViewById(R.id.tvNewGame);
        Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
        findPlayers.setTypeface(army);

        RadioButton.OnClickListener myOptionOnClickListener =
                new RadioButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbOption0.isChecked()) {
                            rbOption1.setChecked(false);
                            rbOption2.setChecked(false);
                        }
                        if (rbOption1.isChecked()) {
                            rbOption0.setChecked(false);
                            rbOption2.setChecked(false);
                        }
                        if (rbOption2.isChecked()) {
                            rbOption0.setChecked(false);
                            rbOption1.setChecked(false);
                        }
                    }
                };

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(getString(R.string.btn_send_game_invitation),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (rbOption0.isChecked()) {
                                    jgd.sendGameInviteToBot(JGamePicker.this, currentUsr);
                                } else if (rbOption1.isChecked()) {
                                    jgd.sendGameInvite(JGamePicker.this, currentUsr, null, true);
                                } else if (rbOption2.isChecked()) {
                                    if (selectedFriend != null) {
                                        jgd.sendGameInvite(JGamePicker.this, currentUsr, selectedFriend, false);
                                    } else {
                                        //System.out.println("no friend selected");
                                    }
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        radioGroup = (RadioGroup) promptsView.findViewById(R.id.radioGroup1);
        rbOption0 = (RadioButton) promptsView.findViewById(R.id.ngOption0);
        rbOption1 = (RadioButton) promptsView.findViewById(R.id.ngOption1);
        rbOption2 = (RadioButton) promptsView.findViewById(R.id.ngOption2);

        rbOption0.setOnClickListener(myOptionOnClickListener);
        rbOption1.setOnClickListener(myOptionOnClickListener);
        if (friends.size() == 0) {
            rbOption2.setEnabled(false);
            rbOption2.setTextColor(Color.DKGRAY);
        } else {
            rbOption2.setOnClickListener(myOptionOnClickListener);
        }

        allFriends = new ArrayList<String>();
        allRatings = new ArrayList<Integer>();
        allCountrys = new ArrayList<String>();
        int i;
        for (i = 0; i < friends.size(); i++) {
            try {
                String frDetailsArr[] = friends.get(i).split("&");
                allFriends.add(frDetailsArr[0]);
                allRatings.add(Integer.parseInt(frDetailsArr[1]));
                allCountrys.add(frDetailsArr[2]);
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
        }

        ListView lvFriends = (ListView) promptsView.findViewById(R.id.friendsListView);
        final SelectedAdapter selectedAdapter = new SelectedAdapter(this, 0, allFriends, allRatings, allCountrys);
        selectedAdapter.setNotifyOnChange(true);

        lvFriends.setAdapter(selectedAdapter);
        lvFriends.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                rbOption2.setChecked(true);
                selectedAdapter.setSelectedPosition(position);
                selectedFriend = allFriends.get(position).toString();
            }
        });

        lvFriends.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, final View view, int position, long arg3) {
                rbOption2.setChecked(true);
                selectedAdapter.setSelectedPosition(position);
                selectedFriend = allFriends.get(position).toString();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JGamePicker.this);
                String txt1 = String.format(getString(R.string.fremove_player_from_friends_list), selectedFriend);
                alertDialogBuilder.setMessage(txt1);

                alertDialogBuilder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (selectedFriend != null) {
                            jgd.sendRemoveFriendFromList(JGamePicker.this, currentUsr, selectedFriend);
                            alertDialog.dismiss();
                            selectedFriend = null;
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);

                return true;
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
    }


    public void receiveSearchResult(final ArrayList<String> foundUsers) {
        if (dialog2 != null) {
            dialog2.cancel();
        }
        if (foundUsers.size() > 0) {
            allFriends = new ArrayList<String>();
            allRatings = new ArrayList<Integer>();
            allCountrys = new ArrayList<String>();

            int i;
            for (i = 0; i < foundUsers.size(); i++) {
                try {
                    String frDetailsArr[] = foundUsers.get(i).split("&");
                    allRatings.add(Integer.parseInt(frDetailsArr[1]));
                    allFriends.add(frDetailsArr[0]);
                    allCountrys.add(frDetailsArr[2]);
                } catch (Exception e) {

                }
            }

            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.found_friend_dialog, null);
            LinearLayout layout_root = (LinearLayout) promptsView.findViewById(R.id.layout_root);
            switch (themeID) {
                case 0:
                    layout_root.setBackgroundResource(R.drawable.chatbackground);
                    break;
                case 1:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_green);
                    break;
                case 2:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_white);
                    break;
                case 3:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_orange);
                    break;
                case 4:
                    layout_root.setBackgroundResource(R.drawable.chatbackground);
                    break;
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);

            ListView lvFoundUsers = (ListView) promptsView.findViewById(R.id.foundUsersView);
            final SelectedAdapter selectedAdapter = new SelectedAdapter(this, 0, allFriends, allRatings, allCountrys);
            selectedAdapter.setNotifyOnChange(true);

            lvFoundUsers.setAdapter(selectedAdapter);
            lvFoundUsers.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                    selectedAdapter.setSelectedPosition(position);
                    selectedFoundUser = allFriends.get(position).toString();
                }
            });

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.btn_send_game_invitation),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (foundUsers.size() == 1) {
                                        selectedFoundUser = allFriends.get(0).toString();
                                    }
                                    if (selectedFoundUser != null) {
                                        jgd.sendGameInvite(JGamePicker.this, currentUsr, selectedFoundUser, false);
                                    }
                                }
                            })
                    .setNegativeButton(getString(R.string.btn_add_to_friends_list),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (foundUsers.size() == 1) {
                                        selectedFoundUser = allFriends.get(0).toString();
                                    }
                                    if (selectedFoundUser != null) {
                                        jgd.addToFriendsList(JGamePicker.this, currentUsr, selectedFoundUser);
                                    }
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
        } else {
            Toast.makeText(JGamePicker.this, getString(R.string.toast_nosuchusersfound), Toast.LENGTH_LONG).show();
        }
    }

    class RefreshTask extends TimerTask {
        private JGamePicker a;

        public RefreshTask(JGamePicker a) {
            this.a = a;
        }

        class RunnableLol implements Runnable {
            private JGamePicker a;

            public RunnableLol(JGamePicker a) {
                this.a = a;
            }

            @Override
            public void run() {
                this.a.bRequestingGamesList = true;
                this.a.setButtonRefreshOrRefreshing();
                this.a.jgd.requestRunningGames(JGamePicker.this, currentUsr, appVersion);
                if (this.a.timer != null)
                    this.a.timer.schedule(new RefreshTask(this.a), (JConstants.UPDATE_INTERVAL_GAMEPICKER_SCREEN * 1000));
            }
        }

        @Override
        public void run() {
            RunnableLol r = new RunnableLol(this.a);
            this.a.h.post(r);
        }
    }

    @Override
    protected void onResume() {
        // cancel all existing notifications when entering the gamepicker screen
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(R.string.service_label);

        sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
        styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        SharedPreferences.Editor editor;
        editor = sharedPrefs.edit();
        editor.putBoolean("inAScreenThatDoesNotNeedNotifications", true);
        editor.commit(); // don't receive notifications in the gamepicker screen

        bRequestingGamesList = true;
        setupUi();
        jgd.requestRunningGames(JGamePicker.this, currentUsr, appVersion);

        timer = new Timer();
        timer.schedule(new RefreshTask(this), (JConstants.UPDATE_INTERVAL_GAMEPICKER_SCREEN * 1000));

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (timer != null)
            timer.cancel();
        SharedPreferences.Editor editor;
        editor = sharedPrefs.edit();
        editor.putBoolean("inAScreenThatDoesNotNeedNotifications", false);
        editor.commit(); // receive notifications after leaving this screen
        super.onPause();
    }

    public void receiveGameInviteResult(String resultString) {
        String[] resultArr = null;
        int resultCode = 5;
        String friend = null;
        try {
            resultArr = resultString.split("&");
            resultCode = Integer.parseInt(resultArr[0]);
            if (resultCode == JConstants.RESULT_OK ||
                    resultCode == JConstants.RESULT_ALREADY_RUNNING_GAME)
                friend = resultArr[1];
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }

        selectedFriend = null;
        ibtnNewgame.setEnabled(true);
        switch (resultCode) {
            case JConstants.RESULT_OK:
                // after a successful invitation, request the updated games list:
                Toast.makeText(JGamePicker.this, getString(R.string.toast_invitationsuccessful), Toast.LENGTH_SHORT).show();
                bRequestingGamesList = true;
                setButtonRefreshOrRefreshing();
                jgd.requestRunningGames(JGamePicker.this, currentUsr, appVersion);
                break;
            case JConstants.RESULT_NOT_OK:
                Toast.makeText(JGamePicker.this, "No more users left to invite ...", Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_ALREADY_RUNNING_GAME:
                Toast.makeText(JGamePicker.this, getString(R.string.toast_alreadygamerunning), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_CANNOT_PLAY_AGAINST_YOURSELF:
                Toast.makeText(JGamePicker.this, getString(R.string.toast_cannotinviteyourself), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_UNKNOWN_USER:
                Toast.makeText(JGamePicker.this, getString(R.string.toast_playernonexistent), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_INVALID_REQUEST:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_NO_RESPONSE:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void receiveGameAcceptationResult(int resultCode) {
        switch (resultCode) {
            case JConstants.RESULT_OK:
                // after a successful acceptation or declination of an invitation, request the updated games list:
                bRequestingGamesList = true;
                setButtonRefreshOrRefreshing();
                jgd.requestRunningGames(JGamePicker.this, currentUsr, appVersion);
                break;
            case JConstants.RESULT_NOT_OK:
                // game acceptation/declination could not be sent to server ... DO SOMETHING
                break;
            case JConstants.RESULT_INVALID_REQUEST:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_NO_RESPONSE:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void receiveFriendInviteResult(int resultCode) {
        switch (resultCode) {
            case JConstants.RESULT_OK:
                // friend added
                Toast.makeText(JGamePicker.this, getString(R.string.toast_friendadded), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_NOT_OK:
                // friend not added
                break;
            case JConstants.RESULT_INVALID_REQUEST:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_NO_RESPONSE:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void receiveFriendRemovalResult(int resultCode) {
        switch (resultCode) {
            case JConstants.RESULT_OK:
                // friend added
                Toast.makeText(JGamePicker.this, getString(R.string.toast_friendremoved), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_NOT_OK:
                // friend not added

                break;
            case JConstants.RESULT_INVALID_REQUEST:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();
                break;
            case JConstants.RESULT_NO_RESPONSE:
                Toast.makeText(JGamePicker.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void receiveGamesListResult(ArrayList<GameInstance> gamesList, int timeittook, boolean ontime) {
        long globalMaxChatID;
        bRequestingGamesList = false; //no pending request anymore
        setButtonRefreshOrRefreshing();
        if (!ontime) {
            Toast.makeText(context, getString(R.string.msg_noconnection), Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor;
            if (gamesList == null) {
                Toast.makeText(context, getString(R.string.msg_noconnection), Toast.LENGTH_SHORT).show();
            } else {
                final ArrayList<GameInstance> algi = gamesList;
                final ArrayList<GameInstance> reorderedGamesList = new ArrayList<GameInstance>();
                final int[] alleGameStates = new int[gamesList.size()];

                int element = 0;
                allGames = new ArrayList<String>();
                updateTimes = new ArrayList<String>();
                colorStates = new ArrayList<Integer>();
                chatNotifs = new ArrayList<Boolean>();
                String text;
                long maxChatIDFromPrefs;
                long maxMaxChatID = -1L;

                globalMaxChatID = sharedPrefs.getLong(currentUsr + "globalMaxChatID", -1L);

                // 1e ronde:
                for (GameInstance gi : algi) {
                    String timeago = "";
                    text = "";
                    int diff = gi.getSecondsAgo();
                    if (diff < 60)
                        timeago = diff + " s";
                    else if (diff < 3600)
                        timeago = (diff / 60) + " m";
                    else
                        timeago = (diff / 3600) + " h";

                    if (diff > (3 * 24 * 3600)) // hide games that have not been updated for over 3 days from this list
                        continue;

                    maxChatIDFromPrefs = sharedPrefs.getLong(currentUsr + "MaxChatID" + gi.getGameID(), -1L);
                    if (maxChatIDFromPrefs > maxMaxChatID) maxMaxChatID = maxChatIDFromPrefs;

                    if (gi.getPlayerA().equals(currentUsr)) // I'm player A (the original inviter)
                    {
                        if ((gi.getGameState() == GameInstance.GS_BOARD_BOTH_NOTREADY)
                                || (gi.getGameState() == GameInstance.GS_PLAYER2_BOARD_READY)) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_setupyourfleet), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(1);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER1_TURN) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_yourturn), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(1);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        }
                    } else if (gi.getPlayerB().equals(currentUsr)) // I'm player B (the original invited one)
                    {
                        if (gi.getGameState() == GameInstance.GS_UNACCEPTED) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_invitationfrom), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(1);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if ((gi.getGameState() == GameInstance.GS_BOARD_BOTH_NOTREADY)
                                || (gi.getGameState() == GameInstance.GS_PLAYER1_BOARD_READY)) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_setupyourfleet), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(1);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER2_TURN) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_yourturn), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(1);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        }
                    }
                    if (element == 0) {
                        //sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
                        editor = sharedPrefs.edit();
                        editor.putString("id_and_state_stamp", gi.getGameID() + "&" + gi.getGameState());
                        editor.commit();
                    }
                    if (text != "") {
                        allGames.add(text);
                        reorderedGamesList.add(gi);
                        element++;
                    }
                }

                // 2e ronde:
                for (GameInstance gi : algi) {
                    String timeago = "";
                    text = "";
                    int diff = gi.getSecondsAgo();
                    if (diff < 60)
                        timeago = diff + " s";
                    else if (diff < 3600)
                        timeago = (diff / 60) + " m";
                    else
                        timeago = (diff / 3600) + " h";

                    if (diff > (3 * 24 * 3600)) // hide games that have not been updated for over 3 days from this list
                        continue;

                    maxChatIDFromPrefs = sharedPrefs.getLong(currentUsr + "MaxChatID" + gi.getGameID(), -1L);
                    if (maxChatIDFromPrefs > maxMaxChatID) maxMaxChatID = maxChatIDFromPrefs;

                    if (gi.getPlayerA().equals(currentUsr)) // I'm player A (the original inviter)
                    {
                        if (gi.getGameState() == GameInstance.GS_PLAYER1_WON) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_youwon), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(3);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER2_WON) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_opponentbeatyou), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(4);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER1_RESIGNED) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_youresigned), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(4);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER2_RESIGNED) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_opponentresigned), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(3);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_UNACCEPTED) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_pendinginvitation), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(2);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER1_BOARD_READY) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_waitingfor), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(2);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER2_TURN) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_opponentsturn), gi.getPlayerB());
                            updateTimes.add(timeago);
                            colorStates.add(2);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        }
                    } else if (gi.getPlayerB().equals(currentUsr)) // I'm player B (the original invited one)
                    {
                        if (gi.getGameState() == GameInstance.GS_PLAYER2_WON) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_youwon), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(3);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER1_WON) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_opponentbeatyou), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(4);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER1_RESIGNED) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_opponentresigned), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(3);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER2_RESIGNED) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_youresigned), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(4);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER2_BOARD_READY) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_waitingfor), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(2);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        } else if (gi.getGameState() == GameInstance.GS_PLAYER1_TURN) {
                            alleGameStates[element] = gi.getGameState();
                            text = String.format(getString(R.string.fpick_opponentsturn), gi.getPlayerA());
                            updateTimes.add(timeago);
                            colorStates.add(2);
                            chatNotifs.add((gi.getMaxChatID() > maxChatIDFromPrefs));
                        }
                    }
                    if (element == 0) {
                        sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
                        editor = sharedPrefs.edit();
                        editor.putString("id_and_state_stamp", gi.getGameID() + "&" + gi.getGameState());
                        editor.commit();
                    }
                    if (text != "") {
                        allGames.add(text);
                        reorderedGamesList.add(gi);
                        element++;
                    }
                }

                if (maxMaxChatID > globalMaxChatID) {
                    editor = sharedPrefs.edit();
                    editor.putLong(currentUsr + "globalMaxChatID", maxMaxChatID);
                    editor.commit();
                }

                glAdapter = new LazyAdapter(this, colorStates, allGames, updateTimes, chatNotifs);
                lvGamesList.setAdapter(glAdapter);
                lvGamesList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                        final GameInstance g = reorderedGamesList.get(position);
                        imPlayerA = (g.getPlayerA().equals(currentUsr));

                        switch (alleGameStates[position]) {
                            case GameInstance.GS_UNACCEPTED:

                                if (!imPlayerA) {
                                    showDialog(DIALOG3_KEY);
                                    jgd.getRatingAndCountryForPlayer(JGamePicker.this, g.getPlayerA(), g.getPlayerB());


                                }
                                break;

                            case GameInstance.GS_BOARD_BOTH_NOTREADY:

                                Bundle bundle = new Bundle();
                                bundle.putString("currentUser", currentUsr);
                                bundle.putString("currentOpponent", (imPlayerA ? g.getPlayerB() : g.getPlayerA()));
                                bundle.putLong("shipboardUser1", g.getShipBoardUser1());
                                bundle.putLong("shipboardUser2", g.getShipBoardUser2());
                                bundle.putInt("shipdirectionsUser1", g.getShipDirectionsUser1());
                                bundle.putInt("shipdirectionsUser2", g.getShipDirectionsUser2());
                                bundle.putString("originalInviter", g.getPlayerA());
                                bundle.putInt("state", g.getGameState());
                                bundle.putLong("gameID", g.getGameID());
                                Intent goPlay = new Intent(JGamePicker.this, PlayFieldActivity.class);
                                goPlay.putExtras(bundle);
                                startActivity(goPlay);
                                break;

                            case GameInstance.GS_PLAYER1_BOARD_READY:

                                if (!imPlayerA) {
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("currentUser", currentUsr);
                                    bundle1.putString("currentOpponent", g.getPlayerA());
                                    bundle1.putLong("shipboardUser1", g.getShipBoardUser1());
                                    bundle1.putLong("shipboardUser2", g.getShipBoardUser2());
                                    bundle1.putInt("shipdirectionsUser1", g.getShipDirectionsUser1());
                                    bundle1.putInt("shipdirectionsUser2", g.getShipDirectionsUser2());
                                    bundle1.putString("originalInviter", g.getPlayerA());
                                    bundle1.putInt("state", g.getGameState());
                                    bundle1.putLong("gameID", g.getGameID());
                                    Intent goPlay1 = new Intent(JGamePicker.this, PlayFieldActivity.class);
                                    goPlay1.putExtras(bundle1);
                                    startActivity(goPlay1);
                                }
                                break;

                            case GameInstance.GS_PLAYER2_BOARD_READY:

                                if (imPlayerA) {
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putString("currentUser", currentUsr);
                                    bundle2.putString("currentOpponent", g.getPlayerB());
                                    bundle2.putLong("shipboardUser1", g.getShipBoardUser1());
                                    bundle2.putLong("shipboardUser2", g.getShipBoardUser2());
                                    bundle2.putInt("shipdirectionsUser1", g.getShipDirectionsUser1());
                                    bundle2.putInt("shipdirectionsUser2", g.getShipDirectionsUser2());
                                    bundle2.putString("originalInviter", g.getPlayerA());
                                    bundle2.putInt("state", g.getGameState());
                                    bundle2.putLong("gameID", g.getGameID());
                                    Intent goPlay2 = new Intent(JGamePicker.this, PlayFieldActivity.class);
                                    goPlay2.putExtras(bundle2);
                                    startActivity(goPlay2);
                                }
                                break;

                            case GameInstance.GS_PLAYER1_TURN:
                            case GameInstance.GS_PLAYER2_TURN:
                            case GameInstance.GS_PLAYER1_WON:
                            case GameInstance.GS_PLAYER2_WON:
                            case GameInstance.GS_PLAYER1_RESIGNED:
                            case GameInstance.GS_PLAYER2_RESIGNED:

                                goToShootActivity(g, imPlayerA, position);
                                break;
                        }
                    }
                });
            }
        }
    }

    private void goToShootActivity(GameInstance g, boolean amItheOriginalInviter, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("currentUser", currentUsr);
        bundle.putString("currentOpponent", (amItheOriginalInviter ? g.getPlayerB() : g.getPlayerA()));
        bundle.putLong("shipboardUser1", g.getShipBoardUser1());
        bundle.putLong("shipboardUser2", g.getShipBoardUser2());
        bundle.putInt("shipdirectionsUser1", g.getShipDirectionsUser1());
        bundle.putInt("shipdirectionsUser2", g.getShipDirectionsUser2());
        bundle.putString("originalInviter", g.getPlayerA());
        bundle.putInt("state", g.getGameState());
        bundle.putLong("hits1user1", g.getHits1User1());
        bundle.putLong("hits1user2", g.getHits1User2());
        bundle.putLong("hits2user1", g.getHits2User1());
        bundle.putLong("hits2user2", g.getHits2User2());
        bundle.putLong("gameID", g.getGameID());

        Intent goShoot = new Intent(JGamePicker.this, ShootActivity.class);
        goShoot.putExtras(bundle);
        startActivityForResult(goShoot, (100 + position));
    }

    public void receivePlayerRatingAndCountryResult(String resultString, final String opponentName, final String me) {
        if (dialog3 != null) {
            dialog3.cancel();
        }
        String[] resultArr = null;
        int resultCode = 5;
        int userRating = 0;
        String countryCode = null;
        try {
            resultArr = resultString.split("&");
            resultCode = Integer.parseInt(resultArr[0]);
            if (resultCode == JConstants.RESULT_OK) {
                userRating = Integer.parseInt(resultArr[1]);
                countryCode = resultArr[2];
            }

            LayoutInflater li = LayoutInflater.from(context);
            View acceptView = li.inflate(R.layout.accept_invitation_dialog, null);

            final TextView tvInviteText = (TextView) acceptView.findViewById(R.id.tvInviteText);
            final ImageView ivFlagView = (ImageView) acceptView.findViewById(R.id.flagView);

            LinearLayout layout_root = (LinearLayout) acceptView.findViewById(R.id.layout_root);
            switch (themeID) {
                case 0:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_half);
                    break;
                case 1:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_half_green);
                    break;
                case 2:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_half_white);
                    break;
                case 3:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_half_orange);
                    break;
                case 4:
                    layout_root.setBackgroundResource(R.drawable.chatbackground_half);
                    break;
            }

            String message = String.format(getString(R.string.ffnoti_openinvitation_with_rating), opponentName, userRating);
            tvInviteText.setText(message);
            ivFlagView.setImageResource(JConstants.getCountryFlagResourceID(countryCode));

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(JGamePicker.this);
            alertBuilder.setView(acceptView);


            alertBuilder.setPositiveButton(getString(R.string.btn_accept), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    jgd.sendGameAccepted(JGamePicker.this, opponentName, me);
                }
            });

            alertBuilder.setNegativeButton(getString(R.string.btn_decline), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    jgd.sendGameDeclined(JGamePicker.this, opponentName, me);
                }
            });

            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
            setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        showMenuMenu();
        return false;
    }

    private void showMenuMenu() {
        final Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
        LayoutInflater li = LayoutInflater.from(context);
        final View menuView = li.inflate(R.layout.menu_menu, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(menuView);

        //button style nog

        Button logoff = (Button) menuView.findViewById(R.id.button_logoff);
        Button changepw = (Button) menuView.findViewById(R.id.button_change_pw);
        Button info = (Button) menuView.findViewById(R.id.button_info);
        Button about = (Button) menuView.findViewById(R.id.button_about);
        Button buyPremium = (Button) menuView.findViewById(R.id.button_buy_premium);

        logoff.setTypeface(army);
        info.setTypeface(army);
        changepw.setTypeface(army);
        about.setTypeface(army);
        buyPremium.setTypeface(army);
        logoff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(JGamePicker.this, getString(R.string.toast_loggingoff), Toast.LENGTH_SHORT).show();
                sharedPrefs = JGamePicker.this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.commit();
                Intent goToTheStart = new Intent(JGamePicker.this, LoginOrSignup.class);
                startActivity(goToTheStart);
                finish(); //klaar hier
            }
        });
        changepw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goToChangePassword = new Intent(JGamePicker.this, ResetPassword.class);
                startActivity(goToChangePassword);
                menumenuDialog.cancel();
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goToGameRules = new Intent(JGamePicker.this, GameRules.class);
                startActivity(goToGameRules);
                menumenuDialog.cancel();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goToAbout = new Intent(JGamePicker.this, AboutActivity.class);
                startActivity(goToAbout);
                menumenuDialog.cancel();
            }
        });
        buyPremium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buyPremiumVersion(v);
            }
        });

        menumenuDialog = alertDialogBuilder.create();
        menumenuDialog.show();
        switch (styleID) {
            case 0:
                logoff.setBackgroundResource(R.drawable.menu_button);
                changepw.setBackgroundResource(R.drawable.menu_button);
                info.setBackgroundResource(R.drawable.menu_button);
                about.setBackgroundResource(R.drawable.menu_button);
                buyPremium.setBackgroundResource(R.drawable.menu_button);
                logoff.setTextColor(getResources().getColor(R.color.background_blue));
                changepw.setTextColor(getResources().getColor(R.color.background_blue));
                info.setTextColor(getResources().getColor(R.color.background_blue));
                about.setTextColor(getResources().getColor(R.color.background_blue));
                buyPremium.setTextColor(getResources().getColor(R.color.background_blue));
                break;
            case 1:
                logoff.setBackgroundResource(R.drawable.menu_button_black);
                changepw.setBackgroundResource(R.drawable.menu_button_black);
                info.setBackgroundResource(R.drawable.menu_button_black);
                about.setBackgroundResource(R.drawable.menu_button_black);
                buyPremium.setBackgroundResource(R.drawable.menu_button_black);
                logoff.setTextColor(getResources().getColor(R.color.white));
                changepw.setTextColor(getResources().getColor(R.color.white));
                info.setTextColor(getResources().getColor(R.color.white));
                about.setTextColor(getResources().getColor(R.color.white));
                buyPremium.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                logoff.setBackgroundResource(R.drawable.menu_button_white);
                changepw.setBackgroundResource(R.drawable.menu_button_white);
                info.setBackgroundResource(R.drawable.menu_button_white);
                about.setBackgroundResource(R.drawable.menu_button_white);
                buyPremium.setBackgroundResource(R.drawable.menu_button_white);
                logoff.setTextColor(getResources().getColor(R.color.background_blue));
                changepw.setTextColor(getResources().getColor(R.color.background_blue));
                info.setTextColor(getResources().getColor(R.color.background_blue));
                about.setTextColor(getResources().getColor(R.color.background_blue));
                buyPremium.setTextColor(getResources().getColor(R.color.background_blue));
                break;
            case 3:
                logoff.setBackgroundResource(R.drawable.menu_button_brush);
                changepw.setBackgroundResource(R.drawable.menu_button_brush);
                info.setBackgroundResource(R.drawable.menu_button_brush);
                about.setBackgroundResource(R.drawable.menu_button_brush);
                buyPremium.setBackgroundResource(R.drawable.menu_button_brush);
                logoff.setTextColor(getResources().getColor(R.color.background_blue));
                changepw.setTextColor(getResources().getColor(R.color.background_blue));
                info.setTextColor(getResources().getColor(R.color.background_blue));
                about.setTextColor(getResources().getColor(R.color.background_blue));
                buyPremium.setTextColor(getResources().getColor(R.color.background_blue));
                break;
        }

        if (bPaidVersion)
            buyPremium.setVisibility(View.GONE);
    }

    private void setButtonRefreshOrRefreshing() {
        switch (styleID) {
            case 0:
                ibtnRefresh.setImageResource(bRequestingGamesList ? R.drawable.refreshing : R.drawable.refresh);
                break;
            case 1:
                ibtnRefresh.setImageResource(bRequestingGamesList ? R.drawable.refreshing_black : R.drawable.refresh_black);
                break;
            case 2:
                ibtnRefresh.setImageResource(bRequestingGamesList ? R.drawable.refreshing_white : R.drawable.refresh_white);
                break;
            case 3:
                ibtnRefresh.setImageResource(bRequestingGamesList ? R.drawable.refreshing_brush : R.drawable.refresh_brush);
                break;
        }
    }

    public void setCorrectBackGroundDrawableForButtons(AlertDialog aD, boolean hasPositive, boolean hasNegative, boolean hasNeutral) {
        Button bn = aD.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button bp = aD.getButton(DialogInterface.BUTTON_POSITIVE);
        Button b = aD.getButton(DialogInterface.BUTTON_NEUTRAL);
        switch (styleID) {
            case 0:
                if (hasPositive && bn != null) {
                    bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button));
                    bn.setTextColor(getResources().getColor(R.color.background_blue));
                }
                if (hasNegative && bp != null) {
                    bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button));
                    bp.setTextColor(getResources().getColor(R.color.background_blue));
                }
                if (hasNeutral && b != null) {
                    b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button));
                    b.setTextColor(getResources().getColor(R.color.background_blue));
                }
                break;
            case 1:
                if (hasPositive && bn != null) {
                    bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_black));
                    bn.setTextColor(getResources().getColor(R.color.white));
                }
                if (hasNegative && bp != null) {
                    bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_black));
                    bp.setTextColor(getResources().getColor(R.color.white));
                }
                if (hasNeutral && b != null) {
                    b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_black));
                    b.setTextColor(getResources().getColor(R.color.white));
                }
                break;
            case 2:
                if (hasPositive && bn != null) {
                    bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_white));
                    bn.setTextColor(getResources().getColor(R.color.background_blue));
                }
                if (hasNegative && bp != null) {
                    bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_white));
                    bp.setTextColor(getResources().getColor(R.color.background_blue));
                }
                if (hasNeutral && b != null) {
                    b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_white));
                    b.setTextColor(getResources().getColor(R.color.background_blue));
                }
                break;
            case 3:
                if (hasPositive && bn != null) {
                    bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_brush));
                    bn.setTextColor(getResources().getColor(R.color.background_blue));
                }
                if (hasNegative && bp != null) {
                    bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_brush));
                    bp.setTextColor(getResources().getColor(R.color.background_blue));
                }
                if (hasNeutral && b != null) {
                    b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_brush));
                    b.setTextColor(getResources().getColor(R.color.background_blue));
                }
                break;

        }

    }

    public void buyPremiumVersion(View view) {
        mHelper.launchPurchaseFlow(this, JConstants.SKU_PREMIUM_APP, 10001, mPremiumAppBoughtListener, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (menumenuDialog != null) {
            menumenuDialog.cancel();
        }
    }

    protected void showBuyAppWindow() {
        LayoutInflater li = LayoutInflater.from(context);
        final View buyMeView = li.inflate(R.layout.buyme, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(buyMeView);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(getString(R.string.btn_buy_premium_version),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                buyPremiumVersion(buyMeView);
                                dialog.cancel();

                            }
                        })
                .setNegativeButton(getString(R.string.btn_not_now),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);

   }


    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment()
        { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.gamepick, container, false);
            return rootView;
        }
    }

    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        /**
         * Called when leaving the activity
         */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /**
         * Called when returning to the activity
         */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /**
         * Called before the activity is destroyed
         */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }

    public static class EmptyFragment extends Fragment {

        //private AdView mAdView;

        public EmptyFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            //mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            //AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            //mAdView.loadAd(adRequest);
        }
    }
}
