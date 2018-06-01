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

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.ui.base.BaseFragment
import com.android.virgilsecurity.twiliodemo.util.UiUtils
import com.twilio.chat.*
import kotlinx.android.synthetic.main.fragment_channels_list.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/185/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelsListFragment
 */
class ChannelsListFragment : BaseFragment<ChannelsListActivity>() {

    private val presenter: ChannelsListPresenter by inject()
    private val adapter: ChannelsListRVAdapter by inject()
    private val userManager: UserManager by inject()

    companion object {
        fun newInstance() = ChannelsListFragment()
    }

    override fun provideLayoutId() = R.layout.fragment_channels_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initViewCallbacks()
        initData()
    }

    private fun initUi() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = false
        rvThreads.layoutManager = layoutManager
        rvThreads.adapter = adapter
    }

    private fun initViewCallbacks() {
        adapter.setClickListener { _, channel ->
            rootActivity!!.openChannel(channel)
        }
        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
        }
    }

    private fun initData() {
        presenter.startChatClient(userManager.getCurrentUser()!!.identity,
                                  {
                                      fetchChannels()
                                  },
                                  {
                                     UiUtils.toast(this, "Chat client start failed")
                                  })

        presenter.setupChatListener(object : ChatClientListener {
            override fun onChannelDeleted(p0: Channel?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onInvitedToChannelNotification(p0: String?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onClientSynchronization(p0: ChatClient.SynchronizationStatus?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onNotificationSubscribed() {
                // TODO Implement body or it will be empty ):
            }

            override fun onUserSubscribed(p0: User?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onChannelUpdated(p0: Channel?, p1: Channel.UpdateReason?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onRemovedFromChannelNotification(p0: String?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onNotificationFailed(p0: ErrorInfo?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onChannelJoined(p0: Channel?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onChannelAdded(channel: Channel?) {
                channel?.join(object : StatusListener() {
                    override fun onSuccess() {
                        UiUtils.log(this.javaClass.simpleName, " -> successfully autojoined channel")
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        UiUtils.log(this.javaClass.simpleName, " -> error autojoin channel")
                    }
                })
            }

            override fun onChannelSynchronizationChange(p0: Channel?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onUserUnsubscribed(p0: User?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onAddedToChannelNotification(p0: String?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onChannelInvited(p0: Channel?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onNewMessageNotification(p0: String?, p1: String?, p2: Long) {
                // TODO Implement body or it will be empty ):
            }

            override fun onConnectionStateChange(p0: ChatClient.ConnectionState?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onError(p0: ErrorInfo?) {
                // TODO Implement body or it will be empty ):
            }

            override fun onUserUpdated(p0: User?, p1: User.UpdateReason?) {
                // TODO Implement body or it will be empty ):
            }
        })
    }

    private fun fetchChannels() {
        presenter.fetchChannels(onFetchChannelsSuccess = {
            adapter.setItems(it?.toMutableList())
        })
    }

    fun issueCreateThread(interlocutor: String) {
        presenter.createChannel(interlocutor,
                                {
                                    adapter.addItem(it)
                                },
                                {
                                    UiUtils.toast(this, it.message ?:
                                                        "Some error creating channel")
                                })
    }
}