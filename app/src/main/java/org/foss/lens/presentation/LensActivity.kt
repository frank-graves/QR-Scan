// app/src/main/java/org/foss/lens/presentation/LensActivity.kt
package org.foss.lens.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.foss.lens.R
import org.foss.lens.ScribeApplication
import org.foss.lens.databinding.ActivityLensBinding
import org.foss.lens.domain.ScanState

class LensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLensBinding
    private lateinit var scanVM: ScanVM
    private lateinit var historyVM: HistoryVM
    private lateinit var historyAdapter: HistoryAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scanVM.startScanning()
        } else {
            binding.statusText.text = getString(R.string.permission_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = ScribeApplication.instance
        val archive = app.archive
        val lens = app.createLens(this)

        val factory = LensViewModelFactory(archive, lens)
        val vmProvider = ViewModelProvider(this, factory)
        scanVM = vmProvider.get(ScanVM::class.java)
        historyVM = vmProvider.get(HistoryVM::class.java)

        historyAdapter = HistoryAdapter()
        binding.historyRecycler.layoutManager = LinearLayoutManager(this)
        binding.historyRecycler.adapter = historyAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scanVM.state.collect { state ->
                    updateUiForState(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyVM.entries.collect { entries ->
                    historyAdapter.submitList(entries)
                }
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scanVM.startScanning()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun updateUiForState(state: ScanState) {
        when (state) {
            is ScanState.Idle -> {
                binding.statusText.text = getString(R.string.status_idle)
                binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
                binding.resultText.visibility = View.GONE
                binding.statusText.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(PathInterpolator(0.34f, 1.56f, 0.64f, 1f))
            }

            is ScanState.Detecting -> {
                binding.statusText.text = getString(R.string.status_detecting)
                binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
                binding.resultText.visibility = View.GONE
                binding.statusText.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(400)
                    .setInterpolator(PathInterpolator(0.34f, 1.56f, 0.64f, 1f))
                    .withEndAction {
                        binding.statusText.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(400)
                            .start()
                    }
                    .start()
            }

            is ScanState.Success -> {
                binding.statusText.text = getString(R.string.status_success)
                binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
                binding.resultText.text = state.codex.payload
                binding.resultText.visibility = View.VISIBLE
                binding.resultText.animate()
                    .translationY(20f)
                    .alpha(0f)
                    .setDuration(0)
                    .withEndAction {
                        binding.resultText.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(300)
                            .setInterpolator(PathInterpolator(0.34f, 1.56f, 0.64f, 1f))
                            .start()
                    }
                    .start()
                historyVM.load()
            }

            is ScanState.Error -> {
                binding.statusText.text = getString(R.string.status_error, state.message ?: "Unknown")
                binding.resultText.visibility = View.GONE
                binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.error))
            }
        }
    }
}
