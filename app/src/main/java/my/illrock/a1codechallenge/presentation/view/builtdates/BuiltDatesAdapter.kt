package my.illrock.a1codechallenge.presentation.view.builtdates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.databinding.ItemBuiltDateBinding

class BuiltDatesAdapter(
    private val onClick: (BuiltDate) -> Unit
) : ListAdapter<BuiltDate, BuiltDatesAdapter.BuiltDateViewHolder>(BuiltDatesDiffCallback) {

    class BuiltDateViewHolder(
        private val binding: ItemBuiltDateBinding,
        val onClick: (BuiltDate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(builtDate: BuiltDate) {
            binding.tvDate.text = builtDate.date
            itemView.setOnClickListener { onClick(builtDate) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BuiltDateViewHolder(
        ItemBuiltDateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: BuiltDateViewHolder, position: Int) {
        val builtDate = getItem(position)
        holder.bind(builtDate)
    }

    object BuiltDatesDiffCallback : DiffUtil.ItemCallback<BuiltDate>() {
        override fun areItemsTheSame(oldItem: BuiltDate, newItem: BuiltDate) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BuiltDate, newItem: BuiltDate) = oldItem == newItem
    }
}