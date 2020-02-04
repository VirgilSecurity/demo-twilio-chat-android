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

package com.virgilsecurity.android.feature_login.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.controller.BControllerBindingScope
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.common.view.LinkMovementMethodNoSelection
import com.virgilsecurity.android.feature_login.R
import com.virgilsecurity.android.feature_login.databinding.ControllerRegisterBinding
import com.virgilsecurity.android.feature_login.viewmodel.registration.RegistrationVM
import com.virgilsecurity.android.feature_login.viewmodel.registration.RegistrationVMDefault
import com.virgilsecurity.android.feature_login.viewslice.registration.state.StateSliceRegistration
import com.virgilsecurity.android.feature_login.viewslice.registration.toolbar.ToolbarSliceRegistration
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

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
class RegistrationController() : BControllerBindingScope() {

    override val layoutResourceId: Int = R.layout.controller_register

    private val vmRegistration: RegistrationVM by currentScope.viewModel(this)

    private lateinit var login: (User) -> Unit
    private lateinit var stateSlice: StateSliceRegistration
    private lateinit var toolbarSlice: ToolbarSliceRegistration
    private lateinit var mutableLiveData: MutableLiveData<ToolbarSliceRegistration.Action>

    private lateinit var etUsername: EditText
    private lateinit var tvPolicy2: TextView
    private lateinit var tvPolicy4: TextView
    private lateinit var btnNext: Button

    constructor(login: (User) -> Unit) : this() {
        this.login = login
    }

    override fun initViewBinding(inflater: LayoutInflater,
                                 container: ViewGroup?,
                                 layoutResourceId: Int): View {
        val binding: ControllerRegisterBinding = DataBindingUtil.inflate(inflater,
                                                                         layoutResourceId,
                                                                         container,
                                                                         false)
        binding.registrationViewModel = vmRegistration as RegistrationVMDefault
        return binding.root
    }


    override fun init(containerView: View) {
        mutableLiveData = MutableLiveData()
        initViews(containerView)
        initViewCallbacks()
    }

    private fun initViews(containerView: View) {
        with(containerView) {
            etUsername = findViewById(R.id.etUsername)
            tvPolicy2 = findViewById(R.id.tvPolicy2)
            tvPolicy4 = findViewById(R.id.tvPolicy4)
            btnNext = findViewById(R.id.btnNext)

            openKeyboard(etUsername)

            tvPolicy2.movementMethod = LinkMovementMethodNoSelection.instance
            tvPolicy4.movementMethod = LinkMovementMethodNoSelection.instance
        }
    }

    private fun initViewCallbacks() {
        btnNext.setOnClickListener {
            hideKeyboard()
            vmRegistration.registration(etUsername.text.toString().toLowerCase())
        }
    }

    override fun initViewSlices(window: Window) {
        stateSlice.init(lifecycle, window)
        toolbarSlice.init(lifecycle, window)
    }

    override fun setupViewSlices(view: View) {
        stateSlice.showUsernameTooShort()
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onActionChanged)
    }

    private fun onActionChanged(action: ToolbarSliceRegistration.Action) = when (action) {
        ToolbarSliceRegistration.Action.BackClicked -> {
            hideKeyboard()
            backPressed()
        }
        ToolbarSliceRegistration.Action.InfoClicked -> UiUtils.toastUnderDevelopment(this)
        ToolbarSliceRegistration.Action.Idle -> Unit
    }

    override fun setupVMStateObservers() {
        observe(vmRegistration.getState(), ::onStateChanged)
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
        RegistrationVM.State.AlreadyRegistered -> {
            UiUtils.toast(this, "User already registered")
            stateSlice.showConsistent()
        }
        RegistrationVM.State.Idle -> Unit
    }

    companion object {
        const val KEY_REGISTRATION_CONTROLLER = "KEY_REGISTRATION_CONTROLLER"
    }
}
