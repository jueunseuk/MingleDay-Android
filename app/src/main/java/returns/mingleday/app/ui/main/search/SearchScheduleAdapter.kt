package returns.mingleday.app.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.schedule.SearchScheduleInstanceResponse
import returns.mingleday.databinding.ItemMingleBinding

class SearchScheduleAdapter(
    private val onItemClick: (SearchScheduleInstanceResponse) -> Unit
) : RecyclerView.Adapter<SearchScheduleAdapter.SearchScheduleViewHolder>() {

    private val items = mutableListOf<SearchScheduleInstanceResponse>()

    fun submitList(newItems: List<SearchScheduleInstanceResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchScheduleViewHolder {
        val binding = ItemMingleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchScheduleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SearchScheduleViewHolder(
        private val binding: ItemMingleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchScheduleInstanceResponse) {
            // impl
        }
    }
}