package hh.app.oldmanemu

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kongzue.dialogx.DialogX
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import java.util.concurrent.TimeUnit

class OldmanApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DialogX.init(this)
        DialogX.globalTheme=DialogX.THEME.AUTO
        createNotificationChannel()
        initDrawerImageLoader()
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

    private fun initDrawerImageLoader(){
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String?) {
                GlideApp.with(imageView.context)
                    .load(uri)
                    .placeholder(placeholder).into(imageView)
            }
            override fun cancel(imageView: ImageView) {
                GlideApp.with(imageView.context).clear(imageView)
            }
        })
    }
}