package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.google.android.material.button.MaterialButton

import androidx.fragment.app.activityViewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.viewmodel.SurveyViewModel

class SurveyExperienceFragment : Fragment() {

    private val selectedOptions = mutableSetOf<Int>()
    private val surveyViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModel.SurveyViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_experience, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val btnContinue = view.findViewById<MaterialButton>(R.id.btnContinue)
        val tvMaxSelection = view.findViewById<View>(R.id.tvMaxSelection)

        val optionViews = mapOf(
            R.id.optionHealth to "Health",
            R.id.optionIT to "IT",
            R.id.optionTeaching to "Teaching",
            R.id.optionLanguage to "Language"
        )

        optionViews.forEach { (id, value) ->
            val layout = view.findViewById<LinearLayout>(id)
            layout.setOnClickListener {
                if (selectedOptions.contains(id)) {
                    selectedOptions.remove(id)
                    layout.setBackgroundResource(R.drawable.bg_input_field)
                    tvMaxSelection.visibility = View.GONE
                } else if (selectedOptions.size < 2) {
                    selectedOptions.add(id)
                    layout.setBackgroundResource(R.drawable.bg_survey_item_selected)
                    if (selectedOptions.size == 2) {
                        tvMaxSelection.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "You can select up to 2 options", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnClose.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

        btnContinue.setOnClickListener {
            if (selectedOptions.isNotEmpty()) {
                val experienceText = selectedOptions.map { optionViews[it] }.joinToString(", ")
                surveyViewModel.experience = experienceText
                findNavController().navigate(R.id.action_surveyExperienceFragment_to_surveyInterests1Fragment)
            } else {
                Toast.makeText(requireContext(), "Please select at least one option", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
