/*
 *
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
 *
 */

package com.rozdoum.socialcomponents.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText

import com.rozdoum.socialcomponents.R

/**
 * Created by alexey on 12.05.17.
 */

class EditCommentDialog : DialogFragment() {

    private var callback: CommentDialogCallback? = null
    private var commentText: String? = null
    private var commentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (activity is CommentDialogCallback) {
            callback = activity as CommentDialogCallback
        } else {
            throw RuntimeException(activity.title.toString() + " should implements CommentDialogCallback")
        }

        commentText = arguments.get(COMMENT_TEXT_KEY) as String
        commentId = arguments.get(COMMENT_ID_KEY) as String

        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.dialog_edit_comment, null)

        val editCommentEditText = view.findViewById<View>(R.id.editCommentEditText) as EditText

        if (commentText != null) {
            editCommentEditText.setText(commentText)
        }

        configureDialogButtonState(editCommentEditText)

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
                .setTitle(R.string.title_edit_comment)
                .setNegativeButton(R.string.button_title_cancel, null)
                .setPositiveButton(R.string.button_title_save) { dialog, which ->
                    val newCommentText = editCommentEditText.text.toString()

                    if (newCommentText != commentText && callback != null) {
                        callback!!.onCommentChanged(newCommentText, commentId)
                    }

                    dialog.cancel()
                }

        return builder.create()
    }

    private fun configureDialogButtonState(editCommentEditText: EditText) {
        editCommentEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                val dialog = dialog
                if (dialog != null) {
                    if (TextUtils.isEmpty(s)) {
                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    } else {
                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    }
                }
            }
        })
    }

    interface CommentDialogCallback {
        fun onCommentChanged(newText: String, commentId: String?)
    }

    companion object {
        val TAG = EditCommentDialog::class.java.simpleName
        val COMMENT_TEXT_KEY = "EditCommentDialog.COMMENT_TEXT_KEY"
        val COMMENT_ID_KEY = "EditCommentDialog.COMMENT_ID_KEY"
    }
}
