package com.geekymusketeers.mediVault.ui.mainFragments.settings.prescription

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.geekymusketeers.mediVault.databinding.ActivityAddPrescriptionBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddPrescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPrescriptionBinding
    private var fileUri: Uri? = null
    private var databaseReference: DatabaseReference? = null
    private lateinit var sharedPreference: SharedPreferences

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                fileUri = uri
                binding.filelogo.visibility = View.VISIBLE
                binding.cancelfile.visibility = View.VISIBLE
                binding.imagebrowse.visibility = View.INVISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPrescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = baseContext.getSharedPreferences("UserData", Context.MODE_PRIVATE)

        databaseReference = FirebaseDatabase.getInstance().reference

        binding.imagebrowse.setOnClickListener {
            filePickerLauncher.launch("application/pdf")
        }

        binding.cancelfile.setOnClickListener {
            fileUri = null
            binding.filetitle.text.clear()
            binding.filelogo.visibility = View.INVISIBLE
            binding.cancelfile.visibility = View.INVISIBLE
            binding.imagebrowse.visibility = View.VISIBLE
        }

        binding.imageupload.setOnClickListener {
            if (fileUri == null) {
                Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            processUpload(fileUri!!)
        }
    }

    private fun processUpload(fileUri: Uri) {
        val userId = sharedPreference.getString("uid", "").toString()

        if (binding.filetitle.text.toString().isEmpty()) {
            Toast.makeText(baseContext, "Add file title", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading PDF")
        progressDialog.show()

        val reference: StorageReference =
            FirebaseStorage.getInstance().reference.child("uploads/${System.currentTimeMillis()}.pdf")

        reference.putFile(fileUri)
            .addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener { uri: Uri ->
                    sharedPreference.edit().putString("prescription", uri.toString()).apply()
                    databaseReference?.child("Users")?.child(userId)?.child("prescription")
                        ?.setValue(uri.toString())

                    progressDialog.dismiss()
                    Toast.makeText(baseContext, "File Uploaded", Toast.LENGTH_SHORT).show()

                    // Reset UI after successful upload
                    resetFileSelection()
                }
            }
            .addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val percent =
                    (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                progressDialog.setMessage("Uploaded : ${percent.toInt()}%")
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Upload Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetFileSelection() {
        fileUri = null
        binding.filelogo.visibility = View.INVISIBLE
        binding.cancelfile.visibility = View.INVISIBLE
        binding.imagebrowse.visibility = View.VISIBLE
        binding.filetitle.setText("")
    }
}
