package hh.app.oldmanemu.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import hh.app.oldmanemu.CustomGlideImageGetter
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.PopupViews.CommentPopup
import hh.app.oldmanemu.R
import hh.app.oldmanemu.databinding.ActivitySingleTopicBinding
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.viewmodels.SingleTopicViewModel
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan
import org.sufficientlysecure.htmltextview.GlideImageGetter
import org.sufficientlysecure.htmltextview.OnClickATagListener
import org.sufficientlysecure.htmltextview.OnImageClickListener

class SingleTopicActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleTopicBinding
    private val viewModel:SingleTopicViewModel by viewModels<SingleTopicViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topicContent.let {
            textView->
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            textView.setListIndentPx(metrics.density * 10)

            textView.blockQuoteBackgroundColor = resources.getColor(R.color.purple_200)
            textView.blockQuoteStripColor = resources.getColor(R.color.teal_700)

        }
        var url=intent.getStringExtra("url")
        if(url!=null)
        viewModel.getSingleTopic(url).observe(this,{
            binding.topicTitle.text=it.title
            binding.backBtn.setOnClickListener {
                onBackPressed()
            }
            binding.posterName.text=it.user.userName
            GlideApp.with(this)
                .load(GetPespo.baseUrl+it.user.avatar)
                .into(binding.posterAvatar)
            binding.posterDetail.text=it.user.level
            binding.postTime.text=it.postTime
            binding.comment.text="评论 ${it.commentDetail.commentNum}"
            it.commentDetail.commentList?.let {
                list->
                binding.comment.setOnClickListener {
                        view->
                    XPopup.Builder(this)
                        .popupAnimation(PopupAnimation.ScrollAlphaFromBottom)
                        .isCenterHorizontal(true)
                        .asCustom(CommentPopup(this,list))
                        .show()
                }
            }

            binding.topicContent.setHtml(it.content, CustomGlideImageGetter(binding.topicContent,true),object:OnImageClickListener{
                override fun onClick(image: String?) {
                    startActivity(Intent(this@SingleTopicActivity,ImageViewActivity::class.java).also{
                        it.putExtra("url",image)
                    })
                }
            })
        })
        else {
            Toast.makeText(this,"异常错误，请重试",Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}