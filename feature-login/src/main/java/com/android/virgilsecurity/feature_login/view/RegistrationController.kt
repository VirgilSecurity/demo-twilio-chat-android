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

import LoginDiConst.CONTEXT_REGISTRATION_CONTROLLER
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseControllerBinding
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.common.view.LinkMovementMethodNoSelection
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.databinding.ControllerRegisterBinding
import com.android.virgilsecurity.feature_login.viewmodel.registration.RegistrationVM
import com.android.virgilsecurity.feature_login.viewmodel.registration.RegistrationVMDefault
import com.android.virgilsecurity.feature_login.viewslice.registration.state.StateSliceRegistration
import com.android.virgilsecurity.feature_login.viewslice.registration.toolbar.ToolbarSlice
import kotlinx.android.synthetic.main.controller_register.*
import org.koin.standalone.inject

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
 * RegistrationController
 */
class RegistrationController() : BaseControllerBinding() {

    override val layoutResourceId: Int = R.layout.controller_register
    override val koinContextName: String? = CONTEXT_REGISTRATION_CONTROLLER

    private val viewModel: RegistrationVM by inject()
    private val stateSlice: StateSliceRegistration by inject()
    private val toolbarSlice: ToolbarSlice by inject()

    private lateinit var login: (User) -> Unit

    constructor(login: (User) -> Unit) : this() {
        this.login = login
    }

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, layoutResourceId: Int): View {
        val binding: ControllerRegisterBinding = DataBindingUtil.inflate(inflater,
                                                                       layoutResourceId,
                                                                       container,
                                                                       false)
        binding.registrationViewModel = viewModel as RegistrationVMDefault
        return binding.root
    }


    override fun init() {
        initViews()
        initViewCallbacks()
    }

    private fun initViews() {
        openKeyboard(etUsername)
        tvPolicy2.movementMethod = LinkMovementMethodNoSelection.instance
        tvPolicy4.movementMethod = LinkMovementMethodNoSelection.instance
    }

    private fun initViewCallbacks() {
        btnNext.setOnClickListener {
            hideKeyboard()
            viewModel.registration(etUsername.text.toString())
        }
    }

    override fun initViewSlices(view: View) {
        stateSlice.init(lifecycle, view)
        toolbarSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        stateSlice.showUsernameTooShort()
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction()) { onActionChanged(it) }
    }

    private fun onActionChanged(action: ToolbarSlice.Action) = when(action) {
        ToolbarSlice.Action.BackClicked -> {
            hideKeyboard()
            backPressed()
        }
        ToolbarSlice.Action.InfoClicked -> UiUtils.toastUnderDevelopment(this)
        ToolbarSlice.Action.Idle -> Unit
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState()) { onStateChanged(it) }
    }

    override fun initData() {}

    override fun onDetach(view: View) {
        super.onDetach(view)
        hideKeyboard()
    }

    private fun backPressed() {
        router.popCurrentController()
    }

    private fun onStateChanged(state: RegistrationVM.State) = when (state) {
        is RegistrationVM.State.RegisteredSuccessfully -> login(state.user)
        RegistrationVM.State.ShowLoading -> stateSlice.showLoading()
        is RegistrationVM.State.UsernameInvalid -> when (state.causeCode) {
            RegistrationVM.KEY_USERNAME_EMPTY -> stateSlice.showUsernameEmpty()
            RegistrationVM.KEY_USERNAME_SHORT -> stateSlice.showUsernameTooShort()
            RegistrationVM.KEY_USERNAME_LONG -> stateSlice.showUsernameTooLong()
            else -> Unit
        }
        RegistrationVM.State.UsernameConsistent -> stateSlice.showConsistent()
        RegistrationVM.State.ShowError -> stateSlice.showError()
        RegistrationVM.State.Idle -> Unit
    }

    companion object {
        const val KEY_REGISTRATION_CONTROLLER = "KEY_REGISTRATION_CONTROLLER"
    }
}