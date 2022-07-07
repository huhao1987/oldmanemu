package hh.app.oldmanemu.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import hh.app.oldmanemu.databinding.ActivityLoginBinding
import hh.app.oldmanemu.retrofit.GetPespo

/**
 * Login page.
 * @author Hao
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews(){
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        var cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
        binding.loginPage.clearCache(false)
        binding.loginPage.settings.apply {
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        binding.loginPage.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                hideBottom()
                var cookieStr = cookieManager.getCookie(url)
                if (cookieStr.contains("bbs_token")) {
                    GetPespo.cookie = cookieStr
                    var intent = Intent(this@LoginActivity, MainActivity::class.java).also {
                        it.putExtra("cookie", cookieStr)
                    }
                    setResult(200, intent)
                    finish()
                }
                super.onPageFinished(view, url)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                Log.d("theerror:", "" + error?.errorCode + error?.description)
                super.onReceivedError(view, request, error)
            }
        }
        binding.loginPage.loadUrl("https://bbs.oldmanemu.net/user-login.htm")
    }

    private fun hideBottom() {
        try {
            val javascript = ("javascript:function hideBottom() { "
                    + "document.getElementsByClassName('navbar navbar-expand-lg navbar-dark bg-dark')[0].style.display='none'"
                    + "}")

            binding.loginPage.loadUrl(javascript)
            binding.loginPage.loadUrl("javascript:hideBottom();")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}