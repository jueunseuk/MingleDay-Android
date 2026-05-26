package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.databinding.ItemTextChipBinding

class MingleCategoryAdapter(
) : RecyclerView.Adapter<MingleCategoryAdapter.MemberViewHolder>() {

    private val items = mutableListOf<CategoryResponse>()

    fun submitList(newItems: List<CategoryResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemTextChipBinding.inflate(
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
        private val binding: ItemTextChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryResponse) {

        }
    }
}