package slackClient;

import java.util.TimerTask;

public class ActivityTrackingTask extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ActivityTracker.initialize();
	}

}
