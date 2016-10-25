package com.scube.Gondor.Helpers.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.scube.Gondor.Chat.models.ChatNotificationManager;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by srikanthsridhara on 5/30/15.
 */
public class GcmNotifier {

    public Context context;
    /**
     * NOTIFICATION_ID:
     * A numeric value that identifies the notification that we'll be sending.
     * This value needs to be unique within this app, but it doesn't need to be
     * unique system-wide.
     */

    public GcmNotifier(Context context) {
        this.context = context;
    }

    /**
     * Send a sample notification using the NotificationCompat API.
     */
    public void sendNotification(String data, String dialogId) {

        // BEGIN_INCLUDE(build_action)
        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("dialogId", dialogId);
        intent.putExtra("scubitsFragments", "scubitsFragments");

        GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_nav), "cameFromNotification", "true");

        // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // END_INCLUDE(build_action)

        // BEGIN_INCLUDE (build_notification)
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Get the parameters from the notification manager
        Integer notificationId      = ChatNotificationManager.getChatNotificationManager().getNotificationId(dialogId, data);
        Integer notificationCount   = ChatNotificationManager.getChatNotificationManager().getNotificationCount(dialogId);
        List<String> chatsToDisplay = ChatNotificationManager.getChatNotificationManager().getNotificationData(dialogId);
        Integer uniqueId            = ChatNotificationManager.getChatNotificationManager().getIncrementingNotificationId();

        Log.d("GCM Notifier", "Notification Id: "    + notificationId);
        Log.d("GCM Notifier", "Notification Count: " + notificationCount);
        Log.d("GCM Notifier", "Notification Chats: " + chatsToDisplay);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.chat_icon_2);

        // Set the intent that will fire when the user taps the notification.
//        builder.setContentIntent(pendingIntent);

        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        // Set the number of unread notifications for that chat
        builder.setNumber(notificationCount);

        /**
         *Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.chat_icon));

        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */

        /**
         * This is used to display the notification as an expanded view with multiple lines
         **/
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        StringTokenizer st = new StringTokenizer(data,":");
        String sender = st.nextToken();

        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(sender);
        // Moves events into the expanded layout
        for (int i=0; i < chatsToDisplay.size(); i++) {
            // Restrict it to a total of 5 chats
            if(i < 5) {
                StringTokenizer st2 = new StringTokenizer(chatsToDisplay.get(i),":");
                st2.nextToken();
                inboxStyle.addLine(st2.nextToken());
            } else if(i == 5) {
                inboxStyle.addLine("...");
            }
        }
        // Moves the expanded layout object into the notification object.
        builder.setStyle(inboxStyle);

        builder.setContentTitle("Scube!");
        builder.setContentText(data);
        //builder.setSubText("...");

        // END_INCLUDE (build_notification)
        // Set Group to the dialog id
        builder.setGroup(dialogId);

        /* The stack builder object will contain an artificial back stack for the started Activity.
         * This ensures that navigating backward from the Activity leads out of
         * your application to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(uniqueId, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);


        // BEGIN_INCLUDE(send_notification)
        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
        // END_INCLUDE(send_notification)
    }
}
