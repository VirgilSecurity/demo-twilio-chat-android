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

package com.android.virgilsecurity.twiliodemo.data.remote.virgil

import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.remote.fuel.FuelHelper
import com.virgilsecurity.sdk.cards.Card
import com.virgilsecurity.sdk.cards.CardManager
import com.virgilsecurity.sdk.cards.validation.CardVerifier
import com.virgilsecurity.sdk.crypto.*
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException
import com.virgilsecurity.sdk.crypto.exceptions.EncryptionException
import com.virgilsecurity.sdk.jwt.accessProviders.CallbackJwtProvider
import com.virgilsecurity.sdk.storage.PrivateKeyStorage
import com.virgilsecurity.sdk.utils.ConvertionUtils

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

class VirgilHelper(cardCrypto: CardCrypto,
                   fuelHelper: FuelHelper,
                   cardVerifier: CardVerifier,
                   private val userManager: UserManager,
                   private val privateKeyStorage: PrivateKeyStorage) {

    private val cardManager: CardManager

    val virgilCrypto: VirgilCrypto
        get() = (cardManager.crypto as VirgilCardCrypto).virgilCrypto

    init {
        val tokenProvider = CallbackJwtProvider {
            val unixTimestamp = System.currentTimeMillis() / 1000L
            val privateKey = privateKeyStorage.load(userManager.getCurrentUser().identity)
                    .left as VirgilPrivateKey
            val rawSignature = virgilCrypto.generateSignature((userManager.getUserCard().identifier +
                                                               unixTimestamp)
                                                                      .toByteArray(),
                                                              privateKey)
            val signatureB64 = ConvertionUtils.toBase64String(rawSignature)
            val authHeader = userManager.getUserCard().identifier + "." +
                             unixTimestamp + "." +
                             signatureB64

            fuelHelper.getVirgilTokenSync(userManager.getCurrentUser().identity,
                                          authHeader).token
        }

        cardManager = CardManager(cardCrypto, tokenProvider, cardVerifier)
        cardManager.isRetryOnUnauthorized = true
    }

    fun publishCard(identity: String): Card {
        val keyPair = generateKeyPair()

        privateKeyStorage.store(keyPair.privateKey, identity, null)

        val cardModel = cardManager.generateRawCard(keyPair.privateKey,
                                                    keyPair.publicKey,
                                                    identity)
        return cardManager.publishCard(cardModel)
    }

    fun getCard(cardId: String): Card {
        return cardManager.getCard(cardId)
    }

    fun searchCards(identity: String): List<Card> {
        return cardManager.searchCards(identity)
    }

    fun generateKeyPair(): VirgilKeyPair {
        return virgilCrypto.generateKeys()
    }

    fun decrypt(text: String): String {
        val cipherData = ConvertionUtils.base64ToBytes(text)

        return try {
            val decryptedData = virgilCrypto.decrypt(cipherData,
                                                     privateKeyStorage.load(
                                                         userManager.getCurrentUser().identity)
                                                             .left as VirgilPrivateKey)
            ConvertionUtils.toString(decryptedData)
        } catch (e: CryptoException) {
            e.printStackTrace()
            "Message encrypted"
        }

    }

    fun encrypt(data: String, publicKeys: List<VirgilPublicKey>): String {
        val toEncrypt = ConvertionUtils.toBytes(data)
        return ConvertionUtils.toBase64String(virgilCrypto.encrypt(toEncrypt, publicKeys))
    }
}
