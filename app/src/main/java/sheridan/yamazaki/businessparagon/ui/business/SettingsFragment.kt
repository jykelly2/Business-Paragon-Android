package sheridan.yamazaki.businessparagon.ui.business

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import sheridan.yamazaki.businessparagon.BusinessActivity
import sheridan.yamazaki.businessparagon.MainActivity
import sheridan.yamazaki.businessparagon.R
import sheridan.yamazaki.businessparagon.databinding.FragmentSettingsBinding
import sheridan.yamazaki.businessparagon.ui.authentication.UserViewModel
import sheridan.yamazaki.businessparagon.ui.business.detail.BusinessDetailFragment
import sheridan.yamazaki.businessparagon.ui.chatbot.ChatbotActivity

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: UserViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.editProfile.setOnClickListener {
            startEditProfileFragment()
        }
        binding.talkToChatbot.setOnClickListener {
            startChatbotActivity()
        }
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_firestore, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                viewModel.signOut()
                //Firebase.auth.signOut()
                startMainActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startMainActivity(){
        requireActivity().run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    //navigate to the chat bot activity
    private fun startChatbotActivity(){
        requireActivity().run {
            startActivity(Intent(this, ChatbotActivity::class.java))
            finish()
        }
    }

    //navigate to the edit profile fragment
    private fun startEditProfileFragment(){
        val fragment = EditProfileFragment()
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
}