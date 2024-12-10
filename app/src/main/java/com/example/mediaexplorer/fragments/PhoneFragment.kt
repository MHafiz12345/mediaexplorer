package com.example.mediaexplorer.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mediaexplorer.R
import com.example.mediaexplorer.databinding.FragmentPhoneBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PhoneFragment : Fragment() {
    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialPad()
        setupCallButton()
    }

    private fun setupDialPad() {
        // Number buttons (0-9)
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("button$i", "id", requireContext().packageName)
            view?.findViewById<Button>(buttonId)?.setOnClickListener {
                appendNumber(i.toString())
            }
        }

        // Star and Hash buttons
        binding.buttonStar.setOnClickListener { appendNumber("*") }
        binding.buttonHash.setOnClickListener { appendNumber("#") }
    }

    private fun appendNumber(number: String) {
        val currentText = binding.phoneNumberInput.text.toString()
        binding.phoneNumberInput.setText(currentText + number)
    }

    private fun setupCallButton() {
        binding.callButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberInput.text.toString()
            if (phoneNumber.isNotEmpty()) {
                showCallConfirmation(phoneNumber)
            }
        }
    }

    private fun showCallConfirmation(phoneNumber: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Call")
            .setMessage("Do you want to call $phoneNumber?")
            .setPositiveButton("Call") { _, _ ->
                initiateCall(phoneNumber)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun initiateCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: Exception) {
            showError("Unable to make call")
        }
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}