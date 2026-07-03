package returns.mingleday.app.ui.main.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.DailyScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.ScheduleStatus
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.app.util.DateFormatter
import returns.mingleday.app.util.DateFormatter.formatCustom
import returns.mingleday.databinding.ItemDailyScheduleBinding
import java.time.LocalDate
import java.time.LocalDateTime

class DailyScheduleAdapter(
    private var year: Int,
    private var month: Int,
    private var day: Int,
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
            binding.timeValue.text = makeTimeValue(
                LocalDate.of(year, month, day),
                item.scheduleInstance.startAt,
                item.scheduleInstance.endAt
            )
            binding.scheduleContentValue.text = item.content
            binding.categoryNameValue.text = item.category.name
            binding.categoryColorBar.setBackgroundColor(
                ColorUtil.getColorInt(item.category.backgroundColor)
            )
            when (item.scheduleInstance.scheduleStatus) {
                ScheduleStatus.TODO -> {
                    binding.scheduleStatusIcon.visibility = View.GONE
                }
                ScheduleStatus.COMPLETED -> {
                    binding.scheduleStatusIcon.visibility = View.VISIBLE
                    binding.scheduleStatusIcon.setImageResource(R.drawable.ic_schedule_complete)
                }
                else -> {
                    binding.scheduleStatusIcon.visibility = View.VISIBLE
                    binding.scheduleStatusIcon.setImageResource(R.drawable.ic_schedule_cancel)
                }
            }

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    private fun makeTimeValue(selectedDate: LocalDate, startStr: String, endStr: String): String {
        val start = LocalDateTime.parse(startStr)
        val end = LocalDateTime.parse(endStr)

        return if(DateFormatter.isSameDay(start, end)) {
            if(DateFormatter.isStartOfDay(start) && DateFormatter.isEndOfDay(end)) {
                "하루종일"
            } else {
                start.formatCustom(8)+" ~ "+end.formatCustom(8)
            }
        } else {
            if(selectedDate == start.toLocalDate()) {
                start.formatCustom(8)+" ~ 23:59"
            } else if(selectedDate == end.toLocalDate()) {
                "00:00 ~ "+end.formatCustom(8)
            } else {
                "하루종일"
            }
        }
    }

    fun updateDate(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        notifyDataSetChanged()
    }
}