package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentTransactionHistoryBinding
import com.app.learnbridge.db.Transaction
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryFragment : Fragment() {
    private var _binding: FragmentTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModel.TransactionViewModelFactory((requireActivity().application as LearnBridgeApplication).transactionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SessionManager(requireContext()).getUserId()
        val adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter

        viewModel.getTransactionsByUser(userId).observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.tvNoTransactions.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
            binding.rvTransactions.visibility = if (transactions.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
        private var transactions = listOf<Transaction>()

        fun submitList(newTransactions: List<Transaction>) {
            transactions = newTransactions
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val transaction = transactions[position]
            holder.tvPlanName.text = transaction.planName
            holder.tvAmount.text = "$${transaction.amount}"
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvTimestamp.text = sdf.format(Date(transaction.timestamp))
        }

        override fun getItemCount() = transactions.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvPlanName: TextView = view.findViewById(R.id.tvPlanName)
            val tvAmount: TextView = view.findViewById(R.id.tvAmount)
            val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        }
    }
}
