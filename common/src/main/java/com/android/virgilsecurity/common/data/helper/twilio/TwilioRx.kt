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

package com.android.virgilsecurity.common.data.helper.twilio

import android.content.Context
import com.android.virgilsecurity.base.data.api.ChannelsApi
import com.android.virgilsecurity.base.data.model.ChannelInfo.Companion.KEY_INTERLOCUTOR
import com.android.virgilsecurity.base.data.model.ChannelInfo.Companion.KEY_SENDER
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.common.data.helper.fuel.FuelHelper
import com.android.virgilsecurity.common.data.model.exception.ErrorInfoWrapper
import com.android.virgilsecurity.common.util.UiUtils
import com.twilio.accessmanager.AccessManager
import com.twilio.chat.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONObject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/30/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * TwilioRx
 */
class TwilioRx(private val fuelHelper: FuelHelper,
               private val userProperties: UserProperties) {

    private val channelExistsCode = 50307

    fun getToken(identity: String, authHeader: () -> String): Single<String> = Single.create<String> {
        val token = fuelHelper.getTwilioToken(identity, authHeader()).token
        it.onSuccess(token)
    }

    fun createClient(context: Context, token: String): Single<ChatClient> = Single.create<ChatClient> {
        val props = ChatClient.Properties.Builder().createProperties()
        ChatClient.setLogLevel(android.util.Log.DEBUG)

        ChatClient.create(context,
                          token,
                          props,
                          object : CallbackListener<ChatClient>() {
                              override fun onSuccess(chatClient: ChatClient) {
                                  it.onSuccess(chatClient)
                              }

                              override fun onError(errorInfo: ErrorInfo?) {
                                  UiUtils.log(this.javaClass.simpleName + "_log",
                                              " -> ${errorInfo?.message}")
                                  it.onError(ErrorInfoWrapper(
                                      errorInfo))
                              }
                          })
    }

    fun createAccessManager(token: String,
                            identity: String,
                            authHeader: () -> String,
                            chatClient: ChatClient): Completable =
            Flowable.create<String>({
                                        val accessManager = AccessManager(
                                            token,
                                            object : AccessManager.Listener {
                                                override fun onTokenExpired(accessManager: AccessManager?) {
                                                    val newToken = fuelHelper.getTwilioToken(
                                                        identity,
                                                        authHeader()).token
                                                    accessManager?.updateToken(newToken)
                                                }

                                                override fun onTokenWillExpire(accessManager: AccessManager?) {
                                                    val newToken = fuelHelper.getTwilioToken(
                                                        identity,
                                                        authHeader()).token
                                                    accessManager?.updateToken(newToken)
                                                }

                                                override fun onError(accessManager: AccessManager?, errorMessage: String?) {
                                                    it.onError(Throwable(errorMessage))
                                                }
                                            })

                                        accessManager.addTokenUpdateListener { token ->
                                            it.onNext(
                                                token)
                                        }
                                        it.setCancellable { }
                                    },
                                    BackpressureStrategy.BUFFER)
                    .flatMapCompletable { newToken ->
                        Completable.create {
                            chatClient.updateToken(newToken,
                                                   object : StatusListener() {
                                                       override fun onSuccess() {
                                                           it.onComplete()
                                                       }

                                                       override fun onError(errorInfo: ErrorInfo?) {
                                                           it.onError(
                                                               ErrorInfoWrapper(
                                                                   errorInfo))
                                                       }
                                                   })
                        }
                    }

    /**
     * Creates channel and immediately joins it and invites specified interlocutor to it,
     * in case of failure in any step destroys created channel.
     */
    fun createChannel(interlocutor: String,
                      channelName: String,
                      chatClient: ChatClient?): Completable = Single.create<Channel> {
        val attrs = JSONObject()
        attrs.put(KEY_SENDER, userProperties.currentUser!!.identity)
        attrs.put(KEY_INTERLOCUTOR, interlocutor)

        val builder = chatClient?.channels?.channelBuilder()

        builder?.withFriendlyName(interlocutor + "-" + userProperties.currentUser!!.identity)
                ?.withUniqueName(channelName)
                ?.withType(Channel.ChannelType.PRIVATE)
                ?.withAttributes(attrs)
                ?.build(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel) {
                        it.onSuccess(channel)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        if (errorInfo?.code == channelExistsCode)
                            it.onError(Throwable("ChannelInfo already exists"))
                        else
                            it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
    }.flatMapCompletable { channel ->
        Completable.concat(listOf(joinChannel(channel).toCompletable(),
                                  inviteToChannel(channel, interlocutor)))
                .onErrorResumeNext { destroyChannel(channel) }
    }

    /**
     * Joins given channel .
     */
    fun joinChannel(channel: Channel): Single<Channel> =
            Single.create {
                channel.join(object : StatusListener() {
                    override fun onSuccess() {
                        it.onSuccess(channel)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
            }

    /**
     * Invites specified interlocutor to given channel.
     */
    private fun inviteToChannel(channel: Channel, interlocutor: String): Completable =
            Completable.create {
                channel.members.inviteByIdentity(interlocutor, object : StatusListener() {
                    override fun onSuccess() {
                        it.onComplete()
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
            }

    private fun destroyChannel(channel: Channel): Completable =
            Completable.create {
                channel.destroy(object : StatusListener() {
                    override fun onSuccess() {
                        UiUtils.log(this.javaClass.simpleName, "Remove channel success after join")
                        it.onError(Throwable("Join channel failed. Destroyed channel successfully."))
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        UiUtils.log(this.javaClass.simpleName, "Remove channel error after join")
                        it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
            }

    fun getPublicChannels(chatClient: ChatClient?): Single<List<ChannelDescriptor>> =
            Single.create<List<ChannelDescriptor>> {
                val channelDescriptors = ArrayList<ChannelDescriptor>()

                val firstPaginator = getPublicChannelsFirstPaginator(chatClient).blockingGet()

                if (firstPaginator.hasNextPage()) {
                    channelDescriptors.addAll(getAllNextPages(PaginationResult(firstPaginator,
                                                                               firstPaginator.items)))
                } else {
                    channelDescriptors.addAll(firstPaginator.items)
                    it.onSuccess(channelDescriptors)
                }
            }

    fun getUserChannels(chatClient: ChatClient?): Single<List<ChannelDescriptor>> =
            Single.create<List<ChannelDescriptor>> {
                val channelDescriptors = ArrayList<ChannelDescriptor>()

                val firstPaginator = getUserChannelsFirstPaginator(chatClient).blockingGet()

                if (firstPaginator.hasNextPage()) {
                    channelDescriptors.addAll(getAllNextPages(PaginationResult(firstPaginator,
                                                                               firstPaginator.items)))
                } else {
                    channelDescriptors.addAll(firstPaginator.items)
                    it.onSuccess(channelDescriptors)
                }
            }

    data class PaginationResult(val paginator: Paginator<ChannelDescriptor>,
                                val channels: MutableList<ChannelDescriptor>) // TODO move out after tested

    private fun getAllNextPages(paginationResult: PaginationResult): List<ChannelDescriptor> {
        val newPaginator = getChannelsNextPaginator(paginationResult.paginator).blockingGet()

        return if (paginationResult.paginator.hasNextPage()) {
            val result = paginationResult.channels
            result.addAll(newPaginator.items)
            getAllNextPages(PaginationResult(newPaginator, result))
        } else {
            val result = paginationResult.channels
            result.addAll(newPaginator.items)
            result
        }
    }

    fun getPublicChannelsFirstPaginator(chatClient: ChatClient?): Single<Paginator<ChannelDescriptor>> {
        return Single.create<Paginator<ChannelDescriptor>> {
            chatClient?.channels?.getPublicChannelsList(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(paginator: Paginator<ChannelDescriptor>) {
                    it.onSuccess(paginator)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    it.onError(ErrorInfoWrapper(
                        errorInfo))
                }
            })
        }
    }

    fun getUserChannelsFirstPaginator(chatClient: ChatClient?): Single<Paginator<ChannelDescriptor>> {
        return Single.create<Paginator<ChannelDescriptor>> {
            chatClient?.channels?.getUserChannelsList(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(paginator: Paginator<ChannelDescriptor>) {
                    it.onSuccess(paginator)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    it.onError(ErrorInfoWrapper(
                        errorInfo))
                }
            })
        }
    }

    fun getChannelsNextPaginator(paginator: Paginator<ChannelDescriptor>): Single<Paginator<ChannelDescriptor>> {
        return Single.create<Paginator<ChannelDescriptor>> {
            paginator.requestNextPage(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(paginator: Paginator<ChannelDescriptor>) {
                    it.onSuccess(paginator)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    it.onError(ErrorInfoWrapper(
                        errorInfo))
                }
            })
        }
    }

    fun getChannelFromChannelDescriptor(channelDescriptor: ChannelDescriptor): Single<Channel> =
            Single.create<Channel> {
                channelDescriptor.getChannel(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel?) {
                        it.onSuccess(channel!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(
                            errorInfo))
                    }
                })
            }

    fun getMessages(channel: Channel): Single<MutableList<Message>> =
            Single.create<MutableList<Message>> {
                channel.getMessagesCount(object : CallbackListener<Long>() {
                    override fun onSuccess(messagesCount: Long?) {
                        channel.messages.getLastMessages(messagesCount!!.toInt(),
                                                         object : CallbackListener<MutableList<Message>>() {
                                                             override fun onSuccess(messages: MutableList<Message>?) {
                                                                 it.onSuccess(messages!!)
                                                             }

                                                             override fun onError(errorInfo: ErrorInfo?) {
                                                                 it.onError(ErrorInfoWrapper(
                                                                     errorInfo))
                                                             }
                                                         })
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(
                            errorInfo))
                    }
                })
            }

    fun sendMessage(channel: Channel,
                    body: String,
                    interlocutor: String): Single<Message> =
            Single.create<Message> {
                val attributes = JSONObject()
                attributes.put(KEY_SENDER, userProperties.currentUser!!.identity)
                attributes.put(KEY_INTERLOCUTOR, interlocutor)

                val message = Message.options().withBody(body).withAttributes(attributes)
                channel.messages.sendMessage(message, object : CallbackListener<Message>() {
                    override fun onSuccess(message: Message?) {
                        it.onSuccess(message!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(
                            errorInfo))
                    }
                })
            }

    fun getChannelBySid(chatClient: ChatClient?, channelSid: String): Single<Channel> =
            Single.create<Channel> {
                chatClient?.channels?.getChannel(channelSid, object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel?) {
                        it.onSuccess(channel!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(
                            errorInfo))
                    }
                })
            }

    fun observeChannelsChanges(chatClient: ChatClient?): Flowable<ChannelsApi.ChannelsChanges> =
            Flowable.create({
                                chatClient?.setListener(object : ChatClientListener {
                                    override fun onChannelDeleted(p0: Channel?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ChannelDeleted(p0))
                                    }

                                    override fun onInvitedToChannelNotification(p0: String?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.InvitedToChannelNotification(
                                            p0))
                                    }

                                    override fun onClientSynchronization(p0: ChatClient.SynchronizationStatus?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ClientSynchronization(
                                            p0))
                                    }

                                    override fun onNotificationSubscribed() {
                                        it.onNext(ChannelsApi.ChannelsChanges.NotificationSubscribed)
                                    }

                                    override fun onUserSubscribed(p0: User?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.UserSubscribed(p0))
                                    }

                                    override fun onChannelUpdated(p0: Channel?, p1: Channel.UpdateReason?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ChannelUpdated(p0,
                                                                                             p1))
                                    }

                                    override fun onRemovedFromChannelNotification(p0: String?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.RemovedFromChannelNotification(
                                            p0))
                                    }

                                    override fun onNotificationFailed(p0: ErrorInfo?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.NotificationFailed(p0))
                                    }

                                    override fun onChannelJoined(p0: Channel?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ChannelJoined(p0))
                                    }

                                    override fun onChannelAdded(p0: Channel?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ChannelAdded(p0))
                                    }

                                    override fun onChannelSynchronizationChange(p0: Channel?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ChannelSynchronizationChange(
                                            p0))
                                    }

                                    override fun onUserUnsubscribed(p0: User?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.UserUnsubscribed(p0))
                                    }

                                    override fun onAddedToChannelNotification(p0: String?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.AddedToChannelNotification(
                                            p0))
                                    }

                                    override fun onChannelInvited(p0: Channel?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ChannelInvited(p0))
                                    }

                                    override fun onNewMessageNotification(p0: String?, p1: String?, p2: Long) {
                                        it.onNext(ChannelsApi.ChannelsChanges.NewMessageNotification(
                                            p0,
                                            p1,
                                            p2))
                                    }

                                    override fun onConnectionStateChange(p0: ChatClient.ConnectionState?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.ConnectionStateChange(
                                            p0))
                                    }

                                    override fun onError(p0: ErrorInfo?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.Error(p0))
                                    }

                                    override fun onUserUpdated(p0: User?, p1: User.UpdateReason?) {
                                        it.onNext(ChannelsApi.ChannelsChanges.UserUpdated(p0, p1))
                                    }

                                })
                            }, BackpressureStrategy.BUFFER)
}
