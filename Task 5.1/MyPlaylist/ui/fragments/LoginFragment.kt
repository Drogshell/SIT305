package com.trevin.myplaylists.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.trevin.myplaylists.ui.viewModel.AuthState
import com.trevin.myplaylists.ui.HomePageActivity
import com.trevin.myplaylists.ui.MainActivity
import com.trevin.myplaylists.ui.viewModel.MainViewModel
import com.trevin.myplaylists.R
import com.trevin.myplaylists.databinding.BottomSheetLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : BottomSheetDialogFragment(R.layout.bottom_sheet_login) {

    private var _binding: BottomSheetLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity() as MainActivity).userRepo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = BottomSheetLoginBinding.bind(view)

        binding.textInputEditUsername.addTextChangedListener {
            binding.textInputLayoutUsername.validate(
                { isNotEmpty() },
                "Enter  your name!"
            )
        }

        binding.textInputEditPassword.addTextChangedListener {
            binding.textInputLayoutPassword.validate(
                { isNotEmpty() },
                "Enter your password!"
            )
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.textInputLayoutUsername.validate(
                    { isNotEmpty() },
                    "Enter  your name!"
                ) && binding.textInputLayoutPassword.validate(
                    { isNotEmpty() },
                    "Enter your password!"
                )
            ) {
                viewModel.login(
                    binding.textInputEditUsername.text.toString(),
                    binding.textInputEditPassword.text.toString()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        AuthState.Loading -> binding.loginProgressIndicator.visibility = View.VISIBLE
                        is AuthState.Success -> {
                            viewModel.resetLoginState()
                            dismiss()
                            val intent = Intent(
                                requireContext(),
                                HomePageActivity::class.java
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                putExtra("USER_ID", state.userID)
                            }
                            startActivity(intent)
                        }
                        is AuthState.Error -> {
                            viewModel.resetLoginState()
                            binding.textInputLayoutPassword.error = state.message
                        }
                        AuthState.Idle -> binding.loginProgressIndicator.visibility = View.GONE
                    }
                }
            }
        }

        // Switch to sign up if the sign up link is tapped
        binding.textViewSignupLink.setOnClickListener {
            dismiss()
            SignupFragment().show(parentFragmentManager, "SIGNUP_SHEET")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun TextInputLayout.validate(
        predicate: String.() -> Boolean,
        errorMSG: String
    ): Boolean {
        val text = (editText?.text?.toString() ?: "").trim()
        return if (!text.predicate()) {
            this.error = errorMSG
            false
        } else {
            this.error = null
            true
        }
    }

}