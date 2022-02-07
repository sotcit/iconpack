package app.iconpack.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.iconpack.InstalledPackageInfo

@Composable
fun InstalledPackageColumn(
    installedPackagesInfo: MutableList<InstalledPackageInfo>
) {
    LazyColumn(
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(installedPackagesInfo) { installedPackageInfo ->
            InstalledPackageInfoCard(
                installedPackageInfo = installedPackageInfo
            )
        }
    }
}