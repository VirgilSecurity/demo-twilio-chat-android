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

    override fun onStop() {
        super.onStop()

        presenter.disposeAll()
    }

    fun onLogOut() {
        adapter.clearItems()
        presenter.shutdownChatClient()
    }

    override fun preInitUi() {
        // TODO Implement body or it will be empty ):
    }

    override fun initUi() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = false
        rvThreads.layoutManager = layoutManager
        rvThreads.adapter = adapter
    }

    override fun initCallbacks() {
        adapter.setClickListener { _, channel ->
            rootActivity!!.openChannel(channel)
        }
        srlRefresh.setOnRefreshListener {
            adapter.clearItems()
            showProgress(true)
            fetchChannels()
            srlRefresh.isRefreshing = false
        }
    }

    override fun initData() {
        showProgress(true)
        presenter.startChatClient(userManager.getCurrentUser()!!.identity,
                                  {
                                      UiUtils.log(this.javaClass.simpleName,
                                                  " -> Chat client started")
                                      setupChatClientListener()
                                      fetchChannels()
                                  },
                                  {
                                      UiUtils.toast(this, "Chat client start failed")
                                      showProgress(false)
                                  })
    }

    private fun setupChatClientListener() {
        presenter.setupChatListener(object : ChatClientListener {
            override fun onChannelDeleted(p0: Channel?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onChannelDeleted")
            }

            override fun onInvitedToChannelNotification(p0: String?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onInvitedToChannelNotification")
            }

            override fun onClientSynchronization(p0: ChatClient.SynchronizationStatus?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onClientSynchronization")
            }

            override fun onNotificationSubscribed() {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onNotificationSubscribed")
            }

            override fun onUserSubscribed(p0: User?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onUserSubscribed")
            }

            override fun onChannelUpdated(p0: Channel?, p1: Channel.UpdateReason?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onChannelUpdated")
            }

            override fun onRemovedFromChannelNotification(p0: String?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onRemovedFromChannelNotification")
            }

            override fun onNotificationFailed(p0: ErrorInfo?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onNotificationFailed")
            }

            override fun onChannelJoined(p0: Channel?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onChannelJoined")
            }

            override fun onChannelAdded(channel: Channel?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onChannelAdded")
            }

            override fun onChannelSynchronizationChange(p0: Channel?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onChannelSynchronizationChange")
            }

            override fun onUserUnsubscribed(p0: User?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onUserUnsubscribed")
            }

            override fun onAddedToChannelNotification(p0: String?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onAddedToChannelNotification")
            }

            override fun onChannelInvited(channel: Channel?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onChannelInvited")

                channel?.join(object : StatusListener() {
                    override fun onSuccess() {
                        UiUtils.log(this.javaClass.simpleName,
                                    " -> successfully autojoined channel")
                        adapter.addItem(channel)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        UiUtils.log(this.javaClass.simpleName, " -> error autojoin channel")
                    }
                })
            }

            override fun onNewMessageNotification(p0: String?, p1: String?, p2: Long) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onNewMessageNotification")
            }

            override fun onConnectionStateChange(p0: ChatClient.ConnectionState?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onConnectionStateChange")
            }

            override fun onError(p0: ErrorInfo?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onError")
            }

            override fun onUserUpdated(p0: User?, p1: User.UpdateReason?) {
                UiUtils.log(this@ChannelsListFragment::class.java.simpleName, " -> onUserUpdated")
            }
        })
    }

    private fun fetchChannels() {
        presenter.getPublicChannelsFirstPage(
            { paginator, channels ->
                UiUtils.log(this.javaClass.simpleName, " -> Channels first page fetched")
                adapter.setItems(channels)

                if (paginator.hasNextPage())
                    getChannelsNextPage(paginator)
                else
                    showProgress(false)
            },
            {
                UiUtils.toast(this, "Channels first page fetch error.\n${it.message}")
                showProgress(false)
            }
        )

        presenter.getUserChannelsFirstPage(
            { paginator, channels ->
                UiUtils.log(this.javaClass.simpleName, " -> Channels first page fetched")
                adapter.setItems(channels)

                if (paginator.hasNextPage())
                    getChannelsNextPage(paginator)
                else
                    showProgress(false)
            },
            {
                UiUtils.toast(this, "Channels first page fetch error.\n${it.message}")
                showProgress(false)
            }
        )
    }

    private fun getChannelsNextPage(paginator: Paginator<ChannelDescriptor>) {
        presenter.getChannelsNextPage(paginator,
                                      { paginatorInner, channels ->
                                          UiUtils.log(this.javaClass.simpleName, " -> Channels first page fetched")
                                          adapter.setItems(channels)

                                          if (paginator.hasNextPage()) {
                                              getChannelsNextPage(paginatorInner)
                                          } else {
                                              showProgress(false)
                                              presenter.getSubscribedChannels({
                                                                                  adapter.addItems(it)
                                                                              })
                                          }
                                      },
                                      {
                                          UiUtils.toast(this,
                                                        "Channels next page fetch error.\n${it.message}")
                                          showProgress(false)
                                      })
    }

    fun issueCreateChannel(interlocutor: String) {
        presenter.createChannel(interlocutor,
                                {
                                    adapter.addItem(it)
                                    rootActivity!!.dialogNewChannelCancel()
                                    rootActivity!!.openChannel(it)
                                    UiUtils.log(this.javaClass.simpleName,
                                                " -> Created channel success")
                                },
                                {
                                    UiUtils.toast(this, it.message ?: "Some error creating channel")
                                    rootActivity!!.dialogNewChannelStopLoading()
                                })
    }

    private fun showProgress(show: Boolean) {
        pbLoading?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }
}