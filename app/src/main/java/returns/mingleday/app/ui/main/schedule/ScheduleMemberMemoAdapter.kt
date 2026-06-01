package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.schedule.ScheduleMemberResponse
import returns.mingleday.databinding.ItemScheduleMemberMemoBinding

class ScheduleMemberMemoAdapter
    : RecyclerView.Adapter<ScheduleMemberMemoAdapter.ScheduleMemberMemoViewHolder>() {

    private val items = mutableListOf<ScheduleMemberResponse>()

    fun submitList(newItems: List<ScheduleMemberResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleMemberMemoViewHolder {
        val binding = ItemScheduleMemberMemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ScheduleMemberMemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleMemberMemoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ScheduleMemberMemoViewHolder(
        private val binding: ItemScheduleMemberMemoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleMemberResponse) {
            binding.memberNameValue.text = item.name
            binding.memberMemoValue.text = item.memo.ifBlank { "메모를 추가해주세요" }
        }
    }
}