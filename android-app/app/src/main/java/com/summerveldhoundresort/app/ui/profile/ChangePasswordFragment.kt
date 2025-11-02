package com.summerveldhoundresort.app.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentChangePasswordBinding

/**
 * Fragment for changing user password
 */
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val graphId = navController.graph.id
        val isAdminGraph = graphId == R.id.admin_nav_graph

        binding.btnBackToLogin.setOnClickListener {
            if (isAdminGraph) {
                navController.navigate(R.id.action_global_viewProfile_admin)
            } else {
                navController.navigate(R.id.action_changePasswordFragment_to_profileViewFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

