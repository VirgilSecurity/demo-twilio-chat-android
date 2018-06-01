/*
 * Copyright (c) 2015-2018, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.virgilsecurity.twiliodemo.ui.chat.threadsList.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.transition.TransitionManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.R.id.etIdentity
import com.android.virgilsecurity.twiliodemo.util.DefaultSymbolsInputFilter
import com.android.virgilsecurity.twiliodemo.util.OnFinishTimer
import com.android.virgilsecurity.twiliodemo.util.Validator
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import kotlinx.android.synthetic.main.dialog_create_thread.*

/**
 * Created by Danylo Oliinyk on 11/26/17 at Virgil Security.
 * -__o
 */

class CreateThreadDialog(context: Context,
                         themeResId: Int,
                         private val title: String?,
                         private val message: String) : Dialog(context, themeResId) {

    private lateinit var onClickedOk: (dialog: Dialog, identity: String) -> Unit
    private lateinit var onClickedCancel: (dialog: Dialog) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_create_thread)
        setCancelable(true)

        initUi()
    }

    private fun initUi() {
        if (title != null)
            tvTitle.text = title

        tvMessage.text = message

        etIdentity.filters = arrayOf(DefaultSymbolsInputFilter())
        etIdentity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                etIdentity.error = null
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        etIdentity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }

        btnCancel.setOnClickListener {
            onClickedCancel(this)
        }

        btnOk.setOnClickListener {
            val error: String? = Validator.validate(etIdentity, Validator.FieldType.EMAIL)

            if (error != null)
                etIdentity.error = error
            else
                onClickedOk(this, etIdentity.text.toString())
        }
    }

    fun setOnClickListener(onClickedOk: (dialog: Dialog, identity: String) -> Unit,
                           onClickedCancel: (dialog: Dialog) -> Unit) {
        this.onClickedOk = onClickedOk
        this.onClickedCancel = onClickedCancel
    }

    fun showProgress(show: Boolean) {
        if (show) {
            setCancelable(false)
            llContentRoot.visibility = View.GONE
            TransitionManager.beginDelayedTransition(flRoot)
            llLoadingRoot.visibility = View.VISIBLE
        } else {
            object : OnFinishTimer(1000, 100) {
                override fun onFinish() {
                    setCancelable(true)
                    llLoadingRoot.visibility = View.GONE
                    TransitionManager.beginDelayedTransition(flRoot)
                    llContentRoot.visibility = View.VISIBLE
                }
            }.start()
        }
    }
}
