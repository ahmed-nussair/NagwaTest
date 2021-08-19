package com.nussair.nagwatest.view.adapters

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nussair.nagwatest.R
import com.nussair.nagwatest.view.interfaces.FileDownloader

class FilesAdapter(
    private val context: Context,
    private val filesList: List<Map<String, Any>>,
    private val fileDownloader: FileDownloader
) : RecyclerView.Adapter<FilesAdapter.FilesViewHolder>() {

    class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
        val fileIconImageView: ImageView = itemView.findViewById(R.id.fileIconImageView)
        val downloadButton: Button = itemView.findViewById(R.id.downloadButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.file_item_layout, parent, false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.fileNameTextView.text = "${filesList[position]["name"]}"
        if (filesList[position]["type"]?.equals("VIDEO") == true) {
            Glide
                .with(context)
                .load(R.drawable.video)
                .into(holder.fileIconImageView)
        } else {
            Glide
                .with(context)
                .load(R.drawable.pdf)
                .into(holder.fileIconImageView)
        }

        holder.downloadButton.setOnClickListener {
            run {
                val url = filesList[position]["url"].toString()
                val request = DownloadManager.Request(Uri.parse(url))

                val urlChunks = url.split("/")

                val title: String = urlChunks[urlChunks.size - 1]

                request.setDescription("This is Desc")
                request.setTitle("download picsArt")
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)


                val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

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


//                        runOnUiThread {
//                            linearProgressIndicator.progress = dl_progress
//                            val value = "$dl_progress%"
//                            percentageTextView.text = value
//                        }
                        cursor.close()
                    }
                }.start()
            }
        }
    }

    override fun getItemCount(): Int {
        return filesList.count()
    }
}