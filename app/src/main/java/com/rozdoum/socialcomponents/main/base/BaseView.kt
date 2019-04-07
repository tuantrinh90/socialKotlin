/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rozdoum.socialcomponents.main.base

import android.content.DialogInterface
import android.support.annotation.StringRes
import android.view.View

import com.hannesdorfmann.mosby3.mvp.MvpView

/**
 * Created by Alexey on 03.05.18.
 */

interface BaseView : MvpView {

    fun showProgress()

    fun showProgress(message: Int)

    fun hideProgress()

    fun showSnackBar(message: String)

    fun showSnackBar(message: Int)

    fun showSnackBar(view: View, messageId: Int)

    fun showToast(@StringRes messageId: Int)

    fun showToast(message: String)

    fun showWarningDialog(messageId: Int)

    fun showWarningDialog(message: String)

    fun showNotCancelableWarningDialog(message: String)

    fun showWarningDialog(@StringRes messageId: Int, listener: DialogInterface.OnClickListener)

    fun showWarningDialog(message: String, listener: DialogInterface.OnClickListener)

    fun startLoginActivity()

    fun hideKeyboard()

    fun finish()
}
