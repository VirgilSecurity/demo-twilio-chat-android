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

package com.android.virgilsecurity.twiliodemo.ui.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toolbar
import com.android.virgilsecurity.twiliodemo.R

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

/**
 *
 */
abstract class BaseActivity : AppCompatActivity() {

    private var tvToolbarTitle: TextView? = null
    private var ibToolbarBack: View? = null
    private var ibToolbarHamburger: View? = null
    private var toolbar: Toolbar? = null

    protected abstract fun provideLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutInflater.inflate(provideLayoutId(), null))
    }

    protected fun initToolbar(toolbar: Toolbar, titlePage: String) {
        this.toolbar = toolbar
        this.tvToolbarTitle = toolbar.findViewById(R.id.tvToolbarTitle)
        this.ibToolbarBack = toolbar.findViewById(R.id.ibToolbarBack)
        this.ibToolbarHamburger = toolbar.findViewById(R.id.ibToolbarHamburger)

        setActionBar(toolbar)

        tvToolbarTitle?.text = titlePage
        actionBar?.title = "" // We're using our custom title
        actionBar?.setDisplayHomeAsUpEnabled(false) // Hide default home button
    }

    protected fun showBackButton(show: Boolean, listener: View.OnClickListener?) {
        if (show) {
            ibToolbarBack?.visibility = View.VISIBLE
            ibToolbarBack?.setOnClickListener(listener)
        } else {
            ibToolbarBack?.visibility = View.INVISIBLE
        }
    }

    protected fun showHamburger(show: Boolean, listener: View.OnClickListener?) {
        if (show) {
            ibToolbarHamburger?.visibility = View.VISIBLE
            ibToolbarHamburger?.setOnClickListener(listener)
        } else {
            ibToolbarHamburger?.visibility = View.INVISIBLE
        }
    }

    protected fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }
}