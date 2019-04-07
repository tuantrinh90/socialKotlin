package com.rozdoum.socialcomponents.listeners

import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.Transition

/**
 * Created by alexey on 05.04.17.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
open class CustomTransitionListener : Transition.TransitionListener {

    override fun onTransitionStart(transition: Transition) {

    }

    override fun onTransitionEnd(transition: Transition) {

    }

    override fun onTransitionCancel(transition: Transition) {

    }

    override fun onTransitionPause(transition: Transition) {

    }

    override fun onTransitionResume(transition: Transition) {

    }
}
