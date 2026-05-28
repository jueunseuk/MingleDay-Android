package returns.mingleday.app.ui.main.schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.CalendarDayUiModel
import returns.mingleday.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private val onDayClick: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val items = mutableListOf<CalendarDayUiModel>()

    fun submitList(newItems: List<CalendarDayUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarDayUiModel) {
            binding.scheduleContainer.removeAllViews()

            if (item.day == null) {
                binding.dayText.text = ""
                binding.root.setOnClickListener(null)
                return
            }

            binding.dayText.text = item.day.toString()

            binding.dayText.setTextColor(
                when (item.dayOfWeek) {
                    7 -> Color.RED
                    6 -> Color.BLUE
                    else -> Color.parseColor("#333333")
                }
            )

            item.schedules.take(3).forEach { scheduleTitle ->
                val tagView = LayoutInflater.from(binding.root.context)
                    .inflate(
                        R.layout.item_schedule_tag,
                        binding.scheduleContainer,
                        false
                    ) as TextView

                tagView.text = scheduleTitle
                binding.scheduleContainer.addView(tagView)
            }

            binding.root.setOnClickListener {
                onDayClick(item.day)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}