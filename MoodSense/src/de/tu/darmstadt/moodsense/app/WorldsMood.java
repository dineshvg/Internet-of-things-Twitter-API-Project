package de.tu.darmstadt.moodsense.app;
import de.tu.darmstadt.moodsense.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SimpleTimeZone;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import de.tu.darmstadt.moodsense.R.drawable;
import de.tu.darmstadt.moodsense.R.id;
import de.tu.darmstadt.moodsense.R.layout;
import de.tu.darmstadt.moodsense.auth.TwitterAuthentication;
import de.tu.darmstadt.moodsense.constants.Constants;
import de.tu.darmstadt.moodsense.store.SharedPreferencesCredentialStore;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity is responsible to display the current world's mood in comparison to the user's mood
 * Then visualize the world's mood using Achart library Pie chart
 * @author manishaluthra247
 * 
 */
	@SuppressLint("NewApi")
	public class WorldsMood extends Activity{
		private SharedPreferences prefs;
	
		TextView worldsmood,usersmood,userinfo ;
		String MoodIntensity,MoodName,CurrentMood,userMood ;
		
		Button visualize,database;
		private static int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.rgb(130, 130, 230), Color.RED };
		ImageView userimage, worldimage;
		private boolean alreadyStored = false;
		
		List<String> times;
		ProgressDialog progressDialog;
		float m_worldMoodRatios[] = new float[7];
		float m_worldMoodPercent[]= new float[7];
		double m_world_doughnut[] = new double[7];
		
		 String[] worldMoodNames = {
			 "love",
			 "joy",
			 "surprise",
			 "anger",
			 "envy",
			 "sadness",
			 "fear",
			 		} ;
		@Override
			protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.worlds_mood);
			this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
			final Bundle extras = getIntent().getExtras();
			reset();
			if(extras!=null) {
				MoodIntensity = extras.getString("MoodIntensity");
				MoodName = extras.getString("MoodName");
				userMood = extras.getString("RadioSelection");
				m_worldMoodRatios = extras.getFloatArray("m_worldMoodRatios");
				DecimalFormat df = new DecimalFormat("#.##"); 
				
				//Compute mood percentage
				for (int i= 0; i < Constants.NUM_MOOD_TYPES; i++){
					m_worldMoodPercent[i] = m_worldMoodRatios[i]*100;
					m_world_doughnut[i] =(double) Double.parseDouble(df.format(m_worldMoodPercent[i]));
					worldMoodNames[i]= worldMoodNames[i]+" : "+m_worldMoodPercent[i]+"%";
					System.out.println("m_world_doughnut"+m_world_doughnut[i]);
				}
				
				
				//Display current mood
				CurrentMood = MoodIntensity +" "+MoodName;
				visualize = (Button)findViewById(R.id.vismood);
			
				worldsmood = (TextView) findViewById(R.id.textView2);
				usersmood = (TextView) findViewById(R.id.textView4);
				userinfo = (TextView)findViewById(R.id.userinfo);
				userimage = (ImageView) findViewById(R.id.imageView1);
				worldimage = (ImageView) findViewById(R.id.imageView2);
				worldsmood.setText(CurrentMood);
				usersmood.setText(userMood);
				
				//Set dynamic image smileys for world's mood
				if (MoodName.equals("love")){
					worldimage.setImageResource(R.drawable.loved);
				}
				else if (MoodName.equals("joy")){
					worldimage.setImageResource(R.drawable.happy);
				}
				else if (MoodName.equals("surprise")){
					worldimage.setImageResource(R.drawable.surprised);
				}
				else if (MoodName.equals("anger")){
					worldimage.setImageResource(R.drawable.angry);
				}
				else if (MoodName.equals("envy")){
					worldimage.setImageResource(R.drawable.envy);
				}
				else if (MoodName.equals("sadness")){
					worldimage.setImageResource(R.drawable.sad);
				}
				else if (MoodName.equals("fear")){
					worldimage.setImageResource(R.drawable.fear);
				}
				else if (MoodName.equals("neutral")){
					worldimage.setImageResource(R.drawable.neutral);
				}
								
				//Set dynamic image smileys for user's mood and display worlds mood
				//in comparison to world's mood.
				if (userMood.equals("loved")){
					userinfo.setText(m_world_doughnut[0]+"% of world is also feeling loved!!");
					userimage.setImageResource(R.drawable.loved);
				}
				else if (userMood.equals("joyful")){
					userinfo.setText(df.format(m_worldMoodPercent[1])+"% of world is also feeling "+userMood+"!!");
					userimage.setImageResource(R.drawable.happy);
				}
				else if (userMood.equals("surprised")){
					userinfo.setText(df.format(m_worldMoodPercent[2])+"% of world is also feeling "+userMood+"!!");
					userimage.setImageResource(R.drawable.surprised);
				}
				else if (userMood.equals("angry")){
					userinfo.setText(df.format(m_worldMoodPercent[3])+"% of world is also feeling "+userMood+"!!");
					userimage.setImageResource(R.drawable.angry);
				}
				else if (userMood.equals("envy")){
					userinfo.setText(df.format(m_worldMoodPercent[4])+"% of world is also feeling "+userMood+"!!");
					userimage.setImageResource(R.drawable.envy);
				}
				else if (userMood.equals("sad")){
					userinfo.setText(df.format(m_worldMoodPercent[5])+"% of world is also feeling "+userMood+"!!");
					userimage.setImageResource(R.drawable.sad);
				}
				else if (userMood.equals("frightened")){
					userinfo.setText(df.format(m_worldMoodPercent[6])+"% of world is also  "+userMood+"!!");
					userimage.setImageResource(R.drawable.fear);
				}
				else if (userMood.equals("neutral")){
					
					userimage.setImageResource(R.drawable.neutral);
				}
				
				visualize.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						openChart();
										
					}
				});
					
			}
			else
				Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
			
		
		}
		
		public void reset(){
			
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
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		    // Setting Dialog Title
		    alertDialog.setTitle("Logout");
		
		    // Setting Dialog Message
		    alertDialog.setMessage("Do you want to sign off?");
		
		    // Setting OK Button
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	WorldsMood.this.finish();
		               	new SharedPreferencesCredentialStore(prefs).clearCredentials();
		               	Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
		               	Intent i= new Intent(WorldsMood.this,TwitterAuthentication.class);
		            	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            	startActivity(i);
		            }
		    });
		
		    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	Toast.makeText(getApplicationContext(), "Logout cancelled", Toast.LENGTH_SHORT).show();
	            }
		    });
	            	// Showing Alert Message
		    alertDialog.show();
			
			return true;
			}
			else 
				Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
		}break;
		case R.id.about:
			return false;
	
		case android.R.id.home:
			Intent i = new Intent(this,UserMood.class);
			startActivity(i);
			this.finish();
			return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
		return false;
		
	}
	/* Visualize the worlds mood using
	 * Pie chart from Achart library
	 */
	private void openChart() {
		System.out.println("Open chart called");
		
		 List<double[]> values = new ArrayList<double[]>();
		 List<String[]> titles = new ArrayList<String[]>();
		// int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };
		 values.add(m_world_doughnut);
		 titles.add(worldMoodNames);
		
		 	DefaultRenderer renderer = buildCategoryRenderer(colors);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setZoomButtonsVisible(true);
		    renderer.setZoomEnabled(true);
		    renderer.setShowLegend(true);
		    renderer.setShowLabels(true);
		    
		    renderer.setBackgroundColor(Color.rgb(222, 222, 200));
		    
		    renderer.setLabelsColor(Color.BLACK);
		    
		    renderer.setDisplayValues(true);
		    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
		    r.setGradientEnabled(true);
		    r.setGradientStart(0, Color.BLUE);
		    r.setGradientStop(0, Color.GREEN);
		    r.setDisplayChartValues(true);
		    r.setHighlighted(true);
		   
		    
		    Intent intent = ChartFactory.getDoughnutChartIntent(getApplicationContext(),
		        buildMultipleCategoryDataset("Mood Percentage", titles, values), renderer,
		        "World's Mood Percentage");
		    startActivity(intent);
		  }

	/**
	   * Builds a category renderer to use the provided colors.
	   * 
	   * @param colors the colors
	   * @return the category renderer
	   */
	  protected DefaultRenderer buildCategoryRenderer(int[] colors) {
	    DefaultRenderer renderer = new DefaultRenderer();
	    renderer.setLabelsTextSize(33);
	    renderer.setDisplayValues(true);
	  
	    renderer.setMargins(new int[] { 50, 50, 50, 50,50,50 });
	    for (int color : colors) {
	      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	      r.setColor(color);
	      r.setDisplayChartValues(true);
	      renderer.addSeriesRenderer(r);
	     
	    }
	    return renderer;
	  }
	  
	  /**
	   * Builds a multiple category series using the provided values.
	   * 
	   * @param titles the series titles
	   * @param values the values
	   * @return the category series
	   */
	  protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
	      List<String[]> titles, List<double[]> values) {
	    MultipleCategorySeries series = new MultipleCategorySeries(title);
	    int k = 0;
	    for (double[] value : values) {
	   
	      series.add("",titles.get(k), value);
	   
	      k++;
	    }
	    return series;
	  }
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		Intent i = new Intent(this,UserMood.class);
		startActivity(i);
		this.finish();
		super.onBackPressed();
	}
	
}
