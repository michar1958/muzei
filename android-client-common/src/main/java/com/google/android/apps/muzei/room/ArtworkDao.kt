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

package com.google.android.apps.muzei.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Dao for Artwork
 */
@Dao
abstract class ArtworkDao {

    @get:Query("SELECT * FROM artwork ORDER BY date_added DESC LIMIT 100")
    internal abstract val artworkBlocking: List<Artwork>

    suspend fun getArtwork() = withContext(Dispatchers.Default) {
        artworkBlocking
    }

    @get:Query("SELECT artwork.* FROM artwork " +
            "inner join provider on providerAuthority = authority " +
            "ORDER BY date_added DESC")
    abstract val currentArtwork: LiveData<Artwork?>

    @get:Query("SELECT artwork.* FROM artwork " +
            "inner join provider on providerAuthority = authority " +
            "ORDER BY date_added DESC")
    internal abstract val currentArtworkBlocking: Artwork?

    suspend fun getCurrentArtwork() = withContext(Dispatchers.Default) {
        currentArtworkBlocking
    }

    @Insert
    abstract fun insert(artwork: Artwork): Long

    @Query("SELECT * FROM artwork WHERE providerAuthority = :providerAuthority ORDER BY date_added DESC")
    internal abstract fun getCurrentArtworkForProviderBlocking(
            providerAuthority: String
    ): Artwork?

    suspend fun getCurrentArtworkForProvider(
            providerAuthority: String
    ) = withContext(Dispatchers.Default) {
        getCurrentArtworkForProviderBlocking(providerAuthority)
    }

    @get:Query("SELECT * FROM artwork art1 WHERE _id IN (" +
            "SELECT _id FROM artwork art2 WHERE art1._id=art2._id " +
            "ORDER BY date_added DESC limit 1)")
    abstract val currentArtworkByProvider : LiveData<List<Artwork>>

    @Query("SELECT * FROM artwork WHERE _id=:id")
    internal abstract fun getArtworkByIdBlocking(id: Long): Artwork?

    suspend fun getArtworkById(id: Long) = withContext(Dispatchers.Default) {
        getArtworkByIdBlocking(id)
    }

    @Query("SELECT * FROM artwork WHERE title LIKE :query OR byline LIKE :query OR attribution LIKE :query")
    internal abstract fun searchArtworkBlocking(query: String): List<Artwork>

    suspend fun searchArtwork(query: String) = withContext(Dispatchers.Default) {
        searchArtworkBlocking(query)
    }

    @Query("DELETE FROM artwork WHERE _id=:id")
    abstract fun deleteById(id: Long)
}
