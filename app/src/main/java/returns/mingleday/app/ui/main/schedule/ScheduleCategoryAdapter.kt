package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.databinding.ItemMingleCategoryChipBinding

class ScheduleCategoryAdapter(
) : RecyclerView.Adapter<ScheduleCategoryAdapter.ScheduleCategoryViewHolder>() {

    private val items = mutableListOf<CategoryResponse>()

    fun submitList(newItems: List<CategoryResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleCategoryViewHolder {
        val binding = ItemMingleCategoryChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleCategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ScheduleCategoryViewHolder(
        private val binding: ItemMingleCategoryChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryResponse) {
            binding.mingleCategoryNameValue.text = item.name
        }
    }
}