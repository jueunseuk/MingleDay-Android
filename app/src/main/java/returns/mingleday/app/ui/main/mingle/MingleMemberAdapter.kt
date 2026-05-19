package returns.mingleday.app.ui.main.mingle

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.MingleMemberWithPermissionResponse
import returns.mingleday.app.data.remote.model.mingle.MinglePermissionResponse
import returns.mingleday.app.data.remote.model.mingle.PermissionType
import returns.mingleday.databinding.ItemMemberPermissionBinding
import returns.mingleday.app.util.DateFormatter.formatCustom
import java.time.LocalDateTime

class MingleMemberAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onPermissionChanged: suspend (
        memberId: Long,
        permissionType: PermissionType,
        isAllowed: Boolean
    ) -> Boolean
) : RecyclerView.Adapter<MingleMemberAdapter.MemberViewHolder>() {

    private val items = mutableListOf<MingleMemberWithPermissionResponse>()

    fun submitList(newItems: List<MingleMemberWithPermissionResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberPermissionBinding.inflate(
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
        private val binding: ItemMemberPermissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MingleMemberWithPermissionResponse) {
            binding.memberNameValue.text = item.name
            binding.registerDateValue.text = LocalDateTime.parse(item.createdAt).formatCustom(2)

            val permissionMap = item.permissions.associateBy { it.permissionType }
            bindToggle(
                binding.toggleInvite,
                item.memberId,
                PermissionType.INVITE,
                permissionMap
            )
            bindToggle(
                binding.toggleExpulsion,
                item.memberId,
                PermissionType.EXPULSION,
                permissionMap
            )
            bindToggle(
                binding.toggleCreate,
                item.memberId,
                PermissionType.CREATE,
                permissionMap
            )
            bindToggle(
                binding.toggleModify,
                item.memberId,
                PermissionType.MODIFY,
                permissionMap
            )
            bindToggle(
                binding.toggleDelete,
                item.memberId,
                PermissionType.DELETE,
                permissionMap
            )
        }

        private fun bindToggle(
            imageView: ImageView,
            memberId: Long,
            permissionType: PermissionType,
            permissionMap: Map<PermissionType, MinglePermissionResponse>
        ) {
            var isOn = permissionMap[permissionType]?.value == true

            imageView.setImageResource(
                if (isOn) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
            )

            imageView.setOnClickListener {
                val previousState = isOn
                val newState = !isOn

                isOn = newState
                imageView.setImageResource(
                    if (newState) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
                )

                lifecycleOwner.lifecycleScope.launch {
                    val success = onPermissionChanged(memberId, permissionType, newState)
                    if (!success) {
                        isOn = previousState
                        imageView.setImageResource(
                            if (previousState) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
                        )
                    }
                }
            }
        }

    }
}