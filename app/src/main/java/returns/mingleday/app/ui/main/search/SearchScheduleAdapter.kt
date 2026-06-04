package returns.mingleday.app.ui.main.search

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.schedule.ScheduleStatus
import returns.mingleday.app.data.remote.model.schedule.SearchScheduleInstanceResponse
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.app.util.DateFormatter
import returns.mingleday.app.util.DateFormatter.formatCustom
import returns.mingleday.databinding.ItemSearchScheduleBinding
import java.time.LocalDateTime

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
        val binding = ItemSearchScheduleBinding.inflate(
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
        private val binding: ItemSearchScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchScheduleInstanceResponse) {
            binding.scheduleTitleValue.text = item.title
            binding.scheduleTimeValue.text = LocalDateTime.parse(item.startAt).formatCustom(4)+" ~ "+ LocalDateTime.parse(item.endAt).formatCustom(4)
            binding.scheduleMemoValue.text = item.memo.ifBlank { "메모 없음" }

            binding.scheduleStatusValue.text = when (item.scheduleStatus) {
                ScheduleStatus.TODO -> "예정"
                ScheduleStatus.COMPLETED -> "완료"
                ScheduleStatus.CANCELED -> "취소"
            }

            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f

                setColor(
                    when (item.scheduleStatus) {
                        ScheduleStatus.TODO -> ColorUtil.getColorInt("A7D8F2")      // 하늘색
                        ScheduleStatus.COMPLETED -> ColorUtil.getColorInt("B5E48C") // 연두색
                        ScheduleStatus.CANCELED -> ColorUtil.getColorInt("FFB3C1")  // 연핑크
                    }
                )
            }

            binding.scheduleStatusValue.background = background

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}