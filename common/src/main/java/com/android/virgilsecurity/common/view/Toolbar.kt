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

package com.android.virgilsecurity.common.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.Toolbar
import com.android.virgilsecurity.common.R
import kotlinx.android.synthetic.main.toolbar.view.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/17/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * Toolbar
 */
class Toolbar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int)
    : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private lateinit var onToolbarItemClickListener: (View?) -> Unit
    private lateinit var views: MutableMap<View, Int>

//    private lateinit var ivBack: View
//    private lateinit var ivHamburger: View
//    private lateinit var ivMenu: View
//    private lateinit var ivAddPerson: View
//    private lateinit var ivSearch: View
//    private lateinit var ivClose: View
//    private lateinit var etSearch: View

    init {
//        findViews()
        setupViewsMap()
    }

//    private fun findViews() {
//        ivBack = findViewById<View>(R.id.ivBack)
//        ivHamburger = findViewById<View>(R.id.ivHamburger)
//        ivMenu = findViewById<View>(R.id.ivMenu)
//        ivAddPerson = findViewById<View>(R.id.ivAddPerson)
//        ivSearch = findViewById<View>(R.id.ivSearch)
//        ivClose = findViewById<View>(R.id.ivClose)
//        etSearch = findViewById<View>(R.id.etSearch)
//    }

    private fun setupViewsMap() {
        views = HashMap()
        views[ivBack] = STATE_DEFAULT
        views[ivHamburger] = STATE_DEFAULT
        views[tvTitle] = STATE_DEFAULT
        views[tvSubTitle] = STATE_DEFAULT
        views[ivMenu] = STATE_DEFAULT
        views[ivAddPerson] = STATE_DEFAULT
        views[ivSearch] = STATE_DEFAULT
        views[ivClose] = STATE_DEFAULT
        views[etSearch] = STATE_DEFAULT
    }

    fun showBackButton() {
        ivBack.setOnClickListener(this)
        views[ivBack] = STATE_VISIBLE

        if (ivHamburger.visibility == View.VISIBLE)
            ivHamburger.visibility = View.INVISIBLE

        ivBack.visibility = View.VISIBLE
    }

    fun hideBackButton() {
        ivBack.setOnClickListener(null)
        views[ivBack] = STATE_INVISIBLE
        ivBack.visibility = View.INVISIBLE
    }

    fun showHamburgerButton() {
        ivHamburger.setOnClickListener(this)
        views[ivHamburger] = STATE_VISIBLE

        if (ivBack.visibility == View.VISIBLE)
            ivBack.visibility = View.INVISIBLE

        ivHamburger.visibility = View.VISIBLE
    }

    fun hideHamburgerButton() {
        ivHamburger.setOnClickListener(null)
        views[ivHamburger] = STATE_INVISIBLE
        ivHamburger.visibility = View.INVISIBLE
    }

    fun setTitle(text: String) {
        tvTitle.visibility = View.VISIBLE
        views[tvTitle] = STATE_VISIBLE
        tvTitle.text = text
    }

    fun removeTitle() {
        tvTitle.visibility = View.INVISIBLE
        views[tvTitle] = STATE_INVISIBLE
        tvTitle.text = ""
    }

    fun setSubTitle(text: String) {
        tvSubTitle.visibility = View.INVISIBLE
        views[tvSubTitle] = STATE_INVISIBLE
        tvSubTitle.text = text
    }

    fun removeSubTitle() {
        tvSubTitle.visibility = View.GONE
        views[tvSubTitle] = STATE_INVISIBLE
        tvSubTitle.text = ""
    }

    fun showSearchButton() {
        ivSearch.visibility = View.VISIBLE
        views[ivSearch] = STATE_VISIBLE

        ivSearch.setOnClickListener {
            for (entry in views.entries) {
                if (entry.value == STATE_VISIBLE) {
                    entry.key.visibility = View.INVISIBLE
                    entry.setValue(STATE_HIDDEN)
                }
            }
            etSearch.visibility = View.VISIBLE
            ivClose.visibility = View.VISIBLE
            views[etSearch] = STATE_VISIBLE
            views[ivClose] = STATE_VISIBLE

            ivBack.setOnClickListener {
                for (entry in views.entries) {
                    if (entry.value == STATE_HIDDEN) {
                        entry.key.visibility = View.VISIBLE
                        entry.setValue(STATE_VISIBLE)
                    }
                }

                etSearch.visibility = View.INVISIBLE
                ivClose.visibility = View.INVISIBLE
                views[etSearch] = STATE_INVISIBLE
                views[ivClose] = STATE_INVISIBLE

                ivBack.setOnClickListener(this)
            }
        }
    }

    fun hideSearchButton() {
        ivSearch.setOnClickListener(null)
        views[ivSearch] = STATE_INVISIBLE
        ivSearch.visibility = View.INVISIBLE

        if (ivBack.hasOnClickListeners())
            ivBack.setOnClickListener(this)

        // For case if this function is called after search button was clicked
        etSearch.visibility = View.INVISIBLE
        ivClose.visibility = View.INVISIBLE
        views[etSearch] = STATE_INVISIBLE
        views[ivClose] = STATE_INVISIBLE
    }

    fun showMenuButton() {
        ivMenu.setOnClickListener(this)
        ivMenu.visibility = View.VISIBLE
        views[ivMenu] = STATE_VISIBLE
    }

    fun hideMenuButton() {
        ivMenu.setOnClickListener(null)
        views[ivMenu] = STATE_INVISIBLE
        ivMenu.visibility = View.INVISIBLE
    }

    fun showAddPersonButton() {
        ivAddPerson.setOnClickListener(this)
        ivAddPerson.visibility = View.VISIBLE
        views[ivAddPerson] = STATE_VISIBLE
    }

    fun hidePersonButton() {
        ivAddPerson.setOnClickListener(null)
        views[ivAddPerson] = STATE_INVISIBLE
        ivAddPerson.visibility = View.INVISIBLE
    }

    fun setOnToolbarItemClickListener(listener: (View?) -> Unit) {
        this.onToolbarItemClickListener = listener
    }

    override fun onClick(v: View?) {
        onToolbarItemClickListener(v)
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_VISIBLE = 1
        const val STATE_INVISIBLE = 2
        const val STATE_HIDDEN = 3
    }
}