package hh.app.oldmanemu.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialogx.dialogs.FullScreenDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import hh.app.oldmanemu.CustomGlideImageGetter
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.R
import hh.app.oldmanemu.adapters.CommentListAdapter
import hh.app.oldmanemu.databinding.ActivitySingleTopicBinding
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.viewmodels.SingleTopicViewModel
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
                    FullScreenDialog.show(object :
                        OnBindView<FullScreenDialog?>(R.layout.popup_comment) {
                        override fun onBind(dialog: FullScreenDialog?, v: View) {
                            v.findViewById<SmartRefreshLayout>(R.id.refreshLayout).let {
                                it.setRefreshHeader(MaterialHeader(this@SingleTopicActivity))
                                it.setRefreshFooter(ClassicsFooter(this@SingleTopicActivity))
                                it.setOnRefreshListener{
                                        refreshlayout->
                                    loadHomepage(refreshlayout)
                                }

                                it.setOnLoadMoreListener {
                                        refreshlayout->
                                    loadmorepage(pageIndex,refreshlayout)
                                }
                            }
                            var commentList=v.findViewById<RecyclerView>(R.id.commentList)
                            var commentListAdapter= CommentListAdapter(this@SingleTopicActivity,list)
                            commentList.layoutManager= LinearLayoutManager(this@SingleTopicActivity)
                            commentList.adapter=commentListAdapter
                        }
                    })
                }
            }
            binding.topicContent.setOnClickATagListener { widget, spannedText, href ->
                Log.d("thetag::",spannedText+" "+href)
                true
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