package com.example.movieapp.presentation.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentLoginBinding
import com.example.movieapp.data.local.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject




class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val dataStore: DataStore by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //dataStore = DataStore(requireContext())

        lifecycleScope.launch{
            dataStore.isLogin.collect {isLogin ->
                if(isLogin){
                    findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                }

            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val pass = binding.etLoginPass.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()) {
                loginUser(email, pass)
            } else {
                Toast.makeText(context,"Please fill out all fields! ", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvLoginReg.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


    }

    private fun loginUser(email: String, pass: String) {
        lifecycleScope.launch {
            val storedEmail = dataStore.email.first()
            val storedPass = dataStore.pass.first()

            if(email == storedEmail && pass == storedPass) {
                dataStore.setLogin(true)
                findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                requireActivity().finish()
            } else {
                Toast.makeText(context, "Email or password is invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }


}