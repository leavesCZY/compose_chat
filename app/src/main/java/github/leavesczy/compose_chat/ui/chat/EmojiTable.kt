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
    // Smileys & Emotion
    "\uD83D\uDE00", // 😀 Grinning Face
    "\uD83D\uDE01", // 😁 Beaming Face with Smiling Eyes
    "\uD83D\uDE02", // 😂 Face with Tears of Joy
    "\uD83D\uDE03", // 😃 Grinning Face with Big Eyes
    "\uD83D\uDE04", // 😄 Grinning Face with Smiling Eyes
    "\uD83D\uDE05", // 😅 Grinning Face with Sweat
    "\uD83D\uDE06", // 😆 Grinning Squinting Face
    "\uD83D\uDE07", // 😇 Smiling Face with Halo
    "\uD83D\uDE08", // 😈 Smiling Face with Horns
    "\uD83D\uDE09", // 😉 Winking Face
    "\uD83D\uDE0A", // 😊 Smiling Face with Smiling Eyes
    "\uD83D\uDE0B", // 😋 Face Savoring Food
    "\uD83D\uDE0C", // 😌 Relieved Face
    "\uD83D\uDE0D", // 😍 Smiling Face with Heart-Eyes
    "\uD83D\uDE0E", // 😎 Smiling Face with Sunglasses
    "\uD83D\uDE0F", // 😏 Smirking Face
    "\uD83D\uDE12", // 😒 Unamused Face
    "\uD83D\uDE14", // 😔 Pensive Face
    "\uD83D\uDE16", // 😖 Confounded Face
    "\uD83D\uDE18", // 😘 Face Blowing a Kiss
    "\uD83D\uDE1A", // 😚 Kissing Face with Closed Eyes
    "\uD83D\uDE1C", // 😜 Winking Face with Tongue
    "\uD83D\uDE1D", // 😝 Squinting Face with Tongue
    "\uD83D\uDE1E", // 😞 Disappointed Face
    "\uD83D\uDE20", // 😠 Angry Face
    "\uD83D\uDE21", // 😡 Pouting Face
    "\uD83D\uDE22", // 😢 Crying Face
    "\uD83D\uDE23", // 😣 Persevering Face
    "\uD83D\uDE24", // 😤 Face with Steam From Nose
    "\uD83D\uDE25", // 😥 Sad but Relieved Face
    "\uD83D\uDE28", // 😨 Fearful Face
    "\uD83D\uDE29", // 😩 Weary Face
    "\uD83D\uDE2A", // 😪 Sleepy Face
    "\uD83D\uDE2B", // 😫 Tired Face
    "\uD83D\uDE2D", // 😭 Loudly Crying Face
    "\uD83D\uDE30", // 😰 Anxious Face with Sweat
    "\uD83D\uDE31", // 😱 Face Screaming in Fear
    "\uD83D\uDE32", // 😲 Astonished Face
    "\uD83D\uDE33", // 😳 Flushed Face
    "\uD83D\uDE35", // 😵 Dizzy Face
    "\uD83D\uDE37", // 😷 Face with Medical Mask
    "\uD83D\uDE38", // 😸 Grinning Cat with Smiling Eyes
    "\uD83D\uDE39", // 😹 Cat with Tears of Joy
    "\uD83D\uDE3A", // 😺 Smiling Cat with Open Mouth
    "\uD83D\uDE3B", // 😻 Smiling Cat with Heart-Eyes
    "\uD83D\uDE3C", // 😼 Cat with Wry Smile
    "\uD83D\uDE3D", // 😽 Kissing Cat
    "\uD83D\uDE3E", // 😾 Pouting Cat
    "\uD83D\uDE3F", // 😿 Crying Cat
    "\uD83D\uDE40", // 🙀 Weary Cat

    // People & Body
    "\uD83D\uDC66", // 👦 Boy
    "\uD83D\uDC67", // 👧 Girl
    "\uD83D\uDC68", // 👨 Man
    "\uD83D\uDC69", // 👩 Woman
    "\uD83D\uDC6A", // 👪 Family
    "\uD83D\uDC6B", // 👫 Man and Woman Holding Hands
    "\uD83D\uDC6C", // 👬 Two Men Holding Hands
    "\uD83D\uDC6D", // 👭 Two Women Holding Hands
    "\uD83D\uDC6E", // 👮 Police Officer
    "\uD83D\uDC6F", // 👯 People with Bunny Ears
    "\uD83D\uDC70", // 👰 Bride with Veil
    "\uD83D\uDC71", // 👱 Person with Blond Hair
    "\uD83D\uDC72", // 👲 Man with Chinese Cap
    "\uD83D\uDC73", // 👳 Person Wearing Turban
    "\uD83D\uDC74", // 👴 Old Man
    "\uD83D\uDC75", // 👵 Old Woman
    "\uD83D\uDC76", // 👶 Baby
    "\uD83D\uDC77", // 👷 Construction Worker
    "\uD83D\uDC78", // 👸 Princess
    "\uD83D\uDC7C", // 👼 Baby Angel
    "\uD83D\uDC7D", // 👽 Alien
    "\uD83D\uDC7E", // 👾 Alien Monster
    "\uD83D\uDC7F", // 👿 Imp
    "\uD83D\uDC80", // 💀 Skull
    "\uD83D\uDC81", // 💁 Information Desk Person
    "\uD83D\uDC82", // 💂 Guardsman
    "\uD83D\uDC83", // 💃 Dancer
    "\uD83D\uDC84", // 💄 Lipstick
    "\uD83D\uDC85", // 💅 Nail Polish
    "\uD83D\uDC86", // 💆 Person Getting Massage
    "\uD83D\uDC87", // 💇 Person Getting Haircut
    "\uD83D\uDC88", // 💈 Barber Pole
    "\uD83D\uDC89", // 💉 Syringe
    "\uD83D\uDC8A", // 💊 Pill
)