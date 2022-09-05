package net.diyigemt.arona.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.diyigemt.arona.Arona
import net.diyigemt.arona.advance.AronaUpdateChecker
import net.diyigemt.arona.command.CallMeCommand
import net.diyigemt.arona.command.TrainerCommand
import net.diyigemt.arona.config.AronaConfig
import net.diyigemt.arona.db.DataBaseProvider.query
import net.diyigemt.arona.db.name.TeacherName
import net.diyigemt.arona.db.name.TeacherNameTable
import net.diyigemt.arona.entity.ServerResponse
import net.diyigemt.arona.interfaces.InitializedFunction
import net.diyigemt.arona.util.NetworkUtil.BACKEND_ADDRESS
import net.diyigemt.arona.util.NetworkUtil.baseRequest
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.jetbrains.exposed.sql.and
import org.jsoup.Connection
import org.jsoup.Connection.Response
import org.jsoup.Jsoup
import java.io.File
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object GeneralUtils: InitializedFunction() {

  private const val IMAGE_FOLDER = "/image"
  private const val BACKEND_IMAGE_RESOURCE = "${BACKEND_ADDRESS}$IMAGE_FOLDER"

  fun checkService(group: Contact?): Boolean = when(group) {
    is Group -> AronaConfig.groups.contains(group.id)
    else -> false
  }

  fun clearExtraQute(s: String): String {
    if (s.replace("\"", "").length + 2 == s.length) {
      return s.replaceFirst("\"", "").substring(0, s.length - 2)
    }
    return s
  }

  fun queryTeacherNameFromDB(contact: Contact, user: UserOrBot): String {
    if (!CallMeCommand.enable) return user.nameCardOrNick
    val name = query {
      TeacherName.find { (TeacherNameTable.group eq contact.id) and (TeacherNameTable.id eq user.id) }.firstOrNull()
    }?.name ?: user.nameCardOrNick
    return if (AronaConfig.endWithSensei.isNotBlank() && !name.endsWith(AronaConfig.endWithSensei)) "${name}${AronaConfig.endWithSensei}" else name
  }

  fun randomInt(bound: Int): Int = (System.currentTimeMillis() % bound).toInt()

  fun randomBoolean(): Boolean = System.currentTimeMillis().toString().let {
    it.substring(it.length - 1).toInt() % 2 == 0
  }

  suspend fun uploadChapterHelper() {
    doUpload("/map-cache")
  }

  suspend fun uploadStudentInfo() {
    doUpload("/student_info")
  }

  private suspend fun doUpload(path: String) {
    val imageFileList = File(Arona.dataFolderPath() + path).listFiles() ?: return
    val g = Arona.arona.groups[1002484182]!!
    imageFileList.forEach {
      val name = it.name
      val res = it.toExternalResource("png")
      val upload = g.uploadImage(res)
      val msg = g.sendMessage(upload)
      Arona.info("$name ${msg.source.originalMessage.serializeToMiraiCode()}")
      Thread.sleep(1000)
      res.closed
    }
  }


  private fun imageRequest(subFolder: String, fileName: String, localFile: File): File {
    val connection = baseRequest("$subFolder/$fileName", BACKEND_IMAGE_RESOURCE)
    val res = connection.execute().bodyStream().readAllBytes()
    localFile.writeBytes(res)
    return localFile
  }

  private fun imageFileFolder(subFolder: String = "") = Arona.dataFolderPath(IMAGE_FOLDER) + subFolder

  private fun localImageFile(fileName: String) = File(imageFileFolder(fileName.let { return@let if (fileName.startsWith("/")) fileName else "/$it" }))

  fun getImageOrDownload(subFolder: String, fileName: String): File {
    val file = localImageFile("$subFolder/$fileName")
    if (file.exists()) return file
    return imageRequest(subFolder, fileName, file)
  }

  override fun init() {
    // 初始化本地图片文件夹
    File(imageFileFolder(TrainerCommand.ChapterMapFolder)).also { it.mkdirs() }
    File(imageFileFolder(TrainerCommand.StudentRankFolder)).also { it.mkdirs() }
  }
}