package hh.app.oldmanemu.PopupViews

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.BottomPopupView
import hh.app.oldmanemu.R
import hh.app.oldmanemu.adapters.CommentListAdapter
import hh.app.oldmanemu.beans.CommentBean

class CommentPopup(context: Context,var comments:ArrayList<CommentBean>): BottomPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.popup_comment
    }

    override fun onCreate() {
        super.onCreate()
        var commentList=findViewById<RecyclerView>(R.id.commentList)
        var commentListAdapter=CommentListAdapter(context,comments)
        commentList.layoutManager= LinearLayoutManager(context)
        commentList.adapter=commentListAdapter
    }
}