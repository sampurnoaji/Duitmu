package id.petersam.dhuwite.ui.create.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.petersam.dhuwite.R
import id.petersam.dhuwite.databinding.ItemListCategoryBinding
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.databinding.ItemListChildBinding
import id.petersam.dhuwite.databinding.ItemListHeaderBinding
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.toReadableString
import id.petersam.dhuwite.util.toRupiah
import java.util.Date

class TransactionCategoryListAdapter :
    ListAdapter<String, TransactionCategoryListAdapter.ContentViewHolder>(DiffCallback()) {

    inner class ContentViewHolder(private val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvCategory.text = currentList[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContentViewHolder(ItemListCategoryBinding.inflate(inflater, parent, false))
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
}