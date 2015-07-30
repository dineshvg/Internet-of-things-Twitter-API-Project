/**
 * 
 */
package de.tu.darmstadt.moodsense.services;

import java.util.Calendar;
import java.util.Date;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import de.tu.darmstadt.moodsense.app.UserMood.twitterDate;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.util.TwitterUtils;

/**
 * @author dinesh
 *
 */
public class WorldMoodService {

	public final String TAG = "WorldMoodService";
	
	float m_worldTemperamentRatios[] = new float[7];
	float m_worldMoodCounts[]  = new float[7];
	float m_worldMoodRatios[]  = new float[7];
	int  m_worldMood;
	private SharedPreferences prefs;
	int m_counter =0;
	twitterDate m_time1 = new twitterDate();
	twitterDate m_time2= new twitterDate();
	
	public WorldMoodService() {
		
		if (Constants.emotionSmoothingFactor < 0.0f || Constants.emotionSmoothingFactor > 1.0f)  {
		    Log.d(TAG,"invalid emotionSmoothingFactor");
		  }
		  if (Constants.moodSmoothingFactor < 0.0f ||  Constants.moodSmoothingFactor > 1.0f)  {
			  Log.d(TAG,"invalid moodSmoothingFactor");
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
			  Log.d(TAG,"unexpected m_worldTemperamentRatios sum");
		  }
	}
	
	/**
	 * Reset inputs
	 */
	private void reset() {
		// TODO Auto-generated method stub
		m_counter = 0;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @author dinesh
	 * @throws Exception
	 * Thread for twitter call
	 */
	public void computeWorldMood() {
		Thread worldThread;
		worldThread = new Thread() {
			public void run () {
				Log.d(TAG, "In loop of world Mood");
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
									 tweetsPerMinute = getTweetsPerMinute();
									 registerTweets(i, tweetsPerMinute);
								
							}
						}
					
				} catch (TwitterException e) {
					e.getErrorMessage();
					Log.d(TAG, "Eception throw in world twitter");
				} catch (Exception e) {					
					// TODO Auto-generated catch block
					e.printStackTrace();
				}																								
				////////////////
				stopThread(this);
			}
		};
		
		
				
			
	}
	
	
	private void stopThread(Thread theThread) {
		
		if (theThread != null)
	    {
			theThread = null;
			Log.d(TAG, "Execution complete inside stop thread");
			//userStatusToMood(m_myMood);	        
	    }
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	float getTweetsPerMinute()
	{
	  long minutes = getMinuteDifference(m_time1.date, m_time2.date);	  	  
	  Log.d(TAG, "Difference ::"+ minutes);
	  
	  if (minutes < 1)
	  {
	    Log.d(TAG,"unexpected number of minutes");
	    minutes = 1;
	  }
	 
	   float tpm = (float)m_counter / (float)minutes;
	   Log.d(TAG,"Tweets per minute ::  "+ tpm);
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
	 * Track Tweets per minute for each mood type 
	 * @param moodID
	 * @param tweetsPerMinute
	 */
	void registerTweets(int moodID, float tweetsPerMinute)
	{
	   // check input is valid
	  if (moodID < 0 || moodID >= Constants.NUM_MOOD_TYPES)  {
	    Log.d(TAG,"invalid moodID");
	    return;
	  }
	  if (tweetsPerMinute < 0) {
	    Log.d(TAG,"unexpected tweetsPerMinute");
	  }
	  // I'm using the tweetsPerMinute to be equivalent to the current emotion.
	  // to get the mood, average these potentially noisy and volatile emotions using an exponential moving average
	  if (m_worldMoodCounts[moodID] == Constants.INVALID_MOOD_VALUE)  {		  
	    // first time through
	    m_worldMoodCounts[moodID] = tweetsPerMinute;
	    Log.d(TAG,"For mood ID :: "+ moodID+ " m_worldMoodCounts[moodID] INVALID :: "+ m_worldMoodCounts[moodID]);
	    //String sql = "INSERT INTO worldMood VALUES('"+String.valueOf(m_worldMoodCounts[moodID])+"','"+tweetsPerMinute+"');";
	    //db.execSQL(sql);
	    
	    Log.d(TAG, "invalid mood value");
	  } else  {
	    float a = Constants.emotionSmoothingFactor;
	    m_worldMoodCounts[moodID] = (m_worldMoodCounts[moodID] * (1.0f - a)) + (tweetsPerMinute * a);
	    //String sql = "INSERT INTO worldMood VALUES('"+String.valueOf(m_worldMoodCounts[moodID])+"','"+tweetsPerMinute+"');";
	    //db.execSQL(sql);
	    Log.d(TAG,"For mood ID :: "+ moodID+ " m_worldMoodCounts[moodID] :: "+ m_worldMoodCounts[moodID]);
	  }
	}
	
	/**
	 * Compute current worlds mood 
	 * @return
	 */
	int computeCurrentMood() {
		Log.d(TAG, "In computeCurrentMood");
		  // find the current ratios
		  float sum = 0;
		  for (int i = 0; i < Constants.NUM_MOOD_TYPES; i++) {
			  Log.d(TAG, "computeCurrentMood :: "+ m_worldMoodCounts);
		    sum += m_worldMoodCounts[i];
		    Log.d(TAG,"Compute current mood, m_worldMoodCounts[i]"+m_worldMoodCounts[i]);
		  }
		  
		  if (sum < 1e-4f)  {
			  Log.d(TAG,"unexpected total m_worldMoodCounts");
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
		    	Log.d(TAG,"unexpected m_worldTemperamentRatios");
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
		    	Log.d(TAG,"m_worldTemperamentRatios should be initialised at construction");
		      m_worldTemperamentRatios[i] = m_worldMoodRatios[i];
		    }
		    else   {
		      final float a = Constants.moodSmoothingFactor;
		      m_worldTemperamentRatios[i] = (m_worldTemperamentRatios[i] * (1.0f - a)) + (m_worldMoodRatios[i] * a);
		    }
		    sum += m_worldTemperamentRatios[i];
		  }
		  if (sum < 1e-4f)  {
			  Log.d(TAG,"unexpected total m_worldTemperamentRatios total");
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
			  Log.d(TAG,"unexpected renormalise result");
		  }
		  return m_worldMood;
		}
	
	//**
	 //* Compute intensity for the mood type by comparison to the thresholds 
	// * extreme : highest
	 //* considerable: moderate
	 //* mild: low
	 //* @return
	 //*//*
	private int computeCurrentMoodIntensity() 
	{ 
	  // check input is valid
	  if (m_worldMood < 0   || m_worldMood >= Constants.NUM_MOOD_TYPES) {
		  Log.d(TAG,"invalid world mood");
	    return Constants.MILD;
	  }
	  
	  if (m_worldTemperamentRatios[m_worldMood] < 1e-4f) {
		  Log.d(TAG,"unexpected m_worldTemperamentRatios");
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
	 * Access created date of tweets to calculate minutes between these dates
	 * @author manishaluthra247
	 *
	 */
	public class twitterDate {
			public Date date;
	}
}
