package returns.mingleday.app.ui.main.schedule

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.databinding.ItemMingleCategoryChipBinding

class MingleCategoryAdapter(
    private val onClick: (CategoryResponse) -> Unit
) : RecyclerView.Adapter<MingleCategoryAdapter.MemberViewHolder>() {

    private val items = mutableListOf<CategoryResponse>()
    private var selectedCategoryId: Long = -1

    fun submitList(newItems: List<CategoryResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMingleCategoryChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MemberViewHolder(
        private val binding: ItemMingleCategoryChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryResponse) {
            val isSelected = item.categoryId == selectedCategoryId

            binding.mingleCategoryNameValue.text = item.name

            binding.mingleCategoryNameValue.setTextColor(
                if (isSelected)
                    ColorUtil.getColorInt(item.textColor)
                else
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.disable
                    )
            )

            setChipBackground(
                binding.root,
                if (isSelected)
                    item.backgroundColor
                else "FFFFFF",
                isSelected
            )

            binding.root.setOnClickListener {
                selectedCategoryId = item.categoryId

                notifyDataSetChanged()
                onClick(item)
            }
        }
    }

    private fun setChipBackground(view: View, colorCode: String, isSelected: Boolean) {
        val radius = 15f * view.resources.displayMetrics.density

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            setColor(ColorUtil.getColorInt(colorCode))

            if (!isSelected) {
                setStroke(2, ContextCompat.getColor(view.context, R.color.button_stroke_selector))
            }
        }

        view.background = drawable
    }
}