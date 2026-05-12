package returns.mingleday.app.ui.main.mingle

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.databinding.ItemMingleCategoryBinding
import returns.mingleday.util.ColorUtil
import returns.mingleday.util.ColorUtil.getColorInt

class MingleCategoryAdapter(
    private val mingleId: Int
) : RecyclerView.Adapter<MingleCategoryAdapter.CategoryViewHolder>() {

    private val items = mutableListOf<CategoryResponse>()

    fun submitList(newItems: List<CategoryResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val binding = ItemMingleCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class CategoryViewHolder(
        private val binding: ItemMingleCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryResponse) {
            binding.categoryNameValue.text = item.name
            binding.categoryDescriptionValue.text = item.description

            setRoundedBackground(
                binding.categoryNameValue,
                item.backgroundColor
            )
            binding.categoryNameValue.setTextColor(getColorInt(item.textColor))
            binding.categoryDescriptionValue.text = item.description
        }

        private fun setRoundedBackground(view: View, colorCode: String) {
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f * view.resources.displayMetrics.density
                setColor(getColorInt(colorCode))
            }

            view.background = drawable
        }
    }
}