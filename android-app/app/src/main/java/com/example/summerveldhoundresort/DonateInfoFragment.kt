package com.example.summerveldhoundresort

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class DonateInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donate_info, container, false)

        val btnCopy = view.findViewById<Button>(R.id.btnCopyDetails)
        val txtBank = view.findViewById<TextView>(R.id.txtBankDetails)

        btnCopy.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Bank Details", txtBank.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Bank details copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
