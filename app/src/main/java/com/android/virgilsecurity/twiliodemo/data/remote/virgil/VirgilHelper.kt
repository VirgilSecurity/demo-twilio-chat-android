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

import com.android.virgilsecurity.twiliodemo.R.string.identity
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
import io.reactivex.Single

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

class VirgilHelper(private val cardManager: CardManager,
                   private val userManager: UserManager,
                   private val privateKeyStorage: PrivateKeyStorage,
                   private val virgilRx: VirgilRx) {

    val cardCrypto: CardCrypto = cardManager.crypto

    val virgilCrypto: VirgilCrypto
        get() = (cardManager.crypto as VirgilCardCrypto).virgilCrypto

    init {
        cardManager.isRetryOnUnauthorized = true
    }

    fun publishCard(identity: String): Single<Card> {
        val keyPair = generateKeyPair()
        storePrivateKey(keyPair.privateKey, identity, null)
        val cardModel = generateRawCard(keyPair, identity)
        return virgilRx.publishCard(cardModel)
    }

    fun getCard(cardId: String): Single<Card> {
        return virgilRx.getCard(cardId)
    }

    fun searchCards(identity: String): Single<List<Card>> {
        return virgilRx.searchCards(identity)
    }

    fun generateKeyPair(): VirgilKeyPair {
        return virgilCrypto.generateKeys()
    }

    fun generateRawCard(keyPair: VirgilKeyPair, identity: String) =
            cardManager.generateRawCard(keyPair.privateKey,
                                        keyPair.publicKey,
                                        identity)

    fun storePrivateKey(privateKey: PrivateKey, identity: String, meta: Map<String, String>? = null) =
            privateKeyStorage.store(privateKey, identity, meta)

    fun ifExistsPrivateKey(identity: String) = privateKeyStorage.exists(identity)

    fun deletePrivateKey(identity: String) = privateKeyStorage.delete(identity)

    fun decrypt(text: String): String {
        val cipherData = ConvertionUtils.base64ToBytes(text)

        return try {
            val decryptedData = virgilCrypto.decrypt(cipherData,
                                                     privateKeyStorage.load(
                                                         userManager.getCurrentUser()!!.identity)
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
