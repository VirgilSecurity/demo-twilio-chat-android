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

package com.virgilsecurity.android.feature_channel.viewslice.channel.adapter

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItemDefault
import com.virgilsecurity.android.common.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.feature_channel.R
import com.virgilsecurity.android.feature_channel.domain.ShowMessagePreviewDoDefault
import com.virgilsecurity.android.feature_channel.viewslice.channel.ChannelSlice

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/9/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * MessageItemMe
 */
class MessageItemMe(private val actionLiveData: MutableLiveData<ChannelSlice.Action>,
                    private val userProperties: UserProperties,
                    private val virgilHelper: VirgilHelper,
                    override val layoutResourceId: Int = R.layout.item_message_me
) : DelegateAdapterItemDefault<MessageMeta>() {

    override fun onBind(item: MessageMeta, viewHolder: KViewHolder<MessageMeta>) =
            with(viewHolder.containerView) {
                val text = if (item.threadId == ShowMessagePreviewDoDefault.PREVIEW_CHANNEL_SID)
                    item.body!!
                else
                    virgilHelper.decrypt(item.body!!)

                findViewById<TextView>(R.id.tvMessage).text = text

                setOnClickListener {
                    actionLiveData.value = ChannelSlice.Action.MessageClicked(item)
                    actionLiveData.value = ChannelSlice.Action.Idle
                }

                setOnLongClickListener {
                    actionLiveData.value = ChannelSlice.Action.MessageLongClicked(item)
                    actionLiveData.value = ChannelSlice.Action.Idle
                    true
                }
            }

    override fun onRecycled(holder: KViewHolder<MessageMeta>) {}

    override fun isForViewType(items: List<*>, position: Int): Boolean =
            (items[position] as MessageMeta).sender == userProperties.currentUser!!.identity &&
            (items[position] as MessageMeta).isNotInDevelopment()
}