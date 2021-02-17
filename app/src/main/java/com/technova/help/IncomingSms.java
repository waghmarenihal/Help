package com.technova.help;

/**
 * Created by Training on 8/10/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class IncomingSms extends BroadcastReceiver {

    public static String HelpAskerName="",Help="";
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    public static double latt = 0.0f, longi = 0.0f;

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    String emergency = message.substring(message.indexOf("help.") + 6, message.indexOf("Please"));
                    //Toast.makeText(context, emergency, Toast.LENGTH_LONG).show();
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    if (message.indexOf("I need your help.") > 1) {
                        String senderName = getContactName(senderNum, context);
                        if (!senderName.equals("")) {
                            NotificationCompat.Builder builder =
                                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.help_icon)
                                            .setContentTitle(senderName)
                                            .setContentText(emergency);
                            Help=emergency;
                            HelpAskerName=senderName;
                            builder.setAutoCancel(true);
                            String cordinates = message.substring(message.indexOf("place/") + 6);
                            String lat = cordinates.substring(0, cordinates.indexOf(","));
                            String lon = cordinates.substring(lat.length() + 1);
                            latt = Double.parseDouble(lat);
                            longi = Double.parseDouble(lon);

                            Intent notificationIntent = new Intent(context, MapsActivity.class);
                            notificationIntent.putExtra("lattitude", latt);
                            notificationIntent.putExtra("logitude", longi);
                            notificationIntent.putExtra("name",senderName);
                            notificationIntent.putExtra("emergency",emergency);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(contentIntent);

                            // Add as notification
                            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(0, builder.build());
                        } else {
                            NotificationCompat.Builder builder =
                                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.help_icon)
                                            .setContentTitle(senderNum)
                                            .setContentText(emergency);
                            builder.setAutoCancel(true);

                            Intent notificationIntent = new Intent(context, HomeActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(contentIntent);

                            // Add as notification
                            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(0, builder.build());
                        }
                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                    //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

}