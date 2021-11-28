package com.pablolop.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pablolop.firebase.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding
    private lateinit var auth : FirebaseAuth
    private val tag : String = "SignInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener{
            if(binding.signInMail.text.toString().isNotEmpty()
                && binding.signInPass.text.toString().isNotEmpty()
            ){
                userSignIn(binding.signInMail.text.toString(), binding.signInPass.text.toString())
            } else {
                Toast.makeText(baseContext, "email/password vacios", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun userSignIn(email : String, password : String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Sesion Iniciada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(baseContext, UserActivity::class.java))
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }
}