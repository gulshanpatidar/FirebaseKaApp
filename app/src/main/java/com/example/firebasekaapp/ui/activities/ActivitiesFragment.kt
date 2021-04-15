package com.example.firebasekaapp.ui.activities

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.firebasekaapp.R
import com.example.firebasekaapp.databinding.ActivitiesFragmentBinding

class ActivitiesFragment : Fragment() {

    private lateinit var viewModel: ActivitiesViewModel
    private lateinit var binding: ActivitiesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)
        binding = ActivitiesFragmentBinding.inflate(inflater, container, false)

        viewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textActivities.text = it
        })
        return binding.root
    }

}