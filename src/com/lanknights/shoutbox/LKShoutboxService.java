package com.lanknights.shoutbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LKShoutboxService extends Service {
	
	private LKLogin servLogin;
	private LKShoutManager servMan;
	private Timer timer;
	private ShoutParcel storedShouts = new ShoutParcel();
	
	//Most content is from mindtherobot.com/d.android.com, this is really nasty from my point of view and I imagine there must be a better way to do this
	//		ex: none of that aidl autogenerated garbage
	
	//this is probably bad programming(why would you lock another object just to make sure you're synchronized with a separate one)(to keep the separate one unlocked I suppose, but this can't be right)
	private final Object storedShoutsLock = new Object();
	
	//List of programs we want to update when we have new information.
	private List<NewShoutListener> listeners = new ArrayList<NewShoutListener>();
	
	private NewShoutApi.Stub apiEndpoint = new NewShoutApi.Stub() {
		//I'm override autogenerated functions for Stub...
		
		@Override
		public void getShoutsNow() throws RemoteException {
			synchronized(storedShoutsLock) {
			    Timer tempTimer = new Timer("TempGetShoutsNowTimer");
			    tempTimer.schedule(updateShouts, 0L);
			}
		}
		
		@Override
		public ShoutParcel getLatestShouts() throws RemoteException {
			synchronized(storedShoutsLock) {
				return storedShouts;
			}
		}
		
		@Override
		public void addListener(NewShoutListener listener) throws RemoteException {
			synchronized(listeners) {
				listeners.add(listener);
				Log.d("LKShoutbox", "SERVICE: Listener added: " + listener + " len:" + listeners.size());
			}
		
		}
		
		@Override
		public void removeListener(NewShoutListener listener) throws RemoteException {
			synchronized(listeners) {
				listeners.add(listener);
			}
		}

		@Override
		public void setLKLogin(LKLogin login) throws RemoteException {
			servLogin = login;
			Log.d("LKShoutbox", "SERVICE: LKLogin: " + servLogin.member_id);
		}

		@Override
		public LKShoutManager getCurrentMan() throws RemoteException {
			return servMan;
		}

		@Override
		public void setLastProcessed(int proc) throws RemoteException {
			servMan.processedlast = proc;
		}
		
	};
	
	/*!!!!!!IMPORTANT!!!!!!
	 * timertask runs in its own thread automagically, so no asynctask needed...but it's still necessary for on-demand refreshing
	 */
	private TimerTask updateShouts = new TimerTask() {
		@Override
		public void run() {
			try {
			Log.d("LKShoutbox", "SERVICE: UPDATESHOUTS: TIMERTASK RUNNING");
			
			//Retrieve shouts
			ArrayList<Shout> tempShouts = servMan.downloadParseShouts(servLogin.session_id, servLogin.secure_hash, servLogin.member_id, servLogin.pass_hash);
			
			Log.d("LKShoutbox", "SERVICE: AFTER DOWNLOADED SHOUTS LENGTH: " + tempShouts.size());
			
			//Lock stored shouts, set it to our new value.
			synchronized(storedShoutsLock) {
				Log.d("LKShoutbox", "insidelock1 a: " + tempShouts.toArray() + " b: " + tempShouts.toString());
				storedShouts.shouts = tempShouts.toArray(new Shout[tempShouts.size()]);
//				Log.d("LKShoutbox", "num: " + storedShouts.shouts.length);
			}
			
			Log.d("LKShoutbox", "afterlock1");
			//Lock listeners so they can't retreat, shove our shouts down their throats
			synchronized(listeners) {
				Log.d("LKShoutbox", "SERVICE: Synchronized listeners, looping through");
				for(NewShoutListener listener : listeners) { //terrible naming convention
					Log.d("LKShoutbox", "SERVICE: listener: " + listener);
					try {
						listener.handleShoutsUpdate();
					} catch(RemoteException e) {
						Log.w("LKShoutbox", "SERVICE: UPDATESHOUTS: FAILED TO NOTIFY LISTENER " + listener, e);
					}
				}
			}
			
			Log.d("LKShoutbox", "afterlock2");
			} catch (Throwable t) {
				Log.e("LKShoutbox", "SERVICE: Error in update timer", t);
			}
		}
		
	};
	
	
	@Override
	public IBinder onBind(Intent intent) {
		if(LKShoutboxService.class.getName().equals(intent.getAction())) {
			Log.d("LKShoutbox","SERVICE: Bound by intent " + intent);
			return apiEndpoint;
		} else {
			return null;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
	    servMan = new LKShoutManager();

	    timer = new Timer("CheckShoutsTimer");
	    timer.schedule(this.updateShouts, 0L, 30 * 1000L);
	    

		/*
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        Intent intent = new Intent(super.getApplicationContext(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 135432, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), (long)30000, sender);
		*/
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	

}