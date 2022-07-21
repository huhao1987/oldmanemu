package hh.app.oldmanemu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import com.github.gzuliyujiang.colorpicker.ColorGradientView
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import hh.app.oldmanemu.R
import hh.app.oldmanemu.customToolbar.*
import hh.app.oldmanemu.databinding.ActivityEditorBinding
import okhttp3.internal.toHexString
import org.sufficientlysecure.htmltextview.GlideImageGetter
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.AztecAttributes
import org.wordpress.aztec.AztecText
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.glideloader.GlideImageLoader
import org.wordpress.aztec.glideloader.GlideVideoThumbnailLoader
import org.wordpress.aztec.plugins.IMediaToolbarButton
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener
import org.wordpress.aztec.toolbar.ToolbarAction
import org.wordpress.aztec.toolbar.ToolbarItems
import org.xml.sax.Attributes

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var aztec:Aztec
    private var textColor=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.formattingToolbar.setToolbarItems(
            ToolbarItems.BasicLayout(
                ToolbarAction.HTML,
                ToolbarAction.BOLD,
                ToolbarAction.ITALIC,
                ToolbarAction.UNDERLINE,
                ToolbarAction.ALIGN_LEFT,
                ToolbarAction.ALIGN_CENTER,
                ToolbarAction.ALIGN_RIGHT,
                ToolbarAction.LINK,
                ToolbarAction.CODE
            )
        )

        var toolbarClicklistener=object : IAztecToolbarClickListener {
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
                    binding.source.displayStyledHtml(binding.aztec.toFormattedHtml())
                val uploadingPredicate = object : AztecText.AttributePredicate {
                    override fun matches(attrs: Attributes): Boolean {
                        return attrs.getIndex("uploading") > -1
                    }
                }

                val mediaPending = aztec.visualEditor.getAllElementAttributes(uploadingPredicate).isNotEmpty()

                if (mediaPending) {

                } else {
                    aztec.toolbar.toggleEditorMode()
                }
            }

            override fun onToolbarListButtonClicked() {
            }

            override fun onToolbarMediaButtonClicked(): Boolean {
                return false
            }
        }
//        binding.aztec.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if(textColor!=-1){
//                    var startindex=binding.aztec.text.indexOf(p0!!.toString())
//                    var str=SpannableStringBuilder(binding.aztec.text)
//                    var foregroundcolorspan=ForegroundColorSpan(textColor)
//                    str.setSpan(foregroundcolorspan,startindex,startindex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//                    binding.aztec.setText(str)
//                }
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//            }
//
//        })
        aztec=Aztec.with(
            binding.aztec,
            binding.source,
            binding.formattingToolbar,
            toolbarClicklistener
            )
            .setImageGetter(GlideImageLoader(this))
            .setVideoThumbnailGetter(GlideVideoThumbnailLoader(this))
            .setOnImageTappedListener(object:AztecText.OnImageTappedListener{
                override fun onImageTapped(
                    attrs: AztecAttributes,
                    naturalWidth: Int,
                    naturalHeight: Int
                ) {

                }

            })
            .setOnVideoTappedListener(object:AztecText.OnVideoTappedListener{
                override fun onVideoTapped(attrs: AztecAttributes) {

                }

            })

        aztec
            .addPlugin(ToolbarTextcolorButton(binding.formattingToolbar).apply {
                setMediaToolbarButtonClickListener(object :
                    IMediaToolbarButton.IMediaToolbarClickListener {
                    override fun onClick(view: View) {
                        var color=-1
                        CustomDialog.build()
                            .setCustomView(object:OnBindView<CustomDialog>(R.layout.popup_colorpicker){
                                override fun onBind(dialog: CustomDialog?, v: View?) {
                                    v?.findViewById<ColorGradientView>(R.id.colorpicker)?.setOnColorChangedListener { gradientView, pickcolor ->
                                       color=pickcolor
                                    }
                                    v?.findViewById<Button>(R.id.pickColorbtn)?.setOnClickListener {
                                        textColor=color
                                        dialog?.dismiss()
                                    }
                                }
                            })
                            .setAlign(CustomDialog.ALIGN.CENTER)
                            .show()
                    }
                })
            })
            .addPlugin(
                ToolbarTextsizeButton(binding.formattingToolbar).apply {
                    setMediaToolbarButtonClickListener(object :
                        IMediaToolbarButton.IMediaToolbarClickListener {
                        override fun onClick(view: View) {
                            Log.d("thebutton::", "textcolor")
                        }
                    })
                })
            .addPlugin(
                ToolbarUndoButton(binding.formattingToolbar).apply {
                    setMediaToolbarButtonClickListener(object :
                        IMediaToolbarButton.IMediaToolbarClickListener {
                        override fun onClick(view: View) {
                            aztec.visualEditor.undo()
                        }
                    })
                })
            .addPlugin(
                ToolbarRedoButton(binding.formattingToolbar).apply {
                    setMediaToolbarButtonClickListener(object :
                        IMediaToolbarButton.IMediaToolbarClickListener {
                        override fun onClick(view: View) {
                            aztec.visualEditor.redo()
                        }
                    })
                })
            .addPlugin(
                ToolbarInsertVideoButton(binding.formattingToolbar).apply {
                    setMediaToolbarButtonClickListener(object :
                        IMediaToolbarButton.IMediaToolbarClickListener {
                        override fun onClick(view: View) {
                            Log.d("thebutton::", "textcolor")
                        }
                    })
                })


    }


}