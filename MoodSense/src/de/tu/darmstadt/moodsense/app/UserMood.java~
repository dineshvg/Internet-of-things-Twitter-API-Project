package de.tu.darmstadt.moodsense.app;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.tu.darmstadt.moodsense.auth.TwitterAuthentication;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.store.SharedPreferencesCredentialStore;
import de.tu.darmstadt.moodsense.util.TwitterUtils;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.tu.darmstadt.moodsense.R;

/**
 * This activity is responsible for major computation of worlds mood on twitter
 * @author manishaluthra247
 * 
 */
@SuppressLint("NewApi")
public class UserMood extends Activity {
	private SharedPreferences prefs;
	ProgressDialog progress;
	AlertDialog.Builder alertDialog;
	int m_counter =0;
	//private HomeTimelineAdapter adapter;
	RadioGroup radio;
	int id;
	String twitterMood, homeMood;
	RadioButton loved, joy, surprised, angry, envy, sad, frightened;
	Button proceed, enterNo;
	TextView welcomeText;
	MqttClient client = null;
	float m_worldTemperamentRatios[] = new float[7];
	float m_worldMoodCounts[]  = new float[7];
	float m_worldMoodRatios[]  = new float[7];
	int  m_worldMood;
	String TAG = "UserMoodActivity";
	/**
	 * Initialize variables in constructor
	 */
	public UserMood() {
		// TODO Auto-generated constructor stub
		
		 if (Constants.emotionSmoothingFactor < 0.0f || Constants.emotionSmoothingFactor > 1.0f)  {
			    System.out.println("invalid emotionSmoothingFactor");
			  }
			  if (Constants.moodSmoothingFactor < 0.0f ||  Constants.moodSmoothingFactor > 1.0f)  {
			    System.out.println("invalid moodSmoothingFactor");
			  }

			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    m_worldTemperamentRatios[i] = Constants.temperamentRatios[i];
			    m_worldMoodCounts[i] = Constants.INVALID_MOOD_VALUE;
			    m_worldMoodRatios[i] = Constants.INVALID_MOOD_VALUE;
			  }
			  
			   m_worldMood = Constants.NUM_MOOD_TYPES;

			  // debug code - check sum of m_worldTemperamentRatios is 1.
			  float sum = 0;
			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    sum += m_worldTemperamentRatios[i];
			  }
			  
			  if (sum > 1.0f + 1e-4f || sum < 1.0f - 1e-4f)  {
			    System.out.println("unexpected m_worldTemperamentRatios sum");
			  }
			  
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usermood);
		progress = new ProgressDialog(this);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		proceed =  (Button)findViewById(R.id.enter);
		enterNo =  (Button)findViewById(R.id.enterno);
		radio = (RadioGroup)findViewById(R.id.radiogp);
		
		radio.setSaveEnabled(true);
		alertDialog = new AlertDialog.Builder(this);
		proceed.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//check for Internet connection
				ConnectivityManager cm =
				        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
					int check_id = radio.getCheckedRadioButtonId();
					 SharedPreferences.Editor prefsEditor = prefs.edit();
					 prefsEditor.putInt("radio_check", check_id);
					 prefsEditor.commit();
					//compute mood if user enters his mood
					if(radio.getCheckedRadioButtonId()!=-1 && radio.getCheckedRadioButtonId()!= 0){
						//compute mood
					
						//						new MoodCompute().execute();
						setupMqttClient();
						twitterMood = checkMoodRadio();
						homeMood = Constants.moodIntensityNames[Constants.CONSIDERABLE] + " "+twitterMood;
						SendMessage(homeMood);
						new MoodToTwitter().execute();
						 
					}
					else 
						Toast.makeText(getApplicationContext(), "Please select how you are feeling!!", Toast.LENGTH_LONG).show();
				}
				else 
					Toast.makeText(getApplicationContext(), "Please connect to Internet!!", Toast.LENGTH_LONG).show();
			}

			
		});
		
		enterNo.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 setupMqttClient();
				 String userSelection = Constants.moodIntensityNames[Constants.CONSIDERABLE] + " " +checkMoodRadio();
				 SendMessage(userSelection);
			}
			
		});
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
	private String checkMoodRadio() {
		// TODO Auto-generated method stub
		int id= radio.getCheckedRadioButtonId();
		View radioButton = radio.findViewById(id);
		int radioId = radio.indexOfChild(radioButton);
		RadioButton btn = (RadioButton) radio.getChildAt(radioId);
		String selection = (String) btn.getText();
		
		return selection;
	}
    private void SendMessage(String userMood) {

		 MqttMessage message = new MqttMessage();
	     message.setPayload(userMood.getBytes());
	     Log.d("Send Message", userMood);
	     try {
	          client.publish("/home/colors", message);
	     } catch (MqttPersistenceException e) {
	         // TODO Auto-generated catch block
	          e.printStackTrace();
	     } catch (MqttException e) {
	         // TODO Auto-generated catch block
	          e.printStackTrace();
	     }
    }
		
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("Id checked");
		int id = prefs.getInt("radio_check", -1);
		radio.check(id);
		if(id == -1 || id == 0) {
			Toast.makeText(getApplicationContext(), "Please select how you are feeling!!", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Access created date of tweets to calculate minutes between these dates
	 * @author manishaluthra247
	 *
	 */
	public class twitterDate {
			public Date date;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(radio.getCheckedRadioButtonId()!=-1)
			id = radio.getCheckedRadioButtonId();
		 if ((progress != null) && progress.isShowing())
			  progress.dismiss();
	}
	
	public class MoodToTwitter extends AsyncTask<Uri, Void, Void> {

		@Override
		protected void onPreExecute() {
			
			 progress.setMessage("Processing");
		     progress=ProgressDialog.show(UserMood.this, "Processing", "Posting your mood on Twitter!");
			
		};
		
		@Override
		protected Void doInBackground(Uri... params) {
			// TODO Auto-generated method stub
			try {
				//Get twitter object for sending tweets
				TwitterUtils.sendTweet(prefs, "i'm feeling "+twitterMood);

			} catch (TwitterException ex) {
				ex.printStackTrace();
				Log.d(TAG, ex.getErrorMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			 if ((progress != null) && progress.isShowing())
				  progress.dismiss();
			 alertDialog.setTitle("Success");
			 alertDialog.setMessage("Post tweeted on twitter successfully");
			 alertDialog.show();
		}
		
	}
	
	/**
	 * Background asynchronous class to compute worlds mood 
	 * TODO use this in Service!!
	 * @author manishaluthra247
	 *
	 */
	
	public class WorldsMoodCompute extends AsyncTask<Uri, Void, Void> {
		twitterDate m_time1 = new twitterDate();
		twitterDate m_time2= new twitterDate();
		
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
			
			 progress.setMessage("Processing");
		     progress=ProgressDialog.show(UserMood.this, "Processing", "Computing current world's mood on Twitter! This process can take few seconds!");
			
		};
		
		@Override
		protected Void doInBackground(Uri...params) {
			
			try {
				//Get twitter object for searching tweets
				Twitter t = TwitterUtils.searchTweet(prefs);
				
				for (int i =0 ; i < Constants.NUM_MOOD_TYPES; i++)
				{
					reset();
					
					for (int j =0 ; j < Constants.NUM_MOOD_TYPES; j++)
					{
						
						//Search predefined strings for each mood type.
						Query query = new Query(Constants.searchStrings[i][j]);
						
						//Maximum searched over 100 tweets.
						query.count(Constants.TWEETCOUNT);
						try {
							//Access twitter search api.
							QueryResult result = t.search(query);
						
							//Get tweets using twitter4j REST API get method.
							for (twitter4j.Status status : result.getTweets()) {
								//Increment counter for each tweet
								m_counter++;
								
								//track date for the first tweet
						        if(m_counter ==1){
						    	   m_time1.date = status.getCreatedAt();
						        }
						        else
						        	m_time2.date = status.getCreatedAt();
						        //track date for the last tweet
						    	if(m_counter == Constants.TWEETCOUNT ) {
						    		 Log.d(TAG, "Last tweet reached");
						    	}
						    }
							float tweetsPerMinute;
							
						    tweetsPerMinute = GetTweetsPerMinute();
						    RegisterTweets(i, tweetsPerMinute);
						}catch(TwitterException te) {
							alertDialog.setTitle("Error");
							alertDialog.setMessage("Unable to perform twitter oauth get search request!");
							alertDialog.show();
//							AlertDialog alertDialog1 = alertDialog.create();	 
//							// show it
//							alertDialog1.show();
						}
					}
				}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			
            return null;
		}


		/**
		 * Reset inputs
		 */
		private void reset() {
			// TODO Auto-generated method stub
			m_counter = 0;
		}
		
		/**
		 * Compute tweets per minute
		 * @return
		 */
		
		float GetTweetsPerMinute()
		{
		  long minutes = getMinuteDifference(m_time1.date, m_time2.date);
			
		  if (minutes < 1)
		  {
		    System.out.print("unexpected number of minutes");
		    minutes = 1;
		  }
		 
		   float tpm = (float)m_counter / (float)minutes;

		  return tpm;
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
		
		/**
		 * Compute intensity for the mood type by comparison to the thresholds 
		 * extreme : highest
		 * considerable: moderate
		 * mild: low
		 * @return
		 */
		int ComputeCurrentMoodIntensity() 
		{ 
		  // check input is valid
		  if (m_worldMood < 0   || m_worldMood >= Constants.NUM_MOOD_TYPES) {
		    System.out.println("invalid world mood");
		    return Constants.MILD;
		  }
		  
		  if (m_worldTemperamentRatios[m_worldMood] < 1e-4f) {
		    System.out.print("unexpected m_worldTemperamentRatios");
		    return Constants.EXTREME;
		  }

		 // get the mood ratio as a percent of the temperament ratio.
		 // this will show the mood ratio as a divergence from the norm, and so is a good measure of mood intensity.
		 final float percent = m_worldMoodRatios[m_worldMood] / m_worldTemperamentRatios[m_worldMood];
		
		 if (percent > Constants.extremeMoodThreshold) {
		    return Constants.EXTREME;
		  }
		  else if (percent > Constants.moderateMoodThreshold) {
		    return Constants.CONSIDERABLE;
		  }
		  else  {
		   return Constants.MILD;
		  }
		}
		
		/**
		 * Compute current worlds mood 
		 * @return
		 */
		int ComputeCurrentMood() {
			  // find the current ratios
			  float sum = 0;
			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++) {
			    sum += m_worldMoodCounts[i];
			   // System.out.println("Compute current mood, m_worldMoodCounts[i]"+m_worldMoodCounts[i]);
			  }
			  
			  if (sum < 1e-4f)  {
			    System.out.println("unexpected total m_worldMoodCounts");
			   // System.out.println("Compute current mood, m_worldMood"+m_worldMood);
			    return m_worldMood;
			  }

			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    m_worldMoodRatios[i] = m_worldMoodCounts[i] / sum;
			  }

			  // find the ratio that has increased by the most, as a proportion of its moving average.
			  // So that, for example, an increase from 5% to 10% is more significant than an increase from 50% to 55%.

			  float maxIncrease = -1.0f;
			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    float difference = m_worldMoodRatios[i] - m_worldTemperamentRatios[i];
			    if (m_worldTemperamentRatios[i] < 1e-4f)   {
			      System.out.print("unexpected m_worldTemperamentRatios");
			      continue;
			    }

			    difference /= m_worldTemperamentRatios[i];
			    if (difference > maxIncrease)   {
			      maxIncrease = difference;
			      m_worldMood = i; // this is now the most dominant mood of the world!
			    }
			  }

			  // update the world temperament, as an exponential moving average of the mood.
			  // this allows the baseline ratios, i.e. world temperament, to change slowly over time.
			  // this means, in affect, that the 2nd derivative of the world mood wrt time is part of the current mood calculation.
			  // and so, after a major anger-inducing event, we can see when people start to become less angry.
			  sum = 0;

			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    if (m_worldTemperamentRatios[i]  <= 0)   {
			      System.out.println("m_worldTemperamentRatios should be initialised at construction");
			      m_worldTemperamentRatios[i] = m_worldMoodRatios[i];
			    }
			    else   {
			      final float a = Constants.moodSmoothingFactor;
			      m_worldTemperamentRatios[i] = (m_worldTemperamentRatios[i] * (1.0f - a)) + (m_worldMoodRatios[i] * a);
			    }
			    sum += m_worldTemperamentRatios[i];
			  }
			  if (sum < 1e-4f)  {
			    System.out.println("unexpected total m_worldTemperamentRatios total");
			    return m_worldMood;
			  }

			  // and finally, renormalise, to keep the sum of the moving average ratios as 1.0f
			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    m_worldTemperamentRatios[i] *= 1.0f / sum;
			   // System.out.print("temperament ratio: ");
			   // System.out.println(m_worldTemperamentRatios[i]);
			  }

			  // debug code - check sum is 1.
			  sum = 0;
			  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++)  {
			    sum += m_worldTemperamentRatios[i];
			  }

			  if (sum > 1.0f + 1e-4f || sum < 1.0f - 1e-4f)  {
			    System.out.println("unexpected renormalise result");
			  }
			  return m_worldMood;
			}
		
		/**
		 * Track Tweets per minute for each mood type 
		 * @param moodID
		 * @param tweetsPerMinute
		 */
		void RegisterTweets(int moodID, float tweetsPerMinute)
		{
		   // check input is valid
		  if (moodID < 0 || moodID >= Constants.NUM_MOOD_TYPES)  {
		    System.out.println("invalid moodID");
		    return;
		  }
		  if (tweetsPerMinute < 0) {
		    System.out.println("unexpected tweetsPerMinute");
		  }

		  // I'm using the tweetsPerMinute to be equivalent to the current emotion.
		  // to get the mood, average these potentially noisy and volatile emotions using an exponential moving average
		  if (m_worldMoodCounts[moodID] == Constants.INVALID_MOOD_VALUE)  {
		    // first time through
		    m_worldMoodCounts[moodID] = tweetsPerMinute;
		  }
		  else  {
		    float a = Constants.emotionSmoothingFactor;
		    m_worldMoodCounts[moodID] = (m_worldMoodCounts[moodID] * (1.0f - a)) + (tweetsPerMinute * a);
		  }
		}


		@Override
		protected void onPostExecute(Void result) {
			 if ((progress != null) && progress.isShowing())
				  progress.dismiss();
			//radio = (RadioGroup)findViewById(R.id.radiogp);
			int newMood = ComputeCurrentMood();
			
			String selection= null;
			  int newMoodIntensity = ComputeCurrentMoodIntensity();
			 
			  if(radio.getCheckedRadioButtonId()!=-1){
				  int id= radio.getCheckedRadioButtonId();
				  View radioButton = radio.findViewById(id);
				  int radioId = radio.indexOfChild(radioButton);
				  RadioButton btn = (RadioButton) radio.getChildAt(radioId);
				  selection = (String) btn.getText();
			  }
				     Intent intent =  new Intent(UserMood.this,WorldsMood.class);
				     intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 intent.putExtra("MoodIntensity", Constants.moodIntensityNames[(int)newMoodIntensity]);
					 intent.putExtra("MoodName", Constants.moodNames[(int)newMood]);
					 intent.putExtra("RadioSelection",selection);
					 intent.putExtra("m_worldMoodRatios", m_worldMoodRatios);
					 UserMood.this.finish();
					 startActivity(intent);
					 super.onPostExecute(result);
					 setupMqttClient();
					 String worldsMood = Constants.moodIntensityNames[(int)newMoodIntensity] + " "
							 +Constants.moodNames[(int)newMood];
					 SendMessage(worldsMood);
			}

		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.finish();
		super.onBackPressed();
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.tweetsmenu, menu);
         ActionBar actionBar = getActionBar();
         actionBar.setDisplayHomeAsUpEnabled(true);
         MenuItem m = menu.getItem(1);
 	    
	     m.setVisible(false);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
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
		            	UserMood.this.finish();
		            	Intent i= new Intent(UserMood.this,TwitterAuthentication.class);
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

