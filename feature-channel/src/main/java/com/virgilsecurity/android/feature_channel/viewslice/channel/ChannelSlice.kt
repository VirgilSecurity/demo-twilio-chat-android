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

package com.virgilsecurity.android.feature_channel.viewslice.channel

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.feature_channel.data.interactor.model.ChannelItem
import com.virgilsecurity.android.feature_channel.R
import java.util.*


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
 * ChannelSliceDefault
 */
class ChannelSlice(
        private val action: MutableLiveData<Action>,
        private val adapter: DelegateAdapter<ChannelItem>,
        private val layoutManager: RecyclerView.LayoutManager
) : BaseViewSlice() {

    private lateinit var rvMessages: RecyclerView

    override fun setupViews() {
        with(window) {
            rvMessages = findViewById(R.id.rvMessages)

            rvMessages.adapter = adapter
            rvMessages.layoutManager = layoutManager
        }
    }

    fun getAction(): LiveData<Action> = action

    fun showMessages(messages: List<MessageMeta>) {
        fun sameDate(dt1: Long, dt2: Long): Boolean {
            val cal1: Calendar = Calendar.getInstance()
            val cal2: Calendar = Calendar.getInstance()
            cal1.time = Date(dt1)
            cal2.time = Date(dt2)

            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        }

        val formatDate = { date: Long ->
            DateUtils.getRelativeTimeSpanString(
                    date,
                    System.currentTimeMillis(),
                    DateUtils.DAY_IN_MILLIS
            ).toString()
        }

        var prevMessageDate: Long = 0
        // add dates
        val items = messages.fold(listOf()) { acc: List<ChannelItem>, messageMeta: MessageMeta ->
            val messageDate = messageMeta.getDateMillisSince1970()

            val date = if (sameDate(messageDate, prevMessageDate))
                listOf<ChannelItem>()
            else
                listOf(ChannelItem.Date(formatDate(messageDate)))

            prevMessageDate = messageDate

            acc + date + listOf(ChannelItem.Message(messageMeta))
        } // TODO: Date logic & view

        adapter.swapData(items)
        layoutManager.scrollToPosition(adapter.itemCount - 1)
    }

    sealed class Action {
        data class MessageClicked(val message: MessageMeta) : Action()
        data class MessageLongClicked(val message: MessageMeta) : Action()
        object Idle : Action()
    }
}
