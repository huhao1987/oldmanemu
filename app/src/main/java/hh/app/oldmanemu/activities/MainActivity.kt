package hh.app.oldmanemu.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.matrixxun.starry.badgetextview.MenuItemBadge
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.iconUrl
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.removeAllItems
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.NotifyWorker
import hh.app.oldmanemu.R
import hh.app.oldmanemu.Utils.SharePerferencesUtil
import hh.app.oldmanemu.adapters.TopicListAdapter
import hh.app.oldmanemu.adapters.onClickListener
import hh.app.oldmanemu.beans.HomePageBean
import hh.app.oldmanemu.beans.TopicBean
import hh.app.oldmanemu.databinding.ActivityMainBinding
import hh.app.oldmanemu.retrofit.GetPespo
import hh.app.oldmanemu.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels<MainViewModel>()
    private var adapter: TopicListAdapter? = null
    private var pageIndex = 1
    private var pageNum = 0
    private var accountHeaderView: AccountHeaderView? = null
    private var homePage:PrimaryDrawerItem?=null
    private var chatPage:PrimaryDrawerItem?=null
    private var consolePage:PrimaryDrawerItem?=null
    private var resourcePage:PrimaryDrawerItem?=null

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
        setUpSlidemenu()

        binding.notifyarea.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    private fun setUpPage(position: Int) {
        when (position) {
            1 -> {
                loadpage(1)
                binding.refreshLayout.let {
                    it.setRefreshHeader(MaterialHeader(this))
                    it.setRefreshFooter(ClassicsFooter(this))
                    it.setOnRefreshListener { refreshlayout ->
                        loadpage(1, refreshlayout)
                    }
                    it.setOnLoadMoreListener { refreshlayout ->
                        loadmorepage("index", pageIndex, refreshlayout)
                    }
                }
            }
            2 -> {
                loadpage(2)
                binding.refreshLayout.let {
                    it.setRefreshHeader(MaterialHeader(this))
                    it.setRefreshFooter(ClassicsFooter(this))
                    it.setOnRefreshListener { refreshlayout ->
                        loadpage(2, refreshlayout)
                    }
                    it.setOnLoadMoreListener { refreshlayout ->
                        loadmorepage("forum-1", pageIndex, refreshlayout)
                    }
                }
            }
            3 -> {
                loadpage(3)
                binding.refreshLayout.let {
                    it.setRefreshHeader(MaterialHeader(this))
                    it.setRefreshFooter(ClassicsFooter(this))
                    it.setOnRefreshListener { refreshlayout ->
                        loadpage(3, refreshlayout)
                    }
                    it.setOnLoadMoreListener { refreshlayout ->
                        loadmorepage("forum-2", pageIndex, refreshlayout)
                    }
                }
            }
            4 -> {
                loadpage(4)
                binding.refreshLayout.let {
                    it.setRefreshHeader(MaterialHeader(this))
                    it.setRefreshFooter(ClassicsFooter(this))
                    it.setOnRefreshListener { refreshlayout ->
                        loadpage(4, refreshlayout)
                    }
                    it.setOnLoadMoreListener { refreshlayout ->
                        loadmorepage("forum-3", pageIndex, refreshlayout)
                    }
                }
            }
        }

    }

    private fun setUpSlidemenu() {
        binding.slideMenu.headerView=null
        binding.slideMenu.accountHeader=null
        binding.slideMenu.removeAllItems()
        setUpPage(1)
        if(homePage==null)
        homePage = PrimaryDrawerItem().apply {
            nameRes = R.string.homestr
            identifier = 1
        }
        if(chatPage==null)
         chatPage = PrimaryDrawerItem().apply {
            nameRes = R.string.chatstr
            identifier = 2
        }
        if(consolePage==null)
         consolePage = PrimaryDrawerItem().apply {
            nameRes = R.string.consolestr
            identifier = 3
        }
        if(resourcePage==null)
         resourcePage = PrimaryDrawerItem().apply {
            nameRes = R.string.resorucestr
            identifier = 4
        }
        binding.slideMenu.itemAdapter.add(
            homePage!!,
            chatPage!!,
            consolePage!!,
            resourcePage!!
        )
        if(binding.slideMenu.onDrawerItemClickListener==null) {
            binding.slideMenu.onDrawerItemClickListener = { v, drawerItem, position ->
                setUpPage(drawerItem.identifier.toInt())
                false
            }
            binding.slideMenu.setSelection(1)
        }


    }

    private fun loadpage(position: Int, refreshLayout: RefreshLayout? = null) {
        pageIndex = 1
        viewModel.getHomePageData(position).observe(this, {
            pageIndex++
            pageNum = it.endPage
            setUpLogin(it)

            if (it.notifyNum > 0) {
                binding.notifynum.setBadgeCount(it.notifyNum.toString())
                binding.notifyarea.visibility = View.VISIBLE
            } else {
                binding.notifynum.setText("")
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

    private fun loadmorepage(url: String, pageIndex: Int, refreshLayout: RefreshLayout? = null) {
        viewModel.getMoreData(url, pageIndex).observe(this, {
            if (pageIndex < pageNum) {
                this.pageIndex++
                adapter?.UpdateList(it)
                refreshLayout?.finishLoadMore(true)
            } else {
                refreshLayout?.finishLoadMore(true)
                refreshLayout?.setEnableLoadMore(false)
            }

        })
    }

    private fun setUpLogin(homePageBean: HomePageBean) {
        binding.slideMenu.headerView=null
        if (SharePerferencesUtil.sharedPreferences!!.getString("cookie", null) == null) {
            AccountHeaderView(this).apply {
                findViewById<Button>(R.id.materian_drawer_button).let {
                    it.text="登录账号"
                    it.setOnClickListener {
                        resultContract.launch("")
                        initViews()
                    }
                }
                addProfiles(ProfileDrawerItem().apply {
                    nameText = "未登录"
                    iconRes = R.drawable.main
                })
                selectionListEnabledForSingleProfile = false
                attachToSliderView(binding.slideMenu)
            }

        }
        SharePerferencesUtil.sharedPreferences?.getString("cookie", null)?.apply {
            NotifyWorker.SetupNotifyWorker(this@MainActivity, this)
            binding.sign.setOnClickListener {
                viewModel.postSign().observe(this@MainActivity,{
                    if(it.isSigned)
                        Toast.makeText(this@MainActivity,"签到成功",Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this@MainActivity,"您今天已签到过了",Toast.LENGTH_SHORT).show()

                })
            }
            AccountHeaderView(this@MainActivity).apply {
                var login_Btn=findViewById<Button>(R.id.materian_drawer_button)
                login_Btn?.text = "登出账号"
                binding.notifyarea.visibility = View.GONE
                login_Btn?.setOnClickListener {
                    SharePerferencesUtil.sharedPreferences?.edit {
                        putString("cookie", null)
                    }
                    initViews()
                }
                addProfiles(ProfileDrawerItem().apply {
                    binding.slideMenu.accountHeader?.findViewById<AppCompatTextView>(R.id.drawer_level)?.text=homePageBean.userInfo?.level?:""
                    nameText = homePageBean.userInfo?.userName?:getString(R.string.notlogin)
                    if (homePageBean.userInfo?.avatar == "") iconRes = R.drawable.main
                    else
                        iconUrl =  homePageBean.userInfo?.avatar?:""
                })
                selectionListEnabledForSingleProfile = false
                attachToSliderView(binding.slideMenu)
            }
        }
    }
}