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

package com.android.virgilsecurity.twiliodemo.ui.chat.channel

import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilHelper
import com.twilio.chat.Message
import java.util.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    4/16/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class ChannelRVAdapter internal constructor(private val virgilHelper: VirgilHelper,
                                            private val userManager: UserManager)
    : RecyclerView.Adapter<ChannelRVAdapter.HolderMessage>() {

    private var items: MutableList<Message>? = mutableListOf()

    @IntDef(MessageType.ME, MessageType.YOU)
    private annotation class MessageType {
        companion object {
            val ME = 0
            val YOU = 1
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HolderMessage {
        val viewHolder: HolderMessage
        val inflater = LayoutInflater.from(viewGroup.context)

        viewHolder = when (viewType) {
            MessageType.ME -> HolderMessage(inflater.inflate(R.layout.layout_holder_me,
                                                             viewGroup,
                                                             false))
            MessageType.YOU -> HolderMessage(inflater.inflate(R.layout.layout_holder_you,
                                                              viewGroup,
                                                              false))
            else -> HolderMessage(inflater.inflate(R.layout.layout_holder_me,
                                                   viewGroup,
                                                   false))
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: HolderMessage, position: Int) {
        viewHolder.bind(virgilHelper.decrypt(items!![position]
                                                     .getBody()))
    }

    override fun getItemViewType(position: Int): Int {
        return if (items!![position]
                        .getSender()
                        .equals(firebaseAuth.getCurrentUser()
                                        .getEmail().toLowerCase())) {
            MessageType.ME
        } else {
            MessageType.YOU
        }
    }

    override fun getItemCount(): Int {
        return if (items != null) items!!.size else -1
    }

    internal fun setItems(items: MutableList<DefaultMessage>?) {
        if (items != null) {
            items.removeAll(this.items!!)
            this.items = ArrayList<DefaultMessage>(items)
        } else {
            this.items = emptyList<DefaultMessage>()
        }

        notifyDataSetChanged()
    }

    internal fun addItem(item: DefaultMessage) {
        if (items == null || items!!.isEmpty())
            items = ArrayList<DefaultMessage>()

        items!!.add(item)
        notifyDataSetChanged()
    }

    fun clearItems() {
        if (items != null || !items!!.isEmpty())
            items!!.clear()
    }

    class HolderMessage(v: View) : RecyclerView.ViewHolder(v) {

        var tvMessage: TextView? = null

        init {
            ButterKnife.bind(this, v)
        }

        fun bind(message: String) {
            tvMessage!!.text = message
        }
    }
}
