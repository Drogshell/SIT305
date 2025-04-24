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
import com.trevin.myplaylists.databinding.BottomSheetSignupBinding
import kotlinx.coroutines.launch

class SignupFragment : BottomSheetDialogFragment(R.layout.bottom_sheet_signup) {

    private var _binding: BottomSheetSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity() as MainActivity).userRepo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = BottomSheetSignupBinding.bind(view)

        binding.textInputEditFullName.addTextChangedListener {
            binding.textInputLayoutFullName
                .validate({ isNotEmpty() }, "Full name is required")
        }
        binding.textInputEditUsername.addTextChangedListener {
            binding.textInputLayoutUsername
                .validate({ isNotEmpty() }, "User name is required")
        }
        binding.textInputEditPassword.addTextChangedListener {
            binding.textInputLayoutPassword
                .validate({ length >= 6 && length <= 16 }, "Password must be between 6 – 16 characters")
        }
        binding.textInputEditConfirmPassword.addTextChangedListener {
            binding.textInputLayoutConfirmPassword
                .validate(
                    { this == binding.textInputEditPassword.text.toString() },
                    "Passwords must match"
                )
        }

        binding.buttonSignup.setOnClickListener {
            if (binding.textInputLayoutFullName
                    .validate(
                        { isNotEmpty() },
                        "Full name is required"
                    ) && binding.textInputLayoutUsername
                    .validate(
                        { isNotEmpty() },
                        "User name is required"
                    ) && binding.textInputLayoutPassword
                    .validate(
                        { length >= 6 && length <= 16 },
                        "6–16 chars"
                    ) && binding.textInputLayoutConfirmPassword
                    .validate(
                        { this == binding.textInputEditPassword.text.toString() },
                        "Passwords must match"
                    )
            ) {
                viewModel.signup(
                    binding.textInputEditFullName.text.toString(),
                    binding.textInputEditUsername.text.toString(),
                    binding.textInputEditPassword.text.toString(),
                    binding.textInputEditConfirmPassword.text.toString()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signupState.collect { state ->
                    when (state) {
                        AuthState.Loading -> binding.signupProgressIndicator.visibility = View.VISIBLE
                        is AuthState.Success -> {
                            viewModel.resetSignUpState()
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
                            viewModel.resetSignUpState()
                            binding.textInputLayoutUsername.error = state.message
                        }
                        AuthState.Idle -> binding.signupProgressIndicator.visibility = View.GONE
                    }
                }
            }
        }

        binding.textViewLogInLink.setOnClickListener {
            dismiss()
            LoginFragment().show(parentFragmentManager, "LOGIN_SHEET")
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