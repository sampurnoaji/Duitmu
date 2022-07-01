package id.petersam.duitmu.ui.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.petersam.duitmu.databinding.ItemListCategoryChartLegendBinding
import id.petersam.duitmu.util.toRupiah
import java.text.DecimalFormat

class CategoryChartLegendListAdapter :
    ListAdapter<CategoryChartLegendListAdapter.Item, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    data class Item(
        val color: Int,
        val category: String,
        val amount: Long,
        val percent: Float? = null
    )

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListCategoryChartLegendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ContentViewHolder).bind(currentList[position])
    }

    private inner class ContentViewHolder(private val binding: ItemListCategoryChartLegendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.colorIndicator.setBackgroundColor(item.color)
            binding.tvCategory.text = "(${item.percent?.formatPercentage()}) ${item.category}"
            binding.tvAmount.text = item.amount.toRupiah()
        }
    }

    private fun Float.formatPercentage(): String {
        val formatter = DecimalFormat("###,###,##0.0")
        return "${formatter.format(this)} %"
    }
}