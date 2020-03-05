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

package com.virgilsecurity.android.bcommon.data.helper.smack

import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.bcommon.data.remote.channels.ChannelIdGenerator
import com.virgilsecurity.sdk.utils.ConvertionUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import java.net.InetAddress
import java.util.*

/**
 * SmackRx
 */
class SmackRx {

    fun startClient(identity: String,
                    password: String,
                    xmppHost: String,
                    resource: String,
                    xmppPort: Int): Single<XMPPTCPConnection> =
            Single.create {
                try {
                    val inetAddress = InetAddress.getByName(xmppHost)
                    val config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(identity, password)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                            .setResource(resource)
                            .setXmppDomain(xmppHost)
                            .setHostAddress(inetAddress)
                            .setPort(xmppPort)
                            .build()

                    val connection = XMPPTCPConnection(config)

                    it.onSuccess(connection)
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }

    fun login(connection: XMPPTCPConnection): Completable =
            Completable.create {
                try {
                    val abstractConnection = connection.connect()
                    abstractConnection.login()
                    it.onComplete()
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }

    fun initChatManager(connection: XMPPTCPConnection): Single<ChatManager> =
            Single.create {
                try {
                    val chatManager = ChatManager.getInstanceFor(connection)

                    it.onSuccess(chatManager)
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }

    fun initRoster(connection: XMPPTCPConnection): Single<Roster> =
            Single.create {
                try {
                    val roster = Roster.getInstanceFor(connection)

                    it.onSuccess(roster)
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }

    fun initReconnectionManager(connection: XMPPTCPConnection): Single<ReconnectionManager> =
            Single.create {
                try {
                    val reconnectionManager = ReconnectionManager.getInstanceFor(connection)

                    it.onSuccess(reconnectionManager)
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }

    fun observeChatMessages(
            chatManager: ChatManager,
            currentIdentity: String,
            channelIdGenerator: ChannelIdGenerator
    ): Flowable<Pair<ChannelMeta, MessageMeta>> =
            Flowable.create(
                {
                    chatManager.addIncomingListener { from, message, chat ->
                        try {
                            val sender = from.toString().split('@')[0]
                            val channelId =
                                    channelIdGenerator.generatedChannelId(sender,
                                                                          currentIdentity)
                            val channelMeta = ChannelMeta(channelId,
                                                          sender,
                                                          currentIdentity)

                            message.setStanzaId()

                            val json = ConvertionUtils.base64ToString(message.body!!)
                            val map = ConvertionUtils.deserializeMapFromJson(json)

                            val messageMeta = MessageMeta(message.stanzaId,
                                                          map["ciphertext"]!!,
                                                          sender,
                                                          channelId,
                                                          false,
                                                          map["date"]!!.toLongOrNull()!!)

                            it.onNext(Pair(channelMeta, messageMeta))
                            // FIXME where to place onComplete?
                        } catch (throwable: Throwable) {
                            it.onError(throwable)
                        }
                    }
                },
                BackpressureStrategy.ERROR)

    fun sendMessage(
            chatManager: ChatManager,
            body: String,
            date: Long,
            interlocutor: String,
            xmppHost: String,
            currentIdentity: String,
            channelIdGenerator: ChannelIdGenerator
    ): Single<MessageMeta> = Single.create {
        try {
            val channelId = channelIdGenerator.generatedChannelId(interlocutor,
                                                                  currentIdentity)
            val jid = JidCreate.entityBareFrom("$interlocutor@$xmppHost")
            val chat = chatManager.chatWith(jid)
            val stanza = Message()
            stanza.setStanzaId()

            stanza.body = ConvertionUtils.toBase64String(ConvertionUtils.serializeToJson(mapOf(
                    "date" to date,
                    "ciphertext" to body
            )))


            stanza.type = Message.Type.chat
            stanza.thread = channelId
            chat.send(stanza)

            val messMeta = MessageMeta(
                    stanza.stanzaId,
                    body,
                    currentIdentity,
                    channelId,
                    false,
                    date
            )

            it.onSuccess(messMeta)
        } catch (throwable: Throwable) {
            it.onError(throwable)
        }
    }

    fun stopClient(connection: XMPPTCPConnection): Completable =
            Completable.create {
                try {
                    connection.disconnect()
                    it.onComplete()
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }
}
