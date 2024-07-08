package com.example.movieapp.presentation.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentRegisterBinding
import com.example.movieapp.data.local.DataStore
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val dataStore: DataStore by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dataStore = DataStore(requireContext())

        binding.btnRegis.setOnClickListener {
            val username = binding.etRegisUname.text.toString()
            val email = binding.etRegisEmail.text.toString()
            val pass = binding.etRegisPass1.text.toString()
            val pass2 = binding.etRegisPass2.text.toString()

            if(username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && pass2.isNotEmpty()) {
                if(pass == pass2) {
                    registerUser(username, email, pass)
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill out all fields! ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(username: String, email: String, pass: String) {
        lifecycleScope.launch {
            dataStore.saveUserData(username, email, pass)
            Toast.makeText(context, " Registration success :)", Toast.LENGTH_SHORT).show()
            view?.findNavController()!!.popBackStack(R.id.loginFragment, false)
        }
    }
}