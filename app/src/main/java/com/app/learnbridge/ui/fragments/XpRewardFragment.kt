package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R

class XpRewardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_xp_reward, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btnStartLearning).setOnClickListener {
            findNavController().navigate(R.id.action_xpRewardFragment_to_recommendationIntroFragment)
        }
        view.findViewById<View>(R.id.tvLearnMore).setOnClickListener {
            findNavController().navigate(R.id.action_xpRewardFragment_to_recommendationIntroFragment)
        }
    }
}
