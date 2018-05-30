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

//class GetTokenCallbackImpl(private val helper: ServiceHelper,
//                           private val userManager: UserManager,
//                           private val firebaseAuth: FirebaseAuth,
//                           private val context: Context) : CallbackJwtProvider.GetTokenCallback {
//
//    override fun onGetToken(tokenContext: TokenContext): String {
//        try {
//            var response = helper.getToken(userManager.getToken(),
//                    firebaseAuth.getCurrentUser()
//                            .getEmail()
//                            .toLowerCase())
//                    .execute()
//
//            if (response.errorBody() != null && response.code() === 401) {
//                //                new Handler(Looper.getMainLooper()).post(() -> {
//                //                    UiUtils.toast(context, "Session is ended. Re-signIn please to refresh your token.");
//                //                });
//
//                val executor = Executors.newSingleThreadExecutor()
//
//                val getTokenResultTask = firebaseAuth.getCurrentUser().getIdToken(true)
//                getTokenResultTask.addOnCompleteListener(executor, { task ->
//                    if (task.isSuccessful())
//                        userManager.setToken(DefaultToken(task.getResult().getToken()))
//                })
//
//                try {
//                    executor.awaitTermination(2, TimeUnit.SECONDS)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//
//                response = helper.getToken(userManager.getToken(),
//                        firebaseAuth.getCurrentUser()
//                                .getEmail()
//                                .toLowerCase())
//                        .execute()
//            }
//
//            return response.body().getToken()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            throw ServiceException("Failed on get token")
//        } catch (e: NullPointerException) {
//            e.printStackTrace()
//            throw ServiceException("Failed on get token")
//        }
//
//    }
//}
