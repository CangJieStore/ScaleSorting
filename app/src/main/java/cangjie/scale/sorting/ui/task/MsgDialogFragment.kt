package cangjie.scale.sorting.ui.task

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.DialogApplyBinding
import com.cangjie.frame.kit.tab.dp
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * @author: guruohan
 * @date: 2021/11/6
 */
class MsgDialogFragment : DialogFragment() {

    private var dialogApplyBinding: DialogApplyBinding? = null

    private var action: BtnAction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.PreviewDialog)
    }

    override fun onStart() {
        super.onStart()
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            init()
        }
        dialog!!.setCanceledOnTouchOutside(false)
        val dialogWindow = dialog!!.window
        dialogWindow!!.setGravity(Gravity.CENTER)
        val lp = dialogWindow.attributes
        lp.width = 400.dp
        dialogWindow.attributes = lp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogApplyBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_apply, container, false)
        return dialogApplyBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogApplyBinding?.let {
            it.txtDialogTitle.text = arguments?.getString("title")
            it.txtDialogTip.text = arguments?.getString("content")
            it.actionCancel.setOnClickListener {
                dismiss()
            }
            it.actionDone.setOnClickListener {
                action?.let { ac ->
                    kotlin.run {
                        ac.action(0)
                        dismiss()
                    }
                }
            }
        }
    }

    interface BtnAction {
        fun action(pos: Int)
    }

    fun setBtnAction(ac: BtnAction): MsgDialogFragment {
        this.action = ac
        return this
    }


    companion object {
        fun newInstance(args: Bundle?): MsgDialogFragment {
            val fragment = MsgDialogFragment()
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        }
    }
}