package com.workid.activities.main.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.workid.R
import com.workid.adapters.SuggestContactAdapter
import com.workid.databinding.FragmentHomeBinding
import com.workid.models.AccountModel
import com.workid.utils.Common
import com.workid.utils.hideKeyboard


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var viewRoot: View

    private var contactAdapter = SuggestContactAdapter(arrayListOf())
    private var listContact = ArrayList<AccountModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewRoot = binding.root
        viewRoot.hideKeyboard()

        observeViewModel()

        binding.welcomeMessage.text =
            getString(R.string.welcome_message, Common.currentAccount!!.customerName)

        //Load list suggesting friend from our server
        viewModel.loadRecommendContact(auth)
        contactAdapter = SuggestContactAdapter(listContact)
        binding.suggestContactList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = contactAdapter
        }
        //Handle search event
        binding.searchBarLayout.setOnClickListener {
            viewRoot.findNavController().navigate(R.id.action_navigation_home_to_searchFragment)
        }
        binding.searchBar.setOnClickListener {
            viewRoot.findNavController().navigate(R.id.action_navigation_home_to_searchFragment)
        }
        return viewRoot
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerFrameLayout.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.shimmerFrameLayout.stopShimmerAnimation()
    }

    private fun observeViewModel() {
        viewModel.listSuggestContact.observe(viewLifecycleOwner, {
            contactAdapter.update(it)
            binding.shimmerFrameLayout.stopShimmerAnimation()
            binding.shimmerFrameLayout.visibility = View.GONE
        })
    }

}