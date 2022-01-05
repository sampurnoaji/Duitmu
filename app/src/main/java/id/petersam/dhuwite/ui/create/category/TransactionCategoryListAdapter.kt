package id.petersam.dhuwite.ui.create.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.petersam.dhuwite.databinding.ItemListCategoryBinding

class TransactionCategoryListAdapter(private var listener: OnItemClick? = null) :
    ListAdapter<String, TransactionCategoryListAdapter.ContentViewHolder>(DiffCallback()) {

    inner class ContentViewHolder(private val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvCategory.text = currentList[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListCategoryBinding.inflate(inflater, parent, false)
        return ContentViewHolder(binding).apply {
            binding.imgDelete.setOnClickListener { listener?.onDeleteItem(currentList[adapterPosition]) }
            binding.imgEdit.setOnClickListener { listener?.onEditItem(currentList[adapterPosition]) }
        }
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(position)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClick {
        fun onEditItem(category: String)
        fun onDeleteItem(category: String)
    }
}