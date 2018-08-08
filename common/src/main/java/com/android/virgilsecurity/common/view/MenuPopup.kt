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
import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.android.virgilsecurity.common.R
import com.android.virgilsecurity.common.util.UiUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_popup_menu.*


/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/24/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * MenuPopup
 */
class MenuPopup : PopupWindow(), LayoutContainer {

    override lateinit var containerView: View

    private lateinit var layout: View
    private lateinit var onClick: (View) -> Unit

    fun setupPopup(context: Context) {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layout = layoutInflater.inflate(R.layout.layout_popup_menu, null)
        containerView = layout

        contentView = layout
        width = UiUtils.dpToPixels(LAYOUT_PARAMS_WIDTH, context)
        height = LAYOUT_PARAMS_HEIGHT
        layout.setPadding(0,
                          UiUtils.dpToPixels(PADDING_TOP, context),
                          0,
                          UiUtils.dpToPixels(PADDING_BOTTOM, context))

        isFocusable = IS_FOCUSABLE
        setBackgroundDrawable(context.getDrawable(R.drawable.rect_white_rounded_2))
        animationStyle = R.style.popup_window_animation

        tvMenuItemEdit.setOnClickListener(onClick)
        tvMenuItemLogout.setOnClickListener(onClick)
    }

    fun showPopup(showPoint: Point) {
        showAtLocation(layout, Gravity.NO_GRAVITY, showPoint.x + OFFSET_X, showPoint.y + OFFSET_Y)
    }

    fun setOnClickListener(onClick: (View) -> Unit) {
        this.onClick = onClick
    }

    companion object {
        const val LAYOUT_PARAMS_WIDTH = 128
        const val LAYOUT_PARAMS_HEIGHT = LinearLayout.LayoutParams.WRAP_CONTENT
        const val PADDING_TOP = 24
        const val PADDING_BOTTOM = 24
        const val IS_FOCUSABLE = true
        const val OFFSET_X = LAYOUT_PARAMS_WIDTH * (-1)
        const val OFFSET_Y = 95
    }
}