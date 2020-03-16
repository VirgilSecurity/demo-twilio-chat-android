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

import android.util.Log
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.bcommon.data.helper.fuel.apiSuffix
import com.virgilsecurity.android.bcommon.data.remote.channels.ChannelIdGenerator
import io.reactivex.Completable
import io.reactivex.Flowable
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionListener
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection


/**
 * SmackHelper
 */
class SmackHelper(
        private val smackRx: SmackRx,
        private val userProperties: UserProperties,
        private val channelIdGenerator: ChannelIdGenerator
) {

    private var connection: XMPPTCPConnection? = null
    private lateinit var chatManager: ChatManager
    private lateinit var roster: Roster
    private lateinit var reconnectionManager: ReconnectionManager

    fun startClient(identity: String, password: String): Completable {
        if (connection == null) {
            return smackRx.startClient(identity,
                                       password,
                                       XMPP_HOST,
                                       RESOURCE_ANDROID,
                                       XMPP_PORT)
                    .map {
                        it.addConnectionListener(object : ConnectionListener {
                            override fun connected(connection: XMPPConnection?) {
                                Log.d("[SMACK]", "Connected.")
                            }

                            override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                                Log.d("[SMACK]", "Authenticated.")
                            }

                            override fun connectionClosedOnError(e: Exception) {
                                Log.d("[SMACK]", "Connection closed: ${e}.")
                            }

                            override fun connectionClosed() {
                                Log.d("[SMACK]", "Connection closed.")
                            }
                        })

                        this@SmackHelper.connection = it
                    }
                    .flatMap { smackRx.initRoster(connection!!) }
                    .map { this@SmackHelper.roster = it }
                    .flatMap { smackRx.initChatManager(connection!!) }
                    .map { this@SmackHelper.chatManager = it }
                    .flatMap { smackRx.initReconnectionManager(connection!!) }
                    .map {
                        this@SmackHelper.reconnectionManager = it
                        it.enableAutomaticReconnection()
                        it.addReconnectionListener(object : ReconnectionListener {
                            override fun reconnectingIn(seconds: Int) {
                                Log.d("[SMACK]", "Reconnecting in: $seconds seconds.")
                            }

                            override fun reconnectionFailed(e: Exception) {
                                Log.d("[SMACK]", "Reconnection failed: ${e}.")
                            }
                        })
                    }
                    .flatMapCompletable { smackRx.login(connection!!) }
        } else {
            return Completable.error(IllegalStateException("Already initialized."))
        }
    }

    fun stopClient(): Completable {
        if (connection == null) {
            return Completable.error(IllegalStateException("Not initialized yet."))
        } else {
            return smackRx.stopClient(connection!!)
                    .doOnComplete { this@SmackHelper.connection = null }
        }
    }

    fun observeChatMessages(): Flowable<Pair<ChannelMeta, MessageMeta>> =
            smackRx.observeChatMessages(chatManager,
                                        userProperties.currentUser!!.identity,
                                        channelIdGenerator)

    fun sendMessage(interlocutor: String, body: String, date: Long) =
            smackRx.sendMessage(chatManager,
                                body,
                                date,
                                interlocutor,
                                XMPP_HOST,
                                userProperties.currentUser!!.identity,
                                channelIdGenerator)

    companion object {
        private const val XMPP_HOST = "xmpp$apiSuffix.virgilsecurity.com"
        private const val XMPP_PORT = 5222
        private const val RESOURCE_ANDROID = "Android"
    }
}