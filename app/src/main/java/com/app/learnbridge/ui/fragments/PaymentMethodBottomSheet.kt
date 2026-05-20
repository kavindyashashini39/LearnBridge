package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.app.learnbridge.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentMethodBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_payment_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val paymentMethods = view.findViewById<RadioGroup>(R.id.rgPaymentMethods)

        view.findViewById<View>(R.id.btnConfirmUpgrade).setOnClickListener {
            val selectedMethod = when (paymentMethods.checkedRadioButtonId) {
                R.id.rbBankTransfer -> "Bank Transfer"
                R.id.rbWallet -> "Digital Wallet"
                else -> "Credit / Debit Card"
            }

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply { putString(RESULT_METHOD, selectedMethod) }
            )
            dismiss()
        }
    }

    companion object {
        const val REQUEST_KEY = "payment_method_result"
        const val RESULT_METHOD = "payment_method"
    }
}
