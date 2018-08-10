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

import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.common.data.helper.twilio.TwilioHelper
import com.android.virgilsecurity.common.data.helper.virgil.VirgilHelper
import com.android.virgilsecurity.common.util.AuthUtils

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
 * ChannelsListPresenter
 */
class ChannelsListPresenter(private val twilioHelper: TwilioHelper,
                            private val virgilHelper: VirgilHelper,
                            private val userProperties: UserProperties,
                            private val utils: AuthUtils) {

//    private var chatClient: ChatClient? = null
//    private val compositeDisposable = CompositeDisposable()
//
//    fun startChatClient(identity: String,
//                        onStartClientSuccess: () -> Unit,
//                        onStartClientError: (Throwable) -> Unit) {
//        val startChatClientDisposable =
//                twilioHelper.startChatClient(identity
//                ) {
//                    utils.generateAuthHeader()
//                }
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(
//                            onComplete = {
//                                onStartClientSuccess()
//                            },
//                            onError = {
//                                onStartClientError(it)
//                            })
//
//        compositeDisposable += startChatClientDisposable
//    }

//    fun createChannel(interlocutor: String,
//                      onCreateChannelSuccess: (Channel) -> Unit,
//                      onCreateChannelError: (Throwable) -> Unit) {
//        val createChannelDisposable =
//                virgilHelper.searchCards(interlocutor)
//                        .flatMap {
//                            when {
//                                it.size == 1 -> twilioHelper.createChannel(interlocutor,
//                                                                           generateNewChannelId(interlocutor))
//                                it.isEmpty() -> throw Throwable("This user does not exists")
//                                else -> throw Throwable("Cards count by identity \'$interlocutor\' " +
//                                                        "is: ${it.size}. Must be 1")
//                            }
//                        }.observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(
//                            onSuccess = {
//                                onCreateChannelSuccess(it)
//                            },
//                            onError = {
//                                onCreateChannelError(it)
//                            })
//
//
//        compositeDisposable += createChannelDisposable
//    }

//    fun publicChannelsFirstPage(onGetChannelsSuccess: (Paginator<ChannelDescriptor>, MutableList<Channel>) -> Unit,
//                                   onGetChannelsError: (Throwable) -> Unit) {
//        val getPublicChannelsFirstPageDisposable =
//                twilioHelper.publicChannelsFirstPage()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(
//                            onSuccess = {
//                                val channelsDescriptors = ArrayList<ChannelDescriptor>()
//                                it.items.forEach { channel -> channelsDescriptors.add(channel) }
//                                val channels = ArrayList<Channel>()
//
//                                Observable.fromIterable(channelsDescriptors)
//                                        .flatMap {
//                                            twilioHelper.getChannelFromChannelDescriptor(it)
//                                        }.flatMap {
//                                            channels.add(it)
//
//                                            Observable.create<Unit> { e ->
//                                                it.join(object : StatusListener() {
//                                                    override fun onSuccess() {
//                                                        e.onComplete()
//                                                    }
//
//                                                    override fun onError(errorInfo: ErrorInfo?) {
//                                                        if (it.status == Channel.ChannelStatus.JOINED) {
//                                                            e.onComplete()
//                                                        } else {
//                                                            e.onError(
//                                                                ErrorInfoWrapper(
//                                                                    errorInfo))
//                                                        }
//                                                    }
//                                                })
//                                            }
//                                        }.subscribeBy(
//                                            onComplete = {
//                                                onGetChannelsSuccess(it, channels)
//                                            },
//                                            onError = {
//                                                onGetChannelsError(it)
//                                            }
//                                        )
//                            },
//                            onError = {
//                                onGetChannelsError(it)
//                            }
//                        )
//
//        compositeDisposable += getPublicChannelsFirstPageDisposable
//    }

//    fun getUserChannelsFirstPage(onGetChannelsSuccess: (Paginator<ChannelDescriptor>, MutableList<Channel>) -> Unit,
//                                 onGetChannelsError: (Throwable) -> Unit) {
//        val getUserChannelsFirstPageDisposable =
//                twilioHelper.getUserChannelsFirstPage()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(
//                            onSuccess = {
//                                val channelsDescriptors = ArrayList<ChannelDescriptor>()
//                                it.items.forEach { channel -> channelsDescriptors.add(channel) }
//                                val channels = ArrayList<Channel>()
//
//                                Observable.fromIterable(channelsDescriptors)
//                                        .flatMap {
//                                            twilioHelper.getChannelFromChannelDescriptor(it)
//                                        }.flatMap {
//                                            channels.add(it)
//
//                                            Observable.create<Unit> { e ->
//                                                it.join(object : StatusListener() {
//                                                    override fun onSuccess() {
//                                                        e.onComplete()
//                                                    }
//
//                                                    override fun onError(errorInfo: ErrorInfo?) {
//                                                        if (it.status == Channel.ChannelStatus.JOINED) {
//                                                            e.onComplete()
//                                                        } else {
//                                                            e.onError(
//                                                                ErrorInfoWrapper(
//                                                                    errorInfo))
//                                                        }
//                                                    }
//                                                })
//                                            }
//                                        }.subscribeBy(
//                                            onComplete = {
//                                                onGetChannelsSuccess(it, channels)
//                                            },
//                                            onError = {
//                                                onGetChannelsError(it)
//                                            }
//                                        )
//                            },
//                            onError = {
//                                onGetChannelsError(it)
//                            }
//                        )
//
//        compositeDisposable += getUserChannelsFirstPageDisposable
//    }

//    fun getChannelsNextPage(paginator: Paginator<ChannelDescriptor>,
//                            onGetNextChannelsSuccess: (Paginator<ChannelDescriptor>, MutableList<Channel>) -> Unit,
//                            onGetNextChannelsError: (Throwable) -> Unit) {
//        val getChannelsNextPageDisposable =
//                twilioHelper.getChannelsNextPage(paginator)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(
//                            onSuccess = {
//                                val channelsDescriptors = ArrayList<ChannelDescriptor>()
//                                it.items.forEach { channel -> channelsDescriptors.add(channel) }
//                                val channels = ArrayList<Channel>()
//
//                                Observable.fromIterable(channelsDescriptors)
//                                        .flatMap {
//                                            twilioHelper.getChannelFromChannelDescriptor(it)
//                                        }.map {
//                                            channels.add(it)
//                                        }.subscribeBy(
//                                            onComplete = {
//                                                onGetNextChannelsSuccess(it, channels)
//                                            },
//                                            onError = {
//                                                onGetNextChannelsError(it)
//                                            }
//                                        )
//                            },
//                            onError = {
//                                onGetNextChannelsError(it)
//                            }
//                        )
//
//        compositeDisposable += getChannelsNextPageDisposable
//    }

//    private fun generateNewChannelId(interlocutor: String): String {
//        val userMe = userProperties.currentUser!!.identity
//        val concatenatedHashedUsersData: ByteArray
//
//        concatenatedHashedUsersData = if (userMe >= interlocutor) {
//            virgilHelper.virgilCrypto
//                    .generateHash((userMe + interlocutor).toByteArray(),
//                                  HashAlgorithm.SHA256)
//        } else {
//            virgilHelper.virgilCrypto
//                    .generateHash((interlocutor + userMe).toByteArray(),
//                                  HashAlgorithm.SHA256)
//        }
//
//        return ConvertionUtils.toHex(concatenatedHashedUsersData).toLowerCase()
//    }
//
//    fun disposeAll() {
//        compositeDisposable.clear()
//    }
//
//    fun stopChatClient() {
//        twilioHelper.stopChatClient()
//    }
//
//    fun setupChatListener(chatClientListener: ChatClientListener) =
//            twilioHelper.setChatListener(chatClientListener)
//
//    fun getSubscribedChannels(onGetSubscribedChannels: (MutableList<Channel>) -> Unit) =
//            twilioHelper.getSubscribedChannels(onGetSubscribedChannels)
}