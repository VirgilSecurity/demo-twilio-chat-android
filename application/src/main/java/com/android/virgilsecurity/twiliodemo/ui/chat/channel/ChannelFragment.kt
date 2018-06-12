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
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.ui.base.BaseFragment
import com.android.virgilsecurity.twiliodemo.util.Constants
import com.android.virgilsecurity.twiliodemo.util.UiUtils
import com.twilio.chat.Channel
import com.twilio.chat.ChannelListener
import com.twilio.chat.Member
import com.twilio.chat.Message
import com.virgilsecurity.sdk.cards.Card
import kotlinx.android.synthetic.main.fragment_channel.*
import org.koin.android.ext.android.inject

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

    private val thresholdScroll = 1

    private val presenter: ChannelPresenter by inject()
    private val userManager: UserManager by inject()
    private val adapter: ChannelRVAdapter by inject()

    private lateinit var channel: Channel
    private lateinit var interlocutor: String
    private lateinit var interlocutorCard: Card

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

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.disposeAll()
        channel.removeAllListeners()
    }

    override fun preInitUi() {
        channel = arguments!!.getParcelable(Constants.KEY_CHANNEL) as Channel
    }

    override fun initUi() {
        val attributes = channel.attributes

        val receiver = attributes[Constants.KEY_RECEIVER] as String
        val sender = attributes[Constants.KEY_SENDER] as String
        val currentUser = userManager.getCurrentUser()!!.identity

        interlocutor = if (currentUser == sender) receiver else sender

        rootActivity!!.changeToolbarTitleExposed(interlocutor)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = false
        rvChat.layoutManager = layoutManager
        rvChat.adapter = adapter
    }

    override fun initCallbacks() {
        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
        }
        UiUtils.log(this::class.java.simpleName, " -> Ui init")

        btnSend.setOnClickListener {
            if (etMessage.text.toString().isNotEmpty()) {
                presenter.requestSendMessage(channel,
                                             interlocutor,
                                             etMessage.text.toString(),
                                             interlocutorCard,
                                             {
                                                 UiUtils.log(this@ChannelFragment::class.java.simpleName,
                                                             "Message send successfully")
                                             },
                                             {
                                                 UiUtils.toast(this,
                                                               it.message
                                                               ?: "Some error sending message")
                                             })
                etMessage.text.clear()
            }
        }
    }

    override fun initData() {
        showProgress(true)
        presenter.requestGetChannelBySid(channel.sid,
                                         {
                                             initChannelCallbacks(it)
                                             searchCards()
                                         },
                                         {
                                             showProgress(false)
                                             UiUtils.toast(this,
                                                           it.message
                                                           ?: "Some error getting channel by sid")
                                         })
    }

    private fun initChannelCallbacks(channel: Channel) {
        this.channel = channel

        this.channel.addListener(object : ChannelListener {
            override fun onMemberDeleted(p0: Member?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onMemberDeleted")
            }

            override fun onTypingEnded(p0: Channel?, p1: Member?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onTypingEnded")
            }

            override fun onMessageAdded(message: Message) {
                adapter.addItem(message)
                if (rvChat.adapter.itemCount > thresholdScroll)
                    rvChat.postDelayed({ rvChat.smoothScrollToPosition(adapter.itemCount - 1) },
                                       100)
            }

            override fun onMessageDeleted(p0: Message?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onMessageDeleted")
            }

            override fun onMemberAdded(p0: Member?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onMemberAdded")
            }

            override fun onTypingStarted(p0: Channel?, p1: Member?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onTypingStarted")
            }

            override fun onSynchronizationChanged(p0: Channel?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName,
                            " -> onSynchronizationChanged")
            }

            override fun onMessageUpdated(p0: Message?, p1: Message.UpdateReason?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onMessageUpdated")
            }

            override fun onMemberUpdated(p0: Member?, p1: Member.UpdateReason?) {
                UiUtils.log(this@ChannelFragment::class.java.simpleName, " -> onMemberUpdated")
            }
        })
    }

    private fun searchCards() {
        presenter.requestSearchCard(interlocutor,
                                    {
                                        interlocutorCard = it
                                        getMessages()
                                    },
                                    {
                                        showProgress(false)
                                        UiUtils.toast(this,
                                                      it.message
                                                      ?: "Some error searching interlocutor card")
                                    })
    }

    private fun getMessages() {
        presenter.requestMessages(channel,
                                  {
                                      adapter.setItems(it)
                                      if (rvChat.adapter.itemCount > thresholdScroll)
                                          rvChat.postDelayed({ rvChat.smoothScrollToPosition(adapter.itemCount - 1) },
                                                             400)
                                      showProgress(false)
                                  },
                                  {
                                      showProgress(false)
                                      UiUtils.toast(this,
                                                    it.message ?: "Some error getting messages")
                                  })
    }

    private fun showProgress(show: Boolean) {
        pbLoading?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }
}