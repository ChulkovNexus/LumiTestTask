package com.example.myapplication.userstories.request_ransactions_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRequestTransactionBinding
import com.example.myapplication.userstories.transctions_list_page.TransactionsListFragment
import com.example.myapplication.utils.view_binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestTransactionsFragment : Fragment(R.layout.fragment_request_transaction) {

    private val binding by viewBinding(FragmentRequestTransactionBinding::bind)
    private val viewModel: RequestTransactionsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservables()
    }

    private fun initViews() {
        binding.apply {
            accountInputEditText.doAfterTextChanged {
                viewModel.validateAddress(accountInputEditText.text.toString())
            }
            requestButton.setOnClickListener {
                openTransactionsList()
            }
        }
    }

    private fun openTransactionsList() {
        val address = binding.accountInputEditText.text.toString()
        val bundle = bundleOf(TransactionsListFragment.ACCOUNT_ADDRESS to address)
        findNavController().navigate(
            R.id.action_RequestTransactionsFragment_to_TransactionsListFragment,
            bundle
        )
    }

    private fun initObservables() {
        viewModel.requestTransactionsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RequestTransactionsViewModel.RequestTransactionsState.Initial -> {
                    resetView(state.submitAllowed)
                }
            }
        }
    }

    private fun resetView(submitAllowed: Boolean) {
        binding.apply {
            accountInputEditText.isEnabled = true
            requestButton.isEnabled = submitAllowed
            wrongAddressText.visibility = if (submitAllowed) View.GONE else View.VISIBLE
        }
    }
}