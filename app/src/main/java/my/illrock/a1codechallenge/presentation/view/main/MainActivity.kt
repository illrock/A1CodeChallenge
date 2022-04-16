package my.illrock.a1codechallenge.presentation.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.presentation.view.manufacturer.ManufacturersFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fcvMain, ManufacturersFragment())
            }
        }
    }
}