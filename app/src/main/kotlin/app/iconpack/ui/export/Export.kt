package app.iconpack.ui.export

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import app.iconpack.Util.getPackage
import app.iconpack.Util.saveAndShareAppFilterXmlFile
import app.iconpack.Util.saveAndShareDrawableXmlFile
import app.iconpack.ui.layout.FloatingButton
import app.iconpack.ui.layout.InstalledPackageColumn
import app.iconpack.ui.layout.TopBar

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
//                saveAndShareAppFilterXmlFile(installedPackagesInfo = installedPackagesInfo)
                saveAndShareDrawableXmlFile(installedPackagesInfo = installedPackagesInfo)
            }
        }
    )
}