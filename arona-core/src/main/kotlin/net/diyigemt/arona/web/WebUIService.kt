package net.diyigemt.arona.web

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.diyigemt.arona.Arona
import net.diyigemt.arona.config.AronaWebUIConfig
import net.diyigemt.arona.interfaces.Initialize
import net.diyigemt.arona.service.AronaService
import net.diyigemt.arona.web.database.DBOptionService
import net.diyigemt.arona.web.plugins.configureCROS
import net.diyigemt.arona.web.plugins.configureRouting
import net.diyigemt.arona.web.plugins.configureSecurity
import net.diyigemt.arona.web.plugins.configureSerialization
import net.mamoe.mirai.console.command.SimpleCommand
import java.util.*

object WebUIService : AronaService, Initialize, SimpleCommand(
  Arona, "token", "生成一个WebUI登录密钥（1小时内有效，程序中途退出依然失效）",
  description = "抽卡历史记录"
){
  private lateinit var server: ApplicationEngine
  const val jwtAudience = "http://127.0.0.1/api/v1"
  const val issuer = "http://127.0.0.1/"
  const val realm = "WebUI Access to API 'v1'"
  var secret = generateSecret()

  override fun enableService() {
    Arona.runSuspend {
      server = embeddedServer(Netty, port = AronaWebUIConfig.port, host = "127.0.0.1") {
        configureSecurity()
        configureCROS()
        configureSerialization()
        configureRouting()
      }.start(wait = false)
    }
  }

  @Handler
  fun handler(){
    val res = JWT.create()
      .withAudience(jwtAudience)
      .withIssuer(issuer)
      .withExpiresAt(Date(System.currentTimeMillis() + 60000))
      .sign(Algorithm.HMAC256(secret))

    Arona.info(res)
  }

  override fun disableService() {
    kotlin.runCatching {
      server.stop()
    }
  }

  override val id: Int = 24
  override val name: String = "WebUI"
  override val description: String = "WebUI服务"
  override var enable: Boolean = true
  override val priority: Int = 10

  override fun init() {
    DBOptionService.init()
  }

  private fun generateSecret() : String{
    val dictChars = mutableListOf<Char>().apply { "123456789zxcvbnmasdfghjklqwertyuiop".forEach { this.add(it) } }
    val randomStr = StringBuilder().apply { (1..((10..30).random())).onEach { append(dictChars.random()) } }

    return randomStr.toString()
  }
}