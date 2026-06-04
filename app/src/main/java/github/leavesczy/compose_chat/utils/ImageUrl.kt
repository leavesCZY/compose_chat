package github.leavesczy.compose_chat.utils

import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
private val imageUrlList = listOf(
    "https://p6-passport.byteacctimg.com/img/user-avatar/016451ccd2477ee0378c70ffe2cc3bb6~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/047e80823dc2ee0d4c4a9db7dfd7f47b~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/06d48f05b22d366e72d9e10134610cd7~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/0b926d5295e0fbb32c8881f67257a4dc~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/0f6592ce213d91452e5b2bbcdf0b2c9c~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/1899be2d6caabdb28ce99016a537719f~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/19e1d2bafa3af312206d6eaed0539527~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/1d32d97b6b4344f0f85f1c484430a586~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/2186c1344f7f44a7275c8b32931029b5~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/2b6e1a6b01a259f1912055ed62c8ebe0~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/2df6d33cb79ef805b55880f0e33a162b~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/335727a1f67e82416e203e9516f61273~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/3408593da304a9996d36878b60179047~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/46ece0be144af53eb477cfbbcd1c81c3~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/5569c2276ef448736bde1221ea5fc846~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/5fbeb4bde064f61e5d9a63386041ad01~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/6282246c3618433e96bc24cf13bb2141~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/62e7ebab4c6c4546492a231a1619ce2c~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/65f5a08efbd23787da897c3e3f13b789~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/67c3d8f9939910f2422476eac85fe046~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/699b1b248f339205c19db315b8b6433a~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/745f83f09494ef75bd46af38f5c9abf3~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/74ccf7f8cec4bc9a380f0c310453b20e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/7704dd2e184adf9c5ed2fa8e99afaaac~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/77f05605115f2f410147e137701657bb~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/7e4a5eff57b87e792937a809893bdc2a~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/856648ca107557590741bc2da290934b~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/8e4ec5490a74736bfdd6fb0ed66b7499~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/8fba7e9dbb3f8324db9555cb38fcfcfb~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/895aa57161ffee930f39afb35fec77eb~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/91ee42f8b857afc5312cc4787674a28f~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/979142fd737fce5bd0125175042e1b1e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/99b34a64642e38fd8e8b130e40956cd2~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/aad7ab823bdb48c3a09c2c33d5522f09~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/ad060a12e8ddf528f989e0f7855b88be~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/b68f2fe96b5139e2a8ce4e77642207b3~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/b760d539bc53128f1273a7eaf2157bf4~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/bec3afb275d080c11c5949e69009a508~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/c4bf14479f676f9781005b05e5a9d558~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/c662597577d4e61daffc9aafc21606a4~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/cfc767f958a503f3af9509e969d7d4c6~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d17d45e388afa51ecd46bf89a54c7bff~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d3d047634a3f84a1c8d1771032b71dc0~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d899f094d02735e38ac1258af127982c~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d9b8ab8097610ea914625205baed82c7~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/db0d81c2a30425e1603b2cecb5e1867f~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/de79f7b6c1bfc6a58812c51d6cdd825e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f27962fcaa7317ca33be8804305d105d~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f6fdcbee7f3a96294bddef3b45b261e0~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f7bf2966a5756538a16fd9f68674775a~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f89a37d9f3e402027e58cb4af04f8a50~500x500.awebp"
)

private var randomImageIndex = Random.nextInt(from = 0, until = imageUrlList.size)

fun randomImage(): String {
    var index = randomImageIndex++
    if (index < 0 || index >= imageUrlList.size) {
        index = 0
        randomImageIndex = 0
    }
    return imageUrlList[index]
}