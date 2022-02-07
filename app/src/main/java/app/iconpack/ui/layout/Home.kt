package app.iconpack.ui.layout

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import app.iconpack.Util.getXmlResource
import app.iconpack.Util.parseXmlResourceParser

@Composable
fun Home(navController: NavController) {
    val xmlResource = getXmlResource()
    val installedPackagesInfo = parseXmlResourceParser(xmlResource)
    Scaffold(
        topBar = {
            TopBar {
                navController.navigate("export")
            }
        },
        content = {
            InstalledPackageColumn(
                installedPackagesInfo = installedPackagesInfo
            )
        },
        floatingActionButton = {
        }
    )
}