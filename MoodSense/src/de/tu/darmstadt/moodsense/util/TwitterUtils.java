package de.tu.darmstadt.moodsense.util;

import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.store.SharedPreferencesCredentialStore;

/**
 * Utility class for accessing twitter internal functions using Twitter4j library
 * @author manishaluthra247
 * PS: Part of this code has been taken from https://github.com/ddewaele/
 */
public class TwitterUtils {
	
	/**
	 * Check authentication was successful or not
	 * @param prefs shared preferences object
	 * @return boolean value whether authenticated ot not 
	 */
	public static boolean isAuthenticated(SharedPreferences prefs) {

		String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
		AccessToken a = new AccessToken(tokens[0],tokens[1]);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
		twitter.setOAuthAccessToken(a);
		
		try {
			twitter.getAccountSettings();
			return true;
		} catch (TwitterException e) {
			return false;
		}
	}
	
	/**
	 * Accessing twitter timeline
	 * @param prefs
	 * @return
	 * @throws Exception
	 */
	public static ResponseList<Status> getHomeTimeline(SharedPreferences prefs) throws Exception {
		String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
		AccessToken a = new AccessToken(tokens[0],tokens[1]);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
		twitter.setOAuthAccessToken(a);
        ResponseList<Status> homeTimeline = twitter.getHomeTimeline();
        return homeTimeline;
	}
	
	/**
	 * Post tweet
	 * @param prefs
	 * @param msg
	 * @throws Exception
	 */
	public static void sendTweet(SharedPreferences prefs,String msg) throws Exception {
		String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
		AccessToken a = new AccessToken(tokens[0],tokens[1]);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
		twitter.setOAuthAccessToken(a);
        twitter.updateStatus(msg);
	}	
	
	/**
	 * Accessing twitter object for searching tweets
	 * @param prefs
	 * @return
	 * @throws Exception
	 */
	public static Twitter searchTweet(SharedPreferences prefs) throws Exception {
		String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
		AccessToken a = new AccessToken(tokens[0],tokens[1]);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
		twitter.setOAuthAccessToken(a);
		 return twitter;
	}
	
	/**
	 * Access username of the logged person
	 * @param prefs
	 * @return
	 * @throws Exception
	 */
	public static String getUserName(SharedPreferences prefs) throws Exception {
		String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
		AccessToken a = new AccessToken(tokens[0],tokens[1]);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
		twitter.setOAuthAccessToken(a);
		return twitter.getScreenName();		
	}
	
}
