package com.nussair.myapplication.view.adapters

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.nussair.nagwatest.R

class FilesAdapter(
    private val context: Context,
    private val filesList: List<Map<String, Any>>
) : RecyclerView.Adapter<FilesAdapter.FilesViewHolder>() {

    class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
        val fileIconImageView: ImageView = itemView.findViewById(R.id.fileIconImageView)
        val downloadButton: Button = itemView.findViewById(R.id.downloadButton)
        val downloadProgress: LinearProgressIndicator = itemView.findViewById(R.id.downloadProgress)
        val progressPercentageTextView: TextView =
            itemView.findViewById(R.id.progressPercentageTextView)
        val downloadedImageView: ImageView = itemView.findViewById(R.id.downloadedImageView)
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

                holder.downloadButton.visibility = View.GONE
                holder.downloadProgress.visibility = View.VISIBLE
                holder.progressPercentageTextView.visibility = View.VISIBLE
                holder.progressPercentageTextView.text = "0%"

                val url = filesList[position]["url"].toString()
                val request = DownloadManager.Request(Uri.parse(url))

                val urlChunks = url.split("/")

                val title: String = urlChunks[urlChunks.size - 1]

                request.setDescription("This is Desc")
                request.setTitle("download picsArt")
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

                val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                assert(
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                            != PackageManager.PERMISSION_GRANTED
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (context as Activity).requestPermissions(
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0
                    )
                }

                val downloadId = manager.enqueue(request)

                Thread {
                    var downloading = true
                    Log.d("Nussair", "Downloading ..")
                    while (downloading) {
                        val q = DownloadManager.Query()
                        q.setFilterById(downloadId)
                        val cursor: Cursor = manager.query(q)
                        cursor.moveToFirst()
                        val bytesDownloaded: Int = cursor.getInt(
                            cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        )
                        val bytesTotal: Int =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false
                            Log.d("Nussair", "Downloaded Successfully.")
                            holder.downloadedImageView.visibility = View.VISIBLE
                            holder.downloadProgress.visibility = View.GONE
                            holder.progressPercentageTextView.visibility = View.GONE
                        }
                        val downloadProgress = (bytesDownloaded * 100L / bytesTotal).toInt()
                        Log.d("Nussair", "downloadFile: $downloadProgress")

                        (context as Activity).runOnUiThread {
                            holder.downloadProgress.progress = downloadProgress
                            val value = "$downloadProgress%"
                            holder.progressPercentageTextView.text = value
                        }
                        cursor.close()
                    }
                }.start()
//                try {
//
//                } catch (e : Exception) {
//                    Toast.makeText(context, "Error Downloading The File.", Toast.LENGTH_LONG).show()
//                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filesList.count()
    }
}