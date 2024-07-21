package com.example.freshbite.view.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freshbite.R
import com.example.freshbite.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
        binding = FragmentInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)
        index?.let { updateUI(it) }
    }

    private fun updateUI(index: Int) {
        val freshTitleRes: Int
        val freshLabelRes: Int
        val rottenTitleRes: Int
        val rottenLabelRes: Int
        val freshImageRes: Int
        val rottenImageRes: Int

        when (index) {
            1 -> {
                freshTitleRes = R.string.jeruk_segar
                freshLabelRes = R.string.info_jeruk_segar
                rottenTitleRes = R.string.jeruk_busuk
                rottenLabelRes = R.string.info_jeruk_busuk
                freshImageRes = R.drawable.jeruk_segar
                rottenImageRes = R.drawable.jeruk_busuk
            }
            2 -> {
                freshTitleRes = R.string.apel_segar
                freshLabelRes = R.string.info_apel_segar
                rottenTitleRes = R.string.apel_busuk
                rottenLabelRes = R.string.info_apel_busuk
                freshImageRes = R.drawable.apel_segar
                rottenImageRes = R.drawable.apel_busuk
            }
            3 -> {
                freshTitleRes = R.string.pisang_segar
                freshLabelRes = R.string.info_pisang_segar
                rottenTitleRes = R.string.pisang_busuk
                rottenLabelRes = R.string.info_pisang_busuk
                freshImageRes = R.drawable.pisang_segar
                rottenImageRes = R.drawable.pisang_busuk
            }
            else -> return
        }

        binding.apply {
            freshTitle.text = getString(freshTitleRes)
            freshLabel.text = getString(freshLabelRes)
            rottenTitle.text = getString(rottenTitleRes)
            rottenLabel.text = getString(rottenLabelRes)
            freshImage.setImageResource(freshImageRes)
            rottenImage.setImageResource(rottenImageRes)
        }
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
    }
}