package com.hcraestrak.kartsearch.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hcraestrak.kartsearch.R
import com.hcraestrak.kartsearch.databinding.FragmentUserRecordBinding
import com.hcraestrak.kartsearch.model.viewModel.FirebaseViewModel
import com.hcraestrak.kartsearch.model.viewModel.MatchViewModel
import com.hcraestrak.kartsearch.view.adapter.UserInfoRecyclerViewAdapter
import com.hcraestrak.kartsearch.view.adapter.data.UserInfoData
import com.hcraestrak.kartsearch.view.decoration.RecyclerViewDecoration

class UserRecordFragment(val id: String) : Fragment() {

    private lateinit var binding: FragmentUserRecordBinding
    private lateinit var recyclerAdapter: UserInfoRecyclerViewAdapter
    private val matchViewModel: MatchViewModel by viewModels()
    private val fireBaseViewModel: FirebaseViewModel by viewModels()
    private var spinnerItem: String = "스피드"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingSpinner()
        initRecyclerView()
    }

    private fun bindingSpinner() {
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.game_mode,
                R.layout.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_item)
                binding.userRecordSpinner.adapter = adapter
            }


        binding.userRecordSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerItem = p0?.getItemAtPosition(p2).toString()
                binding.userRecordTitle.text = "$spinnerItem 전적"
                Log.d("item", spinnerItem)
                setData(spinnerItem)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun initRecyclerView() {
        val decoration: RecyclerViewDecoration = RecyclerViewDecoration(40)
        binding.userInfoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            recyclerAdapter = UserInfoRecyclerViewAdapter()
            adapter = recyclerAdapter
            addItemDecoration(decoration)
        }
    }

    private fun setData(type: String) {
        val dataList = mutableListOf<UserInfoData>()
//        val gameType: String = getGameType(type)
        val gameType: String = ""
        Log.d("typeId", gameType)
        matchViewModel.accessIdMatchInquiryWithMatchType(id, gameType)
        matchViewModel.getMatchResponseObserver().observe(viewLifecycleOwner, {
            for (match in it.matches[0].matches) {
                dataList.add(
                    UserInfoData(
                        match.playerCount,
                        match.player.matchRank,
                        match.player.kart,
                        match.trackId,
                        match.player.matchTime,
                        match.player.matchWin,
                        match.player.matchRetired
                    )
                )
            }
            recyclerAdapter.setData(dataList)
        })
    }

//    private fun getGameType(type: String): String {
//
//    }

    private fun getGameTypeWithFirebase(typeName: String): String {
        var typeId: String = ""
        val database: DatabaseReference = Firebase.database("https://gametype.firebaseio.com/").reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val id = postSnapshot.child("id").getValue(String::class.java)
                    val name = postSnapshot.child("name").getValue(String::class.java)
                    if (typeName == name) {
                        typeId = id.toString()
                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "${error.code}: ${error.message}")
            }
        })
        return typeId
    }
}