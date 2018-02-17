/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.muzei.single

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle

/**
 * Settings Activity which allows users to select a new photo
 */
class SingleSettingsActivity : Activity() {

    companion object {
        private const val REQUEST_PHOTO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.takeIf { requestCode == REQUEST_PHOTO && resultCode == RESULT_OK }?.apply {
            val insertArtworkLiveData = SingleArtSource.setArtwork(
                    this@SingleSettingsActivity, this)
            insertArtworkLiveData.observeForever(object : Observer<Boolean> {
                override fun onChanged(success: Boolean?) {
                    insertArtworkLiveData.removeObserver(this)
                    setResult(if (success == true) Activity.RESULT_OK else Activity.RESULT_CANCELED)
                    finish()
                }
            })
        } ?: run {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}