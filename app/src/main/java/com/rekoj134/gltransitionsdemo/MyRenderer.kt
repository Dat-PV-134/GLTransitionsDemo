package com.rekoj134.gltransitionsdemo

import android.content.Context
import android.opengl.GLES20.GL_TEXTURE1
import android.opengl.GLES32
import android.opengl.GLES32.GL_COLOR_BUFFER_BIT
import android.opengl.GLES32.GL_TEXTURE0
import android.opengl.GLES32.GL_TEXTURE_2D
import android.opengl.GLES32.glActiveTexture
import android.opengl.GLES32.glBindTexture
import android.opengl.GLES32.glClear
import android.opengl.GLES32.glGetUniformLocation
import android.opengl.GLES32.glViewport
import android.opengl.GLSurfaceView.Renderer
import com.rekoj134.gltransitionsdemo.util.LoggerConfig
import com.rekoj134.gltransitionsdemo.util.ShaderHelper
import com.rekoj134.gltransitionsdemo.util.ShaderReader
import com.rekoj134.gltransitionsdemo.util.TextureHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : Renderer {
    private val vertices: FloatArray = floatArrayOf(
        1.0f, -1.0f, 1.0f, 1.0f, 0.0f,     // x, y, z - texture x, texture y
        -1.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        -1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 1.0f, 1.0f, 1.0f
    )

    private val indices: IntArray = intArrayOf(
        0, 1, 2,
        0, 3, 1
    )

    private var program = 0
    private var VBO = 0
    private var VAO = 0
    private var EBO = 0

    private var timeElapsed: Float = 0.0f
    private var animationSpeed: Float = 1.0f

    private var texture1: Int = 0
    private var texture2: Int = 0

    private var isDoneTransition = true
    private var isRestoring = false

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShaderSource = ShaderReader.readTextFileFromResource(context, R.raw.transition_2_vertex_shader)
        val fragmentShaderSource =
            ShaderReader.readTextFileFromResource(context, R.raw.transition_1_fragment_shader)
        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource)
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }
        GLES32.glUseProgram(program)

        // Load texture and set texture uniform by texture unit
        texture1 = TextureHelper.loadTexture(context, "test_images/start.jpg")
        GLES32.glUniform1i(glGetUniformLocation(program, "texture1"), 0)
        texture2 = TextureHelper.loadTexture(context, "test_images/end.jpg")
        GLES32.glUniform1i(glGetUniformLocation(program, "texture2"), 1)

        val vaoBuffer = IntBuffer.allocate(1)
        val vboBuffer = IntBuffer.allocate(1)
        val eboBuffer = IntBuffer.allocate(1)
        GLES32.glGenVertexArrays(1, vaoBuffer)
        GLES32.glGenBuffers(1, vboBuffer)
        GLES32.glGenBuffers(1, eboBuffer)
        VAO = vaoBuffer.get(0)
        VBO = vboBuffer.get(0)
        EBO = eboBuffer.get(0)

        GLES32.glBindVertexArray(VAO)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, VBO)
        val vertexBuffer: FloatBuffer = ByteBuffer
            .allocateDirect(vertices.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        GLES32.glBufferData(
            GLES32.GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            vertexBuffer,
            GLES32.GL_STATIC_DRAW
        )
        GLES32.glVertexAttribPointer(0, 3, GLES32.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
        GLES32.glEnableVertexAttribArray(0)

        GLES32.glBufferData(
            GLES32.GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            vertexBuffer,
            GLES32.GL_STATIC_DRAW
        )
        GLES32.glVertexAttribPointer(
            1,
            2,
            GLES32.GL_FLOAT,
            false,
            5 * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES
        )
        GLES32.glEnableVertexAttribArray(1)

        val indicesBuffer: IntBuffer = ByteBuffer
            .allocateDirect(indices.size * Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
        indicesBuffer.put(indices)
        indicesBuffer.position(0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, EBO)
        GLES32.glBufferData(
            GLES32.GL_ELEMENT_ARRAY_BUFFER,
            indices.size * Int.SIZE_BYTES,
            indicesBuffer,
            GLES32.GL_STATIC_DRAW
        )
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)
        GLES32.glBindVertexArray(0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        if (!isDoneTransition) {
            timeElapsed += animationSpeed * 0.016f
        } else if (isRestoring) {
            timeElapsed -= animationSpeed * 0.016f
        }

        if (timeElapsed > 1.0f) {
            timeElapsed = 1.0f
            isDoneTransition = true
        } else if (timeElapsed < 0.0f) {
            timeElapsed = 0.0f
            isRestoring = false
        }

        GLES32.glUniform1f(glGetUniformLocation(program, "progress"), timeElapsed)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture1)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, texture2)

        GLES32.glBindVertexArray(VAO)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.size, GLES32.GL_UNSIGNED_INT, 0)
        GLES32.glBindVertexArray(0)
    }

    fun startTransition() {
        timeElapsed = 0.0f
        this.isDoneTransition = false
    }

    fun restoreState() {
        this.isRestoring = true
    }
}