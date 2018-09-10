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

package com.android.virgilsecurity.common.data.helper.fuel

import com.android.virgilsecurity.base.data.model.SignInResponse
import com.android.virgilsecurity.base.data.model.TokenResponse
import com.android.virgilsecurity.base.extension.toObject
import com.android.virgilsecurity.common.data.model.request.SignInRequest
import com.android.virgilsecurity.common.data.model.request.SignUpRequest
import com.android.virgilsecurity.common.data.model.request.TokenRequest
import com.android.virgilsecurity.common.util.UiUtils
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.google.gson.Gson
import com.virgilsecurity.sdk.cards.model.RawSignedModel
import com.virgilsecurity.sdk.utils.ConvertionUtils

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * FuelHelper helps to work with network requests.
 * @constructor If [baseUrl] is `null` - localhost address will be used (http://10.0.2.2:3000)
 */
class FuelHelper(private val baseUrl: String? = "https://messenger-dev.virgilsecurity.com") {

    private val keyContentType = "Content-Type"
    private val keyAppJson = "application/json"

    private val virgilTokenPath = "get-virgil-jwt"
    private val twilioTokenPath = "get-twilio-jwt"
    private val signUpPath = "signup"
    private val signInPath = "signin"

    private val gson: Gson

    init {
        FuelManager.instance.basePath = baseUrl
        FuelManager.instance.addResponseInterceptor(responseInterceptor())
        gson = Gson()
    }

    private fun responseInterceptor() =
            { next: (Request, Response) -> Response ->
                { req: Request, res: Response ->
                    UiUtils.log(this.javaClass.simpleName, " -> Request\n$req")
                    UiUtils.log(this.javaClass.simpleName, " -> Response\n$res")

                    next(req, res)
                }
            }

    fun getVirgilToken(identity: String, authHeader: String) = Fuel.post(virgilTokenPath)
            .header("Authorization" to "Bearer $authHeader")
            .header(keyContentType to keyAppJson)
            .body(gson.toJson(TokenRequest(identity)))
            .responseString()
            .third
            .get()
            .toObject(TokenResponse::class.java)

    fun getTwilioToken(identity: String, authHeader: String) = Fuel.post(twilioTokenPath)
            .header("Authorization" to "Bearer $authHeader")
            .header(keyContentType to keyAppJson)
            .body(gson.toJson(TokenRequest(identity)))
            .responseString()
            .third
            .get()
            .toObject(TokenResponse::class.java)

    fun signUp(rawCard: RawSignedModel) = Fuel.post(signUpPath)
            .header(keyContentType to keyAppJson)
            .body(ConvertionUtils.serializeToJson(SignUpRequest(rawCard)))
            .responseString()
            .third
            .get()
            .toObject(SignInResponse::class.java)

    fun signIn(identity: String) = Fuel.post(signInPath)
            .header(keyContentType to keyAppJson)
            .body(ConvertionUtils.serializeToJson(SignInRequest(identity)))
            .responseString()
            .third
            .get()
            .toObject(SignInResponse::class.java)
}