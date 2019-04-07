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

package com.rozdoum.socialcomponents.main.login

import android.content.Intent
import android.os.Bundle

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookAuthorizationException
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.editProfile.createProfile.CreateProfileActivity
import com.rozdoum.socialcomponents.utils.GoogleApiHelper
import com.rozdoum.socialcomponents.utils.LogUtil
import com.rozdoum.socialcomponents.utils.LogoutHelper

import java.util.Arrays

class LoginActivity : BaseActivity<LoginView, LoginPresenter>(), LoginView, GoogleApiClient.OnConnectionFailedListener {

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    private var mCallbackManager: CallbackManager? = null
    private var profilePhotoUrlLarge: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        initGoogleSignIn()
        initFirebaseAuth()
        initFacebookSignIn()
    }

    private fun initGoogleSignIn() {
        mGoogleApiClient = GoogleApiHelper.createGoogleApiClient(this)
        mAuth = FirebaseAuth.getInstance()

        findViewById<View>(R.id.googleSignInButton).setOnClickListener({ view -> presenter.onGoogleSignInClick() })
    }

    private fun initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            LogoutHelper.signOut(mGoogleApiClient!!, this)
        }

        mAuthListener = { firebaseAuth ->
            val user = firebaseAuth.getCurrentUser()
            if (user != null) {
                // Profile is signed in
                LogUtil.logDebug(TAG, "onAuthStateChanged:signed_in:" + user!!.getUid())
                presenter.checkIsProfileExist(user!!.getUid())
                setResult(Activity.RESULT_OK)
            } else {
                // Profile is signed out
                LogUtil.logDebug(TAG, "onAuthStateChanged:signed_out")
            }
        }
    }

    private fun initFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                LogUtil.logDebug(TAG, "facebook:onSuccess:$loginResult")
                presenter.handleFacebookSignInResult(loginResult)
            }

            override fun onCancel() {
                LogUtil.logDebug(TAG, "facebook:onCancel")
            }

            override fun onError(e: FacebookException) {
                LogUtil.logError(TAG, "facebook:onError", e)
                showSnackBar(e.message)
                if (e is FacebookAuthorizationException) {
                    if (LoginManager.getInstance() != null) LoginManager.getInstance().logOut()
                }
            }
        })

        findViewById<View>(R.id.facebookSignInButton).setOnClickListener({ v -> presenter.onFacebookSignInClick() })
    }

    public override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)

        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }

        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.stopAutoManage(this)
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun createPresenter(): LoginPresenter {
        return if (presenter == null) {
            LoginPresenter(this)
        } else presenter
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_GOOGLE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            presenter.handleGoogleSignInResult(result)
        }
    }

    override fun startCreateProfileActivity() {
        val intent = Intent(this@LoginActivity, CreateProfileActivity::class.java)
        intent.putExtra(CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY, profilePhotoUrlLarge)
        startActivity(intent)
    }

    override fun firebaseAuthWithCredentials(credential: AuthCredential) {
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    LogUtil.logDebug(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful) {
                        presenter.handleAuthError(task)
                    }
                }
    }

    override fun setProfilePhotoUrl(url: String) {
        profilePhotoUrlLarge = url
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogUtil.logDebug(TAG, "onConnectionFailed:$connectionResult")
        showSnackBar(R.string.error_google_play_services)
        hideProgress()
    }

    override fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE)
    }

    override fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("public_profile", "email"))
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private val SIGN_IN_GOOGLE = 9001
        val LOGIN_REQUEST_CODE = 10001
    }
}

