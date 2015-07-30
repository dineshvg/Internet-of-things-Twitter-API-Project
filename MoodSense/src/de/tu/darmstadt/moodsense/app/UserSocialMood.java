package de.tu.darmstadt.moodsense.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import de.tu.darmstadt.moodsense.R;
import de.tu.darmstadt.moodsense.auth.OAuthAccessTokenActivity;
import de.tu.darmstadt.moodsense.auth.TwitterAuthentication;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.store.SharedPreferencesCredentialStore;
import de.tu.darmstadt.moodsense.util.TwitterUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Screen to just start the service needs to be removed
 * @author manishaluthra247
 *
 */
public class UserSocialMood extends Activity{
	Button twitterMood, enterMood;
	public SharedPreferences prefs;
	ProgressDialog progress;
	AlertDialog.Builder enterMoodAlert;
	String screenName, userName;
	int m_counter;
	long shortMinutes;
	boolean m_enterMood;
	int m_myMood;
	int m_moodIntensity;
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	AlertDialog.Builder alertDialog;
	String TAG = "UserSocialMood";
	/**
	 * V 1.1 @author : DVG
	 * Timer included for service purposes
	 */
	
	Timer timer =new Timer();
	
	
	public UserSocialMood() {
		// TODO Auto-generated constructor stub
		m_myMood = Constants.NUM_MOOD_TYPES;
		m_moodIntensity = Constants.MILD;
		m_enterMood = false;

		m_counter = 0;
		
	}
	/**
	 * V1.1
	 * @author DVG
	 * method used to start a service based on a particular SSID of Wifi service
	 * Code based on the code implemented in 
	 * //http://www.brighthub.com/mobile/google-android/articles/34861.aspx
	 */
	
	private void userServiceConnect(String sSID) {
		// TODO Auto-generated method stub
		final Intent intent = new Intent(this, UserMoodService.class);
		if(sSID.equals(Constants.SSID)) {
			//commented for testing - will be used with real application
			/*long intervals = 10000;	//in milliseconds		
			timer.scheduleAtFixedRate(new TimerTask() {				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startService(intent);
				}
			}, 100, intervals );*/
			
		} else {
			stopService(intent);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socialmood);
		progress = new ProgressDialog(this);
		enterMoodAlert = new AlertDialog.Builder(this);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		enterMood =  (Button)findViewById(R.id.EnterMood);
		alertDialog = new AlertDialog.Builder(this);
		/**
		 * V 1.1 
		 * @author DVG
		 * code snippet used to connect and check if the wifi service is enabled 
		 * and obtain the SSID
		 * Code used from http://stackoverflow.com/questions/7599569/
		 * how-to-get-my-wifi-hotspot-ssid-in-my-current-android-system
		 */		
		WifiManager	wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		 if(wifiMgr.isWifiEnabled()) {
			Constants.SSID =  wifiInfo.getSSID();
			userServiceConnect(Constants.SSID);
		 } 
		 
//		Code moved to act as service
//		twitterMood.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				
//				//check for Internet connection
//				ConnectivityManager cm =
//				        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//				NetworkInfo netInfo = cm.getActiveNetworkInfo();
//				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//					new MyTwitterCompute().execute();
//				}
//			}
//		});
		enterMood.setOnClickListener(new View.OnClickListener() {
	
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserSocialMood.this,UserMood.class);
				i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 if ((progress != null) && progress.isShowing())
			  progress.dismiss();
	}
	
	/**
	 * Service call
	 * Menu selection created for service on and off
	 * Code used for testing service manually
	 */
	
	/** method included by Dinesh Vaithyalingam Gangatharan
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.usrmenu, menu);
		getMenuInflater().inflate(R.menu.tweetsmenu, menu);
		return true;
	}
	
	/** method included by Dinesh Vaithyalingam Gangatharan
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent = new Intent(this, UserMoodService.class);
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		
		case R.id.menu_start_service :			
			startService(intent);			
			return true;
		
		case R.id.menu_stop_service :
			stopService(intent);
			return true;
		case R.id.logout:
		{
			//System.out.println("Logout called");
			ConnectivityManager cm =
	        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			

		    // Setting Dialog Title
		    alertDialog.setTitle("Logout");
		    alertDialog.setCancelable(false);
		    // Setting Dialog Message
		    alertDialog.setMessage("Do you want to sign off?");
		 // create alert dialog
			AlertDialog alertDialog1 = alertDialog.create();
		    // Setting OK Button
			alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            // Write your code here to execute after dialog closed
		            	new SharedPreferencesCredentialStore(prefs).clearCredentials();
		            	Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
		            	UserSocialMood.this.finish();
		            	Intent i= new Intent(UserSocialMood.this,TwitterAuthentication.class);
		            	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            	startActivity(i);
		            }
		    });
		    
		    // Setting Cancel Button
			alertDialog1.setButton2("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	Toast.makeText(getApplicationContext(), "Logout cancelled", Toast.LENGTH_SHORT).show();
		            }
		    });
		
		    // Showing Alert Message
			alertDialog1.show();
			
			return true;
			}
			else 
				Toast.makeText(getApplicationContext(), "Please connect to Internet!!", Toast.LENGTH_LONG).show();
		} break;
		
		case R.id.about: 
			Log.d(TAG,"About called");
			
			Intent i = new Intent(this, AboutMoodSense.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivityForResult(i, 0);
			return true;
					
		case android.R.id.home:
			this.finish();
			return true;
		default:
				return super.onOptionsItemSelected(item);

	}
		return false;
	}

}
