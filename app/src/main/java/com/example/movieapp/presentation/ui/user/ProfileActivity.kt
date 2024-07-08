package com.example.movieapp.presentation.ui.user

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityProfileBinding
import com.example.movieapp.presentation.utils.BlurHandler
import com.example.movieapp.data.local.DataStore
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import org.koin.android.ext.android.inject



class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private val dataStore: DataStore by inject()
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    private var isImageSelected = false

    companion object {
        private const val EXTRA_CAMERA = 1001
        private const val EXTRA_GALLERY = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dataStore = DataStore(this)

        if (!isImageSelected) {
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.img_profile)
                .into(binding.imgProfile)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddImg.setOnClickListener {
            showImagePicker()
        }

        binding.btnEditImg.setOnClickListener{
            showBlurOptDialog()
        }

        observeUserData()

        binding.btnUpdate.setOnClickListener {
            updateUserData()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        setupActivityResultLaunchers()
    }

    private fun showBlurOptDialog() {
        val blurLevels = arrayOf("Original (0%)", "25%", "50%", "75%")
        var blurLevel = 0
        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
        dialogBuilder.setTitle("Select Blur Level")
        dialogBuilder.setSingleChoiceItems(blurLevels, -1){ _, option ->
           blurLevel =  when(option){
                0 -> 0
                1 -> 25
                2 -> 50
                3 -> 75
                else -> 0
            }
        }

        dialogBuilder.setPositiveButton("Apply") {_, _ ->
            applyBlur(blurLevel)
        }
        dialogBuilder.setNegativeButton("Cancel", null)
        dialogBuilder.show()
    }

    private fun applyBlur(blurLevel: Int) {
        val blurTransformation: BitmapTransformation = when (blurLevel) {
            25 -> BlurHandler( 25)
            50 -> BlurHandler( 50)
            75 -> BlurHandler(75)
            else -> BlurHandler( 0)
        }

        imageUri?.let {
            Glide.with(this)
                .load(it)
                .transform(blurTransformation, CircleCrop())
                .into(binding.imgProfile)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            EXTRA_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            EXTRA_GALLERY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupActivityResultLaunchers() {
        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageUri = saveImageToInternalStorage(imageBitmap)
                imageUri?.let {
                    Glide.with(this)
                        .load(it)
                        .circleCrop()
                       // .apply(RequestOptions.bitmapTransform(BlurHandler(25, 3)))
                        .into(binding.imgProfile)
                }
            } else {
                Toast.makeText(this, "Camera action cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    Glide.with(this)
                        .load(it)
                        .circleCrop()
                        .into(binding.imgProfile)
                }
            } else {
                Toast.makeText(this, "Gallery action cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val file = File(filesDir, "profile_image.jpg")
        return try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), EXTRA_CAMERA)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                cameraResultLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), EXTRA_GALLERY)
        } else {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryResultLauncher.launch(pickPhoto)
        }
    }

    private fun showImagePicker() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun observeUserData() {
        lifecycleScope.launch {
            dataStore.username.collect { username ->
                binding.etUname.setText(username ?: "")
            }
        }

        lifecycleScope.launch {
            dataStore.fullName.collect { fullName ->
                binding.etFullName.setText(fullName ?: "")
            }
        }

        lifecycleScope.launch {
            dataStore.address.collect { address ->
                binding.etAddress.setText(address ?: "")
            }
        }

        lifecycleScope.launch {
            dataStore.dob.collect { dob ->
                binding.etDob.setText(dob ?: "")
            }
        }

        lifecycleScope.launch {
            dataStore.profileImageUri.collect { uri ->
                uri?.let {
                    imageUri = Uri.parse(it)
                    Glide.with(this@ProfileActivity)
                        .load(imageUri)
                        .circleCrop()
                        //.apply(RequestOptions.bitmapTransform(BlurHandler(25, 3)))
                        .into(binding.imgProfile)
                }
            }
        }
    }

    private fun updateUserData() {
        val username = binding.etUname.text.toString()
        val fullName = binding.etFullName.text.toString()
        val address = binding.etAddress.text.toString()
        val dob = binding.etDob.text.toString()
        val imageUriString = imageUri?.toString() ?: ""

        lifecycleScope.launch {
            dataStore.updateUserData(username, fullName, address, dob, imageUriString)
            Toast.makeText(this@ProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            dataStore.logout()
            Toast.makeText(this@ProfileActivity, "You've successfully logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ProfileActivity, UserActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}


//class ProfileActivity : AppCompatActivity() {
//    private var _binding: ActivityProfileBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var dataStore: DataStore
//    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
//    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
//    private var imageUri: Uri? = null
//
//    companion object {
//        private const val EXTRA_CAMERA = 1001
//        private const val EXTRA_GALLERY = 1002
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        _binding = ActivityProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        dataStore = DataStore(this)
//
//        binding.btnBack.setOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }
//
//        binding.btnAddImg.setOnClickListener {
//            showImagePicker()
//        }
//
//        observeUserData()
//
//        binding.btnUpdate.setOnClickListener {
//            updateUserData()
//        }
//
//        binding.btnLogout.setOnClickListener {
//            logout()
//        }
//
//        setupActivityResultLaunchers()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            EXTRA_CAMERA -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    openCamera()
//                } else {
//                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            EXTRA_GALLERY -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    openGallery()
//                } else {
//                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun setupActivityResultLaunchers() {
//        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as Bitmap
//                imageUri = saveImageToInternalStorage(imageBitmap)
//                imageUri?.let {
//                    Glide.with(this).load(it).into(binding.imgProfile)
//                }
//            } else {
//                Toast.makeText(this, "Camera action cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imageUri = result.data?.data
//                imageUri?.let {
//                    Glide.with(this).load(it).into(binding.imgProfile)
//                }
//            } else {
//                Toast.makeText(this, "Gallery action cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
//        val file = File(filesDir, "profile_image.jpg")
//        return try {
//            val fos = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            fos.close()
//            Uri.fromFile(file)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    private fun openCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), EXTRA_CAMERA)
//        } else {
//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (takePictureIntent.resolveActivity(packageManager) != null) {
//                cameraResultLauncher.launch(takePictureIntent)
//            }
//        }
//    }
//
//    private fun openGallery() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), EXTRA_GALLERY)
//        } else {
//            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            galleryResultLauncher.launch(pickPhoto)
//        }
//    }
//
//    private fun showImagePicker() {
//        val options = arrayOf("Take Photo", "Choose from Gallery")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Choose an option")
//        builder.setItems(options) { _, which ->
//            when (which) {
//                0 -> openCamera()
//                1 -> openGallery()
//            }
//        }
//        builder.show()
//    }
//
//    private fun observeUserData() {
//        lifecycleScope.launch {
//            dataStore.username.collect { username ->
//                binding.etUname.setText(username ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.fullName.collect { fullName ->
//                binding.etFullName.setText(fullName ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.address.collect { address ->
//                binding.etAddress.setText(address ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.dob.collect { dob ->
//                binding.etDob.setText(dob ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.profileImageUri.collect { uri ->
//                uri?.let {
//                    imageUri = Uri.parse(it)
//                    Glide.with(this@ProfileActivity)
//                        .load(imageUri)
//                        .into(binding.imgProfile)
//                }
//            }
//        }
//    }
//
//    private fun updateUserData() {
//        val username = binding.etUname.text.toString()
//        val fullName = binding.etFullName.text.toString()
//        val address = binding.etAddress.text.toString()
//        val dob = binding.etDob.text.toString()
//        val imageUriString = imageUri?.toString() ?: ""
//
//        lifecycleScope.launch {
//            dataStore.updateUserData(username, fullName, address, dob, imageUriString)
//            Toast.makeText(this@ProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun logout() {
//        lifecycleScope.launch {
//            dataStore.logout()
//            Toast.makeText(this@ProfileActivity, "You've successfully logged out", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this@ProfileActivity, UserActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
//            finish()
//        }
//    }
//}
//class ProfileActivity : AppCompatActivity() {
//    private var _binding: ActivityProfileBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var dataStore: DataStore
//    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
//    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
//    private var imageUri: Uri? = null
//
//    companion object{
//        private const val EXTRA_CAMERA = 1001
//        private const val EXTRA_GALLERY = 1002
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        _binding = ActivityProfileBinding.inflate(layoutInflater)
//        setContentView(binding?.root)
//
//        dataStore = DataStore(this)
//
//        binding.btnBack.setOnClickListener{
//            onBackPressedDispatcher.onBackPressed()
//        }
//
//        binding.btnAddImg.setOnClickListener{
//            showImagePicker()
//        }
//
//        observeUserData()
//
//        binding.btnUpdate.setOnClickListener {
//            updateUserData()
//        }
//
//        binding.btnLogout.setOnClickListener {
//            logout()
//        }
//
//        setupActivityResult()
//
//
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            EXTRA_CAMERA -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    openCamera()
//                } else {
//                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            EXTRA_GALLERY -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    openGallery()
//                } else {
//                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun setupActivityResult() {
//        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as Bitmap
//                imageUri = saveImg(imageBitmap)
//                imageUri?.let {
//                    Glide.with(this).load(it).into(binding.imgProfile)
//                }
//            } else {
//                Toast.makeText(this, "Camera access not allowed", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imageUri = result.data?.data
//                imageUri?.let {
//                    Glide.with(this)
//                        .load(it)
//                        .into(binding.imgProfile)
//                }
//            }  else {
//                Toast.makeText(this, "Gallery access not allowed", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//    }
//
//    private fun saveImg(bitmap: Bitmap): Uri? {
//        val imgFile = File(filesDir, "profile_image")
//        return try {
//            val imgOutput = FileOutputStream(imgFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imgOutput)
//            imgOutput.close()
//            Uri.fromFile(imgFile)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//
//    private fun openCamera() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), EXTRA_CAMERA)
//    } else {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (cameraIntent.resolveActivity(packageManager) != null) {
//            cameraResultLauncher.launch(cameraIntent)
//        }
//    }
//    }
//
//    private fun openGallery() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), EXTRA_GALLERY)
//        } else {
//            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            galleryResultLauncher.launch(pickPhoto)
//        }
//    }
//
//
//
//
//    private fun showImagePicker() {
//        val dialogOpt = arrayOf("Take a photo", "Choose from gallery")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Choose an option")
//        builder.setItems(dialogOpt){ _, which ->
//            when(which) {
//                0 -> openCamera()
//                1 -> openGallery()
//            }
//        }
//        builder.show()
//    }
//
//
//    private fun observeUserData() {
//        lifecycleScope.launch {
//            dataStore.username.collect{username ->
//                binding.etUname.setText(username ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.fullName.collect{fullName ->
//                binding.etFullName.setText(fullName ?: "")
//
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.address.collect{ address ->
//                binding.etAddress.setText(address ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.dob.collect{ dob ->
//                binding.etDob.setText(dob ?: "")
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStore.profileImageUri.collect{uri ->
//                uri?.let {
//                    imageUri = Uri.parse(it)
//                    Glide.with(this@ProfileActivity)
//                        .load(imageUri)
//                        .into(binding.imgProfile)
//                }
//            }
//        }
//    }
//
//    private fun updateUserData() {
//        val username = binding.etUname.text.toString()
//        val fullName = binding.etFullName.text.toString()
//        val address = binding.etAddress.text.toString()
//        val dob = binding.etDob.text.toString()
//        val imageUri = imageUri?.toString() ?: ""
//
//        lifecycleScope.launch {
//            dataStore.updateUserData(username, fullName, address, dob,imageUri)
//            Toast.makeText(this@ProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun logout() {
//        lifecycleScope.launch {
//            dataStore.logout()
//            Toast.makeText(this@ProfileActivity, "You've successfully logged out", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this@ProfileActivity, UserActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
//
//            finish()
//
//        }
//    }
//}