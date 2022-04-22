package my.illrock.a1codechallenge.presentation.view.maintypes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
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
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.databinding.FragmentMainTypesBinding
import my.illrock.a1codechallenge.presentation.view.util.ViewModelResult
import my.illrock.a1codechallenge.util.hideKeyboardFrom
import my.illrock.a1codechallenge.util.isPortraitOrientation
import my.illrock.a1codechallenge.util.showKeyboard

@AndroidEntryPoint
class MainTypesFragment : Fragment() {
    private val args: MainTypesFragmentArgs by navArgs()
    private val vm: MainTypesViewModel by viewModels()

    private var _binding: FragmentMainTypesBinding? = null
    private val binding get() = _binding!!

    private lateinit var tvTitle: AppCompatTextView
    private lateinit var ivSearch: AppCompatImageView
    private lateinit var etSearch: AppCompatEditText
    private lateinit var ivSearchClose: AppCompatImageView

    private val mainTypesAdapter = MainTypesAdapter(::onItemClick)

    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainTypesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding.toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            tvTitle = findViewById(R.id.tvTitle)
            tvTitle.text = args.manufacturer.name
            setupSearch()
        }

        binding.rvMainTypes.apply {
            layoutManager = if (isPortraitOrientation()) {
                LinearLayoutManager(context)
            } else {
                GridLayoutManager(context, resources.getInteger(R.integer.fragment_landscape_columns))
            }
            adapter = mainTypesAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }
        binding.srlPull.setOnRefreshListener {
            vm.loadMainTypes(args.manufacturer.id, true)
        }

        vm.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ViewModelResult.Loading -> {
                    binding.srlPull.isRefreshing = true
                }
                is ViewModelResult.Success -> {
                    binding.srlPull.isRefreshing = false
                    mainTypesAdapter.submitList(result.data)
                    binding.tvError.isVisible = false
                    binding.rvMainTypes.isVisible = true
                }
                is ViewModelResult.Error -> {
                    binding.srlPull.isRefreshing = false
                    binding.rvMainTypes.isVisible = false
                    result.errorMessage?.let { setError(it) }
                    result.errorRes?.let { setError(it) }
                }
            }
        }
        vm.isSearch.observe(viewLifecycleOwner) {
            if (it) startSearch() else stopSearch()
        }
        vm.loadMainTypes(args.manufacturer.id, false)
    }

    private fun setError(errorRes: Int?) {
        val error = errorRes?.let { getString(it) }
        setError(error)
    }

    private fun setError(error: String?) {
        binding.tvError.isVisible = error != null
        error?.let { binding.tvError.text = it }
    }

    private fun onItemClick(mainType: MainType) {
        vm.stopSearch()
        val action = MainTypesFragmentDirections
            .actionMainTypesFragmentToBuiltDatesFragment(args.manufacturer, mainType)
        findNavController()
            .navigate(action)
    }

    private fun Toolbar.setupSearch() {
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)
        ivSearchClose = findViewById(R.id.ivSearchClose)

        ivSearch.setOnClickListener { vm.startSearch() }
        ivSearchClose.setOnClickListener { vm.stopSearch() }
        etSearch.doOnTextChanged { text, _, _, _ ->
            text?.let { vm.onNewSearchInput(it.toString()) }
        }
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                etSearch.clearFocus()
                context?.hideKeyboardFrom(etSearch)
                true
            } else false
        }
    }

    private fun startSearch() {
        tvTitle.isVisible = false
        etSearch.isVisible = true
        etSearch.requestFocus()
        activity?.showKeyboard(etSearch)
        ivSearch.isVisible = false
        ivSearchClose.isVisible = true
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            vm.stopSearch()
        }
    }

    private fun stopSearch() {
        tvTitle.isVisible = true
        etSearch.isVisible = false
        etSearch.setText("")
        etSearch.clearFocus()
        context?.hideKeyboardFrom(etSearch)
        ivSearch.isVisible = true
        ivSearchClose.isVisible = false
        onBackPressedCallback?.remove()
    }
}