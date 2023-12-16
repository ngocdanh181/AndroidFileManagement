package com.example.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MyAdapter(private var context: Context, private var filesAndFolders: Array<File>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFile = filesAndFolders[position]
        holder.textView.text = selectedFile.name
        if (selectedFile.isDirectory) {
            holder.imageView.setImageResource(R.drawable.baseline_folder_24)
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_insert_drive_file_24)
        }
        holder.itemView.setOnClickListener {
            if (selectedFile.isDirectory) {
                val intent = Intent(context, FileListActivity::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                //open the file
                try {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val type = "image/*"
                    intent.setDataAndType(Uri.parse(selectedFile.absolutePath), type)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context.applicationContext,
                        "Cannot open the file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        holder.itemView.setOnLongClickListener { v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menu.add("DELETE")
            popupMenu.menu.add("MOVE")
            popupMenu.menu.add("RENAME")
            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "DELETE") {
                    showConfirmationDialog(
                        "Delete",
                        "Are you sure you want to delete this ${if (selectedFile.isDirectory) "folder" else "file"}?",
                        DialogInterface.OnClickListener { _, _ ->
                            val deleted = deleteFileOrFolder(selectedFile)
                            if (deleted) {
                                filesAndFolders = filesAndFolders.filter { it != selectedFile }.toTypedArray()
                                notifyDataSetChanged()
                                Toast.makeText(
                                    context.applicationContext,
                                    "DELETED",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
                if (item.title == "MOVE") {
                    Toast.makeText(context.applicationContext, "MOVED ", Toast.LENGTH_SHORT)
                        .show()
                }
                if (item.title == "RENAME") {
                    Toast.makeText(context.applicationContext, "RENAME ", Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return filesAndFolders.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView
        var imageView: ImageView

        init {
            textView = itemView.findViewById(R.id.file_name_text_view)
            imageView = itemView.findViewById(R.id.icon_view)
        }
    }
    private fun deleteFileOrFolder(file: File): Boolean {
        if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null) {
                for (child in children) {
                    deleteFileOrFolder(child)
                }
            }
        }
        return file.delete()
    }
    
    private fun showConfirmationDialog(
        title: String,
        message: String,
        listener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, listener)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }
}