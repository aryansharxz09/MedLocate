package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Query("SELECT * FROM availability_requests ORDER BY timestampMillis DESC")
    fun getAllRequests(): Flow<List<RequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: RequestEntity)

    @Update
    suspend fun updateRequest(request: RequestEntity)

    @Query("DELETE FROM availability_requests WHERE id = :id")
    suspend fun deleteRequest(id: String)
}

@Dao
interface SavedHospitalDao {
    @Query("SELECT * FROM saved_hospitals ORDER BY savedAtMillis DESC")
    fun getSavedHospitals(): Flow<List<SavedHospitalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHospital(saved: SavedHospitalEntity)

    @Query("DELETE FROM saved_hospitals WHERE hospitalId = :hospitalId")
    suspend fun removeSavedHospital(hospitalId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_hospitals WHERE hospitalId = :hospitalId)")
    fun isHospitalSaved(hospitalId: String): Flow<Boolean>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestampMillis DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)
}
