package cangjie.scale.sorting.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.DialogSubmitBinding
import cangjie.scale.sorting.entity.LabelInfo
import cangjie.scale.sorting.ui.purchase.LabelAdapter
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.dialog_submit.*
import java.util.stream.Collectors

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/15 09:35
 */
class SubmitDialogFragment : DialogFragment() {


    private var data: MutableList<LabelInfo> = arrayListOf()
    private var submitBinding: DialogSubmitBinding? = null
    private val submitAdapter by lazy {
        LabelAdapter()
    }

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
        val displayMetrics = requireContext().resources.displayMetrics
        lp.width = (displayMetrics.widthPixels * 0.6f).toInt()
        dialogWindow.attributes = lp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        submitBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_submit, container, false)
        return submitBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = arguments?.get("info") as MutableList<LabelInfo>
        submitBinding?.let {
            it.ivClose.setOnClickListener { dismissAllowingStateLoss() }
            if (data.size > 0) {
                it.tvSubmitName.text = "商品名称：" + data[0].goodsName
                it.tvBuyCount.text = "订单数量：" + data[0].quantity
                it.tvSubmitTotal.text = "分拣数量：" + calNum()
                it.tvSendUnit.text = "配送单位：" + data[0].unit
                if (it.rySubmit.itemDecorationCount == 0) {
                    requireActivity().dividerBuilder()
                        .color(Color.TRANSPARENT)
                        .size(10, TypedValue.COMPLEX_UNIT_DIP)
                        .showLastDivider()
                        .showSideDividers()
                        .showFirstDivider()
                        .build()
                        .addTo(it.rySubmit)
                }
                it.adapter = submitAdapter
                submitAdapter.setList(data)
                it.btnSubmit.setOnClickListener {
                    dismissAllowingStateLoss()
                    action?.submit(calNum())
                }
            }
        }
    }

    private fun calNum(): Float {
        var total = 0f
        data.forEach {
            total += it.currentNum
        }
        return total
    }


    interface SubmitAction {
        fun submit(quantity: Float)
    }

    private var action: SubmitAction? = null
    fun setAction(ac: SubmitAction): SubmitDialogFragment {
        this.action = ac
        return this
    }


    companion object {
        fun newInstance(args: Bundle?): SubmitDialogFragment? {
            val fragment = SubmitDialogFragment()
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        }
    }
}