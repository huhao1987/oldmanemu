package hh.app.oldmanemu.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.DialogXStyle
import hh.app.oldmanemu.R
import hh.app.oldmanemu.adapters.NotificationListAdapter
import hh.app.oldmanemu.adapters.OnNotificationReadListener
import hh.app.oldmanemu.beans.NotificationBean
import hh.app.oldmanemu.databinding.ActivityNotificationBinding
import hh.app.oldmanemu.viewmodels.NotificationViewModel

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private val viewModel: NotificationViewModel by viewModels<NotificationViewModel>()
    private var notificationListAdapter:NotificationListAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    private fun initViews(){
        viewModel.getNotificationData().observe(this,{
            it.notificationList?.let {
                list->
                notificationListAdapter= NotificationListAdapter(this,list,object:OnNotificationReadListener{
                    override fun onRead(position: Int, notificationBean: NotificationBean) {
                        viewModel.PostNotifyRead(notificationBean.dataid).observe(this@NotificationActivity,{
                            if(it)
                                initViews()
                            else Toast.makeText(this@NotificationActivity,"错误",Toast.LENGTH_SHORT).show()
                        })
                    }
                })
                binding.notificationList.layoutManager = LinearLayoutManager(this)
                binding.notificationList.adapter = notificationListAdapter
            }

        })
    }
}