package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.extend.clickableNoRipple

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun EmojiTable(appendEmoji: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 6),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 14.dp)
    ) {
        items(
            items = emojis,
            contentType = {
                "emojis"
            },
            key = {
                it
            }
        ) {
            Text(
                modifier = Modifier
                    .clickableNoRipple {
                        appendEmoji(it)
                    }
                    .padding(vertical = 10.dp),
                text = it,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private val emojis = listOf(
    // 笑脸与情感 (Smileys & Emotion)
    "\uD83D\uDE00", // 😀 U+1F600
    "\uD83D\uDE03", // 😃 U+1F603
    "\uD83D\uDE04", // 😄 U+1F604
    "\uD83D\uDE01", // 😁 U+1F601
    "\uD83D\uDE06", // 😆 U+1F606
    "\uD83D\uDE05", // 😅 U+1F605
    "\uD83D\uDE02", // 😂 U+1F602
    "\uD83E\uDD23", // 🤣 U+1F923
    "\uD83D\uDE0A", // 😊 U+1F60A
    "\uD83D\uDE07", // 😇 U+1F607
    "\uD83D\uDE42", // 🙂 U+1F642
    "\uD83D\uDE43", // 🙃 U+1F643
    "\uD83D\uDE09", // 😉 U+1F609
    "\uD83D\uDE0C", // 😌 U+1F60C
    "\uD83D\uDE0D", // 😍 U+1F60D
    "\uD83E\uDD70", // 🥰 U+1F970
    "\uD83D\uDE18", // 😘 U+1F618
    "\uD83D\uDE17", // 😗 U+1F617
    "\uD83D\uDE19", // 😙 U+1F619

    // 人物 (People & Body)
    "\uD83D\uDC68", // 👨 U+1F468
    "\uD83D\uDC69", // 👩 U+1F469
    "\uD83D\uDC66", // 👦 U+1F466
    "\uD83D\uDC67", // 👧 U+1F467
    "\uD83D\uDC76", // 👶 U+1F476
    "\uD83E\uDDD1", // 🧑 U+1F9D1
    "\uD83E\uDD34", // 🤴 U+1F934
    "\uD83E\uDD35", // 👸 U+1F935
    "\uD83E\uDDD3", // 🧓 U+1F9D3

    // 动物与自然 (Animals & Nature)
    "\uD83D\uDC36", // 🐶 U+1F436
    "\uD83D\uDC31", // 🐱 U+1F431
    "\uD83D\uDC2D", // 🐭 U+1F42D
    "\uD83D\uDC39", // 🐹 U+1F439
    "\uD83D\uDC30", // 🐰 U+1F430
    "\uD83D\uDC3B", // 🐻 U+1F43B
    "\uD83D\uDC2F", // 🐯 U+1F42F
    "\uD83D\uDC28", // 🐨 U+1F428
    "\uD83D\uDC35", // 🐵 U+1F435
    "\uD83D\uDC3D", // 🐽 U+1F43D

    // 食物与饮料 (Food & Drink)
    "\uD83C\uDF47", // 🍇 U+1F347
    "\uD83C\uDF48", // 🍈 U+1F348
    "\uD83C\uDF49", // 🍉 U+1F349
    "\uD83C\uDF4A", // 🍊 U+1F34A
    "\uD83C\uDF4B", // 🍋 U+1F34B
    "\uD83C\uDF4C", // 🍌 U+1F34C
    "\uD83C\uDF4D", // 🍍 U+1F34D
    "\uD83C\uDF4E", // 🍎 U+1F34E
    "\uD83C\uDF4F", // 🍏 U+1F34F
    "\uD83C\uDF50", // 🍐 U+1F350

    // 活动 (Activities)
    "\uD83C\uDF89", // 🎉 U+1F389
    "\uD83C\uDFC6", // 🏆 U+1F3C6
    "\uD83C\uDFBE", // 🎾 U+1F3BE
    "\uD83C\uDFC8", // 🏈 U+1F3C8
    "\uD83C\uDFC0", // 🏀 U+1F3C0
    "\uD83C\uDFB1", // 🎱 U+1F3B1
    "\uD83C\uDFAE", // 🎮 U+1F3AE
    "\uD83C\uDFAF", // 🎯 U+1F3AF
    "\uD83C\uDF7E", // 🍾 U+1F37E
    "\uD83C\uDF7F", // 🍿 U+1F37F

    // 旅行与地点 (Travel & Places)
    "\uD83D\uDE95", // 🚕 U+1F695
    "\uD83D\uDE97", // 🚗 U+1F697
    "\uD83D\uDE99", // 🚙 U+1F699
    "\uD83D\uDE9A", // 🚚 U+1F69A
    "\uD83D\uDE9B", // 🚛 U+1F69B
    "\uD83D\uDEA2", // 🚢 U+1F6A2
    "\uD83D\uDEA4", // 🚤 U+1F6A4
    "\uD83D\uDEA5", // 🚥 U+1F6A5
    "\uD83D\uDEA7", // 🚧 U+1F6A7
    "\uD83D\uDE82", // 🚂 U+1F682

    // 物品 (Objects)
    "\uD83D\uDCBB", // 💻 U+1F4BB
    "\uD83D\uDCFA", // 📺 U+1F4FA
    "\uD83D\uDCF1", // 📱 U+1F4F1
    "\uD83D\uDCF2", // 📲 U+1F4F2
    "\uD83D\uDCF7", // 📷 U+1F4F7
    "\uD83D\uDCFB", // 📻 U+1F4FB
    "\uD83D\uDCE0", // 📠 U+1F4E0
    "\uD83D\uDCEA", // 📪 U+1F4EA
    "\uD83D\uDCF3", // 📳 U+1F4F3
    "\uD83D\uDCE1" // 📡 U+1F4E1
)