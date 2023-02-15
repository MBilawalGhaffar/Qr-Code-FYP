package com.example.qrcodefyp.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.qrcodefyp.R
import com.example.qrcodefyp.callback.OnImagePicked
import com.example.qrcodefyp.callback.OnImagePicker
import com.example.qrcodefyp.databinding.ActivityProfileBinding
import com.example.qrcodefyp.dialog.AddProfileDialog
import com.example.qrcodefyp.model.ProfileModel
import com.example.qrcodefyp.model.User
import com.example.qrcodefyp.util.FirebaseUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding
    var dialog: Dialog? = null
    private var user: FirebaseUser? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var galleryActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var mOnImagePicked: OnImagePicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_wait1)
        dialog!!.setCanceledOnTouchOutside(false)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        user = mAuth!!.currentUser

        getProfile()
        binding.buttonAdd.setOnClickListener {
            val dialog=AddProfileDialog(this,R.style.ReceiptDialog,object :OnImagePicker{
                override fun onImagePicker(onImagePicked: OnImagePicked?, camera: Boolean?) {
                    mOnImagePicked= onImagePicked
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    galleryActivityResultLauncher!!.launch(intent)
                }
            })
            dialog.setCancelable(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.show()
        }

        galleryActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Here, no request code
                val data = result.data
                val uri = data!!.data
                CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this@ProfileActivity)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                try {
                    val uri = result.uri
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    if (mOnImagePicked != null) {
                        mOnImagePicked!!.onImagePicked(uri, bitmap)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    mOnImagePicked!!.onImagePicked(null, null)
                }
            }
        }
    }



    private fun getProfile() {
        mDatabase!!.getReference(FirebaseUtil.DB_PROFILE_REF).child(FirebaseUtil.USER.uuid).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val profileModel: ProfileModel = snapshot.getValue(ProfileModel::class.java)!!
                    mDatabase!!.getReference(FirebaseUtil.DB_LOGIN_REF).child(FirebaseUtil.USER.uuid).addValueEventListener(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                val userModel = snapshot.getValue(User::class.java)!!
                                Glide.with(this@ProfileActivity).load(profileModel.imageUrl).into(binding.profileImage)
                                binding.name.text=userModel.name
                                binding.email.text=userModel.email
                                binding.age.text=profileModel.age
                                binding.gender.text=profileModel.gender
                                binding.buttonAdd.visibility=View.GONE
                            }else {
                                Toast.makeText(this@ProfileActivity,"User have no record yet, Add profile", Toast.LENGTH_SHORT).show()
                                binding.buttonAdd.visibility=View.VISIBLE
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@ProfileActivity,"error, ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }else {
                    Toast.makeText(this@ProfileActivity,"User have no record yet, Add profile", Toast.LENGTH_SHORT).show()
                    binding.buttonAdd.visibility=View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Receipts","error -> ${error.message}")
                Toast.makeText(this@ProfileActivity,"error, ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }
}