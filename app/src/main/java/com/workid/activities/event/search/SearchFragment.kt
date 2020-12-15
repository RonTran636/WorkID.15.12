package com.workid.activities.event.search

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.workid.R
import com.workid.adapters.SuggestContactAdapter
import com.workid.databinding.FragmentSearchBinding
import com.workid.models.AccountModel
import com.workid.utils.showKeyboard

class SearchFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var viewRoot: View

    private var contactAdapter = SuggestContactAdapter(arrayListOf())
    private var listContact =  ArrayList<AccountModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        viewRoot = binding.root
        binding.searchBar.showKeyboard()

        viewModel.configureAutoComplete()
        observeViewModel()

        contactAdapter = SuggestContactAdapter(listContact)
        binding.backSpace.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.resultList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = contactAdapter
        }
        binding.searchBar.requestFocus()
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 4) {
                    viewModel.onInputStateChanged(s.toString())
                }
            }
        })
        return viewRoot
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
        }
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog!=null){
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun observeViewModel() {
        viewModel.searchResult.observe(viewLifecycleOwner, {
            contactAdapter.update(it)
        })
    }
}