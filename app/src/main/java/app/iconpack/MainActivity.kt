package app.iconpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.iconpack.ui.layout.Export
import app.iconpack.ui.layout.Home
import app.iconpack.ui.theme.IconPackTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = this
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            IconPackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProvideWindowInsets {
                        rememberSystemUiController().setStatusBarColor(
                            MaterialTheme.colors.primary,
                            darkIcons = MaterialTheme.colors.isLight
                        )
                        Column {
                            Spacer(
                                modifier = Modifier
                                    .statusBarsHeight()
                                    .fillMaxWidth()
                            )
                            val navController = rememberNavController()
                            NavHost(navController = navController, startDestination = "home") {
                                composable("home") {
                                    Home(navController = navController)
                                }
                                composable("export") {
                                    Export(navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        lateinit var app: MainActivity private set
    }
}
