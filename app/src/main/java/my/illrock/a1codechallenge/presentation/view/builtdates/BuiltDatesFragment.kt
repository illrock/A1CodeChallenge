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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.databinding.FragmentBuiltDatesBinding
import my.illrock.a1codechallenge.presentation.view.util.ViewModelResult
import my.illrock.a1codechallenge.util.isPortraitOrientation

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
            layoutManager = if (isPortraitOrientation()) {
                LinearLayoutManager(context)
            } else {
                GridLayoutManager(context, resources.getInteger(R.integer.fragment_landscape_columns))
            }
            adapter = builtDatesAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }
        binding.srlPull.setOnRefreshListener {
            vm.loadBuiltDates(args.manufacturer.id, args.mainType.id, true)
        }

        vm.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ViewModelResult.Loading -> {
                    binding.srlPull.isRefreshing = true
                }
                is ViewModelResult.Success -> {
                    binding.srlPull.isRefreshing = false
                    binding.rvBuiltDates.isVisible = true
                    binding.tvError.isVisible = false
                    builtDatesAdapter.submitList(result.data)
                }
                is ViewModelResult.Error -> {
                    binding.srlPull.isRefreshing = false
                    binding.rvBuiltDates.isVisible = false
                    result.errorRes?.let { setError(it) }
                    result.errorMessage?.let { setError(it) }
                }
            }
        }

        vm.loadBuiltDates(args.manufacturer.id, args.mainType.id, false)
    }

    private fun setError(errorRes: Int?) {
        val error = errorRes?.let { getString(it) }
        setError(error)
    }

    private fun setError(error: String?) {
        binding.tvError.isVisible = error != null
        error?.let { binding.tvError.text = it }
    }

    private fun onItemClick(builtDate: BuiltDate) {
        Toast.makeText(context, builtDate.date, Toast.LENGTH_SHORT).show()
    }
}