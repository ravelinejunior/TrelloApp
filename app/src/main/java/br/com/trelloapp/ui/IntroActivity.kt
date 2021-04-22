package br.com.trelloapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import br.com.trelloapp.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        btn_sign_in_intro.setOnClickListener(this)
        btn_sign_up_intro.setOnClickListener(this)



    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_sign_in_intro -> {
                startActivity(Intent(this, SignInActivity::class.java))
            }

            R.id.btn_sign_up_intro -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }

        }
    }
}