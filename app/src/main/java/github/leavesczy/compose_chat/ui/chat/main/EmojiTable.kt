package github.leavesczy.compose_chat.ui.chat.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
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
import github.leavesczy.compose_chat.extensions.clickableNoRippleNotCheck
import github.leavesczy.compose_chat.theme.AppTheme

@Composable
fun EmojiTable(
    modifier: Modifier,
    appendEmoji: (emoji: String) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(count = 7),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(space = 6.dp, alignment = Alignment.Top),
        contentPadding = PaddingValues(start = 8.dp, top = 10.dp, end = 8.dp, bottom = 20.dp)
    ) {
        items(
            items = emojis,
            key = { emoji ->
                emoji
            },
            contentType = {
                "emojis"
            }
        ) { emoji ->
            Emoji(
                modifier = Modifier,
                emoji = emoji,
                onClick = {
                    appendEmoji(emoji)
                }
            )
        }
    }
}

@Composable
private fun Emoji(
    modifier: Modifier,
    emoji: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1f)
            .clickableNoRippleNotCheck(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = emoji,
            fontSize = 22.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            color = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
        )
    }
}

private val emojis = listOf(
    "🙂", // 微笑
    "😊", // 愉快
    "😁", // 呲牙
    "😄", // 喜悦
    "🤭", // 偷笑
    "🤩", // 憨笑
    "😍", // 色
    "😎", // 得意
    "😜", // 调皮
    "😂", // 破涕为笑
    "🤤", // 流口水
    "😳", // 发呆
    "😔", // 闭嘴
    "🙁", // 难过
    "😭", // 流泪
    "😅", // 尴尬
    "😓", // 流汗
    "😰", // 惊恐
    "🤢", // 吐
    "😱", // 抓狂
    "😤", // 气愤
    "🙄", // 白眼
    "😏", // 冷笑
    "😫", // 疲惫
    "😡", // 发怒
    "😠", // 生气
    "😈", // 微笑的恶魔
    "👿", // 愤怒的恶魔
    "👹", // 鬼
    "👺", // 天狗
    "🤡", // 小丑
    "💀", // 骷髅头
    "👽", // 外星人
    "👻", // 幽灵
    "👾", // 怪物
    "🤖", // 机器人
    "🎉", // 庆祝
    "💣", // 炸弹
    "💩", // 便便
    "❤️", // 爱心
    "💔", // 心碎
    "🎂", // 蛋糕
)