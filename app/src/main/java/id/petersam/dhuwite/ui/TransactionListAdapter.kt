package id.petersam.dhuwite.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.petersam.dhuwite.R
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.databinding.ItemListChildBinding
import id.petersam.dhuwite.databinding.ItemListHeaderBinding
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.toReadableString
import id.petersam.dhuwite.util.toRupiah
import java.util.Date

class TransactionListAdapter :
    ListAdapter<TransactionListAdapter.Item, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_CHILD = 2
    }

    private inner class HeaderViewHolder(private val binding: ItemListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            with(currentList[position]) {
                date?.let {
                    binding.tvDay.text = it.toReadableString(DatePattern.D)
                    binding.tvMonth.text = it.toReadableString(DatePattern.M_LONG)
                    binding.tvYear.text = it.toReadableString(DatePattern.Y_LONG)
                }
                binding.tvIncome.text = "+ ${if (income != 0L) income?.toRupiah() else 0}"
                binding.tvExpense.text = "- ${if (expense != 0L) expense?.toRupiah() else 0}"
            }
        }
    }

    private inner class ChildViewHolder(private val binding: ItemListChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            currentList[position].transaction?.let {
                binding.tvCategory.text = it.category
                binding.tvDesc.text = it.note
                binding.tvAmount.apply {
                    text =
                        if (it.type == Transaction.Type.INCOME) "+${it.amount.toRupiah()}"
                        else "-${it.amount.toRupiah()}"
                    setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            if (it.type == Transaction.Type.INCOME) R.color.green_text
                            else R.color.red_text
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER)
            HeaderViewHolder(ItemListHeaderBinding.inflate(inflater, parent, false))
        else
            ChildViewHolder(ItemListChildBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (currentList[position].viewType == VIEW_TYPE_HEADER) (holder as HeaderViewHolder).bind(position)
        else (holder as ChildViewHolder).bind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }

    data class Item(
        val viewType: Int,
        val transaction: Transaction? = null,
        val date: Date? = null,
        val income: Long? = null,
        val expense: Long? = null
    )

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}