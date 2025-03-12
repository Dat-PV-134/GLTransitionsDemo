package com.rekoj134.gltransitionsdemo.util

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLES32.GL_TEXTURE_2D
import android.opengl.GLES32.glBindTexture
import android.opengl.GLUtils

object TextureHelper {
    fun loadTexture(context: Context, path: String): Int {
        val textureObjectIds = IntArray(1)
        GLES32.glGenTextures(1, textureObjectIds, 0)
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])

        // Set texture wrapper
        GLES32.glTexParameteri(GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT)
        GLES32.glTexParameteri(GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT)

        // Set texture min max filter
        GLES32.glTexParameteri(
            GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_LINEAR_MIPMAP_LINEAR
        )
        GLES32.glTexParameteri(GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)

        // load texture
        val bitmap = AssetsUtil.getImageBitmap(context, path, flipVertical = true)
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap?.recycle()
        GLES32.glGenerateMipmap(GL_TEXTURE_2D)

        return textureObjectIds[0]
    }
}