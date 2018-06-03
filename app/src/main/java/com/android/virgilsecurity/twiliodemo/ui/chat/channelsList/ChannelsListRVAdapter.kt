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

package com.android.virgilsecurity.twiliodemo.ui.chat.channelsList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.util.Constants
import com.twilio.chat.Channel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_channels.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/01/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class ChannelsListRVAdapter constructor(private val userManager: UserManager) :
        RecyclerView.Adapter<ChannelsListRVAdapter.ChannelHolder>() {

    private var items: MutableList<Channel> = mutableListOf()
    private lateinit var clickListener: (Int, Channel) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_channels, parent, false)

        return ChannelHolder(view, clickListener, userManager)
    }

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount()= items.size

    fun setItems(items: MutableList<Channel>?) {
        if (items != null) {
            items.removeAll(this.items)
            this.items = ArrayList(items)
        } else {
            this.items = mutableListOf()
        }

        notifyDataSetChanged()
    }

    fun addItems(channels: List<Channel>) {
        items.addAll(channels)
        notifyDataSetChanged()
    }

    fun addItem(channel: Channel) {
        items.add(channel)
        notifyDataSetChanged()
    }

    fun setClickListener(clickListener: (Int, Channel) -> Unit) {
        this.clickListener = clickListener
    }

    fun getItemById(interlocutor: String): Channel? {
        for (channel in items) {
            if ((channel.attributes[Constants.KEY_RECEIVER] as String) == interlocutor)
                return channel
        }

        return null
    }

    fun clearItems() {
        items.clear()
    }

    class ChannelHolder(override val containerView: View?,
                        private val onItemClicked: (Int, Channel) -> Unit,
                        private val userManager: UserManager) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(channel: Channel) {
            val attributes = channel.attributes

            val receiver = attributes[Constants.KEY_RECEIVER] as String
            val sender = attributes[Constants.KEY_SENDER] as String
            val currentUser = userManager.getCurrentUser()!!.identity

            if (currentUser == sender)
                tvUsername.text = receiver
            else
                tvUsername.text = sender

            rlItemRoot.setOnClickListener { _ ->
                onItemClicked(adapterPosition, channel)
            }
        }
    }
}
