package github.leavesczy.compose_chat.utils

import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2022/1/9 0:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
fun randomFaceUrl(): String {
    return if (Random.nextBoolean()) {
        faceUrlList.random()
    } else {
        randomOnlineImageUrl()
    }
}

private val rangeForRandom = (0..100000)

private fun randomOnlineImageUrl(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): String {
    return "https://picsum.photos/seed/$seed/$width/$height"
}

private val faceUrlList = listOf(
    "https://c-ssl.duitang.com/uploads/blog/202008/10/20200810073934_nntwx.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202006/01/20200601160249_AcRKF.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201908/10/20190810162429_szk8M.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202012/15/20201215084659_81632.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/item/201909/27/20190927010825_KHUck.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202102/15/20210215071709_3816f.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202107/16/20210716213740_fa1b9.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/item/202001/21/20200121093711_PF5zG.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202101/13/20210113111519_a66f7.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202107/19/20210719025556_0414d.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202002/08/20200208084153_nvhRY.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202005/17/20200517102703_hlmuv.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202107/04/20210704194338_f7613.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804235247_739b8.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804234736_c0132.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202105/21/20210521140708_76c83.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/item/202005/05/20200505064350_szajv.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202111/13/20211113174039_02fd1.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804235244_d1f29.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202003/12/20200312132646_tzVPE.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201908/12/20190812000052_uvtvy.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202105/10/20210510204719_37e80.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201911/26/20191126074916_ECsWi.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202111/15/20211115195445_2b112.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202104/25/20210425100448_8f5bc.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202003/10/20200310070514_novhl.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202108/10/20210810122547_82e93.jpg",
    "https://c-ssl.duitang.com/uploads/item/201910/20/20191020231818_mgyvc.jpg",
    "https://c-ssl.duitang.com/uploads/item/201910/20/20191020231819_fofwb.jpg",
    "https://c-ssl.duitang.com/uploads/item/201402/21/20140221180643_RzH8K.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202001/21/20200121093710_XeCcG.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202201/12/20220112083312_63f0a.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202008/10/20200810222529_unbol.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202004/03/20200403202140_lgjcu.jpg",
    "https://c-ssl.duitang.com/uploads/item/201706/10/20170610172654_ZfNyF.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202004/21/2020042131720_GZRkN.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201209/05/20120905111827_mwtRs.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202201/07/20220107124354_dfccb.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202111/20/20211120161523_67b57.jpg",
    "https://img1.doubanio.com/view/group_topic/l/public/p241325768.webp",
    "https://img2.doubanio.com/view/group_topic/l/public/p241325783.webp",
    "https://img9.doubanio.com/view/group_topic/l/public/p241325785.webp"
)