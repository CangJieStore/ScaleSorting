package com.cangjie.frame.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cangjie.scale.sorting.R

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/14 11:15
 */
open class DialogActivity : AppCompatActivity() {

    open fun launchActivity(
        context: Context,
        info: Bundle?,
        cla: Class<*>?
    ) {
        val intent = Intent(context, cla)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("bundle", info)
        context.startActivity(intent)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_out)
    }
}