package hh.app.oldmanemu.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialogx.dialogs.FullScreenDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import hh.app.oldmanemu.CustomGlideImageGetter
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.R
import hh.app.oldmanemu.adapters.CommentListAdapter
import hh.app.oldmanemu.beans.CommentBean
import hh.app.oldmanemu.databinding.ActivitySingleTopicBinding
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.viewmodels.SingleTopicViewModel
import org.sufficientlysecure.htmltextview.GlideImageGetter
import org.sufficientlysecure.htmltextview.OnImageClickListener
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.AztecText
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.source.SourceViewEditText
import org.wordpress.aztec.toolbar.AztecToolbar
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener


class SingleTopicActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleTopicBinding
    private val viewModel:SingleTopicViewModel by viewModels<SingleTopicViewModel>()
    private var commentpageNum=0
    private var commentIndex=2
    private var commentListAdapter:CommentListAdapter?=null
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
            commentpageNum=it.commentPage
            binding.comment.text="评论 ${it.commentDetail.commentNum}"
            it.commentDetail.commentList?.let {
                list->
                binding.comment.setOnClickListener {
                        view->
                        setUpCommentView(url,list)
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

    private fun setUpCommentView(url:String,list:ArrayList<CommentBean>){
        FullScreenDialog.show(object :
            OnBindView<FullScreenDialog?>(R.layout.popup_comment) {
            override fun onBind(dialog: FullScreenDialog?, v: View) {
//                var visual=v.findViewById<AztecText>(R.id.visual)
//                var formatting_toolbar=v.findViewById<AztecToolbar>(R.id.formatting_toolbar)
//                var source=v.findViewById<SourceViewEditText>(R.id.source)
//                Aztec.with(visual,source,formatting_toolbar,object:IAztecToolbarClickListener{
//                    override fun onToolbarCollapseButtonClicked() {
//
//                    }
//
//                    override fun onToolbarExpandButtonClicked() {
//                    }
//
//                    override fun onToolbarFormatButtonClicked(
//                        format: ITextFormat,
//                        isKeyboardShortcut: Boolean
//                    ) {
//                    }
//
//                    override fun onToolbarHeadingButtonClicked() {
//                    }
//
//                    override fun onToolbarHtmlButtonClicked() {
//                    }
//
//                    override fun onToolbarListButtonClicked() {
//                    }
//
//                    override fun onToolbarMediaButtonClicked(): Boolean {
//                        return false
//                    }
//                })

                v.findViewById<SmartRefreshLayout>(R.id.refreshLayout).let {
//                                it.setRefreshHeader(MaterialHeader(this@SingleTopicActivity))
                    if(commentpageNum>0) {
                        it.setRefreshFooter(ClassicsFooter(this@SingleTopicActivity))
                        it.setOnLoadMoreListener { refreshlayout ->
                            loadMoreComments(it, url, commentIndex, refreshlayout)
                        }
                    }
                }
                commentListAdapter= CommentListAdapter(this@SingleTopicActivity,list)
                var commentList=v.findViewById<RecyclerView>(R.id.commentList)
                commentList.layoutManager= LinearLayoutManager(this@SingleTopicActivity).also {
                    it.stackFromEnd=true
                }
                commentList.adapter=commentListAdapter
                v.findViewById<ImageView>(R.id.sendComment).setOnClickListener {
                    var text=v.findViewById<EditText>(R.id.shortReply).text.toString()
                    var topicid=url.split("/").get(0).replace("thread-","").replace(".htm","")
                    if(text.isNotBlank())
                        viewModel.postComment(topicid,text).observe(this@SingleTopicActivity,{
                            Toast.makeText(this@SingleTopicActivity,"评论成功",Toast.LENGTH_SHORT).show()
                            dialog?.dismiss()
                        })
                    else
                        Toast.makeText(this@SingleTopicActivity,"请输入评论",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadMoreComments(smartRefreshLayout: SmartRefreshLayout,url: String,index:Int,refreshLayout: RefreshLayout){
        viewModel.getMoreComments(url,index).observe(this,{
            commentListAdapter?.UpdateList(it)
            if(commentpageNum<=commentIndex){
                smartRefreshLayout.setEnableLoadMore(false)
            }
            else
            commentIndex++
            refreshLayout.finishLoadMore(true)
        })
    }
}