package cangjie.scale.sorting.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.DialogPreviewImgBinding


/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/15 09:35
 */
class PreviewCardDialogFragment : DialogFragment() {


    private var path: String? = null
    private var previewCardBinding: DialogPreviewImgBinding? = null

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
        lp.height = (displayMetrics.heightPixels * 0.8f).toInt()
        lp.width=(displayMetrics.widthPixels * 0.7f).toInt()
        dialogWindow.attributes = lp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        previewCardBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_preview_img, container, false)
        return previewCardBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        path = arguments?.get("info") as String?
        previewCardBinding!!.path = path
        previewCardBinding!!.ivClose.setOnClickListener {
            dismiss()
        }
    }


    companion object {
        fun newInstance(args: Bundle?): PreviewCardDialogFragment? {
            val fragment = PreviewCardDialogFragment()
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        }
    }
}