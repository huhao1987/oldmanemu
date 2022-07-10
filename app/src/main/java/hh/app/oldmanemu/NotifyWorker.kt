package hh.app.oldmanemu

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import hh.app.oldmanemu.activities.MainActivity
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.retrofit.OldmanService
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class NotifyWorker(var cont:Context,workerParameters: WorkerParameters):Worker(cont,workerParameters) {
    override fun doWork(): Result {
        GetPespo
            .init()
            .create(OldmanService::class.java)
            .getHomepage()
            .execute()
            .body()?.apply {
                Jsoup.parse(string()).apply {
                    getElementsByClass("d-none unread badge badge-danger badge-pill").let {
                        if(it.isNotEmpty())
                            CreateNotification(it.get(0).text())
                    }
                }
            }
        WorkManager.getInstance(cont).cancelAllWorkByTag("oldmannotify")
        var notifyWorkRequest= OneTimeWorkRequestBuilder<NotifyWorker>()
            .addTag("oldmannotify")
            .setInitialDelay(5,TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(cont)
            .enqueue(notifyWorkRequest)
        return Result.success()
    }

    private fun CreateNotification(numMessage:String){
        var context=applicationContext
        val pendingIntent=PendingIntent.getActivity(context,0, Intent(context,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        },PendingIntent.FLAG_MUTABLE)
        var builder=NotificationCompat.Builder(context,context.getString(R.string.app_name))
            .setSmallIcon(R.drawable.main)
            .setContentTitle(context.getString(R.string.notificationtitle))
            .setContentText(context.getString(R.string.notificationtext,numMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true
            )
        with(NotificationManagerCompat.from(context)){
            notify(123,builder.build())
        }
    }
}