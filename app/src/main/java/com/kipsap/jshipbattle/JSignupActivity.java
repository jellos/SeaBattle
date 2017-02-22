package com.kipsap.jshipbattle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.kipsap.commonsource.JConstants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class JSignupActivity extends Activity {	
	
	EditText usr, pw, pw2, email;
	private TextView output, textView1, choosePassword, repeatPassword, mail, land;
	private Button btnSignUp;
	private Boolean successfulSignUp;
	private String countryCode;
	private Spinner cSpin;
	SharedPreferences sharedPrefs;
	int styleID;
	private LinearLayout rootView;
    boolean bPaidVersion;
	String theUser, thePassword, theEmail;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
		
		int verNumber = -1;
		PackageInfo pinfo;
		try 
		{
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			verNumber = pinfo.versionCode;			
		} 
		catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		final int versionNumber = verNumber;		
		
		countryCode = "UNKNOWN";
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
            styleID = 0;
		
		rootView = (LinearLayout) findViewById(R.id.RootView);
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		switch (styleID)
		{
			case 0:
				btnSignUp.setBackgroundResource(R.drawable.menu_button);
				btnSignUp.setTextColor(getResources().getColor(R.color.background_blue));
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
				break;
			case 1:
				btnSignUp.setBackgroundResource(R.drawable.menu_button_black);				
				btnSignUp.setTextColor(getResources().getColor(R.color.white));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
			case 2:
				btnSignUp.setBackgroundResource(R.drawable.menu_button_white);				
				btnSignUp.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
				break;	
			case 3:
				btnSignUp.setBackgroundResource(R.drawable.menu_button_brush);				
				btnSignUp.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
				break;	
		}
		
		usr = (EditText) findViewById(R.id.EditText01);
		pw = (EditText) findViewById(R.id.editChoosePW);
		pw2 = (EditText) findViewById(R.id.editRepeatPW);
		email = (EditText) findViewById(R.id.editEmail);
		output = (TextView) findViewById(R.id.TextView01);
		
		cSpin = (Spinner) findViewById(R.id.countrySpinner);
		
		textView1 = (TextView) findViewById(R.id.textView1);
		choosePassword = (TextView) findViewById(R.id.choosePassword);
		repeatPassword = (TextView) findViewById(R.id.repeatPassword);
		mail = (TextView) findViewById(R.id.txtEmail);
		land = (TextView) findViewById(R.id.yourCountry);
		
		textView1.setTypeface(army);
		choosePassword.setTypeface(army);
		repeatPassword.setTypeface(army);
		mail.setTypeface(army);
		land.setTypeface(army);
					
		btnSignUp.setEnabled(true);
		btnSignUp.setTypeface(army);
		btnSignUp.setOnClickListener(new View.OnClickListener() 
		{					
			public void onClick(View v) 
			{				
				
				theUser = usr.getText().toString();
				thePassword = pw.getText().toString();
				theEmail = email.getText().toString();
				
				countryCode = shortCodeFromCountry(cSpin.getSelectedItem().toString());
				int iitem = cSpin.getSelectedItemPosition();
				
				if (theUser.equals(""))
				{					
					display(getString(R.string.msg_usernamecannotbeempty));
				}
				else if ((pw2.getText().toString().equals("")) || (thePassword.equals("")))
				{					
					display(getString(R.string.msg_passwordcannotbeempty));
				}
				else if (theUser.length() > 32)
				{
					display(getString(R.string.msg_thirtytwocharacters));
				}
				else if (theUser.length() < 2)
				{
					display(getString(R.string.msg_twocharacters));
				}
				else if (!thePassword.equals(pw2.getText().toString()))
				{
					display(getString(R.string.msg_samepasswordtwice));
				}
				else if ((theUser.contains("&")) || (theUser.contains("#")) || (theUser.contains("'")))
				{
					display(getString(R.string.msg_nofunnystuff));
				}
				else if ((!theEmail.contains("@")) || (!theEmail.contains(".")))
				{
					display(getString(R.string.msg_notavalidemailaddress));
				}
				else if (iitem == 0)
				{
					display(getString(R.string.msg_selectcountry));
				}
				else
				{					
					new JGetDataFromWebService().sendSignUpRequest(JSignupActivity.this,
											theUser,
											md5(thePassword),
											theEmail,
											versionNumber,
											countryCode);
					btnSignUp.setEnabled(false); // disable the button until server reply					
				}
				
			}
		});
	}
	
	public void receiveSignUpResult(int returnCode)
	{
		successfulSignUp = false;
		btnSignUp.setEnabled(true);
		switch (returnCode)
		{
			case JConstants.RESULT_OK:
				successfulSignUp = true;
				display(getString(R.string.msg_signupsuccess));
				break;
			case JConstants.RESULT_USERNAME_ALREADY_EXISTS:
				display(getString(R.string.msg_usernametaken));
				break;
			case JConstants.RESULT_INVALID_REQUEST:
				display(getString(R.string.msg_invalidrequest));
				break;	
			case JConstants.RESULT_NO_RESPONSE:
				display(getString(R.string.msg_nocommunication));
				break;
			case JConstants.RESULT_INCORRECT_VERSION:
				display(getString(R.string.msg_incorrectversion));
				break;
			case JConstants.RESULT_GENERAL_ERROR:
				display(getString(R.string.msg_generalerror));
				break;
		}
		
		if (successfulSignUp)
		{
			sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPrefs.edit();
	    	editor.putString("username", theUser);
	    	editor.putString("password", md5(thePassword));
	    	editor.putString("countryCode", countryCode);
	    	editor.commit();
			
			Bundle bundle = new Bundle();
			bundle.putString("currentUser", usr.getText().toString());
			bundle.putBoolean("somethingChanged", true); //to force a screen refresh
			Intent goLogIn = new Intent(JSignupActivity.this, JGamePicker.class);
			goLogIn.putExtras(bundle);
			startActivityForResult(goLogIn, 0);
		}
	}
	
	private void display(String text) 
	{
		output.setText(text); // + "\n" + output.getText().toString());
	}
	
	private static String md5(String s) 
	{
	    MessageDigest digest;
	    try 
	    {
	        digest = MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes(),0,s.length());
	        String hash = new BigInteger(1, digest.digest()).toString(16);
	        return hash;
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
	}	
		
	
	private String shortCodeFromCountry(String countryLong)
	{
		if (countryLong.equals("Afghanistan"))
			return "AF";
		if (countryLong.equals("Albania"))
			return "AL";
		if (countryLong.equals("Algeria"))
			return "DZ";
		if (countryLong.equals("American Samoa"))
			return "AS";
		if (countryLong.equals("Andorra"))
			return "AD";
		if (countryLong.equals("Angola"))
			return "AO";
		if (countryLong.equals("Anguilla"))
			return "AI";
		if (countryLong.equals("Antarctica"))
			return "AQ";
		if (countryLong.equals("Antigua and Barbuda"))
			return "AG";
		if (countryLong.equals("Argentina"))
			return "AR";
		if (countryLong.equals("Armenia"))
			return "AM";
		if (countryLong.equals("Aruba"))
			return "AW";
		if (countryLong.equals("Australia"))
			return "AU";
		if (countryLong.equals("Austria"))
			return "AT";
		if (countryLong.equals("Azerbaijan"))
			return "AZ";
		if (countryLong.equals("Bahrain"))
			return "BH";
		if (countryLong.equals("Bangladesh"))
			return "BD";
		if (countryLong.equals("Barbados"))
			return "BB";
		if (countryLong.equals("Belarus"))
			return "BY";		
		if (countryLong.equals("Belgium"))
			return "BE";
		if (countryLong.equals("Belize"))
			return "BZ";
		if (countryLong.equals("Benin"))
			return "BJ";
		if (countryLong.equals("Bermuda"))
			return "BM";
		if (countryLong.equals("Bhutan"))
			return "BT";
		if (countryLong.equals("Bolivia"))
			return "BO";
		if (countryLong.equals("Bosnia and Herzegovina"))
			return "BA";
		if (countryLong.equals("Botswana"))
			return "BW";
		if (countryLong.equals("Bouvet Island"))
			return "BV";
		if (countryLong.equals("Brazil"))
			return "BR";
		if (countryLong.equals("British Indian Ocean Territory"))
			return "IO";
		if (countryLong.equals("British Virgin Islands"))
			return "VG";
		if (countryLong.equals("Brunei"))
			return "BN";
		if (countryLong.equals("Bulgaria"))
			return "BG";
		if (countryLong.equals("Burkina Faso"))
			return "BF";
		if (countryLong.equals("Burundi"))
			return "BI";
		if (countryLong.equals("Cambodia"))
			return "KH";
		if (countryLong.equals("Cameroon"))
			return "CM";
		if (countryLong.equals("Canada"))
			return "CA";
		if (countryLong.equals("Cape Verde"))
			return "CV";
		if (countryLong.equals("Cayman Islands"))
			return "KY";
		if (countryLong.equals("Central African Republic"))
			return "CF";
		if (countryLong.equals("Chad"))
			return "TD";
		if (countryLong.equals("Chile"))
			return "CL";
		if (countryLong.equals("China"))
			return "CN";
		if (countryLong.equals("Christmas Island"))
			return "CX";
		if (countryLong.equals("Cocos (Keeling) Islands"))
			return "CC";
		if (countryLong.equals("Colombia"))
			return "CO";
		if (countryLong.equals("Comoros"))
			return "KM";
		if (countryLong.equals("Congo"))
			return "CG";
		if (countryLong.equals("Cook Islands"))
			return "CK";
		if (countryLong.equals("Costa Rica"))
			return "CR";
		if (countryLong.equals("Cote d\'Ivoire"))
			return "CI";
		if (countryLong.equals("Croatia"))
			return "HR";
		if (countryLong.equals("Cuba"))
			return "CU";
		if (countryLong.equals("Cyprus"))
			return "CY";
		if (countryLong.equals("Czech Republic"))
			return "CZ";
		if (countryLong.equals("Democratic Republic of the Congo"))
			return "CD";
		if (countryLong.equals("Denmark"))
			return "DK";
		if (countryLong.equals("Djibouti"))
			return "DJ";
		if (countryLong.equals("Dominica"))
			return "DM";
		if (countryLong.equals("Dominican Republic"))
			return "DO";
		if (countryLong.equals("East Timor"))
			return "TL"; // Timor-Leste --> used to be "TP"
		if (countryLong.equals("Ecuador"))
			return "EC";
		if (countryLong.equals("Egypt"))
			return "EG";
		if (countryLong.equals("El Salvador"))
			return "SV";
		if (countryLong.equals("Equatorial Guinea"))
			return "GQ";
		if (countryLong.equals("Eritrea"))
			return "ER";
		if (countryLong.equals("Estonia"))
			return "EE";
		if (countryLong.equals("Ethiopia"))
			return "ET";
		if (countryLong.equals("Faeroe Islands"))
			return "FO";
		if (countryLong.equals("Falkland Islands"))
			return "FK";
		if (countryLong.equals("Fiji"))
			return "FJ";
		if (countryLong.equals("Finland"))
			return "FI";
		if (countryLong.equals("Former Yugoslav Republic of Macedonia"))
			return "MK";				
		if (countryLong.equals("France"))
			return "FR";
		if (countryLong.equals("French Guiana"))
			return "GF";
		if (countryLong.equals("French Polynesia"))
			return "PF";
		if (countryLong.equals("French Southern Territories"))
			return "TF";
		if (countryLong.equals("Gabon"))
			return "GA";
		if (countryLong.equals("Georgia"))
			return "GE";	
		if (countryLong.equals("Germany"))
			return "DE";
		if (countryLong.equals("Ghana"))
			return "GH";
		if (countryLong.equals("Gibraltar"))
			return "GI";
		if (countryLong.equals("Greece"))
			return "GR";
		if (countryLong.equals("Greenland"))
			return "GL";
		if (countryLong.equals("Grenada"))
			return "GD";
		if (countryLong.equals("Guadeloupe"))
			return "GP";
		if (countryLong.equals("Guam"))
			return "GU";
		if (countryLong.equals("Guatemala"))
			return "GT";
		if (countryLong.equals("Guinea"))
			return "GN";
		if (countryLong.equals("Guinea-Bissau"))
			return "GW";
		if (countryLong.equals("Guyana"))
			return "GY";
		if (countryLong.equals("Haiti"))
			return "HT";
		if (countryLong.equals("Heard Island and McDonald Islands"))
			return "HM";
		if (countryLong.equals("Honduras"))
			return "HN";
		if (countryLong.equals("Hong Kong"))
			return "HK";
		if (countryLong.equals("Hungary"))
			return "HU";
		if (countryLong.equals("Iceland"))
			return "IS";
		if (countryLong.equals("India"))
			return "IN";
		if (countryLong.equals("Indonesia"))
			return "ID";
		if (countryLong.equals("Iran"))
			return "IR";
		if (countryLong.equals("Iraq"))
			return "IQ";
		if (countryLong.equals("Ireland"))
			return "IE";
		if (countryLong.equals("Israel"))
			return "IL";
		if (countryLong.equals("Italy"))
			return "IT";
		if (countryLong.equals("Jamaica"))
			return "JM";		
		if (countryLong.equals("Japan"))
			return "JP";
		if (countryLong.equals("Jordan"))
			return "JO";
		if (countryLong.equals("Kazakhstan"))
			return "KZ";
		if (countryLong.equals("Kenya"))
			return "KE";
		if (countryLong.equals("Kiribati"))
			return "KI";
		if (countryLong.equals("Kuwait"))
			return "KW";
		if (countryLong.equals("Kyrgyzstan"))
			return "KG";
		if (countryLong.equals("Laos"))
			return "LA";
		if (countryLong.equals("Latvia"))
			return "LV";
		if (countryLong.equals("Lebanon"))
			return "LB";
		if (countryLong.equals("Lesotho"))
			return "LS";
		if (countryLong.equals("Liberia"))
			return "LR";
		if (countryLong.equals("Libya"))
			return "LY";
		if (countryLong.equals("Liechtenstein"))
			return "LI";
		if (countryLong.equals("Lithuania"))
			return "LT";
		if (countryLong.equals("Luxembourg"))
			return "LU";
		if (countryLong.equals("Macau"))
			return "MO";
		if (countryLong.equals("Madagascar"))
			return "MG";
		if (countryLong.equals("Malawi"))
			return "MW";
		if (countryLong.equals("Malaysia"))
			return "MY";
		if (countryLong.equals("Maldives"))
			return "MV";		
		if (countryLong.equals("Mali"))
			return "ML";
		if (countryLong.equals("Malta"))
			return "MT";
		if (countryLong.equals("Marshall Islands"))
			return "MH";
		if (countryLong.equals("Martinique"))
			return "MQ";
		if (countryLong.equals("Mauritania"))
			return "MR";
		if (countryLong.equals("Mauritius"))
			return "MU";
		if (countryLong.equals("Mayotte"))
			return "YT";
		if (countryLong.equals("Mexico"))
			return "MX";
		if (countryLong.equals("Micronesia"))
			return "FM";
		if (countryLong.equals("Moldova"))
			return "MD";
		if (countryLong.equals("Monaco"))
			return "MC";
		if (countryLong.equals("Mongolia"))
			return "MN";
		if (countryLong.equals("Montserrat"))
			return "MS";
		if (countryLong.equals("Morocco"))
			return "MA";
		if (countryLong.equals("Mozambique"))
			return "MZ";
		if (countryLong.equals("Myanmar"))
			return "MM";
		if (countryLong.equals("Namibia"))
			return "NA";
		if (countryLong.equals("Nauru"))
			return "NR";
		if (countryLong.equals("Nepal"))
			return "NP";
		if (countryLong.equals("Netherlands"))
			return "NL";
		if (countryLong.equals("Netherlands Antilles"))
			return "AN";
		if (countryLong.equals("New Caledonia"))
			return "NC";
		if (countryLong.equals("New Zealand"))
			return "NZ";
		if (countryLong.equals("Nicaragua"))
			return "NI";
		if (countryLong.equals("Niger"))
			return "NE";
		if (countryLong.equals("Nigeria"))
			return "NG";
		if (countryLong.equals("Niue"))
			return "NU";
		if (countryLong.equals("Norfolk Island"))
			return "NF";
		if (countryLong.equals("North Korea"))
			return "KP";
		if (countryLong.equals("Northern Marianas"))
			return "MP";
		if (countryLong.equals("Norway"))
			return "NO";
		if (countryLong.equals("Oman"))
			return "OM";
		if (countryLong.equals("Pakistan"))
			return "PK";
		if (countryLong.equals("Palau"))
			return "PW";
		if (countryLong.equals("Panama"))
			return "PA";
		if (countryLong.equals("Papua New Guinea"))
			return "PG";
		if (countryLong.equals("Paraguay"))
			return "PY";
		if (countryLong.equals("Peru"))
			return "PE";
		if (countryLong.equals("Philippines"))
			return "PH";
		if (countryLong.equals("Pitcairn Islands"))
			return "PN";
		if (countryLong.equals("Poland"))
			return "PL";
		if (countryLong.equals("Portugal"))
			return "PT";
		if (countryLong.equals("Puerto Rico"))
			return "PR";
		if (countryLong.equals("Qatar"))
			return "QA";
		if (countryLong.equals("Reunion"))
			return "RE";
		if (countryLong.equals("Romania"))
			return "RO";
		if (countryLong.equals("Russia"))
			return "RU";
		if (countryLong.equals("Rwanda"))
			return "RW";
		if (countryLong.equals("Sao Tome and Principe"))
			return "ST";
		if (countryLong.equals("Saint Helena"))
			return "SH";
		if (countryLong.equals("Saint Kitts and Nevis"))
			return "KN";
		if (countryLong.equals("Saint Lucia"))
			return "LC";
		if (countryLong.equals("Saint Pierre and Miquelon"))
			return "PM";
		if (countryLong.equals("Saint Vincent and the Grenadines"))
			return "VC";
		if (countryLong.equals("Samoa"))
			return "WS";
		if (countryLong.equals("San Marino"))
			return "SM";
		if (countryLong.equals("Saudi Arabia"))
			return "SA";
		if (countryLong.equals("Senegal"))
			return "SN";
		if (countryLong.equals("Seychelles"))
			return "SC";
		if (countryLong.equals("Sierra Leone"))
			return "SL";
		if (countryLong.equals("Singapore"))
			return "SG";
		if (countryLong.equals("Slovakia"))
			return "SK";
		if (countryLong.equals("Slovenia"))
			return "SI";
		if (countryLong.equals("Solomon Islands"))
			return "SB";
		if (countryLong.equals("Somalia"))
			return "SO";
		if (countryLong.equals("South Africa"))
			return "ZA";
		if (countryLong.equals("South Georgia and the South Sandwich Islands"))
			return "GS";
		if (countryLong.equals("South Korea"))
			return "KR";		
		if (countryLong.equals("South Sudan"))
			return "SS";
		if (countryLong.equals("Spain"))
			return "ES";
		if (countryLong.equals("Sri Lanka"))
			return "LK";
		if (countryLong.equals("Sudan"))
			return "SD";
		if (countryLong.equals("Suriname"))
			return "SR";
		if (countryLong.equals("Svalbard and Jan Mayen"))
			return "SJ";
		if (countryLong.equals("Swaziland"))
			return "SZ";
		if (countryLong.equals("Sweden"))
			return "SE";
		if (countryLong.equals("Switzerland"))
			return "CH";
		if (countryLong.equals("Syria"))
			return "SY";
		if (countryLong.equals("Taiwan"))
			return "TW";
		if (countryLong.equals("Tajikistan"))
			return "TJ";
		if (countryLong.equals("Tanzania"))
			return "TZ";
		if (countryLong.equals("Thailand"))
			return "TH";
		if (countryLong.equals("The Bahamas"))
			return "BS";
		if (countryLong.equals("The Gambia"))
			return "GM";
		if (countryLong.equals("Togo"))
			return "TG";
		if (countryLong.equals("Tokelau"))
			return "TK";
		if (countryLong.equals("Tonga"))
			return "TO";
		if (countryLong.equals("Trinidad and Tobago"))
			return "TT";
		if (countryLong.equals("Tunisia"))
			return "TN";
		if (countryLong.equals("Turkey"))
			return "TR";
		if (countryLong.equals("Turkmenistan"))
			return "TM";
		if (countryLong.equals("Turks and Caicos Islands"))
			return "TC";
		if (countryLong.equals("Tuvalu"))
			return "TV";
		if (countryLong.equals("Virgin Islands"))
			return "VG";
		if (countryLong.equals("Uganda"))
			return "UG";
		if (countryLong.equals("Ukraine"))
			return "UA";
		if (countryLong.equals("United Arab Emirates"))
			return "AE";		
		if (countryLong.equals("United Kingdom"))
			return "GB";
		if (countryLong.equals("United States"))
			return "US";
		if (countryLong.equals("United States Minor Outlying Islands"))
			return "UM";
		if (countryLong.equals("Uruguay"))
			return "UY";
		if (countryLong.equals("Uzbekistan"))
			return "UZ";
		if (countryLong.equals("Vanuatu"))
			return "VU";
		if (countryLong.equals("Vatican City"))
			return "VA";
		if (countryLong.equals("Venezuela"))
			return "VE";
		if (countryLong.equals("Vietnam"))
			return "VN";
		if (countryLong.equals("Wallis and Futuna"))
			return "WF";
		if (countryLong.equals("Western Sahara"))
			return "EH";
		if (countryLong.equals("Yemen"))
			return "YE";		
		if (countryLong.equals("Zambia"))
			return "ZM";
		if (countryLong.equals("Zimbabwe"))
			return "ZW";
		
		return "UNKNOWN";	
	}
	
	
}