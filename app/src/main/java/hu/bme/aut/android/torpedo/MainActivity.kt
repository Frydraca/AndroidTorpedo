package hu.bme.aut.android.torpedo

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener { registerClick() }
        btnLogin.setOnClickListener { loginClick() }
    }

    private fun validateForm(): Boolean {
        if (etEmail.text.isEmpty()) {
            etEmail.error = "Required"
            return false
        }
        if (etPassword.text.isEmpty()) {
            etPassword.error = "Required"
            return false
        }
        return true
    }

    private fun registerClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnSuccessListener { result ->
                hideProgressDialog()

                val firebaseUser = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(firebaseUser?.email?.substringBefore('@'))
                    .build()
                firebaseUser?.updateProfile(profileChangeRequest)

                toast("Registration successful")
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.message)
            }
    }

    private fun loginClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnSuccessListener {
                hideProgressDialog()

                startActivity(Intent(this@MainActivity, LobbyBrowserActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.localizedMessage)
            }
    }

}
