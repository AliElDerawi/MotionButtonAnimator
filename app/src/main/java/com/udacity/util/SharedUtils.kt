package com.udacity.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.udacity.R
import com.udacity.main.view.MainActivity

object SharedUtils {

    private var mToast: Toast? = null

    fun Activity.showToast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
        mToast?.cancel()
        mToast = Toast.makeText(
            ProjectApp.getApp().applicationContext,
            getString(message),
            duration
        )
        mToast!!.show()
    }

    fun showToast(message: String?, duration: Int = Toast.LENGTH_LONG) {
        mToast?.cancel()
        mToast = Toast.makeText(ProjectApp.getApp().applicationContext, message, duration)
        mToast!!.show()
    }

    fun Activity.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(findViewById(android.R.id.content), message, duration).show()
    }

    fun Activity.showSnackBar(message: Int, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(findViewById(android.R.id.content), getString(message), duration).show()
    }

    fun Fragment.setTitle(title: String) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }
    }

    fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                bool
            )
        }
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            ProjectApp.getApp().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun isSupportsTiramisu(f: () -> Unit): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            f()
            return true
        } else {
            return false
        }
    }

    fun isSupportsTiramisu(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun DownloadManager.download(downloadUrl: String?, title: String, description: String): Long {
        if (downloadUrl.isNullOrEmpty()) return -1
        val request = DownloadManager.Request(downloadUrl.toUri()).apply {
            setTitle(title)
            setDescription(description)
            setRequiresCharging(false)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        return enqueue(request)
    }

    fun View.applyWindowsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun Activity.setStatusBarColorAndStyle(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Set the listener to customize the status bar area
            window.decorView.apply {
                setOnApplyWindowInsetsListener { v, insets ->
                    val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                    // Create a view for the status bar background
                    val statusBarBackground = View(this@setStatusBarColorAndStyle).apply {
                        setBackgroundColor(color)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            statusBarInsets.top // Height matches the status bar inset
                        )
                    }
                    // Add the view to the decorView
                    (this as ViewGroup).apply {
                        if (statusBarBackground.parent == null) {
                            addView(statusBarBackground)
                        }
                    }
                    insets
                }
                // Request insets to trigger the listener
                requestApplyInsets()
            }

            if (color == Color.BLACK || ColorUtils.calculateLuminance(color) < 0.5) {
                // If the color is dark, use light icons
                window.insetsController?.apply {
                    setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                    setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            } else {
                // If the color is light, use dark icons
                window.insetsController?.apply {
                    setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                    setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            }

        } else {
            // Fallback for older versions
            window.apply {
                statusBarColor = color
                navigationBarColor = color
                decorView.systemUiVisibility = when {
                    ColorUtils.calculateLuminance(statusBarColor) >= 0.5 -> {
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                    ColorUtils.calculateLuminance(navigationBarColor) >= 0.5 -> {
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                    else -> 0 // No flags for dark icons
                }
            }
        }
    }

    fun Context.getCompatColor(color: Int): Int {
        return ResourcesCompat.getColor(resources, color, null)
    }

}