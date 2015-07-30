package de.tu.darmstadt.moodsense.constants;
import java.text.SimpleDateFormat;

import android.R;
/**
 * Utility class for constants
 * @author manishaluthra247
 *
 */
public class Constants {

	public static final String TAG = "AndroidTwitterOauth1";
	
	public static final String API_KEY = "7O2p398AOf1DODeSVuWHddRlz";
	public static final String API_SECRET= "nR22GOArGvV4dSGnoHA6txgtNuornQPCccRJTtPxky4clnAbX4";
	
	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	public static String[] moodNames = {
		 "loved",
		 "happy",
		 "surprised",
		 "angry",
		 "envy",
		 "sad",
		 "fear",
		 "neutral"
		} ;

	public static String[] moodIntensityNames= {
		 "mild",
		 "considerable",
		 "extreme",
		};
	
	public static float temperamentRatios[] = {
			 0.13f,
			 0.15f,
			 0.20f,
			 0.14f,
			 0.16f,
			 0.12f,
			 0.10f,
	};
		
	public static String[] string0 = {"i love you", "i love her", "i love him", "all my love", "i'm in love", "i really love", "i'm feeling loved"};
	public static String[] string1 = {"happiest", "i'm happy", "so happy", "excited", "so excited", "i'm feeling happy", "woot"};
	public static String[] string2 = {"wow", "O_o", "can't believe", "wtf", "unbelievable", "i'm feeling surprised", "got surprised"};
	public static String[] string3 = {"i hate", "really angry", "iam mad", "really hate", "so angry", "so much hatred", "iam feeling angry"};
	public static String[] string4 = {"i wish i", "i'm envious", "i'm jealous", "i want to", "why can't i", "i desire", "i'm feeling envy"};
	public static String[] string5 = {"i'm so sad", "i'm heartbroken", "i'm so upset", "i'm depressed", "i can't stop crying", "i'm sad", "i'm feeling sad"};
	public static String[] string6 = {"i'm so scared", "i'm really scared", "i'm terrified", "i'm really afraid", "so scared i", "its scary", "i'm feeling fear"};
    
	public static String searchStrings[][] = {
		string0,
		string1,
		string2,
		string3,
		string4,
		string5,
		string6,
	};
	
	public static int[][] m_considerable = 
	  {{0, 1, 2, 3, 4, -1, -1},  
	   {-1, -1, 2, -1, 4, -1, 6},
	   {-1, -1, -1, -1, 4, -1, -1}, 
	   {-1, -1, 2, -1, -1, -1, 6},
	   {0, 1, 2, -1, -1, 5, -1},
	   {-1, 1, -1, 3, -1, 5, -1},
	   {-1, -1, 2, -1, -1, -1, 6}};
	
	public static int[][] m_extreme = 
	   {{-1, -1, -1, -1, -1, 5, -1},  
		{0, -1, -1, -1, -1, -1, -1},
		{0, -1, 2, 3, -1, -1, -1}, 
		{-1, 1, -1, 3, 4, 5, -1},
		{-1, -1, -1, -1, 4, -1, 6},
		{0, -1, 2, -1, 4, -1, -1},
		{0, 1, -1, 3, 4, -1, -1}};
	
	
	
	public static int[][] m_mild = 
		{{6},  
		{1, 3, 5},
		{1, 5, 6}, 
		{0},
		{3},
		{6},
		{5}};
	
	public static final String	OAUTH_CALLBACK_URL= "http://localhost";
	public static final int TWEETCOUNT =10;
	public static final float emotionSmoothingFactor = 0.1f;
	public static final float moodSmoothingFactor = (0.05f);
	public static final float  moderateMoodThreshold = (2.0f);
	public static final float extremeMoodThreshold = (4.0f);
	public static final float INVALID_MOOD_VALUE =  -1;
	public final int LOVE = 0;
	public final int JOY = 1;
	public final int SURPRISE = 2;
	public final int ANGER = 3;
	public final int ENVY = 4;
	public final int SADNESS = 5;
	public final int FEAR = 6;
	public final int NEUTRAL = 7;
	public final static int NUM_MOOD_TYPES = 7;
	public final static int MILD = 0;
	public final static int CONSIDERABLE = 1;
	public final static int EXTREME = 2;
	public final static int NUM_MOOD_INTENSITY = 3;
	public int MOOD_TYPE[] = {
			  LOVE ,
			  JOY,
			  SURPRISE,
			  ANGER,
			  ENVY,
			  SADNESS,
			  FEAR,
			  NEUTRAL,
			  NUM_MOOD_TYPES,
			 
		};
	public int MOOD_INTENSITY[]=  {
			  MILD ,
			  CONSIDERABLE,
			  EXTREME,
			  NUM_MOOD_INTENSITY,
		};
	
	/**
	 * @author dvg
	 *  added for V1.1
	 */
	public static final String TWITTER_USER_MOOD = "userMood in twitter";
	public static final String USER_ON = "user tweet available";
	public static final String USER_OFF = "user tweet not available";
	public static String SSID = "";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final String userNoTwitter = "User mood not updated";
	
}

