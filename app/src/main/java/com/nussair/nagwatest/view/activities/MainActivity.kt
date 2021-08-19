package com.nussair.nagwatest.view.activities

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nussair.nagwatest.R
import com.nussair.nagwatest.mvvm.FilesViewModel
import com.nussair.nagwatest.view.adapters.FilesAdapter
import com.nussair.nagwatest.view.interfaces.FileDownloader


class MainActivity : AppCompatActivity(), FileDownloader {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        PRDownloader.initialize(getApplicationContext());

        val filesRecyclerView: RecyclerView = findViewById(R.id.filesRecyclerView)

        val viewModel = ViewModelProviders.of(this).get(FilesViewModel::class.java)
        viewModel.getFiles().observe(this, { files ->
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

                filesRecyclerView.adapter = FilesAdapter(this, filesList, this)
                filesRecyclerView.layoutManager = LinearLayoutManager(this)

            }
        })
    }

    override fun downloadFile(url: String) {

        val request = DownloadManager.Request(Uri.parse(url))

        val urlChunks = url.split("/")

        val title: String = urlChunks[urlChunks.size - 1]

        request.setDescription("This is Desc")
        request.setTitle("download picsArt")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadId = manager.enqueue(request)

//        val mProgressBar = findViewById<View>(R.id.progressBar1) as ProgressBar

        Thread {
            var downloading = true
            Log.d("Nussair", "Downloading ..")
            while (downloading) {
                val q = DownloadManager.Query()
                q.setFilterById(downloadId)
                val cursor: Cursor = manager.query(q)
                cursor.moveToFirst()
                val bytes_downloaded: Int = cursor.getInt(
                    cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )
                val bytes_total: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                    Log.d("Nussair", "Downloaded Successfully.")
                }
                val dl_progress = (bytes_downloaded * 100L / bytes_total).toInt()
                Log.d("Nussair", "downloadFile: $dl_progress")
//                runOnUiThread { mProgressBar.progress = dl_progress as Int }
                cursor.close()
            }
        }.start()
//        Log.d("Nussair", "downloadFile: ${Environment.getRootDirectory().}")

//        val request = DownloadManager.Request(Uri.parse(url))
//        val title : String = URLUtil.guessFileName(url, null, null)
//        request.setTitle(title)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
//        val downloadManager : DownloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//        downloadManager.enqueue(request)
//        Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show()

//        try {
//            val u = URL(url)
//            val conn: URLConnection = u.openConnection()
//            val contentLength: Int = conn.contentLength
//            val stream = DataInputStream(u.openStream())
//            val buffer = ByteArray(contentLength)
//            stream.readFully(buffer)
//            stream.close()
//            val fos = DataOutputStream(FileOutputStream(File("${Environment.getRootDirectory()}/${Environment.DIRECTORY_DOWNLOADS}/test.mp4")))
//            fos.write(buffer)
//            fos.flush()
//            fos.close()
//        } catch (e: FileNotFoundException) {
//            return  // swallow a 404
//        } catch (e: IOException) {
//            return  // swallow a 404
//        }

    }
}