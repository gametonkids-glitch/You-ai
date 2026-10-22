package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.ScheduledShort
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortDao {

    @Query("SELECT * FROM scheduled_shorts ORDER BY scheduledTimeEpochMs ASC")
    fun getAllShorts(): Flow<List<ScheduledShort>>

    @Query("SELECT * FROM scheduled_shorts WHERE status = :status ORDER BY scheduledTimeEpochMs ASC")
    fun getShortsByStatus(status: String): Flow<List<ScheduledShort>>

    @Query("SELECT * FROM scheduled_shorts WHERE status != 'PUBLISHED' ORDER BY scheduledTimeEpochMs ASC")
    fun getActiveQueue(): Flow<List<ScheduledShort>>

    @Query("SELECT * FROM scheduled_shorts WHERE status = 'PUBLISHED' ORDER BY updatedAtEpochMs DESC")
    fun getPublishedHistory(): Flow<List<ScheduledShort>>

    @Query("SELECT * FROM scheduled_shorts WHERE id = :id LIMIT 1")
    suspend fun getShortById(id: Long): ScheduledShort?

    @Query("SELECT * FROM scheduled_shorts WHERE status = 'SCHEDULED' AND scheduledTimeEpochMs <= :currentTimeMs")
    suspend fun getPendingDueShorts(currentTimeMs: Long): List<ScheduledShort>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShort(short: ScheduledShort): Long

    @Update
    suspend fun updateShort(short: ScheduledShort)

    @Delete
    suspend fun deleteShort(short: ScheduledShort)

    @Query("DELETE FROM scheduled_shorts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE scheduled_shorts SET status = :status, uploadProgressPercent = :progress, errorMessage = :error WHERE id = :id")
    suspend fun updateStatusAndProgress(id: Long, status: String, progress: Int, error: String? = null)

    @Query("UPDATE scheduled_shorts SET status = 'PUBLISHED', uploadProgressPercent = 100, youtubeVideoId = :youtubeId, youtubeVideoUrl = :url, updatedAtEpochMs = :updatedAt WHERE id = :id")
    suspend fun markPublished(id: Long, youtubeId: String, url: String, updatedAt: Long = System.currentTimeMillis())
}
