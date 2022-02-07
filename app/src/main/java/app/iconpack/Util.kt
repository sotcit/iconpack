package app.iconpack

import android.content.Intent
import android.content.res.XmlResourceParser
import androidx.core.content.res.ResourcesCompat
import org.xmlpull.v1.XmlPullParser
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object Util {
    private val app = MainActivity.app
    private val packageManager = app.packageManager

    /**
     * 获取带有图标的应用列表
     * @return 返回 packageName 列表
     */
    fun getPackage(): MutableList<InstalledPackageInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val installedPackagesInfo = mutableListOf<InstalledPackageInfo>()
        packageManager.queryIntentActivities(intent, 0).forEach {
            val packageName = it.activityInfo.packageName
            val applicationLabel =
                packageManager.getPackageInfo(packageName, 0).applicationInfo.loadLabel(
                    packageManager
                ).toString()
            val component = "ComponentInfo{${packageName}/${it.activityInfo.name}}"
            val drawable = packageName.lowercase().replace(".", "_")
            val applicationIcon = it.loadIcon(packageManager)
            installedPackagesInfo.add(
                InstalledPackageInfo(
                    applicationLabel = applicationLabel,
                    component = component,
                    drawable = drawable,
                    packageName = packageName,
                    applicationIcon = applicationIcon
                )
            )
        }
        installedPackagesInfo.sortBy {
            it.drawable
        }
        return installedPackagesInfo
    }

    fun parseXmlResourceParser(xmlResourceParser: XmlResourceParser?): MutableList<InstalledPackageInfo> {
        val installedPackagesInfo = mutableListOf<InstalledPackageInfo>()
        if (xmlResourceParser != null) {
            while (xmlResourceParser.eventType != XmlPullParser.END_DOCUMENT) {
                when (xmlResourceParser.eventType) {
                    XmlPullParser.START_TAG -> {
                        if (xmlResourceParser.name.equals("item")) {
                            val applicationLabel =
                                xmlResourceParser.getAttributeValue(null, "applicationLabel")
                            val component = xmlResourceParser.getAttributeValue(null, "component")
                            val drawable = xmlResourceParser.getAttributeValue(null, "drawable")
                            val packageName =
                                xmlResourceParser.getAttributeValue(null, "packageName")
                            val applicationIconId =
                                app.resources.getIdentifier(drawable, "drawable", app.packageName)
                            val applicationIcon =
                                ResourcesCompat.getDrawable(app.resources, applicationIconId, null)
                            installedPackagesInfo.add(
                                InstalledPackageInfo(
                                    applicationLabel = applicationLabel,
                                    component = component,
                                    drawable = drawable,
                                    packageName = packageName,
                                    applicationIcon = applicationIcon!!
                                )
                            )
                        }
                    }
                }
                xmlResourceParser.next()
            }
        }
        return installedPackagesInfo
    }

    /**
     * 获取 xml
     */
    fun getXmlResource(): XmlResourceParser? {
        return packageManager.getXml(
            app.packageName,
            R.xml.appfilter,
            null
        )
    }

    fun exportXml(installedPackagesInfo: MutableList<InstalledPackageInfo>) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.newDocument()
        val resources = document.createElement("resources")
        installedPackagesInfo.forEach {
            val item = document.createElement("item")
            item.setAttribute(
                "applicationLabel",
                it.applicationLabel
            )
            item.setAttribute("component", it.component)
            item.setAttribute(
                "drawable",
                it.packageName.lowercase().replace(".", "_")
            )
            item.setAttribute("packageName", it.packageName)
            resources.appendChild(item)
        }
        document.appendChild(resources)
        val transformer =
            TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(document), StreamResult(
                File(app.filesDir, "appfilter.xml")
            )
        )
    }

    fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/xml"
            putExtra(Intent.EXTRA_TITLE, File(app.filesDir, "appfilter.xml"))
        }
//        app.openFileOutput("aa", Context.MODE_PRIVATE).use {
//            it.write("a11111".toByteArray())
//        }
    }

}