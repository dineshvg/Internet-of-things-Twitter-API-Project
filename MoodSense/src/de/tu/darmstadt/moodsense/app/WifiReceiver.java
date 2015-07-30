/**
 * 
 */
package de.tu.darmstadt.moodsense.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * @author dinesh
 *
 */
public class WifiReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        CharSequence textOn = "Wifi On"; CharSequence textOff = "Wifi On";
		/*if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) 
        	Toast.makeText(context, textOn, Toast.LENGTH_LONG).show();
        else
        	Toast.makeText(context, textOff, Toast.LENGTH_LONG).show();*/
	}

}
