/*
 * Copyright 2017 Rozdoum
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

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rozdoum.socialcomponents.Constants
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.ProfileStatus
import com.rozdoum.socialcomponents.main.login.LoginActivity

/**
 * Created by alexey on 05.12.16.
 */

abstract class BaseActivity<V : BaseView, P : BasePresenter<V>> : MvpActivity<V, P>(), BaseView {
    val TAG = this.javaClass.simpleName
    var progressDialog: ProgressDialog? = null
    var actionBar: ActionBar? = null
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar = supportActionBar

    }

    override fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LoginActivity.LOGIN_REQUEST_CODE)
    }

    override fun showProgress() {
        showProgress(R.string.loading)
    }

    override fun showProgress(@StringRes message: Int) {
        hideProgress()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(getString(message))
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    override fun hideProgress() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    override fun hideKeyboard() {
        // Check if no view has focus:
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun showSnackBar(@StringRes messageId: Int) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                messageId, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun showSnackBar(view: View, @StringRes messageId: Int) {
        val snackbar = Snackbar.make(view, messageId, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun showToast(@StringRes messageId: Int) {
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).show()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showWarningDialog(messageId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(messageId)
        builder.setPositiveButton(R.string.button_ok, null)
        builder.show()
    }

    override fun showWarningDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.button_ok, null)
        builder.show()
    }

    override fun showNotCancelableWarningDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.button_ok, null)
        builder.setCancelable(false)
        builder.show()
    }

    override fun showWarningDialog(message: Int, listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.button_ok, listener)
        builder.show()
    }

    override fun showWarningDialog(message: String, listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.button_ok, listener)
        builder.show()
    }

    fun hasInternetConnection(): Boolean {
        return presenter.hasInternetConnection()
    }

    fun checkAuthorization(): Boolean {
        return presenter.checkAuthorization()
    }

    @JvmOverloads
    fun attemptToExitIfRoot(anchorView: View? = null) {
        if (isTaskRoot) {
            if (backPressedTime + Constants.General.DOUBLE_CLICK_TO_EXIT_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed()
            } else {
                if (anchorView != null) {
                    showSnackBar(anchorView, R.string.press_once_again_to_exit)
                } else {
                    showSnackBar(R.string.press_once_again_to_exit)
                }

                backPressedTime = System.currentTimeMillis()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}
