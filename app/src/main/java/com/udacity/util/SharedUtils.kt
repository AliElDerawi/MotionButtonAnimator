package com.udacity.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.udacity.R
import com.udacity.main.view.MainActivity

object SharedUtils {

    private var mToast: Toast? = null

    fun showToast(message: Int, duration: Int = Toast.LENGTH_LONG) {
        mToast?.cancel()
        mToast = Toast.makeText(ProjectApp.getApp().applicationContext, ProjectApp.getApp().applicationContext.getString(message), duration)
        mToast!!.show()
    }

    fun showToast(message: String?, duration: Int = Toast.LENGTH_LONG) {
        mToast?.cancel()
        mToast = Toast.makeText(ProjectApp.getApp().applicationContext, message, duration)
        mToast!!.show()
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager = ProjectApp.getApp().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun Application.createNotificationToDetailScreenWithExtra(
        title: String, message: String, downloadStatus: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constants.EXTRA_FILE_NAME, message)
            putExtra(Constants.EXTRA_FILE_STATUS, downloadStatus)
            action = "actionstring" + System.currentTimeMillis()
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )

        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, getString(R.string.text_notification_channel_id)
        ).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(message)
            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setAutoCancel(true)
            setSound(defaultSoundUri)
            setContentIntent(pendingIntent)
            addAction(
                R.mipmap.ic_launcher, getString(R.string.text_msg_check_the_status), pendingIntent
            )
            if (!isSupportsOreo { }) priority = NotificationCompat.PRIORITY_HIGH
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isReceiveNotificationPermissionGranted(mActivity: Activity): Boolean {
        return (ContextCompat.checkSelfPermission(
            mActivity.applicationContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun Activity.createNotificationChannel() {
        if (isSupportsOreo {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val mChannel = NotificationChannel(
                    getString(R.string.text_notification_channel_id),
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setShowBadge(true)
                    enableLights(true)
                    lightColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
                }
                notificationManager.createNotificationChannel(mChannel)
            }) {
        }
    }

    fun isSupportsOreo(f: () -> Unit): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            f()
            return true
        } else {
            return false
        }
    }

    fun isSupportsAndroidM(f: () -> Unit): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            f()
            return true
        } else {
            return false
        }
    }

    fun isSupportsTiramisu(f: () -> Unit): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            f()
            return true
        } else {
            return false
        }
    }
}