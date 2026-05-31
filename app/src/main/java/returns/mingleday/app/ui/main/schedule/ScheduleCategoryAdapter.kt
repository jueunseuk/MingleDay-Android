package returns.mingleday.app.ui.main.schedule

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.databinding.ItemScheduleCategoryChipBinding

class ScheduleCategoryAdapter(
) : RecyclerView.Adapter<ScheduleCategoryAdapter.ScheduleCategoryViewHolder>() {

    private val items = mutableListOf<CategoryResponse>()

    fun submitList(newItems: List<CategoryResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleCategoryViewHolder {
        val binding = ItemScheduleCategoryChipBinding.inflate(
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
        private val binding: ItemScheduleCategoryChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryResponse) {
            binding.mingleCategoryNameValue.text = item.name

            binding.mingleCategoryNameValue.setTextColor(
                ColorUtil.getColorInt(item.textColor)
            )

            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 30f
                setColor(ColorUtil.getColorInt(item.backgroundColor))
            }
            binding.root.background = background
        }
    }
}