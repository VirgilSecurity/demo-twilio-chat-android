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

package com.android.virgilsecurity.feature_login.domain.registration

import com.android.virgilsecurity.base.data.model.SignInResponse
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.domain.BaseDo
import com.android.virgilsecurity.common.data.helper.virgil.VirgilHelper
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.feature_login.data.interactor.AuthInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
 * SignUpDoDefault
 */
class SignUpDoDefault(
        private val authInteractor: AuthInteractor,
        private val virgilHelper: VirgilHelper
) : BaseDo<SignUpDo.Result>(), SignUpDo {

    override fun execute(identity: String) {
        UiUtils.log("SignUpDoDefault identity: ", identity)
        authInteractor.signUp(identity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::success, ::error)
                .track()
    }

    private fun success(signInResponse: SignInResponse) {
        virgilHelper.parseCard(signInResponse.rawSignedModel).let {
            liveData.value = SignUpDo.Result.OnSuccess(User(it.identity, it.rawCard.exportAsBase64String()))
        }
    }

    private fun error(throwable: Throwable) {
        UiUtils.log("SignUpDoDefault", throwable.message?: "no message")
        liveData.value = SignUpDo.Result.OnError(throwable)
    }
}