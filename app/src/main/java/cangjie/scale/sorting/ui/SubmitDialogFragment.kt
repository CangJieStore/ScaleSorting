package cangjie.scale.sorting.ui

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import cangjie.scale.sorting.R
import cangjie.scale.sorting.adapter.SubmitAdapter
import cangjie.scale.sorting.databinding.DialogSubmitBinding
import cangjie.scale.sorting.entity.SubmitInfo
import com.fondesa.recyclerviewdivider.dividerBuilder
import kotlinx.android.synthetic.main.dialog_submit.*

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/15 09:35
 */
class SubmitDialogFragment : DialogFragment() {


    private var data: MutableList<SubmitInfo> = arrayListOf()
    private var submitBinding: DialogSubmitBinding? = null
    private val submitAdapter by lazy {
        SubmitAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.PreviewDialog)
    }


    override fun onStart() {
        super.onStart()
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
        data = arguments?.get("info") as MutableList<SubmitInfo>
        submitBinding!!.ivClose.setOnClickListener {
            dismiss()
        }
        submitBinding!!.rbType1.setOnClickListener {
            rb_type1.isChecked = true
            rb_type2.isChecked = false
        }
        submitBinding!!.rbType2.setOnClickListener {
            rb_type2.isChecked = true
            rb_type1.isChecked = false
        }
        if (data.size > 0) {
            submitBinding!!.tvSubmitName.text = "商品名称：" + data[0].name
            submitBinding!!.tvBuyCount.text =
                "收货价格：" + if (data[0].costPrice.isNullOrEmpty()) "0.00" else String.format(
                    "%.2f元",
                    data[0].costPrice!!.toFloat()
                )
            submitBinding!!.tvSubmitTotal.text = "验收总数量：" + calTotal()
            submitBinding!!.tvSendUnit.text = "收货单位：" + data[0].receive_unit
            requireActivity().dividerBuilder()
                .color(Color.parseColor("#cccccc"))
                .size(1, TypedValue.COMPLEX_UNIT_DIP)
                .showLastDivider()
                .build()
                .addTo(submitBinding!!.rySubmit)
            submitBinding!!.adapter = submitAdapter
            submitAdapter.setList(data)
            submitBinding!!.btnSubmit.setOnClickListener {
                if (action != null) {
                    dismiss()
                    val count = if (data[0].matchCount.isNullOrEmpty()) "" else data[0].matchCount!!
                    val price = if (data[0].matchPrice.isNullOrEmpty()) "" else data[0].matchPrice!!
                    val csPrice =
                        if (data[0].costPrice.isNullOrEmpty()) "0.00" else data[0].costPrice!!
                    action!!.submit(
                        data[0].id,
                        connectTotal(),
                        data[0].isLess,
                        count,
                        price,
                        csPrice
                    )
                }
            }
        }
    }

    private fun calTotal(): String {
        var total = 0f
        if (data.size > 0) {
            for (item in data) {
                total += item.batch_count.toFloat()
            }
        }
        return total.toString()
    }

    private fun connectTotal(): String {
        val buffer = StringBuffer()
        if (data.size > 0) {
            for (item in data) {
                buffer.append(item.batch_count)
                buffer.append(",")
            }
        }
        return buffer.toString()
    }

    interface SubmitAction {
        fun submit(
            id: String,
            count: String,
            isLess: Int,
            dCount: String,
            dPrice: String,
            costPrice: String
        )
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