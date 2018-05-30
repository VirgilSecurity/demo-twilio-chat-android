package com.android.virgilsecurity.twiliodemo.ui.chat.threadsList

import android.app.Activity
import android.content.Intent
import com.android.virgilsecurity.twiliodemo.ui.base.BaseActivity

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class ThreadsListActivity : BaseActivity() {

    override fun provideLayoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun startWithFinish(from: Activity) {
            from.startActivity(Intent(from, ThreadsListActivity::class.java))
            from.finish()
        }
    }
}
