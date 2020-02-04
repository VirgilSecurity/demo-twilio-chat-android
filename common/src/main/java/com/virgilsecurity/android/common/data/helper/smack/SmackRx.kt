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

package com.virgilsecurity.android.common.data.helper.smack

import io.reactivex.Completable
import io.reactivex.Single
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import java.net.InetAddress

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
                    val inetAddress = InetAddress.getByName(xmppHost);
                    val config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(identity, password)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                            .setResource(resource)
                            .setXmppDomain(xmppHost)
                            .setHostAddress(inetAddress)
                            .setPort(xmppPort)
                            .build()

                    val connection = XMPPTCPConnection(config)
                    val abstractConnection = connection.connect()
                    abstractConnection.login()

                    it.onSuccess(connection)
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
