package com.pablolop.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pablolop.firebase.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseUser




class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityRegisterBinding
    private val tag : String = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener{
            if (binding.registerMail.text.toString().isNotEmpty()
                && binding.registerPass1.text.toString().isNotEmpty()
                && binding.registerPass2.text.toString().isNotEmpty()
                && binding.registerPass1.text.toString().compareTo(binding.registerPass2.text.toString()) == 0
            ){
                userRegister(binding.registerMail.text.toString(), binding.registerPass1.text.toString())
            } else {
                Toast.makeText(baseContext, "email o passwords vacios/diferentes", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //TODO para comprobar si hay un usuario conectado

    /*  public override fun onStart() {
          super.onStart()
          // Check if user is signed in (non-null) and update UI accordingly.
          val currentUser = auth.currentUser
          if(currentUser != null){
              //reload();
          } else {
              toUser()
          }
      }*/

    private fun userRegister(email : String, password : String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    Toast.makeText(baseContext, "Registrado $email $password", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(baseContext,UserActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }
}