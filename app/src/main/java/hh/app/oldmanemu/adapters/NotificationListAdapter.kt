package hh.app.oldmanemu.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hh.app.oldmanemu.CustomGlideImageGetter
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.R
import hh.app.oldmanemu.activities.SingleTopicActivity
import hh.app.oldmanemu.beans.NotificationBean
import hh.app.oldmanemu.beans.TopicBean
import hh.app.oldmanemu.databinding.RowNotificationBinding
import hh.app.oldmanemu.retrofit.GetPespo
import org.sufficientlysecure.htmltextview.HtmlTextView

class NotificationListAdapter(var context: Context, list: ArrayList<NotificationBean>,onNotificationReadListener: OnNotificationReadListener?=null) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    private var Notificationlist = list
    private var onNotificationReadListener=onNotificationReadListener

    inner class ViewHolder(binding: RowNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val notificationContext = binding.notificationContext
        val postTime=binding.posttime
        val poster=binding.poster
        val avatar=binding.avatar
        val setRead=binding.setRead
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var notification = Notificationlist[position]
        holder.notificationContext.setHtml(
            notification.NotifyContent,
            CustomGlideImageGetter(holder.notificationContext, true), null
        )
        holder.poster.text=notification.user?.userName
        holder.postTime.text=notification.user?.postTime
        GlideApp.with(context)
            .load(GetPespo.baseUrl+notification.user?.avatar)
            .into(holder.avatar)
        holder.notificationContext.setOnClickATagListener { widget, spannedText, href ->
            context.startActivity(Intent(context, SingleTopicActivity::class.java).also {
                it.putExtra("url", href)
            })
            true
        }
        holder.setRead.setOnClickListener {
            onNotificationReadListener?.onRead(position,notification)
        }
    }

    override fun getItemCount() = Notificationlist.size
    fun UpdateList(notifyList: ArrayList<NotificationBean>) {
        this.Notificationlist = ArrayList(this.Notificationlist + notifyList)
        notifyDataSetChanged()
    }
}
interface OnNotificationReadListener{
    fun onRead(position: Int,notificationBean: NotificationBean)
}