package my.illrock.a1codechallenge.presentation.view.maintype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.databinding.FragmentMainTypesBinding

@AndroidEntryPoint
class MainTypesFragment : Fragment() {
    private val args: MainTypesFragmentArgs by navArgs()
    private val vm: MainTypesViewModel by viewModels()

    private var _binding: FragmentMainTypesBinding? = null
    private val binding get() = _binding!!

    private val mainTypesAdapter = MainTypesAdapter(::onItemClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainTypesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding.toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            findViewById<TextView>(R.id.tvTitle)?.let {
                it.text = args.manufacturer.name
            }
        }

        binding.rvMainTypes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainTypesAdapter
        }
        binding.srlPull.setOnRefreshListener {
            vm.loadMainTypes(args.manufacturer.id, true)
        }

        vm.mainTypes.observe(viewLifecycleOwner) {
            mainTypesAdapter.submitList(it)
        }

        vm.isLoading.observe(viewLifecycleOwner) {
            binding.srlPull.isRefreshing = it
        }

        vm.errorMessage.observe(viewLifecycleOwner) {
            showError(it)
        }

        vm.errorRes.observe(viewLifecycleOwner) {
            showError(it)
        }

        vm.loadMainTypes(args.manufacturer.id, false)
    }

    private fun showError(errorRes: Int?) {
        val error = errorRes?.let { getString(it) }
        showError(error)
    }

    private fun showError(error: String?) {
        binding.rvMainTypes.isVisible = error == null
        binding.tvError.isVisible = error != null
        error?.let { binding.tvError.text = it }
    }

    private fun onItemClick(mainType: MainType) {
        val action = MainTypesFragmentDirections
            .actionMainTypesFragmentToBuiltDatesFragment(args.manufacturer, mainType)
        findNavController()
            .navigate(action)
    }
}