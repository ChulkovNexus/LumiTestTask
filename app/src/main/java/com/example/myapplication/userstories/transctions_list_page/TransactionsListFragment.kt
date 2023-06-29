package com.example.myapplication.userstories.transctions_list_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTransactionsListBinding
import com.example.myapplication.network.responses.EtherTransactionEntity
import com.example.myapplication.network.responses.EtherTransactionWrapper
import com.example.myapplication.userstories.current_transaction.TransactionInputDialog
import com.example.myapplication.utils.view_binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class TransactionsListFragment : Fragment(R.layout.fragment_transactions_list) {

    private val binding by viewBinding(FragmentTransactionsListBinding::bind)
    private val viewModel: TransactionsListViewModel by viewModels()

    private lateinit var recyclerViewAdapter: TransactionsListAdapter
    private val interactionListener = object : InteractionListener {

        override fun onTransactionClick(transaction: EtherTransactionWrapper) {
            openCurrentTransactionDialog(transaction.transaction)
        }
    }

    private fun openCurrentTransactionDialog(transaction: EtherTransactionEntity) {
        val bundle = bundleOf(TransactionInputDialog.CURRENT_TRANSACTION to transaction)
        findNavController().navigate(
            R.id.action_TransactionsListFragment_to_CurrentTransactionDialog,
            bundle
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerViewAdapter = TransactionsListAdapter(interactionListener)
        readArguments()
    }

    private fun readArguments() {
        arguments?.let {
            it.getString(ACCOUNT_ADDRESS)?.let { accountAddress ->
                viewModel.setCurrentAddress(accountAddress)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservables()
        viewModel.loadTransactionsList()
    }

    private fun initObservables() {
        viewModel.transactionsListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TransactionsListViewModel.TransactionsListState.ErrorState -> {
                    showError(state.text ?: "")
                }
                TransactionsListViewModel.TransactionsListState.RequestProcession -> {
                    showProgress()
                }
                is TransactionsListViewModel.TransactionsListState.TransactionsReceived -> {
                    if (state.transactions.isEmpty()) {
                        showError(requireContext().getString(R.string.empty_transactions_list))
                    } else {
                        fillTransactions(state.transactions)
                    }
                }

                TransactionsListViewModel.TransactionsListState.UnknownErrorState -> {
                    showError(requireContext().getString(R.string.unknown_error))
                }
            }
        }
    }

    private fun showProgress() {
        binding.transactionsRecyclerView.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.refreshButton.visibility = View.GONE
        binding.requestProgress.visibility = View.VISIBLE
    }

    private fun fillTransactions(transactions: List<EtherTransactionWrapper>) {
        binding.transactionsRecyclerView.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
        binding.refreshButton.visibility = View.GONE
        binding.requestProgress.visibility = View.GONE
        recyclerViewAdapter.setData(transactions)
    }

    private fun showError(text: String) {
        binding.transactionsRecyclerView.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = text
        binding.refreshButton.visibility = View.VISIBLE
        binding.requestProgress.visibility = View.GONE
    }

    private fun initViews() {
        binding.apply {
            transactionsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
            transactionsRecyclerView.adapter = recyclerViewAdapter
            refreshButton.setOnClickListener {
                viewModel.loadTransactionsList()
            }
        }
    }

    companion object {
        const val ACCOUNT_ADDRESS = "account_address"
    }
}