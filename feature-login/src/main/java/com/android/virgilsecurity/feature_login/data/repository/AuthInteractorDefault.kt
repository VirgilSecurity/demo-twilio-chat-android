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

package com.android.virgilsecurity.feature_login.data.repository

import android.content.Context
import com.android.virgilsecurity.base.data.api.AuthApi
import com.android.virgilsecurity.base.data.api.UserManager
import com.android.virgilsecurity.base.data.model.SignInResponse
import com.android.virgilsecurity.common.data.model.UserVT
import com.android.virgilsecurity.common.data.remote.virgil.VirgilHelper
import com.android.virgilsecurity.common.data.repository.AuthInteractor
import com.android.virgilsecurity.feature_login.R
import io.reactivex.Single

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/6/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * AuthInteractorDefault
 */
class AuthInteractorDefault(
        private val authApi: AuthApi,
        private val virgilHelper: VirgilHelper,
        private val userManager: UserManager,
        private val context: Context
) : AuthInteractor {

    override fun signIn(identity: String): Single<SignInResponse> =
            authApi.signIn(identity).map {
                userManager.currentUser = UserVT(identity, it.rawSignedModel)
                it
            }

    override fun signUp(identity: String): Single<SignInResponse> =
            virgilHelper.ifExistsPrivateKey(identity).let {
                if (!it) {
                    virgilHelper.generateKeyPair().let {
                        virgilHelper.storePrivateKey(it.privateKey, identity)
                        virgilHelper.generateRawCard(it, identity)
                    }.let {
                        authApi.signUp(it).map {
                            userManager.currentUser = UserVT(identity, it.rawSignedModel)
                            it
                        }.doOnError {
                            virgilHelper.deletePrivateKey(identity)
                        }
                    }
                } else {
                    throw Throwable(context.getString(R.string.already_registered))
                }
            }
}