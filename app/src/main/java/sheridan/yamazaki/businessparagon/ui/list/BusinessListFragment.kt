package sheridan.yamazaki.businessparagon.ui.list

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import sheridan.yamazaki.businessparagon.R
import sheridan.yamazaki.businessparagon.databinding.BusinessListFragmentBinding
import sheridan.yamazaki.businessparagon.model.Business

@AndroidEntryPoint
class BusinessListFragment : Fragment() {

    private lateinit var binding: BusinessListFragmentBinding
    private val viewModel: BusinessListViewModel by viewModels()
   // private val adapter: BusinessListAdapter?=null

   // var items = List<Business>()
    //val displayList = ArrayList<Business>()
    override fun onCreateView(
       inflater: LayoutInflater, container: ViewGroup?,
       savedInstanceState: Bundle?
   ): View? {
        binding = BusinessListFragmentBinding.inflate(inflater, container, false)

        val adapter = BusinessListAdapter(onClick = {
            Log.d("clicked", (viewModel.businesses.value?.size ?: 0).toString())
        })

        binding.recyclerBusinesses.adapter = adapter
      //  binding.lifecycleOwner = viewLifecycleOwner
        //binding.viewModel = viewModel

       viewModel.businesses.observe(viewLifecycleOwner){
           adapter?.submitList(it)
        }
        return binding.root
    }

    private fun notImplemented() {
        Snackbar.make(
            binding.root,
            getString(R.string.not_implemented),
            Snackbar.LENGTH_LONG
        ).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        //super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    private fun startSearch(search: String){
        val filteredItems = ArrayList<Business>()
        for (i in 0 until (viewModel.businesses.value?.size ?: 0)){

        }
    }
}

/*import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sheridan.yamazaki.businessparagon.databinding.FragmentExploreBinding
import sheridan.yamazaki.businessparagon.databinding.FragmentLoginBinding

class ExploreFragment : Fragment() {
    private lateinit var binding: FragmentExploreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }
}*/