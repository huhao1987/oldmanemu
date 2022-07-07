package hh.app.oldmanemu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hh.app.oldmanemu.GlideApp
import hh.app.oldmanemu.databinding.ActivityImageViewBinding

class ImageViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var img=intent.getStringExtra("url")
        if(img!=null)
            GlideApp.with(this).load(img).into(binding.imageView)
    }
}