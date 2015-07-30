package de.tu.darmstadt.moodsense.auth;
import android.R;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.http.apache.ApacheHttpTransport;

import de.tu.darmstadt.moodsense.app.UserSocialMood;
import de.tu.darmstadt.moodsense.app.UserMood;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.store.CredentialStore;
import de.tu.darmstadt.moodsense.store.SharedPreferencesCredentialStore;
import de.tu.darmstadt.moodsense.util.QueryStringParser;

/**
 * This activity does the OAuth authentication for twitter.
 * After successful login launches SocialMood activity.
 * @author manishaluthra
 * PS: Part of this code has been taken from "https://github.com/ddewaele/"
 * */

@SuppressLint("SetJavaScriptEnabled")
public class OAuthAccessTokenActivity extends Activity {

	final String TAG = getClass().getName();
	private String CALLBACK_URL = "callback://tweeter";
	private SharedPreferences prefs;
	ProgressDialog progress;
	private boolean handled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progress = new ProgressDialog(this);
        Log.i(TAG, "Starting task to retrieve request token.");
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	private WebView webview;
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 if ((progress != null) && progress.isShowing())
		        progress.dismiss();
		   
	}
	@Override
	protected void onResume() {
		super.onResume();
		webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        handled=false;
        
        new PreProcessToken().execute();
        
	}
	
	/**
	 * Launch OAuth authentication screen for twitter login
	 * @author manishaluthra247
	 *
	 */
	private class PreProcessToken extends AsyncTask<Uri, Void, Void> {

		final OAuthHmacSigner signer = new OAuthHmacSigner();
		private String authorizationUrl;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress.setMessage("Processing");
		    progress =ProgressDialog.show(OAuthAccessTokenActivity.this, "Processing", "Connecting to Twitter");
			super.onPreExecute();
		}
					
		@Override
		protected Void doInBackground(Uri...params) {
			
			try {
			  
		        signer.clientSharedSecret = Constants.API_SECRET;
		        
				OAuthGetTemporaryToken temporaryToken = new OAuthGetTemporaryToken(Constants.REQUEST_URL);
				temporaryToken.transport = new ApacheHttpTransport();
				temporaryToken.signer = signer;
				temporaryToken.consumerKey = Constants.API_KEY;
				temporaryToken.callback = Constants.OAUTH_CALLBACK_URL;
				
				OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
				signer.tokenSharedSecret = tempCredentials.tokenSecret;
				
				OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(Constants.AUTHORIZE_URL);
				authorizeUrl.temporaryToken = tempCredentials.token;
				authorizationUrl = authorizeUrl.build();

		        Log.i(Constants.TAG, "Using authorizationUrl = " + authorizationUrl);
		        
		        handled=false;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		        
            return null;
		}

		
		/**
		 * When we're done and we've retrieved either a valid token or an error from the server,
		 * we'll return to our original activity 
		 */
		@Override
		protected void onPostExecute(Void result) {

	        Log.i(TAG, "Retrieving request token from Google servers");
	       
    	
	        webview.setWebViewClient(new WebViewClient() {  

	        	@Override  
	            public void onPageStarted(WebView view, String url,Bitmap bitmap)  {  
	        		Log.i(Constants.TAG, "onPageStarted : " + url + " handled = " + handled);
	        		
	            }
	        	@Override  
	            public void onPageFinished(final WebView view, final String url)  {
	        		if ((progress != null) && progress.isShowing())
	        			progress.dismiss();
	        		Log.i(Constants.TAG, "onPageFinished : " + url + " handled = " + handled);
	        		
	        		if (url.startsWith(Constants.OAUTH_CALLBACK_URL)) {
		        		if (url.indexOf("oauth_token=")!=-1) {
			        		webview.setVisibility(View.INVISIBLE);
			        		
			        		if (!handled) {
			        			new ProcessToken(url,signer).execute();
			        		}
		        		} else {
		        			webview.setVisibility(View.VISIBLE);
		        		}
	        		}
	            }

	        });  
	        
	        webview.loadUrl(authorizationUrl);	

		}

	}	
	
	/**
	 * Authenticate with the credentials and generate access token and access token secret.
	 * Launch SocialMood Activity after successful authentication.
	 * @author manishaluthra247
	 *
	 */
	
	private class ProcessToken extends AsyncTask<Uri, Void, Void> {

		String url;
		private OAuthHmacSigner signer;
		
		public ProcessToken(String url,OAuthHmacSigner signer) {
			this.url=url;
			this.signer = signer;
		}
		
		@Override
		protected Void doInBackground(Uri...params) {

			Log.i(Constants.TAG, "doInbackground called with url " + url);
			if (url.startsWith(Constants.OAUTH_CALLBACK_URL) && !handled) {
        		try {
					
        			if (url.indexOf("oauth_token=")!=-1) {
        				handled=true;
            			String requestToken  = extractParamFromUrl(url,"oauth_token");
            			String verifier= extractParamFromUrl(url,"oauth_verifier");
						
            			signer.clientSharedSecret = Constants.API_SECRET;

            			OAuthGetAccessToken accessToken = new OAuthGetAccessToken(Constants.ACCESS_URL);
            			accessToken.transport = new ApacheHttpTransport();
            			accessToken.temporaryToken = requestToken;
            			accessToken.signer = signer;
            			accessToken.consumerKey = Constants.API_KEY;
            			accessToken.verifier = verifier;

            			OAuthCredentialsResponse credentials = accessToken.execute();
            			signer.tokenSharedSecret = credentials.tokenSecret;

            			CredentialStore credentialStore = new SharedPreferencesCredentialStore(prefs);
			  		      credentialStore.write(new String[] {credentials.token,credentials.tokenSecret});
			  		      
        			} else if (url.indexOf("error=")!=-1) {
        				new SharedPreferencesCredentialStore(prefs).clearCredentials();
        			}
        			
				} catch (IOException e) {
					e.printStackTrace();
				}

        	}
            return null;
		}

		private String extractParamFromUrl(String url,String paramName) {
			String queryString = url.substring(url.indexOf("?", 0)+1,url.length());
			QueryStringParser queryStringParser = new QueryStringParser(queryString);
			return queryStringParser.getQueryParamValue(paramName);
		}   
		
		@Override
		protected void onPreExecute() {
			progress=ProgressDialog.show(OAuthAccessTokenActivity.this, "Processing", "Authenticating with your credentials");
			//progress.show();
		}

		/**
		 * When we're done and we've retrieved either a valid token or an error from the server,
		 * we'll return to our original activity 
		 */
		@Override
		protected void onPostExecute(Void result) {
			if ((progress != null) && progress.isShowing())
				progress.dismiss();
			Log.i(Constants.TAG," ++++++++++++ Starting mainscreen again");
			Intent i = new Intent(OAuthAccessTokenActivity.this,UserSocialMood.class);
			i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			startActivity(i);
			finish();
		}
	}	
}
