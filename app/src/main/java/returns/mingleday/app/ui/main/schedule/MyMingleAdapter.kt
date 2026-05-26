package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.mingle.SimpleMingleResponse
import returns.mingleday.databinding.ItemTextChipBinding

class MyMingleAdapter(
    private val onMingleClick: (SimpleMingleResponse) -> Unit
): RecyclerView.Adapter<MyMingleAdapter.MemberViewHolder>(
) {
    private val items = mutableListOf<SimpleMingleResponse>()

    fun submitList(newItems: List<SimpleMingleResponse>) {
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

        fun bind(item: SimpleMingleResponse) {
            binding.root.setOnClickListener {
                onMingleClick(item)
            }
        }
    }
}