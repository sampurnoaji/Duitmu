package id.petersam.dhuwite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.petersam.dhuwite.databinding.ItemListChildBinding
import id.petersam.dhuwite.databinding.ItemListHeaderBinding
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.toReadableString
import id.petersam.dhuwite.util.toRupiah

class TransactionListAdapter(val items: List<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_CHILD = 2
    }

    private inner class HeaderViewHolder(private val binding: ItemListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            items[position].transaction?.date?.let {
                binding.tvDay.text = it.toReadableString(DatePattern.D)
                binding.tvMonth.text = it.toReadableString(DatePattern.M_LONG)
                binding.tvYear.text = it.toReadableString(DatePattern.Y_LONG)
            }
        }
    }

    private inner class ChildViewHolder(private val binding: ItemListChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            items[position].transaction?.let {
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
        if (items[position].viewType == VIEW_TYPE_HEADER) (holder as HeaderViewHolder).bind(position)
        else (holder as ChildViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    data class Item(val viewType: Int, val transaction: Transaction? = null)
}