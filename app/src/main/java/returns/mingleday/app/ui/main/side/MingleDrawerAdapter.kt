package returns.mingleday.app.ui.main.side

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.MingleType
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse
import returns.mingleday.databinding.ItemDrawerMingleBinding

class MingleDrawerAdapter(
    private val onItemClick: (MinglesResponse) -> Unit
) : RecyclerView.Adapter<MingleDrawerAdapter.DrawerMingleViewHolder>() {

    private val items = mutableListOf<MinglesResponse>()

    fun submitList(newItems: List<MinglesResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrawerMingleViewHolder {
        val binding = ItemDrawerMingleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DrawerMingleViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DrawerMingleViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class DrawerMingleViewHolder(
        private val binding: ItemDrawerMingleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MinglesResponse) {
            binding.mingleNameValue.text = item.mingleName

            if(item.profileUrl.isBlank()) {
                when(item.mingleType) {
                    MingleType.FAMILY -> binding.mingleImageValue.setImageResource(R.drawable.bg_family_default)
                    MingleType.FRIEND -> binding.mingleImageValue.setImageResource(R.drawable.bg_friend_default)
                    MingleType.LOVER -> binding.mingleImageValue.setImageResource(R.drawable.bg_lover_default)
                    MingleType.SCHOOL -> binding.mingleImageValue.setImageResource(R.drawable.bg_school_default)
                    MingleType.COMPANY -> binding.mingleImageValue.setImageResource(R.drawable.bg_compamy_default)
                    MingleType.CLUB -> binding.mingleImageValue.setImageResource(R.drawable.bg_club_default)
                    MingleType.STUDY -> binding.mingleImageValue.setImageResource(R.drawable.bg_study_default)
                    MingleType.CUSTOM -> binding.mingleImageValue.setImageResource(R.drawable.bg_custom_default)
                    else -> binding.mingleImageValue.setImageResource(R.drawable.bg_custom_default)
                }
            } else {
                Glide.with(binding.root)
                    .load(item.profileUrl)
                    .placeholder(R.drawable.default_profile)
                    .fallback(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.mingleImageValue)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}