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

package com.rozdoum.socialcomponents

/**
 * Created by alexey on 08.12.16.
 */

class Constants {

    companion object {
        val MAX_AVATAR_SIZE = 1280 //px, side of square
        val MIN_AVATAR_SIZE = 100 //px, side of square
        val MAX_NAME_LENGTH = 120

        val MAX_TEXT_LENGTH_IN_LIST = 300 //characters
        val MAX_POST_TITLE_LENGTH = 255 //characters
        val POST_AMOUNT_ON_PAGE = 10

        val MAX_UPLOAD_RETRY_MILLIS = 60000 //1 minute


        val LARGE_ICONE_SIZE = 256 //px


        val DOUBLE_CLICK_TO_EXIT_INTERVAL: Long = 3000 // in milliseconds

        val POST_ID_EXTRA_KEY = "PostDetailsActivity.POST_ID_EXTRA_KEY"
        val AUTHOR_ANIMATION_NEEDED_EXTRA_KEY = "PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY"
        val UPDATE_POST_REQUEST = 1
        val POST_STATUS_EXTRA_KEY = "PostDetailsActivity.POST_STATUS_EXTRA_KEY"
    }
}
