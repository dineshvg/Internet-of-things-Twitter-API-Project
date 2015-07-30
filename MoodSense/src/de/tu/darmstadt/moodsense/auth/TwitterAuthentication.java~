package de.tu.darmstadt.moodsense.auth;

import java.util.ArrayList;
import java.util.List;

import de.tu.darmstadt.moodsense.R;
import twitter4j.Status;
import twitter4j.ResponseList;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.tu.darmstadt.moodsense.R.id;
import de.tu.darmstadt.moodsense.R.layout;
import de.tu.darmstadt.moodsense.app.AboutMoodSense;
import de.tu.darmstadt.moodsense.app.UserSocialMood;
import de.tu.darmstadt.moodsense.app.UserMood;
import de.tu.darmstadt.moodsense.store.SharedPreferencesCredentialStore;

/**
 * This is the main activity class which further launch OAuth authentication for Twitter
 * or launches the UserMood Activity if the login is already saved.
 * @author manishaluthra
 * PS: Part of this code has been taken from https://github.com/ddewaele/
 */
public class TwitterAuthentication extends Activity {

	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
			
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Button launchOauth = (Button) findViewById(R.id.authenticate);
		
		//check for saved log in details..
		checkForSavedLogin();
		
		launchOauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				//check for Internet connection.
				ConnectivityManager cm =
				        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
					Intent i = new Intent();
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//OAuth authentication start.
					startActivity(i.setClass(v.getContext(),OAuthAccessTokenActivity.class));
				
			}
				else Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
			}
		});

	}
	
	/**
	 * Check for saved login details and direct launches SocialMood activity.
	 * @param none
	 */
	private void checkForSavedLogin() {
		// Get Access Token and persist it
		ConnectivityManager cm =
		        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		twitter4j.auth.AccessToken accessToken = getAccessToken();
		if (accessToken==null) return;	//if there are no credentials stored then return to usual activity

			startFirstActivity();
			finish();
		}
		else Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
		
	}
	
	/**
	 * Gets access token to ensure relogin
	 * @param none
	 * @return AccessToken
	 */
	private twitter4j.auth.AccessToken getAccessToken() {
		String token[] =  new SharedPreferencesCredentialStore(prefs).read();
		if (!token[0].equals(null)&& !token[1].equals(null) && !"".equals(token[1]) && !"".equals(token[0])){
			System.out.println("yes");
			return new twitter4j.auth.AccessToken(token[0], token[1]);
		}
		return null;
	}
	
    /**
     * Starts activity SocialMood if login is already saved.
     */
	public void startFirstActivity() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent i= new Intent(this,UserSocialMood.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivityForResult(i, 0);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.tweetsmenu, menu);
	     MenuItem m = menu.getItem(0);
	    
	     m.setVisible(false);
	     
		return super.onCreateOptionsMenu(menu);
		
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		
			
		case R.id.about:
			System.out.println("About called");
				
			Intent intent = new Intent(this, AboutMoodSense.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivityForResult(intent, 0);
			
		default:
				return super.onOptionsItemSelected(item);
		
			}
		}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		this.finish();
		super.onBackPressed();
	}
}
