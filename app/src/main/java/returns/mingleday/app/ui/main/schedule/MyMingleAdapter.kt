package returns.mingleday.app.ui.main.schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.SimpleMingleResponse
import returns.mingleday.databinding.ItemMingleChipBinding

class MyMingleAdapter(
    private val onMingleClick: (SimpleMingleResponse) -> Unit
): RecyclerView.Adapter<MyMingleAdapter.MemberViewHolder>(
) {
    private val items = mutableListOf<SimpleMingleResponse>()

    private var selectedMingleId: Int = -1

    fun setSelectedMingleId(mingleId: Int) {
        selectedMingleId = mingleId
        notifyDataSetChanged()
    }

    fun submitList(newItems: List<SimpleMingleResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMingleChipBinding.inflate(
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
        private val binding: ItemMingleChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SimpleMingleResponse) {
            binding.mingleNameValue.text = item.mingleName
            val isSelected = item.mingleId == selectedMingleId

            binding.root.isSelected = isSelected

            binding.mingleNameValue.setTextColor(
                if (isSelected)
                    Color.WHITE
                else
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.disable
                    )
            )
            binding.root.setOnClickListener {
                selectedMingleId = item.mingleId
                binding.mingleNameValue.setTextColor(Color.WHITE)
                notifyDataSetChanged()
                onMingleClick(item)
            }
        }
    }
}