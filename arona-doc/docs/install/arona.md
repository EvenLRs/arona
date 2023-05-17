# 安装arona

1. 由于图片生成使用到了Java的图形库，如果在Linux环境下部署，需要安装额外的包，以下仅给出apt的安装命令，其他Linux发行版请自行搜索对应包的安装

 ```shell
 sudo apt update
 sudo apt install libxrender-dev
 sudo apt install fontconfig
 sudo fc-cache --force
 ```

2. 在[releases](https://github.com/diyigemt/arona/releases)下载最新版本的jar包并放入mirai-console的`plugins`目录下

3. 启动mirai-console，等到显示如下字样后退出：

 ```bash
 yyyy-MM-dd HH:mm:ss I/arona: arona loaded
 yyyy-MM-dd HH:mm:ss I/arona: arona gacha module init success.
 yyyy-MM-dd HH:mm:ss I/ba-activity-pusher: 别名配置更新成功
 yyyy-MM-dd HH:mm:ss I/ba-activity-pusher: 中文字体初始化成功
 yyyy-MM-dd HH:mm:ss I/ba-activity-pusher: Source: STUDENT from GitHub already up to date.
 yyyy-MM-dd HH:mm:ss I/ba-activity-pusher: Source: LOCALIZATION from GitHub already up to date.
 yyyy-MM-dd HH:mm:ss I/ba-activity-pusher: Source: RAID from GitHub already up to date.
 yyyy-MM-dd HH:mm:ss I/ba-activity-pusher: Source: COMMON from GitHub already up to date.
 ```

当然由于网络原因可能并没有最后这四项，不过并不影响使用。

4. 在[releases](https://github.com/diyigemt/arona/releases)下载最新版本的`arona.db`的数据库文件，替换掉`data/net.diyigemt.arona/arona.db`

5. 在`config/net.diyigemt.arona/`文件夹下根据自己的喜好编辑arona的配置文件，具体内容将会在下一节解释

## 初始化

arona的运行依赖`chat-command`插件，因此在使用arona的指令之前，需要在mirai-console的控制台中给予指令使用权限。

arona一共提供了如下的指令：

| 指令权限组                                       | 作用域 | 内置权限控制    | 作用             |
|---------------------------------------------|-----|-----------|----------------|
| net.diyigemt.arona:command.active           | 所有  | 好友/陌生人/群员 | 获取国际服/日服活动状态   |
| net.diyigemt.arona:command.gacha            | 所有  | 管理员       | 配置抽卡参数         |
| net.diyigemt.arona:command.gacha_one        | 仅限群 | 群员        | 单抽             |
| net.diyigemt.arona:command.gacha_multi      | 仅限群 | 群员        | 十连             |
| net.diyigemt.arona:command.gacha_dog        | 仅限群 | 群员        | 查看pickup最小抽取记录 |
| net.diyigemt.arona:command.gacha_history    | 仅限群 | 群员        | 查看抽卡记录         |
| net.diyigemt.arona:command.hentai           | 所有  | 管理员       | 配置发情关键词回复      |
| net.diyigemt.arona:command.config           | 所有  | 管理员       | 配置个服务的开关       |
| net.diyigemt.arona:command.tarot            | 所有  | 好友/陌生人/群员 | 抽一张塔罗牌         |
| net.diyigemt.arona:command.call_me          | 仅限群 | 群员        | 设置自己的昵称        |
| net.diyigemt.arona:command.trainer          | 所有  | 群员        | 查看主线地图和学生攻略    |
| net.diyigemt.arona:command.game_name        | 所有  | 群员        | 让arona记住你的游戏名  |
| net.diyigemt.arona:command.game_name_search | 所有  | 群员        | 模糊查询游戏名对应的群友   |

**注意**，以上语境中的"管理员"指arona主配置文件中`managerGroup`指定的管理员，并不是qq群的管理员

一些解释：

1. 作用域(所有)、权限控制(好友/陌生人/群员)指无论通过何种方法向arona发送信息并被其识别，指令即可触发。

换句话说，你私聊arona发送`/活动 jp`或者在群里发送都能得到响应

2. 作用域(所有)、权限控制(管理员)指无论通过何种方法向arona发送信息并被其识别，指令即可触发，前提是你位于`arona.yml`文件中的`managerGroup`字段中
3. 作用域(仅限群)指只有在群中发送的指令才会被arona响应

因为本插件的指令依附于mirai-console，必须先在console其中给予指令的执行权限才能在群聊中使用指令，因此如果你不是很了解mirai-console的权限管理机制，你可以直接在mirai-console的界面中运行以下命令来直接激活arona的对应指令权限：

```bash
/permission add * net.diyigemt.arona:command.active
/permission add * net.diyigemt.arona:command.gacha
/permission add * net.diyigemt.arona:command.gacha_one
/permission add * net.diyigemt.arona:command.gacha_multi
/permission add * net.diyigemt.arona:command.gacha_dog
/permission add * net.diyigemt.arona:command.gacha_history
/permission add * net.diyigemt.arona:command.hentai
/permission add * net.diyigemt.arona:command.config
/permission add * net.diyigemt.arona:command.tarot
/permission add * net.diyigemt.arona:command.call_me
/permission add * net.diyigemt.arona:command.trainer
/permission add * net.diyigemt.arona:command.game_name
/permission add * net.diyigemt.arona:command.game_name_search
```