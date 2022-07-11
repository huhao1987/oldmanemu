package hh.app.oldmanemu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.edit
import hh.app.oldmanemu.NotifyWorker
import hh.app.oldmanemu.Utils.SharePerferencesUtil
import hh.app.oldmanemu.databinding.ActivityNotificationsettingBinding
import java.util.concurrent.TimeUnit

class NotificationSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsettingBinding
    private var time=15
    private var timeSp= TimeUnit.MINUTES
    private var timePos=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    fun initViews(){
        var storeTime=SharePerferencesUtil.shareIntance(this).getLong("time",15)
        var storeTimesp=SharePerferencesUtil.shareIntance(this).getInt("timeUnit",0)
        binding.notificationTimepicker.setText(storeTime.toString())
        binding.timeSpinner.setSelection(storeTimesp)
        binding.timeSpinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                timePos=position
                when(position){
                    0->timeSp=TimeUnit.MINUTES
                    1->timeSp=TimeUnit.SECONDS
                    2->timeSp=TimeUnit.HOURS
                    3->timeSp=TimeUnit.DAYS
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.saveBtn.setOnClickListener {
            var finTime=binding.notificationTimepicker.text.toString().toLong()
            if(finTime==0L)
                Toast.makeText(this,"时间必须大于0",Toast.LENGTH_SHORT).show()
            else {
                var cookie=SharePerferencesUtil.sharedPreferences!!.getString("cookie", null)
                if(cookie!=null) {
                    NotifyWorker.SetupNotifyWorker(this, cookie, finTime, timeSp)
                    SharePerferencesUtil.shareIntance(this).edit {
                        putLong("time",finTime)
                        putInt("timeUnit",timePos)
                    }
                    Toast.makeText(this,"通知已设置",Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show()

            }
        }
    }
}