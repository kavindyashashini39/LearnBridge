package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentSubscriptionBinding
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.SubscriptionViewModel

class SubscriptionFragment : Fragment() {
    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubscriptionViewModel by viewModels {
        SubscriptionViewModel.SubscriptionViewModelFactory((requireActivity().application as LearnBridgeApplication).subscriptionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SessionManager(requireContext()).getUserId()
        var selectedPaymentMethod = "Credit / Debit Card"
        var upgradeRequested = false
        binding.tvPaymentMethod.text = "Payment Method: $selectedPaymentMethod"

        if (userId == -1) {
            Toast.makeText(requireContext(), "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            return
        }

        parentFragmentManager.setFragmentResultListener(
            PaymentMethodBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            selectedPaymentMethod = bundle.getString(
                PaymentMethodBottomSheet.RESULT_METHOD,
                "Credit / Debit Card"
            )
            binding.tvPaymentMethod.text = "Payment Method: $selectedPaymentMethod"
            if (upgradeRequested) {
                viewModel.upgradeToPremium(userId)
                Toast.makeText(
                    requireContext(),
                    R.string.upgrade_success,
                    Toast.LENGTH_SHORT
                ).show()
                upgradeRequested = false
            }
        }

        binding.btnChoosePaymentMethod.setOnClickListener {
            upgradeRequested = false
            PaymentMethodBottomSheet().show(parentFragmentManager, "PaymentMethodBottomSheet")
        }

        viewModel.getLatestSubscription(userId).observe(viewLifecycleOwner) { subscription ->
            if (subscription?.plan == "Premium") {
                binding.btnFree.text = "Switch to Free"
                binding.btnFree.isEnabled = true
                binding.btnPremium.text = "Current Plan"
                binding.btnPremium.isEnabled = false
                binding.btnChoosePaymentMethod.isEnabled = false
            } else {
                binding.btnFree.text = "Current Plan"
                binding.btnFree.isEnabled = false
                binding.btnPremium.text = "Upgrade Now"
                binding.btnPremium.isEnabled = true
                binding.btnChoosePaymentMethod.isEnabled = true
            }
        }

        binding.btnPremium.setOnClickListener {
            upgradeRequested = true
            PaymentMethodBottomSheet().show(parentFragmentManager, "PaymentMethodBottomSheet")
        }

        binding.btnFree.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancel Subscription")
                .setMessage("Are you sure you want to cancel your Premium subscription?")
                .setPositiveButton("Yes, Cancel") { _, _ ->
                    viewModel.cancelSubscription(userId)
                    Toast.makeText(requireContext(), R.string.cancel_success, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Keep Premium", null)
                .show()
        }

        binding.btnViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_subscriptionFragment_to_transactionHistoryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
