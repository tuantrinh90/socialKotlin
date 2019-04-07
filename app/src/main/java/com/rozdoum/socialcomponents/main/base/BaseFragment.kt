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
import android.view.View

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpFragment

import java.util.Objects

/**
 * Created by Alexey on 08.05.18.
 */

abstract class BaseFragment<V : BaseFragmentView, P : MvpBasePresenter<V>> : MvpFragment<V, P>(), BaseFragmentView {

    override fun showProgress() {
        (activity as BaseActivity<*, *>).showProgress()
    }

    override fun showProgress(message: Int) {
        (activity as BaseActivity<*, *>).showProgress(message)
    }

    override fun hideProgress() {
        (activity as BaseActivity<*, *>).hideProgress()
    }

    override fun showSnackBar(message: String) {
        (Objects.requireNonNull<FragmentActivity>(activity) as BaseActivity<*, *>).showSnackBar(message)
    }

    override fun showSnackBar(message: Int) {
        (activity as BaseActivity<*, *>).showSnackBar(message)
    }

    override fun showSnackBar(view: View, messageId: Int) {
        (activity as BaseActivity<*, *>).showSnackBar(view, messageId)
    }

    override fun showToast(messageId: Int) {
        (activity as BaseActivity<*, *>).showToast(messageId)
    }

    override fun showToast(message: String) {
        (activity as BaseActivity<*, *>).showToast(message)
    }

    override fun showWarningDialog(messageId: Int) {
        (activity as BaseActivity<*, *>).showWarningDialog(messageId)
    }

    override fun showWarningDialog(message: String) {
        (activity as BaseActivity<*, *>).showWarningDialog(message)
    }

    override fun showNotCancelableWarningDialog(message: String) {
        (activity as BaseActivity<*, *>).showNotCancelableWarningDialog(message)
    }

    override fun showWarningDialog(messageId: Int, listener: DialogInterface.OnClickListener) {
        (activity as BaseActivity<*, *>).showWarningDialog(messageId, listener)
    }

    override fun showWarningDialog(message: String, listener: DialogInterface.OnClickListener) {
        (activity as BaseActivity<*, *>).showWarningDialog(message, listener)
    }

    override fun hasInternetConnection(): Boolean {
        return (activity as BaseActivity<*, *>).hasInternetConnection()
    }

    override fun startLoginActivity() {
        (activity as BaseActivity<*, *>).startLoginActivity()
    }

    override fun hideKeyboard() {
        (activity as BaseActivity<*, *>).hideKeyboard()
    }

    override fun finish() {
        (activity as BaseActivity<*, *>).finish()
    }
}
