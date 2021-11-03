package cangjie.scale.sorting.ui.task

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.FragmentTaskItemBinding
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmFragment

/**
 * @author: guruohan
 * @date: 2021/11/3
 */
class TaskItemFragment : BaseMvvmFragment<FragmentTaskItemBinding, TaskViewModel>() {
    companion object {
        fun newInstance(type: Int) = TaskItemFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun layoutId(): Int = R.layout.fragment_task_item

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
    }

    override fun initVariableId(): Int = BR.itemModel
}