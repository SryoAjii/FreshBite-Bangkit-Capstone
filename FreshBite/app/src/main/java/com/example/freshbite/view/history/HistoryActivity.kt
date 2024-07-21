package com.example.freshbite.view.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshbite.R
import com.example.freshbite.databinding.ActivityHistoryBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvHistory
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter(historyList)
        recyclerView.adapter = historyAdapter

        loadHistoryData()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadHistoryData() {
        loading(true)
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("classifications")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            loading(false)
                            val historyData = dc.document.toObject(HistoryData::class.java)
                            historyList.add(historyData)
                            Log.d("Firestore", "New data: ${historyData.result}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            // Handle modified documents if necessary
                        }
                        DocumentChange.Type.REMOVED -> {
                            // Handle removed documents if necessary
                        }
                    }
                }
                historyAdapter.notifyDataSetChanged()
            }
    }

    private fun loading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}