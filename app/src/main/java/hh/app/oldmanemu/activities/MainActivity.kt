package hh.app.oldmanemu.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.NotifyWorker
import hh.app.oldmanemu.Utils.SharePerferencesUtil
import hh.app.oldmanemu.adapters.TopicListAdapter
import hh.app.oldmanemu.adapters.onClickListener
import hh.app.oldmanemu.beans.TopicBean
import hh.app.oldmanemu.databinding.ActivityMainBinding
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels<MainViewModel>()
    private var adapter: TopicListAdapter? = null
    private var pageIndex=1
    private var resultContract = registerForActivityResult(LoginActivityResultContract()) {
        if (it.contains("bbs_token")) {
            SharePerferencesUtil.sharedPreferences?.edit {
                putString("cookie", it)
                NotifyWorker.SetupNotifyWorker(this@MainActivity, it)
            }
            initViews()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        SharePerferencesUtil.shareIntance(this)
        setUpLogin()
        loadHomepage()
        binding.refreshLayout.let {
            it.setRefreshHeader(MaterialHeader(this))
            it.setRefreshFooter(ClassicsFooter(this))
            it.setOnRefreshListener{
                refreshlayout->
                    loadHomepage(refreshlayout)
            }

            it.setOnLoadMoreListener {
                refreshlayout->
                loadmorepage(pageIndex,refreshlayout)
            }
        }
    }

    private fun loadmorepage(pageIndex:Int,refreshLayout: RefreshLayout?=null){
        viewModel.getMoreData(pageIndex).observe(this,{
                this.pageIndex++
                adapter?.UpdateList(it)
                refreshLayout?.finishLoadMore(true)
        })
    }
    private fun loadHomepage(refreshLayout: RefreshLayout?=null){
        pageIndex=1
        viewModel.getHomePageData().observe(this, {
            pageIndex++
            GlideApp.with(this)
                .load(GetPespo.baseUrl + it.userInfo?.avatar)
                .into(binding.userAvatar)
            binding.userName.text = it.userInfo?.userName
            if (it.notifyNum > 0) {
                binding.notifynum.text = it.notifyNum.toString()
                binding.notifyarea.visibility = View.VISIBLE
            } else {
                binding.notifynum.text = ""
            }

            adapter = TopicListAdapter(this, it.topicList!!, object : onClickListener {
                override fun onclick(position: Int, topicBean: TopicBean) {
                    startActivity(Intent(this@MainActivity, SingleTopicActivity::class.java).also {
                        it.putExtra("url", topicBean.link)
                    })
                }
            })
            binding.topicList.layoutManager = LinearLayoutManager(this)
            binding.topicList.adapter = adapter
            refreshLayout?.finishRefresh(true)
        })
    }

    private fun setUpLogin() {
        if (SharePerferencesUtil.sharedPreferences!!.getString("cookie", null) == null) {
            binding.loginBtn.text = "登录账号"
            binding.notifyarea.visibility = View.GONE
            binding.loginBtn.setOnClickListener {
                resultContract.launch("")
                initViews()
            }
        }
        SharePerferencesUtil.sharedPreferences?.getString("cookie", null)?.apply {
            NotifyWorker.SetupNotifyWorker(this@MainActivity, this)
            binding.loginBtn.text = "登出账号"
            binding.notifyarea.visibility = View.VISIBLE
            binding.loginBtn.setOnClickListener {
                SharePerferencesUtil.sharedPreferences?.edit {
                    putString("cookie", null)
                }
                initViews()
            }
        }
    }
}