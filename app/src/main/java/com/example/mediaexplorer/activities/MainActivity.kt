package com.example.mediaexplorer

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mediaexplorer.databinding.ActivityMainBinding
import com.example.mediaexplorer.fragments.CameraFragment
import com.example.mediaexplorer.fragments.PhoneFragment
import com.example.mediaexplorer.fragments.VideoFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupQuickActionFab()
        checkAndRequestPermissions()
    }

    private fun setupBottomNavigation() {
        binding.bottomBar.onItemSelected = { position: Int ->
            when (position) {
                0 -> switchFragment(VideoFragment())
                1 -> switchFragment(CameraFragment())
                2 -> switchFragment(PhoneFragment())
                else -> false
            }
        }

        // Set initial selection
        binding.bottomBar.setActiveItem(0)
    }

    private fun switchFragment(fragment: Fragment): Boolean {
        binding.apply {
            fragmentContainer.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        currentFragment = fragment

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        return true
    }

    private fun setupQuickActionFab() {
        binding.quickActionFab.setOnClickListener {
            showQuickActionDialog()
        }
    }

    private fun showQuickActionDialog() {
        val options = arrayOf("Take Photo", "Make Call")
        MaterialAlertDialogBuilder(this)
            .setTitle("Quick Actions")
            .setItems(options) { _, which: Int ->
                when (which) {
                    0 -> binding.bottomBar.setActiveItem(1) // Camera
                    1 -> binding.bottomBar.setActiveItem(2) // Phone
                }
            }
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount > 0 -> {
                supportFragmentManager.popBackStack()
            }
            currentFragment !is VideoFragment -> {
                binding.bottomBar.setActiveItem(0)
            }
            else -> {
                showExitDialog()
            }
        }
        super.onBackPressed()
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit") { _, _ -> finish() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkAndRequestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (it.areAllPermissionsGranted()) {
                            binding.bottomBar.setActiveItem(0)
                        } else {
                            showPermissionError()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun showPermissionError() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permissions Required")
            .setMessage("This app requires camera and phone permissions to function properly.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }
}