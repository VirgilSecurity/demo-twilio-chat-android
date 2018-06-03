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

package com.android.virgilsecurity.twiliodemo.data.remote.twilio

import android.content.Context
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.model.exception.ErrorInfoWrapper
import com.android.virgilsecurity.twiliodemo.data.remote.fuel.FuelHelper
import com.android.virgilsecurity.twiliodemo.util.Constants
import com.android.virgilsecurity.twiliodemo.util.Constants.KEY_RECEIVER
import com.android.virgilsecurity.twiliodemo.util.Constants.KEY_SENDER
import com.android.virgilsecurity.twiliodemo.util.UiUtils
import com.twilio.accessmanager.AccessManager
import com.twilio.chat.*
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
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
               private val userManager: UserManager) {

    private val channelExistsCode = 50307

    fun getToken(identity: String, authHeader: () -> String): Single<String> = Single.create<String> {
        val token = fuelHelper.getTwilioTokenSync(identity, authHeader()).token
        it.onSuccess(token)
    }.subscribeOn(Schedulers.io())

    fun createClient(context: Context, token: String): Single<ChatClient> = Single.create<ChatClient> {
        val props = ChatClient.Properties.Builder().createProperties()

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
                                  it.onError(ErrorInfoWrapper(errorInfo))
                              }
                          })
    }.subscribeOn(Schedulers.io())

    fun createAccessManager(token: String,
                            identity: String,
                            authHeader: () -> String,
                            chatClient: ChatClient): Completable =
            Flowable.create<String>({
                                        val accessManager = AccessManager(
                                            token,
                                            object : AccessManager.Listener {
                                                override fun onTokenExpired(accessManager: AccessManager?) {
                                                    val newToken = fuelHelper.getTwilioTokenSync(
                                                        identity,
                                                        authHeader()).token
                                                    accessManager?.updateToken(newToken)
                                                }

                                                override fun onTokenWillExpire(accessManager: AccessManager?) {
                                                    val newToken = fuelHelper.getTwilioTokenSync(
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
                        Completable.create({
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
                                           })
                    }.subscribeOn(Schedulers.io())

    fun createChannel(interlocutor: String,
                      channelName: String,
                      chatClient: ChatClient?): Single<Channel> = Single.create<Channel> {
        val attrs = JSONObject()
        attrs.put(KEY_SENDER, userManager.getCurrentUser()!!.identity)
        attrs.put(KEY_RECEIVER, interlocutor)

        val builder = chatClient?.channels?.channelBuilder()

        builder?.withFriendlyName(interlocutor + "-" + userManager.getCurrentUser()!!.identity)
                ?.withUniqueName(channelName)
                ?.withType(Channel.ChannelType.PRIVATE)
                ?.withAttributes(attrs)
                ?.build(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel) {
                        it.onSuccess(channel)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        if (errorInfo?.code == channelExistsCode)
                            it.onError(Throwable("Channel already exists"))
                        else
                            it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
    }.flatMap { channel ->
        Completable.create(
            { e1 ->
                channel.join(object : StatusListener() {
                    override fun onSuccess() {
                        e1.onComplete()
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        e1.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
            }).doOnError { throwable ->
            channel.destroy(object : StatusListener() {
                override fun onSuccess() {
                    UiUtils.log(this.javaClass.simpleName,
                                "Remove channel success after join")
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    UiUtils.log(this.javaClass.simpleName,
                                "Remove channel error after join")
                }
            })
        }.doOnComplete {
            Completable.create(
                { e2 ->
                    channel.members
                            .inviteByIdentity(interlocutor,
                                              object : StatusListener() {
                                                  override fun onSuccess() {
                                                      e2.onComplete()
                                                  }

                                                  override fun onError(errorInfo: ErrorInfo?) {
                                                      e2.onError(ErrorInfoWrapper(
                                                          errorInfo))
                                                  }
                                              })
                }).doOnError { throwable ->
                channel.destroy(object : StatusListener() {
                    override fun onSuccess() {
                        UiUtils.log(this.javaClass.simpleName,
                                    "Remove channel success")
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        UiUtils.log(this.javaClass.simpleName,
                                    "Remove channel error")
                    }
                })
            }.subscribe()
        }.toSingle {
            channel
        }

    }.subscribeOn(Schedulers.io())

    fun getPublicChannelsFirstPaginator(chatClient: ChatClient?): Single<Paginator<ChannelDescriptor>> {
        return Single.create<Paginator<ChannelDescriptor>> {
            chatClient?.channels?.getPublicChannelsList(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(paginator: Paginator<ChannelDescriptor>) {
                    it.onSuccess(paginator)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    it.onError(ErrorInfoWrapper(errorInfo))
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun getUserChannelsFirstPaginator(chatClient: ChatClient?): Single<Paginator<ChannelDescriptor>> {
        return Single.create<Paginator<ChannelDescriptor>> {
            chatClient?.channels?.getUserChannelsList(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(paginator: Paginator<ChannelDescriptor>) {
                    it.onSuccess(paginator)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    it.onError(ErrorInfoWrapper(errorInfo))
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun getChannelsNextPaginator(paginator: Paginator<ChannelDescriptor>): Single<Paginator<ChannelDescriptor>> {
        return Single.create<Paginator<ChannelDescriptor>> {
            paginator.requestNextPage(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(paginator: Paginator<ChannelDescriptor>) {
                    it.onSuccess(paginator)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    it.onError(ErrorInfoWrapper(errorInfo))
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun getChannelFromChannelDescriptor(channelDescriptor: ChannelDescriptor): Single<Channel> =
            Single.create<Channel> {
                channelDescriptor.getChannel(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel?) {
                        it.onSuccess(channel!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
            }

//    fun getMessages(channel: Channel): Single<MutableList<Message>> =
//            Single.create<MutableList<Message>> {
//                channel.getMessagesCount(object : CallbackListener<Long>() {
//                    override fun onSuccess(messagesCount: Long?) {
//                        channel.messages.getLastMessages(messagesCount!!.toInt(),
//                                                         object : CallbackListener<MutableList<Message>>() {
//                                                             override fun onSuccess(messages: MutableList<Message>?) {
//                                                                 it.onSuccess(messages!!)
//                                                             }
//
//                                                             override fun onError(errorInfo: ErrorInfo?) {
//                                                                 it.onError(ErrorInfoWrapper(
//                                                                     errorInfo))
//                                                             }
//                                                         })
//                    }
//
//                    override fun onError(errorInfo: ErrorInfo?) {
//                        it.onError(ErrorInfoWrapper(errorInfo))
//                    }
//                })
//            }.subscribeOn(Schedulers.io())

    fun getMessages(channel: Channel): Single<MutableList<Message>> =
            Single.create<MutableList<Message>> {
                channel.messages.getLastMessages(50,
                                                 object : CallbackListener<MutableList<Message>>() {
                                                     override fun onSuccess(messages: MutableList<Message>?) {
                                                         it.onSuccess(messages!!)
                                                     }

                                                     override fun onError(errorInfo: ErrorInfo?) {
                                                         it.onError(ErrorInfoWrapper(
                                                             errorInfo))
                                                     }
                                                 })
            }.subscribeOn(Schedulers.io())

    fun sendMessage(channel: Channel,
                    body: String,
                    interlocutor: String): Single<Message> =
            Single.create<Message> {
                val attributes = JSONObject()
                attributes.put(Constants.KEY_SENDER, userManager.getCurrentUser()!!.identity)
                attributes.put(Constants.KEY_RECEIVER, interlocutor)

                val message = Message.options().withBody(body).withAttributes(attributes)
                channel.messages.sendMessage(message, object : CallbackListener<Message>() {
                    override fun onSuccess(message: Message?) {
                        it.onSuccess(message!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
            }.subscribeOn(Schedulers.io())
}