package com.example.myapplication.userstories.transctions_list_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutEtherTransactionBinding
import com.example.myapplication.network.responses.EtherTransactionWrapper
import com.example.myapplication.utils.AmountFormatter
import com.example.myapplication.utils.date_formatters.DateUtil
import com.example.myapplication.utils.web3.Web3AmountConverter
import java.math.BigDecimal

class TransactionsListAdapter(val interactionListener: InteractionListener) :
    RecyclerView.Adapter<EtherTransactionViewHolder>() {

    private val data = ArrayList<EtherTransactionWrapper>()

    fun setData(data: List<EtherTransactionWrapper>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtherTransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutEtherTransactionBinding.inflate(inflater, parent, false)
        return EtherTransactionViewHolder(binding, interactionListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EtherTransactionViewHolder, position: Int) {
        holder.fillView(data[position])
    }
}

class EtherTransactionViewHolder(
    val binding: LayoutEtherTransactionBinding,
    val interactionListener: InteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun fillView(item: EtherTransactionWrapper) {
        val transaction = item.transaction
        val context = binding.root.context
        val directionText = if (item.isIncomeTransaction) {
            context.getString(R.string.direction_income)
        } else {
            context.getString(R.string.direction_outcome)
        }
        val ethValue = if (transaction.value == 0L && transaction.input.isNotEmpty() && transaction.input != "0x") {
            Web3AmountConverter.decodeAmountFromInput(transaction)
        } else {
             Web3AmountConverter.fromWei(BigDecimal(transaction.value), Web3AmountConverter.Unit.ETHER)
        }
        val formattedValue = AmountFormatter.format(ethValue)
        binding.apply {
            transactionDateText.text = DateUtil.timestampToDateStr(transaction.timeStamp)
            transactionReceiverText.text = transaction.to
            transactionSenderText.text = transaction.from
            transactionDirectionText.text = directionText
            transactionValueText.text = formattedValue
            root.setOnClickListener {
                interactionListener.onTransactionClick(item)
            }
        }
    }
}

interface InteractionListener {
    fun onTransactionClick(transaction: EtherTransactionWrapper)
}