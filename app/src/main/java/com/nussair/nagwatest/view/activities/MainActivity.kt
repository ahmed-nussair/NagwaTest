package com.nussair.nagwatest.view.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nussair.myapplication.view.adapters.FilesAdapter
import com.nussair.nagwatest.DaggerNagwaTestApplicationComponent
import com.nussair.nagwatest.R
import com.nussair.nagwatest.mvvm.FilesViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var filesViewModel: FilesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val appComponent = DaggerNagwaTestApplicationComponent.create()
        appComponent.inject(this)

        val filesRecyclerView: RecyclerView = findViewById(R.id.filesRecyclerView)
        val errorLoadingFilesTextView: TextView = findViewById(R.id.errorLoadingFilesTextView)
        val loadingFilesProgressBar: ProgressBar = findViewById(R.id.loadingFilesProgressBar)


        filesViewModel.getFiles().observe(this, { files ->
            run {
                val filesList: MutableList<Map<String, Any>> = mutableListOf()
                for (item in files) {
                    filesList.add(
                        mapOf(
                            "name" to item.name,
                            "type" to item.type.name,
                            "url" to item.url,
                            "id" to item.id
                        )
                    )
                }

                loadingFilesProgressBar.visibility = View.GONE
                errorLoadingFilesTextView.visibility = View.GONE
                filesRecyclerView.visibility = View.VISIBLE

                filesRecyclerView.adapter = FilesAdapter(this, filesList)
                filesRecyclerView.layoutManager = LinearLayoutManager(this)

            }
        })
    }
}