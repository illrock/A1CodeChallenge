package my.illrock.a1codechallenge.presentation.view.builtdates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.databinding.FragmentBuiltDatesBinding

@AndroidEntryPoint
class BuiltDatesFragment : Fragment() {
    private val args: BuiltDatesFragmentArgs by navArgs()
    private val vm: BuiltDatesViewModel by viewModels()

    private var _binding: FragmentBuiltDatesBinding? = null
    private val binding get() = _binding!!

    private val builtDatesAdapter = BuiltDatesAdapter(::onItemClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBuiltDatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding.toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            findViewById<TextView>(R.id.tvTitle)?.let {
                it.text = getString(R.string.built_dates_title_placeholder, args.manufacturer.name, args.mainType.name)
            }
        }

        binding.rvBuiltDates.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = builtDatesAdapter
        }
        binding.srlPull.setOnRefreshListener {
            vm.loadBuiltDates(args.manufacturer.id, args.mainType.id, true)
        }

        vm.builtDates.observe(viewLifecycleOwner) {
            builtDatesAdapter.submitList(it)
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

        vm.loadBuiltDates(args.manufacturer.id, args.mainType.id, false)
    }

    private fun showError(errorRes: Int?) {
        val error = errorRes?.let { getString(it) }
        showError(error)
    }

    private fun showError(error: String?) {
        binding.rvBuiltDates.isVisible = error == null
        binding.tvError.isVisible = error != null
        error?.let { binding.tvError.text = it }
    }

    private fun onItemClick(builtDate: BuiltDate) {
        Toast.makeText(context, builtDate.date, Toast.LENGTH_SHORT).show()
    }
}