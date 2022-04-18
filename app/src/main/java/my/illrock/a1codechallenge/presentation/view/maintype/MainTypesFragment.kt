package my.illrock.a1codechallenge.presentation.view.maintype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
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

        binding.rvMainTypes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainTypesAdapter
        }

        vm.mainTypes.observe(viewLifecycleOwner) {
            mainTypesAdapter.submitList(it)
        }

        vm.loadMainTypes(args.manufacturer.id, false)
    }

    private fun onItemClick(mainType: MainType) {
        val action = MainTypesFragmentDirections
            .actionMainTypesFragmentToBuiltDatesFragment(args.manufacturer, mainType)
        findNavController()
            .navigate(action)
    }
}