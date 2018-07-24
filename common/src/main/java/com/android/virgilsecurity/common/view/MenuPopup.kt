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
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.support.annotation.LayoutRes
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.android.virgilsecurity.common.R
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
class MenuPopup(
//        @LayoutRes private val layoutResId: Int,
) : PopupWindow(), LayoutContainer {

    override lateinit var containerView: View

    private lateinit var layout: View
    private lateinit var onClick: (View) -> Unit

    fun setupPopup(context: Context) {
//        val viewGroup = context.findViewById(R.id.llSortChangePopup) as LinearLayout
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layout = layoutInflater.inflate(R.layout.layout_popup_menu, null)
        containerView = layout

        contentView = layout
        width = LAYOUT_PARAMS
        height = LAYOUT_PARAMS
        isFocusable = IS_FOCUSABLE
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        animationStyle = R.style.popup_window_animation

        tvMenuItem1.setOnClickListener(onClick)
        tvMenuItem2.setOnClickListener(onClick)
    }

    fun showPopup(showPoint: Point) {
        showAtLocation(layout, Gravity.NO_GRAVITY, showPoint.x + OFFSET_X, showPoint.y + OFFSET_Y)
    }

    fun setOnClickListener(onClick: (View) -> Unit) {
        this.onClick = onClick
    }

    companion object {
        const val LAYOUT_PARAMS = LinearLayout.LayoutParams.WRAP_CONTENT
        const val IS_FOCUSABLE = true
        const val OFFSET_X = -20
        const val OFFSET_Y = 95
    }
}