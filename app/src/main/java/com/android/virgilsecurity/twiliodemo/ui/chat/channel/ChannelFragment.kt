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

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.ui.base.BaseFragment
import com.android.virgilsecurity.twiliodemo.util.Constants
import com.twilio.chat.Channel
import com.twilio.chat.ChannelListener
import com.twilio.chat.Member
import com.twilio.chat.Message
import kotlinx.android.synthetic.main.fragment_channel.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/1/186/1/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelFragment
 */

class ChannelFragment : BaseFragment<ChannelActivity>() {

    private lateinit var channel: Channel

    companion object {
        fun newInstance() = ChannelFragment()

        fun newInstance(key: String, parcelable: Parcelable): ChannelFragment {
            val bundle = Bundle()
            bundle.putParcelable(key, parcelable)
            val fragment = ChannelFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun provideLayoutId() = R.layout.fragment_channel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        channel = arguments!!.getParcelable<Channel>(Constants.KEY_CHANNEL)

        initUi()
        initViewCallbacks()
        initData()
    }

    private fun initUi() {
        // TODO Implement body or it will be empty ):
    }

    private fun initViewCallbacks() {
        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
        }
    }

    private fun initData() {
        channel.addListener(object : ChannelListener {
            override fun onMemberDeleted(p0: Member?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onTypingEnded(p0: Channel?, p1: Member?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onMessageAdded(p0: Message?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onMessageDeleted(p0: Message?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onMemberAdded(p0: Member?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onTypingStarted(p0: Channel?, p1: Member?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onSynchronizationChanged(p0: Channel?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onMessageUpdated(p0: Message?, p1: Message.UpdateReason?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onMemberUpdated(p0: Member?, p1: Member.UpdateReason?) {
                // TODO Implement body or it will be empty ):
            }

        })
    }
}