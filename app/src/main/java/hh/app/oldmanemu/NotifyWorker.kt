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
import androidx.work.*
import com.blankj.utilcode.util.AppUtils
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.DialogXStyle
import hh.app.oldmanemu.activities.MainActivity
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.retrofit.OldmanService
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class NotifyWorker(var cont: Context, workerParameters: WorkerParameters) :
    Worker(cont, workerParameters) {
    companion object {
        fun SetupNotifyWorker(
            context: Context,
            cookie: String?,
            duration: Long = 15,
            timeUnit: TimeUnit = TimeUnit.MINUTES
        ) {
            WorkManager.getInstance(context).cancelAllWorkByTag("oldmannotify")
            var cookiedata = workDataOf("cookie" to cookie)
            var notifyWorkRequest = OneTimeWorkRequestBuilder<NotifyWorker>()
                .addTag("oldmannotify")
                .setInputData(cookiedata)
                .setInitialDelay(duration, timeUnit)
                .build()
            WorkManager.getInstance(context)
                .enqueue(notifyWorkRequest)
        }
    }

    override fun doWork(): Result {
        var cookie = inputData.getString("cookie")
        var unreadNum = 0
        GetPespo
            .init(cookie)
            .create(OldmanService::class.java)
            .getNotification().execute()
            .body()?.apply {
                Jsoup.parse(string()).apply {
                    getElementsByClass("notice media text-small ").forEach {
                        var unReads =
                            it.getElementsByClass("readbtn btn btn-small btn-light ml-2 text-muted")
                        if (unReads.isNotEmpty())
                            unreadNum++
                    }
                    if (unreadNum > 0) {
                        if (!AppUtils.isAppForeground()) CreateNotification(unreadNum.toString())
                        PopTip
                            .show(
                                R.drawable.ic_notification,
                                cont.getString(R.string.notificationtext, "2")
                            ).also {
                                it.align = DialogXStyle.PopTipSettings.ALIGN.TOP
                            }
                    }
                }
            }
//            .getHomepage()
//            .execute()
//            .body()?.apply {
//                Jsoup.parse(string()).apply {
//                    getElementsByClass("d-none unread badge badge-danger badge-pill").let {
//                        if (it.isNotEmpty())
//                            CreateNotification(it.get(0).text())
//                    }
//                }
//            }
        SetupNotifyWorker(cont, cookie)
        return Result.success()
    }

    private fun CreateNotification(numMessage: String) {
        if (numMessage.toInt() > 0) {
            var context = applicationContext
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                PendingIntent.FLAG_MUTABLE
            )
            var builder = NotificationCompat.Builder(context, context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.main)
                .setContentTitle(context.getString(R.string.notificationtitle))
                .setContentText(context.getString(R.string.notificationtext, numMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(
                    true
                )
            with(NotificationManagerCompat.from(context)) {
                notify(123, builder.build())
            }
        }
    }
}