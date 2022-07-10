package hh.app.oldmanemu

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kongzue.dialogx.DialogX
import java.util.concurrent.TimeUnit

class OldmanApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DialogX.init(this)
        DialogX.globalTheme=DialogX.THEME.AUTO
        createNotificationChannel()
        initNotification()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.notifationdes)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(name, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun initNotification(){
        WorkManager.getInstance(this).cancelAllWorkByTag("oldmannotify")
        var notifyWorkRequest= OneTimeWorkRequestBuilder<NotifyWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .addTag("oldmannotify")
            .build()
        WorkManager.getInstance(this)
            .enqueue(notifyWorkRequest)
    }
}