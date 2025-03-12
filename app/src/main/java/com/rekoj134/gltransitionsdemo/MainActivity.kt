package com.rekoj134.gltransitionsdemo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rekoj134.gltransitionsdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOpenGLES()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOpenGLES() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs32 = configurationInfo.reqGlEsVersion >= 0x30002

        if (supportsEs32) {
            val myRenderer = MyRenderer(this@MainActivity)
            binding.myGlSurfaceView.setEGLContextClientVersion(3)
            binding.myGlSurfaceView.setRenderer(myRenderer)
            rendererSet = true

            binding.btnTransition1.setOnClickListener {
                myRenderer.startTransition()
            }

            binding.btnRestore.setOnClickListener {
                myRenderer.restoreState()
            }
        } else {
            Toast.makeText(this@MainActivity, "This device doesn't support OpenGL ES 3.2", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            binding.myGlSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            binding.myGlSurfaceView.onPause()
        }
    }
}