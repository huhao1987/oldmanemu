package hh.app.oldmanemu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import hh.app.oldmanemu.GlideApp
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
    private var resultContract=registerForActivityResult(LoginActivityResultContract()) {
        if (it.contains("bbs_token")) {
            SharePerferencesUtil.sharedPreferences?.edit {
                putString("cookie", it)
            }
            setUpLogin()
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
        viewModel.homePageData.observe(this, {
            GlideApp.with(this)
                .load(GetPespo.baseUrl+it.userInfo?.avatar)
                .into(binding.userAvatar)
            binding.userName.text=it.userInfo?.userName
            adapter = TopicListAdapter(this, it.topicList!!, object : onClickListener {
                override fun onclick(position: Int, topicBean: TopicBean) {
                    startActivity(Intent(this@MainActivity,SingleTopicActivity::class.java).also {
                        it.putExtra("url",topicBean.link)
                    })
                }
            })
            binding.topicList.layoutManager = LinearLayoutManager(this)
            binding.topicList.adapter = adapter
        })
    }

    private fun setUpLogin() {

        if (SharePerferencesUtil.sharedPreferences!!.getString("cookie", null) == null) {
            binding.loginBtn.text = "登录账号"
            binding.loginBtn.setOnClickListener {
                resultContract.launch("")
            }
        }
        SharePerferencesUtil.sharedPreferences?.getString("cookie", null)?.apply {
            binding.loginBtn.text = "登出账号"
            binding.loginBtn.setOnClickListener {
                SharePerferencesUtil.sharedPreferences?.edit {
                    putString("cookie", null)
                }
                setUpLogin()
            }
        }
    }
}