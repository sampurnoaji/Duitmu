package id.petersam.catatankeuangan.ui.create.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.petersam.catatankeuangan.databinding.ItemListCategoryBinding
import id.petersam.catatankeuangan.model.Category

class TransactionCategoryListAdapter(private var listener: OnItemClick? = null) :
    ListAdapter<Category, TransactionCategoryListAdapter.ContentViewHolder>(DiffCallback()) {

    inner class ContentViewHolder(private val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvCategory.text = currentList[position].category
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

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClick {
        fun onEditItem(category: Category)
        fun onDeleteItem(category: Category)
    }
}