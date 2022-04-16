package my.illrock.a1codechallenge.presentation.view.manufacturer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.databinding.FragmentManufacturersBinding

@AndroidEntryPoint
class ManufacturersFragment : Fragment(R.layout.fragment_manufacturers) {
    private val vm: ManufacturerViewModel by viewModels()

    private var _binding: FragmentManufacturersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManufacturersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.manufacturers.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }

        vm.loadManufacturers()
    }
}