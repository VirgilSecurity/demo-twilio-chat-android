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

package com.virgilsecurity.android.feature_settings.domain

import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.domain.BaseDo
import com.virgilsecurity.android.common.data.helper.smack.SmackHelper
import com.virgilsecurity.android.common.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.common.data.repository.UsersRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/25/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * LogoutDoDefault
 */
class DeleteAccountDoDefault(
        private val userProperties: UserProperties,
        private val smackHelper: SmackHelper,
        private val virgilHelper: VirgilHelper,
        private val usersRepository: UsersRepository
) : BaseDo<DeleteAccountDo.Result>(), DeleteAccountDo {

    override fun execute() {
        mutableListOf<Completable>().apply {
            add(Completable.fromCallable {
                usersRepository.deleteUser(userProperties.currentUser!!)
            })
            add(Completable.fromCallable {
                virgilHelper.deletePrivateKey(userProperties.currentUser!!.identity)
            })
            add(Completable.fromCallable { userProperties.clearCurrentUser() })
        }.run {
            Completable.concat(this)
                    .subscribeOn(Schedulers.io())
                    .doOnComplete {
                        smackHelper.stopClient().subscribe()
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::success, ::error)
                    .track()
        }
    }

    private fun success() {
        liveData.value = DeleteAccountDo.Result.OnSuccess
    }

    private fun error(throwable: Throwable) {
        liveData.value = DeleteAccountDo.Result.OnError(throwable)
    }
}
