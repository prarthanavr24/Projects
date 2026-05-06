package auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nammahomestay.MainActivity
import com.example.nammahomestay.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail    = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)

        // Login Button
        findViewById<MaterialButton>(R.id.btn_login).setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this,
                "Welcome! 🙏",
                Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Register Button
        findViewById<MaterialButton>(R.id.btn_register).setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this,
                "HomeStay Registered! 🎉",
                Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}