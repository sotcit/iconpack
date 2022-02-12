package app.iconpack

import android.content.Intent
import android.content.res.XmlResourceParser
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import org.w3c.dom.Document
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
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

    /**
     * 初始化一个空的 xml 文件，用于构建 appfilter.xml 及 drawable.xml
     * @return 返回空的 xml 文件
     */
    private fun newEmptyXmlDocument(): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.newDocument()
    }

    /**
     * 根据 installedPackagesInfo 构建 appfilter.xml Document
     * @param installedPackagesInfo 用户应用信息
     * @return 返回构建好的 Document
     */
    private fun buildAppFilterXmlDocument(installedPackagesInfo: MutableList<InstalledPackageInfo>): Document {
        val document = newEmptyXmlDocument()
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
        return document
    }

    /**
     * 根据 installedPackagesInfo 构建 drawable.xml Document
     * @param installedPackagesInfo 用户应用信息
     * @return 返回构建好的 Document
     */
    private fun buildDrawableXmlDocument(installedPackagesInfo: MutableList<InstalledPackageInfo>): Document {
        val document = newEmptyXmlDocument()
        val resources = document.createElement("resources")
        val category = document.createElement("category")
        category.setAttribute("title", "ALL")
        resources.appendChild(category)
        installedPackagesInfo.forEach {
            val item = document.createElement("item")
            item.setAttribute(
                "applicationLabel",
                it.applicationLabel
            )
            item.setAttribute(
                "drawable",
                it.packageName.lowercase().replace(".", "_")
            )
            resources.appendChild(item)
        }
        document.appendChild(resources)
        return document
    }

    /**
     * 删除指定目录及其下文件
     * @param dir 目录
     * @return 目录不存在返回 true
     */
    private fun deleteDir(dir: File): Boolean {
        return if (!dir.exists()) {
            !dir.exists()
        } else {
            dir.listFiles().run {
                if (this != null) {
                    for (file in this) {
                        if (file.isFile) {
                            file.delete()
                        }
                    }
                }
            }
            dir.delete()
            !dir.exists()
        }
    }

    /**
     *创建指定目录
     * @param dir 目录
     * @return 目录存在返回 true
     */
    private fun createDir(dir: File): Boolean {
        return if (dir.exists()) {
            dir.exists()
        } else {
            dir.mkdirs()
            dir.exists()
        }
    }

    /**
     * 保存 xml 文件到指定目录
     * @param document xml 文件
     * @param xmlFile xml 文件
     */
    private fun saveXmlFile(document: Document, xmlFile: File) {
        TransformerFactory.newInstance().newTransformer()
            .transform(DOMSource(document), StreamResult(xmlFile))
    }

    private fun zipFile(input: File, output: File) {
        val fileOutputStream = FileOutputStream(output)
        val zipOutputStream = ZipOutputStream(fileOutputStream)
        input.listFiles().run {
            if (this != null) {
                for (xml in this) {
                    zipOutputStream.putNextEntry(ZipEntry(xml.name))
                    zipOutputStream.write(xml.readBytes())
                    zipOutputStream.closeEntry()
                }
            }
        }
        zipOutputStream.close()
        fileOutputStream.close()
    }

    /**
     * 分享文件
     * @param file 要分享的文件
     */
    private fun shareFile(file: File) {
        val uriForFile = FileProvider.getUriForFile(app, "app.iconpack.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uriForFile)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            setDataAndType(uriForFile, "application/zip")
        }
        val chooserIntent: Intent = Intent.createChooser(intent, "分享文件").apply {
            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(chooserIntent)
    }

    fun saveAndShareRequest(installedPackagesInfo: MutableList<InstalledPackageInfo>) {
        val dirPath = "${app.filesDir.absolutePath}/export"
        val dir = File(dirPath)
        if (deleteDir(dir = dir) && createDir(dir = dir)) {
            saveXmlFile(
                document = buildDrawableXmlDocument(installedPackagesInfo),
                xmlFile = File(dirPath, "drawable.xml")
            )
            saveXmlFile(
                document = buildAppFilterXmlDocument(installedPackagesInfo),
                xmlFile = File(dirPath, "appfilter.xml")
            )
        }
        zipFile(
            File("${app.filesDir.absolutePath}/export"),
            File(app.filesDir, "export.zip")
        )
        shareFile(File(app.filesDir, "export.zip"))
    }
}