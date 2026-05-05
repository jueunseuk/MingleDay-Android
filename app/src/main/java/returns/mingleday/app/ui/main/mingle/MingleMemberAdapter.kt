package returns.mingleday.app.ui.main.mingle

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.MingleMemberWithPermissionResponse
import returns.mingleday.app.data.remote.model.mingle.MinglePermissionResponse
import returns.mingleday.app.data.remote.model.mingle.PermissionType
import returns.mingleday.databinding.ItemMemberPermissionBinding

class MingleMemberAdapter(
    private val onPermissionChanged: (
        memberId: Long,
        permissionType: PermissionType,
        isAllowed: Boolean
    ) -> Unit
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
                val newState = !isOn
                imageView.setImageResource(
                    if (newState) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
                )
                onPermissionChanged(memberId, permissionType, newState)
            }
        }
    }
}