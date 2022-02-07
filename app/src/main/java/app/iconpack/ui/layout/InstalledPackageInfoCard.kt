package app.iconpack.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.iconpack.InstalledPackageInfo
import coil.compose.rememberImagePainter

@Composable
fun InstalledPackageInfoCard(
    installedPackageInfo: InstalledPackageInfo
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(installedPackageInfo.applicationIcon),
            contentDescription = null,
            modifier = Modifier.size(55.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        Column {
            Text(
                text = installedPackageInfo.applicationLabel,
                textAlign = TextAlign.Start
            )
            Text(
                text = installedPackageInfo.packageName,
                textAlign = TextAlign.Start
            )
        }
    }
}