package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.google.android.material.button.MaterialButton

import androidx.fragment.app.activityViewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.viewmodel.SurveyViewModel

class SurveyCareerStageFragment : Fragment() {

    private val surveyViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModel.SurveyViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_career_stage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val btnContinue = view.findViewById<MaterialButton>(R.id.btnContinue)
        val rgStages = view.findViewById<RadioGroup>(R.id.rgStages)

        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnClose.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

        btnContinue.setOnClickListener {
            val selectedId = rgStages.checkedRadioButtonId
            if (selectedId != -1) {
                val radioButton = view.findViewById<RadioButton>(selectedId)
                surveyViewModel.careerStage = radioButton.text.toString()
                findNavController().navigate(R.id.action_surveyCareerStageFragment_to_surveyHobbiesFragment)
            } else {
                android.widget.Toast.makeText(requireContext(), "Please select a career stage", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        rgStages.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val rb = group.getChildAt(i) as RadioButton
                if (rb.id == checkedId) {
                    rb.setBackgroundResource(R.drawable.bg_survey_item_selected)
                } else {
                    rb.setBackgroundResource(R.drawable.bg_input_field)
                }
            }
        }
    }
}
