package com.app.learnbridge.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Utility for managing user profile photos
 * Saves photos locally with user ID and provides methods to load them
 */
object ProfilePhotoManager {

    private const val PROFILE_PHOTOS_DIR = "profile_photos"

    /**
     * Save profile photo from Uri
     * @param context Application context
     * @param userId User ID
     * @param photoUri URI of the photo to save
     * @return Path to saved photo, or null if save failed
     */
    fun saveProfilePhoto(context: Context, userId: Int, photoUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(photoUri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            saveProfilePhotoFromBitmap(context, userId, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Save profile photo from Bitmap
     * @param context Application context
     * @param userId User ID
     * @param bitmap Bitmap to save
     * @return Path to saved photo, or null if save failed
     */
    fun saveProfilePhotoFromBitmap(context: Context, userId: Int, bitmap: Bitmap): String? {
        return try {
            // Create profile photos directory if it doesn't exist
            val profilePhotosDir = File(context.filesDir, PROFILE_PHOTOS_DIR)
            if (!profilePhotosDir.exists()) {
                profilePhotosDir.mkdirs()
            }

            // Create file with user ID
            val photoFile = File(profilePhotosDir, "user_${userId}_profile.jpg")

            // Save bitmap to file
            FileOutputStream(photoFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush()
            }

            // Return the absolute path
            photoFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get the path to a user's profile photo
     * @param context Application context
     * @param userId User ID
     * @return Path to profile photo if it exists, null otherwise
     */
    fun getProfilePhotoPath(context: Context, userId: Int): String? {
        val photoFile = File(context.filesDir, "$PROFILE_PHOTOS_DIR/user_${userId}_profile.jpg")
        return if (photoFile.exists()) photoFile.absolutePath else null
    }

    /**
     * Delete a user's profile photo
     * @param context Application context
     * @param userId User ID
     * @return true if deletion was successful, false otherwise
     */
    fun deleteProfilePhoto(context: Context, userId: Int): Boolean {
        return try {
            val photoFile = File(context.filesDir, "$PROFILE_PHOTOS_DIR/user_${userId}_profile.jpg")
            photoFile.exists() && photoFile.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if a user has a profile photo
     * @param context Application context
     * @param userId User ID
     * @return true if profile photo exists, false otherwise
     */
    fun hasProfilePhoto(context: Context, userId: Int): Boolean {
        val photoFile = File(context.filesDir, "$PROFILE_PHOTOS_DIR/user_${userId}_profile.jpg")
        return photoFile.exists()
    }
}

