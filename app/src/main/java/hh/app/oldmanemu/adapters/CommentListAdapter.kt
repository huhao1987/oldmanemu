package hh.app.oldmanemu.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import hh.app.oldmanemu.CustomGlideImageGetter
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.R
import hh.app.oldmanemu.activities.ImageViewActivity
import hh.app.oldmanemu.beans.CommentBean
import hh.app.oldmanemu.beans.TopicBean
import hh.app.oldmanemu.databinding.RowCommentBinding
import hh.app.oldmanemu.databinding.RowTopicBinding
import hh.app.oldmanemu.retrofit.GetPespo
import org.sufficientlysecure.htmltextview.OnClickATagListener
import org.sufficientlysecure.htmltextview.OnImageClickListener

class CommentListAdapter(var context:Context, var commentbeanList: ArrayList<CommentBean>, var onClickListener: onCommentClickListener?=null):
    RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val topicTitle=binding.topicTitle
        val posterAvatar=binding.posterAvatar
        val poster=binding.poster
        val replyNum=binding.replyNum
        val favNum=binding.favNum
        val replyDetail=binding.replyDetail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var comment=commentbeanList[position]
        holder.topicTitle.blockQuoteBackgroundColor=context.resources.getColor(R.color.lightgray)
        holder.topicTitle.blockQuoteStripColor=context.resources.getColor(R.color.lightgray)
        holder.topicTitle.setOnClickATagListener(object:OnClickATagListener{
            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
                Log.d("thetag:::",spannedText+" "+href)
                return true
            }

        })
        holder.topicTitle.setHtml(comment.commentContent, CustomGlideImageGetter(holder.topicTitle,true),object:
            OnImageClickListener {
            override fun onClick(image: String?) {
                startActivity(context,Intent(context,ImageViewActivity::class.java).also {
                    it.putExtra("url",image)
                },null)
            }
        })
        GlideApp.with(context)
            .load(GetPespo.baseUrl+comment.user?.avatar)
            .into(holder.posterAvatar)

        holder.poster.text=comment.user?.userName
//        holder.favNum.text="点赞数:"+comment.favNum
//        holder.replyNum.text="回复数:"+comment.replyNum
        holder.replyDetail.text=comment.user?.level

        holder.itemView.setOnClickListener {
            onClickListener?.onclick(position,comment)
        }
    }
    override fun getItemCount() = commentbeanList.size

}
interface onCommentClickListener{
    fun onclick(position: Int,commentBean: CommentBean)
}