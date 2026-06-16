package returns.mingleday.app.ui.main.side

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.app.data.remote.model.mingle.MingleLogType
import returns.mingleday.app.data.remote.model.mingle.MyMingleLogResponse
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.app.util.DateFormatter.formatCustom
import returns.mingleday.databinding.ItemDrawerNotificationBinding
import java.time.LocalDateTime

class NotificationDrawerAdapter(
) : RecyclerView.Adapter<NotificationDrawerAdapter.DrawerNotificationViewHolder>() {

    private val items = mutableListOf<MyMingleLogResponse>()

    fun submitList(newItems: List<MyMingleLogResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrawerNotificationViewHolder {
        val binding = ItemDrawerNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DrawerNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DrawerNotificationViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class DrawerNotificationViewHolder(
        private val binding: ItemDrawerNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyMingleLogResponse) {
            binding.logTargetValue.text = item.content
            binding.logContentValue.text = item.targetName
            binding.logCreatedAtValue.text = LocalDateTime.parse(item.createdAt).formatCustom(3)

            binding.logTypeBadge.text = getTypeText(item.mingleLogType)

            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 30f
                setColor(getTypeColor(item.mingleLogType))
            }

            binding.logTypeBadge.background = background
        }

        private fun getTypeText(type: MingleLogType): String {
            return when (type) {
                MingleLogType.CREATE -> "생성"
                MingleLogType.MODIFY -> "수정"
                MingleLogType.DELETE -> "삭제"
                MingleLogType.COMPLETE -> "완료"
                MingleLogType.CANCEL -> "취소"
                MingleLogType.JOIN -> "참여"
                MingleLogType.LEAVE -> "탈퇴"
                MingleLogType.EXPULSION -> "강퇴"
                MingleLogType.PERMISSION -> "권한"
            }
        }

        private fun getTypeColor(type: MingleLogType): Int {
            return when (type) {
                MingleLogType.CREATE     -> ColorUtil.getColorInt("8FD3A8") // 민트
                MingleLogType.MODIFY     -> ColorUtil.getColorInt("A7D8F2") // 하늘
                MingleLogType.DELETE     -> ColorUtil.getColorInt("FF9AA2") // 연한 빨강
                MingleLogType.COMPLETE   -> ColorUtil.getColorInt("B5E48C") // 연두
                MingleLogType.CANCEL     -> ColorUtil.getColorInt("FFD6A5") // 연한 주황
                MingleLogType.JOIN       -> ColorUtil.getColorInt("CDB4DB") // 연보라
                MingleLogType.LEAVE      -> ColorUtil.getColorInt("D6D6D6") // 회색
                MingleLogType.EXPULSION  -> ColorUtil.getColorInt("F4978E") // 진한 살구
                MingleLogType.PERMISSION -> ColorUtil.getColorInt("F9C74F") // 노랑
            }
        }
    }
}