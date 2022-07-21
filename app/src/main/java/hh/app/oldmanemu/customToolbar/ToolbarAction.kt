package hh.app.oldmanemu.customToolbar

import hh.app.oldmanemu.R
import org.wordpress.aztec.AztecTextFormat
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.toolbar.IToolbarAction
import org.wordpress.aztec.toolbar.ToolbarActionType

enum class ToolbarAction constructor(
        override val buttonId: Int,
        override val buttonDrawableRes: Int,
        override val actionType: ToolbarActionType,
        override val textFormats: Set<ITextFormat> = setOf()
) : IToolbarAction {
    TEXTCOLOR(R.id.barButton, R.drawable.textcolor,ToolbarActionType.OTHER,setOf(AztecTextFormat.FORMAT_NONE)),
    TEXTSIZE(R.id.textsizebarButton, R.drawable.textsize,ToolbarActionType.OTHER,setOf(AztecTextFormat.FORMAT_NONE)),
    UNDO(R.id.undobarButton, R.drawable.undo,ToolbarActionType.OTHER,setOf(AztecTextFormat.FORMAT_NONE)),
    REDO(R.id.redobarButton, R.drawable.redo,ToolbarActionType.OTHER,setOf(AztecTextFormat.FORMAT_NONE)),
    INSERTVIDEO(R.id.insertvideobarButton, R.drawable.video,ToolbarActionType.OTHER,setOf(AztecTextFormat.FORMAT_NONE))
}
