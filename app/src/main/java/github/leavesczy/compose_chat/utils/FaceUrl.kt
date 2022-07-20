package github.leavesczy.compose_chat.utils

/**
 * @Author: leavesCZY
 * @Date: 2022/1/9 0:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
fun randomFaceUrl(): String {
    return faceUrlList.random()
}

private val faceUrlList = listOf(
    "https://c-ssl.dtstatic.com/uploads/blog/202202/03/20220203231744_d0ac5.thumb.1000_0.gif",
    "https://c-ssl.dtstatic.com/uploads/blog/202204/11/20220411232905_d0985.thumb.1000_0.jpg",
    "https://p9-passport.byteacctimg.com/img/user-avatar/584c9fad4b0edfb0a283361b8bc32c3e~300x300.image",
    "https://c-ssl.dtstatic.com/uploads/blog/202203/23/20220323012720_f9ddc.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/item/201910/20/20191020231818_mgyvc.jpg",
    "https://c-ssl.duitang.com/uploads/item/202001/21/20200121093711_PF5zG.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202203/23/20220323012719_1d917.thumb.1000_0.jpg",
    "https://c-ssl.dtstatic.com/uploads/blog/202204/01/20220401201009_4afe1.thumb.1000_0.gif",
    "https://p26-passport.byteacctimg.com/img/user-avatar/6019f80db5be42d33c31c98adaf3fa8c~300x300.image",
    "https://c-ssl.duitang.com/uploads/item/202003/10/20200310070514_novhl.jpg",
    "https://c-ssl.dtstatic.com/uploads/blog/202201/28/20220128154108_4fcb6.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202112/04/20211204121832_2ef64.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202008/10/20200810073934_nntwx.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201910/20/20191020231819_fofwb.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202201/07/20220107124354_dfccb.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202202/21/20220221122933_70648.thumb.1000_0.jpeg",
    "https://p3-passport.byteacctimg.com/img/user-avatar/a0383600d66ccc81b3396b75cf3a95ea~300x300.image",
    "https://c-ssl.duitang.com/uploads/item/202006/01/20200601160249_AcRKF.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202101/13/20210113111519_a66f7.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202002/08/20200208084153_nvhRY.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804235247_739b8.thumb.1000_0.jpeg",
    "https://p9-passport.byteacctimg.com/img/user-avatar/d939b6f587216b2265a8fa1ec1beb863~300x300.image",
    "https://c-ssl.duitang.com/uploads/blog/202108/04/20210804234736_c0132.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202112/04/20211204121831_00080.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202203/11/20220311203255_5e928.thumb.1000_0.jpeg",
    "https://p6-passport.byteacctimg.com/img/mosaic-legacy/3793/3114521287~300x300.image",
    "https://c-ssl.dtstatic.com/uploads/blog/202203/25/20220325162227_04c73.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/item/202003/24/20200324010958_XASxG.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202102/15/20210215071709_3816f.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202011/15/20201115135710_94522.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202202/14/20220214233734_ad971.thumb.1000_0.jpg",
    "https://c-ssl.dtstatic.com/uploads/blog/202011/23/20201123120657_32a01.thumb.1000_0.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202201/27/20220127222112_b24cf.thumb.1000_0.jpg",
    "https://c-ssl.dtstatic.com/uploads/blog/202107/19/20210719005117_e5194.thumb.1000_0.jpg",
    "https://c-ssl.duitang.com/uploads/blog/202111/20/20211120161523_67b57.jpg",
    "https://c-ssl.dtstatic.com/uploads/blog/202012/08/20201208195403_ab738.thumb.1000_0.jpg",
    "https://c-ssl.dtstatic.com/uploads/item/202004/25/20200425145945_WkiiX.thumb.1000_0.jpeg",
    "https://p3-passport.byteacctimg.com/img/mosaic-legacy/3791/5035712059~300x300.image",
    "https://c-ssl.dtstatic.com/uploads/blog/202201/22/20220122135414_aef94.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/202001/21/20200121093710_XeCcG.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202108/05/20210805213040_c302d.thumb.1000_0.jpg",
    "https://c-ssl.dtstatic.com/uploads/blog/202109/05/20210905133229_fb258.thumb.1000_0.jpeg",
    "https://c-ssl.duitang.com/uploads/item/201402/21/20140221180643_RzH8K.jpeg",
    "https://c-ssl.duitang.com/uploads/blog/202008/10/20200810222529_unbol.jpeg",
    "https://c-ssl.dtstatic.com/uploads/blog/202201/17/20220117175109_6112f.thumb.1000_0.jpg",
    "https://p9-passport.byteacctimg.com/img/user-avatar/3f2faa2df28bc891b2723b1cee19a136~300x300.image",
    "https://img9.doubanio.com/view/group_topic/l/public/p241325785.webp",
)