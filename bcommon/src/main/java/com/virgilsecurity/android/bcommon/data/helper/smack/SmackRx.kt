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

import android.provider.Settings.Secure.ANDROID_ID
import android.provider.Settings.Secure.getString
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.base.util.GeneralConstants.MESSAGE_VERSION
import com.virgilsecurity.android.base.util.GeneralConstants.PUSHES_NODE
import com.virgilsecurity.android.base.util.GeneralConstants.SMACK_PUSH_HOST
import com.virgilsecurity.android.bcommon.data.remote.channels.ChannelIdGenerator
import com.virgilsecurity.android.bcommon.util.JsonUtils
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
import org.jivesoftware.smackx.push_notifications.PushNotificationsManager
import org.jxmpp.jid.impl.JidCreate
import java.net.InetAddress
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.virgilsecurity.android.bcommon.data.helper.fuel.apiSuffix
import com.virgilsecurity.common.util.toHexString
import org.jivesoftware.smack.packet.Presence

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

                    // init push notifications
                    val pushNotificationsManager = PushNotificationsManager.getInstanceFor(connection)

                    val pushSupport = pushNotificationsManager.isSupported
                    Log.d("[SMACK]", "Push notifications support: $pushSupport.")
                    if (pushSupport) {
                        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w("[FIREBASE]", "getInstanceId failed", task.exception)
                                it.onError(task.exception!!)
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val token = task.result?.token ?: return@OnCompleteListener

                            val res = pushNotificationsManager.enable(
                                    JidCreate.from(SMACK_PUSH_HOST),
                                    PUSHES_NODE,
                                    hashMapOf(
                                            "device_id" to token,
                                            "service" to "fcm"
                                    )
                            )

                            Log.d("[SMACK]", "Enabled push notifications: $res")

                            it.onComplete()
                        })
                    }
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

                            val map = JsonUtils.stringToMap(json)

                            val messageMeta = MessageMeta(message.stanzaId,
                                    map["ciphertext"]!! as String,
                                    sender,
                                    channelId,
                                    false,
                                    (map["date"]!! as Double).toLong(),
                                    map["version"] as? String ?: "v1")

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
                    "ciphertext" to body,
                    "version" to MESSAGE_VERSION
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
                    date,
                    MESSAGE_VERSION
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