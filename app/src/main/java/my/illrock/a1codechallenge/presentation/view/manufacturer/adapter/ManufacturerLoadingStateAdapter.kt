package my.illrock.a1codechallenge.presentation.view.manufacturer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import my.illrock.a1codechallenge.databinding.ItemManufacturerLoadingStateBinding
import my.illrock.a1codechallenge.util.getErrorMessage

class ManufacturerLoadingStateAdapter(
    private val onRetryClick: () -> Unit
) : LoadStateAdapter<ManufacturerLoadingStateAdapter.ManufacturerLoadingStateViewHolder>() {
    class ManufacturerLoadingStateViewHolder(
        private val binding: ItemManufacturerLoadingStateBinding,
        private val onRetryClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { onRetryClick.invoke() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                pbLoading.isVisible = loadState is LoadState.Loading
                btnRetry.isVisible = loadState is LoadState.Error
                val error = (loadState as? LoadState.Error)?.error
                tvError.isVisible = !error?.message.isNullOrBlank()
                tvError.text = error.getErrorMessage(itemView.resources)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = ManufacturerLoadingStateViewHolder(
        ItemManufacturerLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        onRetryClick()
    }

    override fun onBindViewHolder(holder: ManufacturerLoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}