package my.illrock.a1codechallenge.presentation.view.manufacturer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.Manufacturer
import my.illrock.a1codechallenge.databinding.FragmentManufacturersBinding
import my.illrock.a1codechallenge.presentation.view.manufacturer.adapter.ManufacturerLoadingStateAdapter
import my.illrock.a1codechallenge.presentation.view.manufacturer.adapter.ManufacturersAdapter
import my.illrock.a1codechallenge.util.getErrorMessage
import my.illrock.a1codechallenge.util.isPortraitOrientation

@AndroidEntryPoint
class ManufacturersFragment : Fragment() {
    private val vm: ManufacturersViewModel by viewModels()

    private var _binding: FragmentManufacturersBinding? = null
    private val binding get() = _binding!!

    private val manufacturersAdapter = ManufacturersAdapter(::onItemClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManufacturersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvManufacturers.apply {
            layoutManager = if (isPortraitOrientation()) {
                LinearLayoutManager(context)
            } else {
                GridLayoutManager(context, resources.getInteger(R.integer.fragment_landscape_columns))
            }
            adapter = manufacturersAdapter.withLoadStateHeaderAndFooter(
                ManufacturerLoadingStateAdapter { manufacturersAdapter.retry() },
                ManufacturerLoadingStateAdapter { manufacturersAdapter.retry() }
            )
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }

        binding.srlPull.setOnRefreshListener {
            manufacturersAdapter.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            manufacturersAdapter.loadStateFlow.collectLatest {
                val loadState = it.refresh
                binding.srlPull.isRefreshing = loadState is LoadState.Loading
                if (loadState is LoadState.Error) {
                    loadState.error.getErrorMessage(resources)?.let { message ->
                        binding.tvError.isVisible = true
                        binding.tvError.text = message
                        binding.rvManufacturers.isVisible = false
                    }
                } else {
                    binding.tvError.isVisible = false
                    binding.rvManufacturers.isVisible = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.manufacturersFlow.collectLatest { pagingData ->
                manufacturersAdapter.submitData(pagingData)
            }
        }
    }

    private fun onItemClick(manufacturer: Manufacturer) {
        val action = ManufacturersFragmentDirections
            .actionManufacturersFragmentToMainTypesFragment(manufacturer)
        findNavController()
            .navigate(action)
    }
}