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

class FeelsFragment : Fragment() {

    private lateinit var feelsViewModel: FeelsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feelsViewModel =
            ViewModelProvider(this).get(FeelsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_feels, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        feelsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}