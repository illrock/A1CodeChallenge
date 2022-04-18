package my.illrock.a1codechallenge.presentation.view.manufacturer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.Manufacturer
import my.illrock.a1codechallenge.databinding.ItemManufacturerBinding
import my.illrock.a1codechallenge.presentation.view.manufacturer.ManufacturersAdapter.ManufacturerViewHolder

class ManufacturersAdapter constructor(
    private val onClick: (Manufacturer) -> Unit
) : PagingDataAdapter<Manufacturer, ManufacturerViewHolder>(ManufacturersDiffUtil) {

    class ManufacturerViewHolder(
        private val binding: ItemManufacturerBinding,
        val onClick: (Manufacturer) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(manufacturer: Manufacturer) = with(binding) {
            tvName.text = manufacturer.name
            switchBackgroundColor(this@ManufacturerViewHolder.bindingAdapterPosition)
            itemView.setOnClickListener { onClick(manufacturer) }
        }

        private fun switchBackgroundColor(position: Int) {
            val backgroundColorRes = if (position % 2 == 0) {
                R.color.item_background_first
            } else R.color.item_background_second
            itemView.setBackgroundResource(backgroundColorRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ManufacturerViewHolder(
        ItemManufacturerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: ManufacturerViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    object ManufacturersDiffUtil : DiffUtil.ItemCallback<Manufacturer>() {
        override fun areItemsTheSame(oldItem: Manufacturer, newItem: Manufacturer) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Manufacturer, newItem: Manufacturer) = oldItem == newItem
    }
}