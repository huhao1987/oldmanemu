package hh.app.oldmanemu.activities

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class LoginActivityResultContract: ActivityResultContract<String, String>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context,LoginActivity::class.java).putExtra("params",input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        if(resultCode==200)
            intent?.let {
                return it.getStringExtra("cookie")?:""
            }
        return ""
    }
}