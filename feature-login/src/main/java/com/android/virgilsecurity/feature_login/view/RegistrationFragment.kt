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

package com.android.virgilsecurity.feature_login.view

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseFragmentBinding
import com.android.virgilsecurity.common.view.LinkMovementMethodNoSelection
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.databinding.FragmentRegisterBinding
import com.android.virgilsecurity.feature_login.viewmodel.registration.RegistrationVM
import com.android.virgilsecurity.feature_login.viewmodel.registration.RegistrationVMDefault
import com.android.virgilsecurity.feature_login.viewslice.registration.StateSliceRegistration
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/3/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * RegistrationFragment
 */
class RegistrationFragment @SuppressLint("ValidFragment") constructor(
        override val layoutResourceId: Int = R.layout.fragment_register
) : BaseFragmentBinding<AuthActivity>() {

    private val viewModel: RegistrationVM by inject()
    private val stateSliceRegistration: StateSliceRegistration by inject()

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, layoutResourceId: Int): View {
        val binding: FragmentRegisterBinding = DataBindingUtil.inflate(inflater,
                                                                       layoutResourceId,
                                                                       container,
                                                                       false)
        binding.registrationViewModel = viewModel as RegistrationVMDefault
        return binding.root
    }


    override fun init(view: View, savedInstanceState: Bundle?) {
        initViews()
        initViewCallbacks()
    }

    private fun initViewCallbacks() {
        btnNext.setOnClickListener { viewModel.registration(etUsername.text.toString()) }
    }

    private fun initViews() {
        tvPolicy2.movementMethod = LinkMovementMethodNoSelection.instance
        tvPolicy4.movementMethod = LinkMovementMethodNoSelection.instance
    }

    override fun initViewSlices(view: View) {
        stateSliceRegistration.init(lifecycle, view)
    }

    override fun setupVSActionObservers() {}

    override fun setupVMStateObservers() {
        observe(viewModel.getState()) { onStateChanged(it) }
    }

    private fun onStateChanged(state: RegistrationVM.State) = when (state) {
        is RegistrationVM.State.RegisteredSuccessfully -> {
            stateSliceRegistration.cleanUp()
            startChatList(state.user)
        }
        RegistrationVM.State.ShowLoading -> stateSliceRegistration.showLoading()
        is RegistrationVM.State.UsernameInvalid -> when (state.causeCode) {
            RegistrationVM.KEY_USERNAME_EMPTY -> stateSliceRegistration.showUsernameEmpty()
            RegistrationVM.KEY_USERNAME_SHORT -> stateSliceRegistration.showUsernameTooShort()
            RegistrationVM.KEY_USERNAME_LONG -> stateSliceRegistration.showUsernameTooLong()
            else -> Unit
        }
        RegistrationVM.State.UsernameConsistent -> stateSliceRegistration.showConsistent()
        RegistrationVM.State.ShowError -> stateSliceRegistration.showError()
    }

    private fun startChatList(user: User) = rootActivity?.login(user)

    companion object {
        fun instance() = RegistrationFragment()
    }
}