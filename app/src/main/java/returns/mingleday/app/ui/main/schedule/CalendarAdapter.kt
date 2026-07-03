package returns.mingleday.app.ui.main.schedule

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.CalendarDayUiModel
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.databinding.ItemCalendarDayBinding
import androidx.core.graphics.toColorInt
import returns.mingleday.app.data.remote.model.schedule.ScheduleStatus

class CalendarAdapter(
    private val onDayClick: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val items = mutableListOf<CalendarDayUiModel>()

    private var selectedDay: Int? = null

    fun submitList(newItems: List<CalendarDayUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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

    fun updateSelectedDay(day: Int) {
        selectedDay = day
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarDayUiModel) {
            binding.scheduleContainer.removeAllViews()

            if (item.day == null) {
                binding.dayValue.text = ""
                binding.dayValue.background = null
                binding.dayValue.setTextColor(Color.TRANSPARENT)

                binding.anniversaryValue.text = ""
                binding.anniversaryValue.visibility = View.GONE

                binding.root.setBackgroundResource(R.drawable.bg_calendar_cell)
                binding.root.isClickable = false
                binding.root.setOnClickListener(null)
                return
            }

            binding.root.isClickable = true
            binding.dayValue.text = item.day.toString()

            val isSelected = selectedDay == item.day

            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.bg_calendar_cell_selected
                else R.drawable.bg_calendar_cell
            )

            when {
                item.isHoliday -> {
                    binding.dayValue.setBackgroundResource(R.drawable.bg_holiday_circle)
                    binding.dayValue.setTextColor(Color.WHITE)
                }

                item.isToday -> {
                    binding.dayValue.setBackgroundResource(R.drawable.bg_today_circle)
                    binding.dayValue.setTextColor(Color.WHITE)
                }

                else -> {
                    binding.dayValue.background = null
                    binding.dayValue.setTextColor(
                        when (item.dayOfWeek) {
                            7 -> Color.RED
                            6 -> Color.BLUE
                            else -> Color.parseColor("#333333")
                        }
                    )
                }
            }

            if (item.anniversaryName != null) {
                binding.anniversaryValue.visibility = View.VISIBLE
                binding.anniversaryValue.text = item.anniversaryName
            } else {
                binding.anniversaryValue.visibility = View.GONE
                binding.anniversaryValue.text = ""
            }

            item.schedules.take(3).forEach { schedule ->
                val tagView = LayoutInflater.from(binding.root.context)
                    .inflate(
                        R.layout.item_schedule_tag,
                        binding.scheduleContainer,
                        false
                    ) as TextView

                tagView.text = schedule.title
                tagView.setTextColor(ColorUtil.getColorInt(schedule.textColor))

                when (schedule.scheduleStatus) {
                    ScheduleStatus.COMPLETED -> {
                        tagView.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_schedule_complete,
                            0,
                            0,
                            0
                        )
                        tagView.compoundDrawablePadding = 3
                    }
                    ScheduleStatus.CANCELED -> {
                        tagView.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_schedule_cancel,
                            0,
                            0,
                            0
                        )
                        tagView.compoundDrawablePadding = 3
                    }
                    else -> {
                        tagView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }

                val background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 10f
                    setColor(ColorUtil.getColorInt(schedule.backgroundColor))
                }

                tagView.background = background
                binding.scheduleContainer.addView(tagView)
            }

            val hiddenCount = item.schedules.size - 3
            if (hiddenCount > 0) {
                val moreView = TextView(binding.root.context).apply {
                    text = "+${hiddenCount}"
                    textSize = 9f
                    setTextColor("#ADB8C3".toColorInt())
                    gravity = android.view.Gravity.CENTER
                }

                binding.scheduleContainer.addView(moreView)
            }

            binding.root.setOnClickListener {
                updateSelectedDay(item.day)
                onDayClick(item.day)
            }
        }
    }
}