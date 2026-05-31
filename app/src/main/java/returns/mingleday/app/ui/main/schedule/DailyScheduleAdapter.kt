package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.schedule.DailyScheduleResponse
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.app.util.DateFormatter.formatCustom
import returns.mingleday.databinding.ItemDailyScheduleBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DailyScheduleAdapter(
    private val onClick: (DailyScheduleResponse) -> Unit
): RecyclerView.Adapter<DailyScheduleAdapter.ScheduleViewHolder>(
) {
    private val items = mutableListOf<DailyScheduleResponse>()

    fun submitList(newItems: List<DailyScheduleResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemDailyScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ScheduleViewHolder(
        private val binding: ItemDailyScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyScheduleResponse) {
            binding.scheduleTitleValue.text = item.title
            binding.timeValue.text = LocalDateTime.parse(item.scheduleInstance.startAt).formatCustom(8) +" ~ "+LocalDateTime.parse(item.scheduleInstance.endAt).formatCustom(8)
            binding.scheduleContentValue.text = item.content
            binding.categoryNameValue.text = item.category.name
            binding.categoryColorBar.setBackgroundColor(
                ColorUtil.getColorInt(item.category.backgroundColor)
            )

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}