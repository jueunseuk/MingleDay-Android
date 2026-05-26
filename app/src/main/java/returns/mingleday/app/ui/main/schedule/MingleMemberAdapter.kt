package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.mingle.MingleMembersResponse
import returns.mingleday.databinding.ItemMingleMemberBinding

class MingleMemberAdapter(
    private val onClick: (MingleMembersResponse) -> Unit
) : RecyclerView.Adapter<MingleMemberAdapter.MemberViewHolder>() {

    private val items = mutableListOf<MingleMembersResponse>()

    fun submitList(newItems: List<MingleMembersResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMingleMemberBinding.inflate(
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
        private val binding: ItemMingleMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MingleMembersResponse) {
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}