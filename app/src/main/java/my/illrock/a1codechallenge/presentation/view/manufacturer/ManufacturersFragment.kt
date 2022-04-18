package my.illrock.a1codechallenge.presentation.view.manufacturer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import my.illrock.a1codechallenge.data.model.Manufacturer
import my.illrock.a1codechallenge.databinding.FragmentManufacturersBinding

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
            layoutManager = LinearLayoutManager(context)
            adapter = manufacturersAdapter
        }

        //todo pull-to-refresh

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.manufacturersFlow.collectLatest { pagingData ->
                manufacturersAdapter.submitData(pagingData)
            }
        }
    }

    private fun onItemClick(manufacturer: Manufacturer) {
        val action = ManufacturersFragmentDirections
            .actionManufacturersFragmentToMainTypesFragment(manufacturer.id, manufacturer.name)
        findNavController()
            .navigate(action)
    }
}