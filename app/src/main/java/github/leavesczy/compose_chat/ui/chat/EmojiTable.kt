package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.extend.clickableNoRippleNotCheck
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun EmojiTable(appendEmoji: (String) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.Fixed(count = 6),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(space = 20.dp, alignment = Alignment.Top),
        contentPadding = PaddingValues(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 12.dp)
    ) {
        items(
            items = emojis,
            key = {
                it
            },
            contentType = {
                "emojis"
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableNoRippleNotCheck {
                        appendEmoji(it)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    text = it,
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
                )
            }
        }
    }
}

private val emojis = listOf(
    // 笑脸与表情
    "😀", // 哈哈大笑
    "😃", // 大笑
    "😄", // 喜悦
    "😁", // 露齿笑
    "😆", // 眯眼大笑
    "😅", // 笑着流汗
    "😂", // 喜极而泣
    "🤣", // 倒地大笑
    "😊", // 微笑
    "😇", // 天使微笑
    // 友好的表情
    "😉", // 眨眼
    "😌", // 轻松
    "😍", // 喜爱
    "😘", // 飞吻
    "😗", // 亲吻
    "😙", // 亲吻，闭眼
    "😚", // 亲吻，脸红
    "😋", // 享用美食
    "😜", // 吐舌头，眨眼
    "😝", // 吐舌头，眯眼
    // 搞怪与有趣
    "🤑", // 钱脸
    "🤗", // 拥抱
    "🤓", // 书呆子
    "😎", // 戴墨镜
    "🤡", // 小丑
    "🤠", // 牛仔
    // 负面与不舒服
    "😏", // 邪魅一笑
    "😒", // 无语
    "😞", // 失望
    "😔", // 沮丧
    "😟", // 担忧
    "😕", // 困惑
    "🙁", // 难过
    "☹️", // 皱眉
    "😣", // 痛苦
    "😖", // 困扰
    // 生气与愤怒
    "😫", // 疲惫
    "😩", // 累
    "😤", // 气愤
    "😠", // 生气
    "😡", // 愤怒
    "😈", // 微笑的恶魔
    "👿", // 愤怒的恶魔
    // 特殊符号与角色
    "💀", // 骷髅头
    "💩", // 便便
    "👹", // 鬼
    "👺", // 天狗
    "👻", // 幽灵
    "👽", // 外星人
    "👾", // 怪物
    "🤖", // 机器人
    // 动物
    "😺", // 咧嘴笑的猫
    "😸", // 喜极而泣的猫
    "😹", // 哭笑不得的猫
    "😻", // 恋爱的猫
    "😼", // 邪魅的猫
    "😽", // 亲吻的猫
    "😿", // 哭泣的猫
    "😾", // 生气的猫
    // 手势
    "🤲", // 掌心向上
    "🙌", // 举起双手
    "👏", // 鼓掌
    "🤝", // 握手
    "👍", // 竖起大拇指
    "👎", // 倒竖大拇指
    "👊", // 拳头
    "✊", // 举起拳头
    "🤛", // 左向拳头
    "🤜", // 右向拳头
    "🤞", // 交叉手指
    "✌️", // 胜利手势
    "🤟", // 爱你手势
    "🤘", // 摇滚手势
    "🤙", // 叫我手势
    "🤚", // 举手
    "🖐️", // 摊开手掌
    "✋", // 举起手
    "👌", // OK手势
    "👈", // 指向左
    "👉", // 指向右
    "👆", // 指向上
    "👇", // 指向下
    "☝️", // 食指向上
    // 人体与服装
    "💪", // 肌肉
    "🙏", // 合掌
    "💍", // 戒指
    "👑", // 皇冠
    "🎩", // 礼帽
    "👒", // 女士帽子
    "🎓", // 毕业帽
    "⛑️", // 安全帽
    "📿", // 念珠
    "💄", // 口红
    "💋", // 嘴唇印
    "👄", // 嘴巴
    "👅", // 舌头
)