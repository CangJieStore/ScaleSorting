package cangjie.scale.sorting.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cangjie.scale.sorting.R
import cangjie.scale.sorting.adapter.OrderAdapter
import cangjie.scale.sorting.base.DateUtil
import cangjie.scale.sorting.base.workOnIO
import cangjie.scale.sorting.databinding.FragmentChekedBinding
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.entity.OrderInfo
import cangjie.scale.sorting.vm.ScaleViewModel
import com.cangjie.frame.core.BaseMvvmFragment
import com.cangjie.frame.kit.show
import com.fondesa.recyclerviewdivider.dividerBuilder
import kotlinx.coroutines.launch

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/13 17:26
 */
class CheckedFragment : BaseMvvmFragment<FragmentChekedBinding, ScaleViewModel>() {


    private var chooseDate: String? = null
    private val orderAdapter by lazy {
        OrderAdapter(1)
    }

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        requireActivity().dividerBuilder()
            .color(Color.parseColor("#cccccc"))
            .size(1, TypedValue.COMPLEX_UNIT_DIP)
            .showLastDivider()
            .build()
            .addTo(mBinding!!.ryOrders)
        mBinding!!.adapter = orderAdapter
        netTime()
        orderAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(requireActivity(), CheckedDetailActivity::class.java)
            val info = adapter.data[position] as OrderInfo
            intent.putExtra("id", info.id)
            startActivity(intent)
        }
    }

    private fun netTime() {
        lifecycleScope.launch {
            workOnIO {
                try {
                    val infoUrl = URL("http://www.baidu.com")
                    val connection = infoUrl.openConnection()
                    connection.connect()
                    val ld = connection.date
                    val now = DateUtil.dateToString(Date(ld), DateUtil.DATE_FORMAT)
                    chooseDate = now
                    viewModel.loadMain(now)
                } catch (e: Exception) {
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val date = Date(System.currentTimeMillis())
                    chooseDate = simpleDateFormat.format(date)
                    viewModel.loadMain(simpleDateFormat.format(date))
                }

            }
        }
    }

    override fun initVariableId(): Int = cangjie.scale.sorting.BR.checkModel

    override fun layoutId(): Int = R.layout.fragment_cheked

    override fun subscribeModel(model: ScaleViewModel) {
        super.subscribeModel(model)
        model.getOrderInfo().observe(this, Observer {
            it?.let {
                orderAdapter.setList(it.filter { it1 -> it1.purchaser_count - it1.sorting_purchaser_count == 0 })
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.code == 0) {
            chooseDate = event.msg
            viewModel.loadMain(event.msg)
        }
    }

    override fun toast(notice: String?) {
        super.toast(notice)
        show(requireActivity(), 2000, notice!!)
    }
}