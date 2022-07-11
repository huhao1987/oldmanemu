package hh.app.oldmanemu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hh.app.oldmanemu.R
import hh.app.oldmanemu.databinding.ActivityEditorBinding
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Aztec.with(binding.aztec,binding.source,binding.formattingToolbar,object: IAztecToolbarClickListener {
            override fun onToolbarCollapseButtonClicked() {

            }

            override fun onToolbarExpandButtonClicked() {
            }

            override fun onToolbarFormatButtonClicked(
                format: ITextFormat,
                isKeyboardShortcut: Boolean
            ) {
            }

            override fun onToolbarHeadingButtonClicked() {
            }

            override fun onToolbarHtmlButtonClicked() {
            }

            override fun onToolbarListButtonClicked() {
            }

            override fun onToolbarMediaButtonClicked(): Boolean {
                return false
            }
        })
    }
}