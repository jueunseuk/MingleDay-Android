package returns.mingleday.app.ui.main.mingle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.MingleType
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse
import returns.mingleday.databinding.ItemMingleBinding

class MingleAdapter(
    private val onItemClick: (MinglesResponse) -> Unit
) : RecyclerView.Adapter<MingleAdapter.MingleViewHolder>() {

    private val items = mutableListOf<MinglesResponse>()

    fun submitList(newItems: List<MinglesResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MingleViewHolder {
        val binding = ItemMingleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MingleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MingleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MingleViewHolder(
        private val binding: ItemMingleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MinglesResponse) {
            binding.mingleNameValue.text = item.mingleName

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

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}