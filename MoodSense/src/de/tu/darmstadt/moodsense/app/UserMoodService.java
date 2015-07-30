/**
 * 
 */
package de.tu.darmstadt.moodsense.app;

import java.lang.Thread.State;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.services.WorldMoodService;
import de.tu.darmstadt.moodsense.util.TwitterUtils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * @author Dinesh Vaithyalingam Gangatharan
 * Added for V1.1
 * Code style based on : https://newcircle.com/s/post/1049/tutorial_services_part_1_android_bootcamp_series_2012
 *
 */
public class UserMoodService extends Service{

	static final String TAG = "UserMoodService";
	public static boolean userMoodSet = false;
	//declarations for twitter
	private SharedPreferences prefs;
	SharedPreferences userPref;
	String userTwitterMood = "";
	String worldTwitterMood = "";
	String screenName, userName;
	int m_counter;
	long shortMinutes;
	boolean m_enterMood;
	int m_myMood;
	int m_moodIntensity;
	MqttClient client = null;
	Calendar cal = Calendar.getInstance();	
	
	public void reset() {
		
		m_myMood = Constants.NUM_MOOD_TYPES;
		m_moodIntensity = Constants.MILD;
		m_enterMood = false;

		m_counter = 0;

	}
	@Override
	public IBinder onBind(Intent arg0) {	
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onTaskRemoved(android.content.Intent)
	 */
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub
		Intent restartService = new Intent(getApplicationContext(),this.getClass());
		restartService.setPackage(getPackageName());
		PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(),
				1, restartService, PendingIntent.FLAG_ONE_SHOT);
		
		 AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
	}

	/** (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.d(TAG, "OnCreation");
		super.onCreate();
	}
	

	/** (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		Log.d(TAG, "OnStartCommand");
		try {
			ConnectivityManager cm =
			        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {	
				Log.d(TAG,"Twitter loop enter");
				//Check the user's mood on twitter
				computeMoodOnTwitter();
				if(userMoodSet) {
					Log.d(TAG, "user's twitter mood" + userTwitterMood);
				} else {
					Log.d(TAG, "user mood not set, world mood computation started");
					//If user's mood is not set then check for world's mood
				}
					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return START_STICKY;
	}

	private void computeMoodOnTwitter() {
		// TODO Auto-generated method stub
		reset();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Thread twitterThread;
		twitterThread = new Thread() {
			public void run() {
				try {						
				  List<Status> statuses = TwitterUtils.getHomeTimeline(prefs);
					 for(int i=0; i < Constants.NUM_MOOD_TYPES; i++) {					
						for (int j =0 ; j < Constants.NUM_MOOD_TYPES; j++)
						{			
							for (twitter4j.Status status : statuses) {
							if(status.getText().contains(Constants.searchStrings[i][j])) {
								Date date = status.getCreatedAt();
								long Minutes = getMinuteDifference(cal.getTime(), date);
							    if(Constants.sdf.format(date).equals(Constants.sdf.format(cal.getTime()))) {
								  //Increment counter for each tweet
								  m_counter++;
								  //track time for the first tweet
								  if(m_counter == 1) {
									shortMinutes = Minutes;
									m_moodIntensity = computeMoodIntensity(i,j);
									m_myMood = i;
								    Log.d(TAG, "intensity + mood" + m_moodIntensity +","+ m_myMood);
								    Log.d(TAG,"SocialMood:: mymood- " + Constants.moodIntensityNames[m_moodIntensity]+
								    		   " "+ Constants.moodNames[m_myMood]);								       
								    Log.d(TAG, "SocialMood:: status-"+status.getText());								    	
								    		   
								   }
								   else //counter more than 1
									//track time for the later tweets
								   {  //take latest tweet only if logged minutes is shorter than earlier minutes
									   if(Minutes < shortMinutes) {
									 	  shortMinutes = Minutes;
									   	  Log.d(TAG, "Called compute mood_intensity :: "+ m_counter);
									   	  m_moodIntensity = computeMoodIntensity(i,j);
									   	  m_myMood = i;
										}
									       
								   }
									userMoodSet = true;	
								  }
								}
							  }
							}
					 	 }					  					 					 					 
					} catch(TwitterException te) {
					  userMoodSet = false;								  	
					  	Log.d(TAG, "Unable to process twitter get requests "+te.getErrorCode()+ " "+ te.getErrorMessage());
								  	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d(TAG,"Error msg");
						e.printStackTrace();
					}
				
				try {
					stopThread(this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		twitterThread.start();	
		
	}
	
	public int computeMoodIntensity(int m_detect, int m_type) {
		// TODO Auto-generated method stub
		for(int j=0; j < Constants.m_extreme.length; j++) {
			if(m_type == Constants.m_extreme[m_detect][j])
				return Constants.EXTREME;
		}
		for(int j=0; j < Constants.m_considerable.length; j++) {
			if(m_type == Constants.m_considerable[m_detect][j])
				return Constants.CONSIDERABLE;
		}
				
		return Constants.MILD;
	
	}
	
	/**
	 * Compute minutes between the first and the last date	
	 * @param afterDate
	 * @param beforeDate
	 * @return minutes
	 */
	public  long getMinuteDifference(Date afterDate, Date beforeDate){
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        now.setTime(afterDate);
        then.setTime(beforeDate);

        
        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();
        
        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;
        
        // Calculate difference in seconds
        long diffMinutes = diff / (60 * 1000);
        return diffMinutes;
	}
	
	private String userStatusToMood(int myMood) {
		// TODO Auto-generated method stub
		String userMood = Constants.userNoTwitter;
		 if(m_myMood >= Constants.NUM_MOOD_TYPES) {
			 m_enterMood = true;				
        	 Log.d(TAG, userMood);
        	 //TODO show notification!! and then user will be sent to our default UserMood activity!
        	 Intent i = new Intent(UserMoodService.this,UserMood.class);
			 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(i);
		 }
		 else {
			 userMood = "User mood is "+ Constants.moodNames[m_myMood];
			 userTwitterMood = Constants.moodIntensityNames[m_moodIntensity]
					 +" "+Constants.moodNames[m_myMood];
			 
			 Log.d(TAG, "Updated user mood is "+userTwitterMood);	
			 
			 //MQTT commented
			 /*setupMqttClient();
	      	     
		     MqttMessage message = new MqttMessage();
		     message.setPayload(userTwitterMood.getBytes());
		     Log.d("Send Message", userTwitterMood);
		     try {
		          client.publish("/home/colors", message);
		     } catch (MqttPersistenceException e) {
		         // TODO Auto-generated catch block
		          e.printStackTrace();
		     } catch (MqttException e) {
		         // TODO Auto-generated catch block
		          e.printStackTrace();
		     }*/
		 }
		 return userMood;
	}

	private void setupMqttClient() {
		// TODO Auto-generated method stub
		MemoryPersistence persistence = new MemoryPersistence();
		try {
		   client = new MqttClient("tcp://192.168.0.103:1883", "192.168.0.102", persistence);
		   client.connect();
		} catch (MqttException e1) {
		    e1.printStackTrace();
		}
		
	}
	private void stopThread(Thread theThread) throws Exception {
		// method to stop the worker thread once the process needed to do has been completed
		if (theThread != null)
	    {
			theThread = null;
			Log.d(TAG, "Execution complete inside stop thread");
			userStatusToMood(m_myMood);	        
	    }
		
		if(!userMoodSet) {
			
			//Call world Service
			WorldMoodService worldService = new WorldMoodService();
			worldService.computeWorldMood();
			
		}
	}
	/** (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {	
		Log.d(TAG, "OnDeletion");
		super.onDestroy();
	}	
}