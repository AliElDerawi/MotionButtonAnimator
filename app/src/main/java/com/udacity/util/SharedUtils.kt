package com.udacity.util

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.udacity.R
import com.udacity.detail.DetailActivity

object SharedUtils {

    private var mToast: Toast? = null


    fun showToast(message: Int, duration: Int = Toast.LENGTH_LONG) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(
            ProjectApp.getInstance()!!.applicationContext,
            ProjectApp.getInstance()!!.applicationContext.getString(message),
            duration
        )
        mToast!!.show()
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            (ProjectApp.getInstance()!!.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
    }

    fun Activity.createNotificationToDetailScreenWithExtra(
        title: String, message: String, downloadStatus: String
    ) {

        val intent = Intent(this, DetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)



        intent.putExtra(Constants.EXTRA_FILE_NAME, message)
        intent.putExtra(Constants.EXTRA_FILE_STATUS, downloadStatus)

        intent.setAction("actionstring" + System.currentTimeMillis())


        val pendingIntent = PendingIntent.getActivity(
            this, 0,  /* Request code */intent, PendingIntent.FLAG_MUTABLE
        )


        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                getString(R.string.text_notification_channel_id),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            mChannel.lightColor = ResourcesCompat.getColor(
                resources, R.color.colorPrimary, null
            );
            notificationManager.createNotificationChannel(mChannel)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, getString(R.string.text_notification_channel_id)
        ).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
            .setContentText(message).setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent)
            .addAction(
                R.mipmap.ic_launcher,
                getString(R.string.text_msg_check_the_status),
                pendingIntent
            )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
        }

        notificationManager.notify(
            System.currentTimeMillis().toInt(),  /* ID of notification */
            notificationBuilder.build()
        )
    }

    fun isReceiveNotificationPermissionGranted(mActivity: Activity): Boolean {
        return (ContextCompat.checkSelfPermission(
            mActivity.applicationContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun Activity.createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val mChannel = NotificationChannel(
                getString(R.string.text_notification_channel_id),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            mChannel.lightColor = ResourcesCompat.getColor(
                resources, R.color.colorPrimary, null
            );
            if (notificationManager != null) notificationManager.createNotificationChannel(mChannel)
        }
    }


}