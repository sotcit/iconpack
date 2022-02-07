package app.iconpack.ui.layout

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import app.iconpack.Util.exportXml
import app.iconpack.Util.getPackage

@Composable
fun Export(navController: NavController) {
    val installedPackagesInfo = getPackage()
    Scaffold(
        topBar = {
            TopBar {
                navController.navigate("home")
            }
        },
        content = {
            InstalledPackageColumn(
                installedPackagesInfo = installedPackagesInfo
            )
        },
        floatingActionButton = {
            FloatingButton {
                exportXml(installedPackagesInfo = installedPackagesInfo)
            }
        }
    )
}