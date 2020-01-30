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

package com.virgilsecurity.android.feature_contacts.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.virgilsecurity.android.base.data.model.ChannelInfo
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.controller.BaseCBWithScope
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.feature_contacts.R
import com.virgilsecurity.android.feature_contacts.databinding.ControllerAddContactBinding
import com.virgilsecurity.android.feature_contacts.di.Const.VM_ADD_CONTACT
import com.virgilsecurity.android.feature_contacts.viewmodel.addContact.AddContactVM
import com.virgilsecurity.android.feature_contacts.viewmodel.addContact.AddContactVMDefault
import com.virgilsecurity.android.feature_contacts.viewslice.addContact.state.StateSliceAddContact
import com.virgilsecurity.android.feature_contacts.viewslice.addContact.toolbar.ToolbarSlice
import kotlinx.android.synthetic.main.controller_add_contact.*
import org.koin.core.inject
import org.koin.core.qualifier.named

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/6/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * AddContactController
 */
class AddContactController() : BaseCBWithScope() {

    override val layoutResourceId: Int = R.layout.controller_add_contact

    private val viewModel: AddContactVM by inject(named(VM_ADD_CONTACT))
    private val stateSlice: StateSliceAddContact by inject()
    private val toolbarSlice: ToolbarSlice by inject()

    private lateinit var openChannel: (ChannelInfo) -> Unit

    constructor(openChannel: (channel: ChannelInfo) -> Unit) : this() {
        this.openChannel = openChannel
    }

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, layoutResourceId: Int): View {
        val binding: ControllerAddContactBinding = DataBindingUtil.inflate(inflater,
                                                                           layoutResourceId,
                                                                           container,
                                                                           false)
        binding.addContactVM = viewModel as AddContactVMDefault
        return binding.root
    }


    override fun init() {
        openKeyboard(etUsername)
        initViewCallbacks()
    }

    private fun initViewCallbacks() {
        btnAdd.setOnClickListener {
            hideKeyboard()
            viewModel.addContact(etUsername.text.toString().toLowerCase())
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
        observe(toolbarSlice.getAction(), ::onActionChanged)
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {}

    override fun onDetach(view: View) {
        super.onDetach(view)
        hideKeyboard()
    }

    private fun onActionChanged(action: ToolbarSlice.Action) = when(action) {
        ToolbarSlice.Action.BackClicked -> {
            hideKeyboard()
            backPress()
        }
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun onStateChanged(state: AddContactVM.State) = when (state) {
        is AddContactVM.State.ContactAdded -> openChannel(state.channel)
        AddContactVM.State.ShowLoading -> stateSlice.showLoading()
        is AddContactVM.State.UsernameInvalid -> when (state.causeCode) {
            AddContactVM.KEY_USERNAME_EMPTY -> stateSlice.showUsernameEmpty()
            AddContactVM.KEY_USERNAME_SHORT -> stateSlice.showUsernameTooShort()
            AddContactVM.KEY_USERNAME_LONG -> stateSlice.showUsernameTooLong()
            AddContactVM.KEY_YOUR_OWN_USERNAME -> stateSlice.showYourOwnUsername()
            else -> Unit
        }
        AddContactVM.State.UsernameConsistent -> stateSlice.showConsistent()
        AddContactVM.State.NoSuchUser -> {
            UiUtils.toast(this, "No such user")
            stateSlice.showConsistent()
        }
        AddContactVM.State.ShowError -> stateSlice.showTryAgain()
        AddContactVM.State.UserAlreadyAdded -> {
            UiUtils.toast(this, "User already added")
            stateSlice.showConsistent()
        }
    }

    private fun backPress() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_ADD_CONTACT_CONTROLLER = "KEY_ADD_CONTACT_CONTROLLER"
    }
}
