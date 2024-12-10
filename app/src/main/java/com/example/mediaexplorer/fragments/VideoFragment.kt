package com.example.mediaexplorer.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.mediaexplorer.R
import com.example.mediaexplorer.databinding.FragmentVideoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VideoFragment : Fragment() {
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private var mediaController: MediaController? = null
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVideoView()
        setupControls()
    }

    private fun setupVideoView() {
        mediaController = MediaController(requireContext()).apply {
            setAnchorView(binding.videoView)
        }

        binding.videoView.apply {
            setMediaController(mediaController)

            // Set up video path - using sample video from raw resources
            val videoPath = "android.resource://${requireContext().packageName}/${R.raw.sample_video}"
            setVideoURI(Uri.parse(videoPath))

            setOnPreparedListener { mediaPlayer ->
                with(binding) {
                    videoProgress.visibility = View.GONE
                    thumbnailView.visibility = View.GONE
                    videoDuration.text = formatDuration(mediaPlayer.duration)
                }
            }

            setOnCompletionListener {
                pauseVideo()
                binding.thumbnailView.visibility = View.VISIBLE
            }

            setOnErrorListener { _, _, _ ->
                showPlaybackError()
                true
            }
        }
    }

    private fun setupControls() {
        with(binding) {
            playPauseButton.setOnClickListener {
                if (isPlaying) {
                    pauseVideo()
                } else {
                    playVideo()
                }
            }

            videoTitle.text = "Sample Video"
            videoProgress.visibility = View.VISIBLE
        }
    }

    private fun playVideo() {
        with(binding) {
            videoView.start()
            playPauseButton.setImageResource(R.drawable.ic_pause)
            thumbnailView.visibility = View.GONE
        }
        isPlaying = true
    }

    private fun pauseVideo() {
        with(binding) {
            videoView.pause()
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
        isPlaying = false
    }

    private fun formatDuration(duration: Int): String {
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun showPlaybackError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Playback Error")
            .setMessage("Failed to play the video. Please try again.")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pauseVideo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.videoView?.stopPlayback()
        mediaController = null
        _binding = null
    }
}