package my.illrock.a1codechallenge.presentation.view.maintype

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.databinding.ItemMainTypeBinding

class MainTypesAdapter(
    private val onClick: (MainType) -> Unit
) : ListAdapter<MainType, MainTypesAdapter.MainTypeViewHolder>(MainTypeDiffCallback) {

    class MainTypeViewHolder(
        private val binding: ItemMainTypeBinding,
        val onClick: (MainType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mainType: MainType) {
            binding.tvName.text = mainType.name
            itemView.setOnClickListener { onClick(mainType) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainTypeViewHolder(
        ItemMainTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: MainTypeViewHolder, position: Int) {
        val mainType = getItem(position)
        holder.bind(mainType)
    }
}

object MainTypeDiffCallback : DiffUtil.ItemCallback<MainType>() {
    override fun areItemsTheSame(oldItem: MainType, newItem: MainType) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MainType, newItem: MainType) = oldItem == newItem
}