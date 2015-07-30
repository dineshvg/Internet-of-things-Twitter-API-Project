package de.tu.darmstadt.moodsense.app;

import de.tu.darmstadt.moodsense.R;
import de.tu.darmstadt.moodsense.R.layout;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class AboutMoodSense extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// MenuInflater inflater = getMenuInflater();
      //   inflater.inflate(R.layout.tweetsmenu, menu);
         ActionBar actionBar = getActionBar();
         actionBar.setDisplayHomeAsUpEnabled(true);
        
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		}
		return false;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.finish();
		//moveTaskToBack(true);
		super.onBackPressed();
		
	
	}
}
