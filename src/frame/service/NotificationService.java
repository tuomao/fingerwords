package frame.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class NotificationService extends Service {
	public static final String ACTION_NOTIFICATION_CONTROL = "action_notification_control";
	public static final String COMMAND_KEY = "cmd_key";
	public static final String TITLE = "title";
	public static final String SOURCE = "source";
	public static final String KEY_COMMAND_SHOW = "show_notification";
	public static final String KEY_COMMAND_REMOVE = "remove_notification";
	public static final int NOTIFICATIN_ID = 999;

	private Notification mNotification;
	private String title;
	private int source;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if (ACTION_NOTIFICATION_CONTROL.equals(action)) {
			String cmd = intent.getStringExtra(COMMAND_KEY);
			title = intent.getStringExtra(TITLE);
			source = intent.getIntExtra(SOURCE, 0);
			if (KEY_COMMAND_SHOW.equals(cmd)) {
				showNotification();
			} else if (KEY_COMMAND_REMOVE.equals(cmd)) {
				removeNotification();
			}
		} else {
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void showNotification() {
		createNotification();
	}

	public void removeNotification() {
		stopForeground(true);
		stopSelf();
	}
    
	public void createNotification() {
		/*
		if (mNotification == null) {
			mNotification = new Notification();
		}
		mNotification.icon = R.drawable.icon;
		mNotification.tickerText = title;
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(this, StudyActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(SOURCE, source);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		mNotification.contentIntent = pendingIntent;
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notify);
		contentView.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.text1, "正在播放-" + title);
		mNotification.contentView = contentView;
		startForeground(NOTIFICATIN_ID, mNotification);*/
	}
}