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

package com.android.virgilsecurity.twiliodemo.ui.login

import android.os.Bundle
import android.service.autofill.Validators.and
import android.view.View
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.ui.base.BaseFragment
import com.android.virgilsecurity.twiliodemo.ui.chat.channelsList.ChannelsListActivity
import com.android.virgilsecurity.twiliodemo.util.UiUtils
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.HttpException
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/185/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * LoginFragment
 */

class LoginFragment : BaseFragment<LoginActivity>() {

    private val presenter: LoginPresenter by inject()

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun provideLayoutId() = R.layout.fragment_login

    override fun preInitUi() {
        // TODO Implement body or it will be empty ):
    }

    override fun initUi() {
        // TODO Implement body or it will be empty ):
    }

    override fun initData() {
        // TODO Implement body or it will be empty ):
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.disposeAll()
    }

    override fun initCallbacks() {
        btnSignUp.setOnClickListener {
            val identity = etIdentity.text.toString()
            if (identity.isNotEmpty()) {
                showProgress(true)
                presenter.requestSignUp(etIdentity.text.toString(),
                                        {
                                            ChannelsListActivity.startWithFinish(rootActivity!!)
                                        },
                                        {
                                            UiUtils.toast(this,
                                                          "SignUp Error.\nMessage: ${it.message}")
                                            showProgress(false)
                                        })
            }
        }

        btnSignIn.setOnClickListener {
            val identity = etIdentity.text.toString()
            if (identity.isNotEmpty()) {
                showProgress(true)
                presenter.requestSignIn(etIdentity.text.toString(),
                                        {
                                            ChannelsListActivity.startWithFinish(rootActivity!!)
                                        },
                                        {
                                            if (it is FuelError && (it.response.statusCode == 400)) {
                                                UiUtils.toast(this,
                                                              "Identity not registered")
                                            } else {
                                                UiUtils.toast(this,
                                                              "SignUp Error.\n${it.message}")
                                            }
                                            showProgress(false)
                                        })
            }
        }
    }

    private fun showProgress(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.INVISIBLE
        btnSignIn.visibility = if (show) View.INVISIBLE else View.VISIBLE
        btnSignUp.visibility = if (show) View.INVISIBLE else View.VISIBLE
    }
}