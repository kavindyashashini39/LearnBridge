package com.app.learnbridge.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import java.io.File

/**
 * Utility for loading course images with proper fallback logic:
 * 1. Try to load from URL first
 * 2. If URL fails, try to load from local file path
 * 3. If both fail, show placeholder image
 */
object ImageLoader {

    fun loadCourseImage(
        imageView: ImageView,
        imageUrl: String?,
        placeholderId: Int = android.R.drawable.ic_menu_gallery,
        isCircle: Boolean = false
    ) {
        loadCourseImageInternal(imageView, imageUrl, placeholderId, false, isCircle)
    }

    /**
     * Load image bypassing cache (useful for updated images)
     */
    fun loadCourseImageSkipCache(
        imageView: ImageView,
        imageUrl: String?,
        placeholderId: Int = android.R.drawable.ic_menu_gallery,
        isCircle: Boolean = false
    ) {
        loadCourseImageInternal(imageView, imageUrl, placeholderId, true, isCircle)
    }

    private fun loadCourseImageInternal(
        imageView: ImageView,
        imageUrl: String?,
        placeholderId: Int,
        skipCache: Boolean,
        isCircle: Boolean
    ) {
        if (imageUrl.isNullOrBlank()) {
            // No URL provided, show placeholder
            imageView.setImageResource(placeholderId)
            return
        }

        // Check if it's a local file path (starts with /)
        val isLocalPath = imageUrl.startsWith("/")

        if (isLocalPath) {
            // Load from local file directly
            loadFromLocalFile(imageView, imageUrl, placeholderId, skipCache, isCircle)
        } else {
            // Try to load from URL with fallback to local
            loadFromUrlWithFallback(imageView, imageUrl, placeholderId, skipCache, isCircle)
        }
    }

    /**
     * Load from URL with fallback to local file if URL fails
     */
    private fun loadFromUrlWithFallback(
        imageView: ImageView,
        imageUrl: String,
        placeholderId: Int,
        skipCache: Boolean = false,
        isCircle: Boolean = false
    ) {
        var glideRequest = Glide.with(imageView.context)
            .load(imageUrl)
            .placeholder(placeholderId)
            .error(placeholderId)

        if (isCircle) {
            glideRequest = glideRequest.circleCrop()
        }

        if (skipCache) {
            // Reassign the request with cache-busting options
            glideRequest = glideRequest
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        }

        glideRequest.into(imageView)
    }

    /**
     * Load from local file path
     */
    private fun loadFromLocalFile(
        imageView: ImageView,
        filePath: String,
        placeholderId: Int,
        skipCache: Boolean = false,
        isCircle: Boolean = false
    ) {
        val file = File(filePath)

        if (file.exists()) {
            // File exists, load it
            var glideRequest = Glide.with(imageView.context)
                .load(file)
                // Use file modification time as signature to detect changes automatically
                .signature(ObjectKey(file.lastModified().toString()))
                .placeholder(placeholderId)
                .error(placeholderId)

            if (isCircle) {
                glideRequest = glideRequest.circleCrop()
            }

            if (skipCache) {
                // If explicitly skipping cache, use current time for absolute fresh load
                glideRequest = glideRequest
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }

            glideRequest.into(imageView)
        } else {
            // File doesn't exist, show placeholder
            imageView.setImageResource(placeholderId)
        }
    }
}
