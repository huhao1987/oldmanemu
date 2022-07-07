package hh.app.oldmanemu.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.beans.TopicBean
import hh.app.oldmanemu.databinding.RowTopicBinding
import hh.app.oldmanemu.retrofit.GetPespo

class TopicListAdapter(var context:Context,var topiclist: ArrayList<TopicBean>, var onClickListener: onClickListener?=null):
    RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        val topicTitle=binding.topicTitle
        val posterAvatar=binding.posterAvatar
        val poster=binding.poster
        val replyNum=binding.replyNum
        val favNum=binding.favNum
        val replyDetail=binding.replyDetail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var topic=topiclist[position]
        holder.topicTitle.text=topic.topic
        GlideApp.with(context)
            .load(GetPespo.baseUrl+topic.postAvatar)
            .into(holder.posterAvatar)

        holder.poster.text=topic.postDetail
        holder.favNum.text="点赞数:"+topic.favNum
        holder.replyNum.text="回复数:"+topic.replyNum
        holder.replyDetail.text=topic.replyDetail

        holder.itemView.setOnClickListener {
            onClickListener?.onclick(position,topic)
        }
    }
    override fun getItemCount() = topiclist.size

}
interface onClickListener{
    fun onclick(position: Int,topicBean: TopicBean)
}