package com.example.firebasekaapp.ui.feels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.firebasekaapp.R
import com.example.firebasekaapp.databinding.FragmentFeelsBinding

class FeelsFragment : Fragment() {

    private lateinit var feelsViewModel: FeelsViewModel
    private lateinit var binding: FragmentFeelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feelsViewModel =
            ViewModelProvider(this).get(FeelsViewModel::class.java)
        binding = FragmentFeelsBinding.inflate(inflater, container, false)
        feelsViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textFeels.text = it
        })
        return binding.root
    }
}