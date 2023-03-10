package com.shakhrashidov.virtual_news.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.shakhrashidov.virtual_news.ui.MainActivity
import com.shakhrashidov.virtual_news.ui.NewsViewModel

abstract class BaseFragment(layout: Int) : Fragment(layout) {

    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

}