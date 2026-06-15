package com.jeiel85.daddyweekend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jeiel85.daddyweekend.data.database.AppDatabase
import com.jeiel85.daddyweekend.data.repository.AppRepository
import com.jeiel85.daddyweekend.ui.navigation.AppNavigation
import com.jeiel85.daddyweekend.ui.theme.MyApplicationTheme
import com.jeiel85.daddyweekend.ui.viewmodel.MainViewModel
import com.jeiel85.daddyweekend.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(
            familyProfileDao = database.familyProfileDao(),
            courseTemplateDao = database.courseTemplateDao(),
            savedCourseDao = database.savedCourseDao()
        )
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
