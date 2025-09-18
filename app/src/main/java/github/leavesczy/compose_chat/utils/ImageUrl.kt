package github.leavesczy.compose_chat.utils

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private var randomImageIndex = -1;

fun randomImage(): String {
    randomImageIndex++
    if (randomImageIndex < 0 || randomImageIndex >= imageUrlList.size) {
        randomImageIndex = 0
    }
    return imageUrlList[randomImageIndex]
}

private val imageUrlList = listOf(
    "https://p6-passport.byteacctimg.com/img/user-avatar/016451ccd2477ee0378c70ffe2cc3bb6~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/047e80823dc2ee0d4c4a9db7dfd7f47b~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/0b926d5295e0fbb32c8881f67257a4dc~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/0d0187c1a617b8af913d5ccd48d5fc15~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/0f6592ce213d91452e5b2bbcdf0b2c9c~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/121ca85166e2e7efdfe448627023bb92~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/1899be2d6caabdb28ce99016a537719f~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/19e1d2bafa3af312206d6eaed0539527~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/1d32d97b6b4344f0f85f1c484430a586~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/1f7832871df592b97a000c2b953fd8f7~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/2186c1344f7f44a7275c8b32931029b5~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/2df6d33cb79ef805b55880f0e33a162b~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/46ece0be144af53eb477cfbbcd1c81c3~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/4f55f3f82b34d13ff90438e23944465e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/4f90f4bced606d0a0a2352fac3b87383~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/51045de303b62d80be38b639e40266e2~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/6282246c3618433e96bc24cf13bb2141~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/65f5a08efbd23787da897c3e3f13b789~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/67c3d8f9939910f2422476eac85fe046~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/6f2daf285c265de008181d05cbe4769c~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/745f83f09494ef75bd46af38f5c9abf3~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/74ccf7f8cec4bc9a380f0c310453b20e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/7704dd2e184adf9c5ed2fa8e99afaaac~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/77f05605115f2f410147e137701657bb~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/78d4cdfab4c5a78b065dd0864d2f1cd9~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/895aa57161ffee930f39afb35fec77eb~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/8e4ec5490a74736bfdd6fb0ed66b7499~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/8fba7e9dbb3f8324db9555cb38fcfcfb~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/932c4ff99c89b4df9e53344951625885~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/979142fd737fce5bd0125175042e1b1e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/99b34a64642e38fd8e8b130e40956cd2~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/aad7ab823bdb48c3a09c2c33d5522f09~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/ad060a12e8ddf528f989e0f7855b88be~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/b68f2fe96b5139e2a8ce4e77642207b3~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/bec3afb275d080c11c5949e69009a508~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/c662597577d4e61daffc9aafc21606a4~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/cfc767f958a503f3af9509e969d7d4c6~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d17d45e388afa51ecd46bf89a54c7bff~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d2b419e422eed2470f7b41719a5333e1~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/d9b8ab8097610ea914625205baed82c7~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/db0d81c2a30425e1603b2cecb5e1867f~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/dc9cf7af0827068ce8d7b50f26981d78~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/e748afa580d168bf3dd1e3c2b95b03af~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f27962fcaa7317ca33be8804305d105d~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f624fa6dd541b9c35bd3b4c4e111e9cd~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f670fdbc94ce86c0bf8258651aae659e~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f6fdcbee7f3a96294bddef3b45b261e0~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f7bf2966a5756538a16fd9f68674775a~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/f89a37d9f3e402027e58cb4af04f8a50~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/fc36aab9103b32e5c2da4c79a0806c66~500x500.awebp",
    "https://p6-passport.byteacctimg.com/img/user-avatar/fd31816bea1e1369bbbf60ba4cc96483~500x500.awebp",
)