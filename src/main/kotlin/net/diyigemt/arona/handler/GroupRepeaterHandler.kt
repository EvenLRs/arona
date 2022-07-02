package net.diyigemt.arona.handler

import net.diyigemt.arona.Arona
import net.diyigemt.arona.config.AronaRepeatConfig
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.content
import java.awt.image.BufferedImage

// 复读
object GroupRepeaterHandler: AronaEventHandler<GroupMessageEvent> {
  private var last: String = ""
  private var count: Int = 0
  override suspend fun handle(event: GroupMessageEvent) {
    if (!AronaRepeatConfig.enable) return
    val now = event.message.serializeToMiraiCode()
    if (now.startsWith("/")) return
    if (now == last) {
      count++
      if (count >= AronaRepeatConfig.times) {
        event.subject.sendMessage(event.message)
        count = 0
        last = now
      }
    } else {
      last = now
      count = 1
    }
  }
}