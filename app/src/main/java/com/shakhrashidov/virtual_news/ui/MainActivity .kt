package com.shakhrashidov.virtual_news.ui

import am.appwise.components.ni.NoInternetDialog
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shakhrashidov.virtual_news.R
import com.shakhrashidov.virtual_news.database.ArticleDatabase
import com.shakhrashidov.virtual_news.repository.NewsRepository
import com.shakhrashidov.virtual_news.util.countryPicker.CustomCountryPicker
import com.shakhrashidov.virtual_news.util.setOnItemReselectedListener
import com.shakhrashidov.virtual_news.util.slideDown
import com.shakhrashidov.virtual_news.util.slideUp
import com.shakhrashidov.virtual_news.util.swipeDetector.SwipeActions
import com.shakhrashidov.virtual_news.util.swipeDetector.SwipeGestureDetector
import github.com.st235.lib_expandablebottombar.navigation.ExpandableBottomBarNavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.country_picker_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    lateinit var actionButton: FloatingActionButton
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var newsRepository: NewsRepository
    private lateinit var countryPicker: CustomCountryPicker
    lateinit var noInternetDialog: NoInternetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_VirtualNews)
        newsRepository = NewsRepository(this, ArticleDatabase(this))
        newsRepository.getDataStore().readUiModeFromDataStore.asLiveData()
            .observe(this, Observer { isDarkMode ->
                nightModeButton.isChecked = isDarkMode
            })

        loadLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actionButton = findViewById(R.id.changeLang)
        supportActionBar?.hide()
        countryPicker = CustomCountryPicker(this).attach(country_picker_bottom_sheet)


        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        setUpBottomSheet()
        detectSwipeGestures()

        noInternetDialog = noInternetAlert()


        val navController = findNavController(R.id.nav_host_fragment)
        ExpandableBottomBarNavigationUI.setupWithNavController(
            bottom_bar,
            navController
        )
        bottom_bar.setOnItemReselectedListener { view, menuItem ->

            viewModel.currentNewsPosition = 0
            navController.navigate(menuItem.itemId)
        }

        actionButton.setOnClickListener(View.OnClickListener {

            showChangeLang()

        })

    }


    private fun showChangeLang() {

        val listItems = arrayOf("English", "Uzbek", "Russ")

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(R.string.choose_language)
        builder.setSingleChoiceItems(listItems, -1) { dialog, which ->

            if (which == 0) {
                setLocate("def")
                recreate()

            } else if (which == 1) {
                setLocate("uz")
                recreate()

            } else if (which == 2) {
                setLocate("ru")
                recreate()

            }
            dialog.dismiss()

        }
        val dialog = builder.create()
        dialog.show()

    }

    fun setLocate(s: String) {
        val locale = Locale(s)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", s)
        editor.apply()

    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        setLocate(language.toString())
    }

    private fun detectSwipeGestures() {
        val swipeGestureDetector = SwipeGestureDetector(object : SwipeActions {
            override fun onSwipeLeft() {}

            override fun onSwipeUp() {
                if (bottom_bar.isVisible) bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_EXPANDED
                else bottom_bar.slideUp()
            }

            override fun onSwipeDown() {
                bottom_bar.slideDown()
            }
        })

        val gestureDetectorCompat = GestureDetectorCompat(applicationContext, swipeGestureDetector)
        btn_swipe_up.setOnTouchListener { view, motionEvent ->
            gestureDetectorCompat.onTouchEvent(motionEvent)
            view.performClick()
            true
        }
    }

    private fun restartActivity() {
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(Intent(applicationContext, MainActivity::class.java), options.toBundle())
        finish()
    }

    private fun isDarkMode(isDarkMode: Boolean) = GlobalScope.launch(Dispatchers.IO) {
        newsRepository.getDataStore().saveUiMode(isDarkMode)
    }

    private fun saveCurrentCountry(country: String) = GlobalScope.launch(Dispatchers.IO) {
        newsRepository.getDataStore().saveToDataStore(country)
    }


    private fun setUpBottomSheet() {
        viewModel.currentCountryLiveData.observe(this, Observer {
            tv_selectedCountry.text = countryPicker.getCountryByCode(it)
            viewModel.currentCountry = it
        })

        enable_dark_mode.setOnClickListener {
            nightModeButton.performClick()
        }

        nightModeButton.setOnCheckedChangeListener { _, isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                isDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                isDarkMode(false)
            }
        }

        choose_country.setOnClickListener {
            countryPicker.show()
            countryPicker.adapter.setOnCountrySelectedListener {
                tv_selectedCountry.apply {
                    text = it.name
                    tag = it.name
                }
                saveCurrentCountry(it.code)
                countryPicker.dismiss()
                restartActivity()

            }
        }

        feedback.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "https://github.com/akbarshokhabdurashidov", null)
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            startActivity(Intent.createChooser(emailIntent, "Feedback"))
        }

    }

    private fun noInternetAlert() = NoInternetDialog.Builder(this)
        .setCancelable(false)
        .setDialogRadius(50f)
        .setBgGradientCenter(resources.getColor(R.color.light_blue))
        .setBgGradientStart(resources.getColor(R.color.light_blue))
        .setBgGradientEnd(resources.getColor(R.color.light_blue))
        .setButtonColor(resources.getColor(R.color.white))
        .setButtonIconsColor(resources.getColor(R.color.light_blue))
        .setButtonTextColor(resources.getColor(R.color.black))
        .setWifiLoaderColor(resources.getColor(R.color.light_blue))
        .build()


    override fun onDestroy() {
        super.onDestroy()
        noInternetDialog.onDestroy()
    }


    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            countryPicker.dismiss()
        } else {
            super.onBackPressed()
        }

    }

}