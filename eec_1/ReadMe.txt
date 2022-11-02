
一.robust使用
    1.打包
    2.每次打包记得保存到/eec_1/robust 两个 build里面的重要文件 分别是 mapping.txt，methodMap.robust
    2.修改app的build.gradle 的 apply plugin: 'auto-patch-plugin'（隐藏不打补丁 打开开始打补丁）
    4.修改要更待的bug地方 （@Add注释是新增的方法）
    5.再次打包 抛异常正常 在outputs/robust/文件夹下会看到两个文件，patch.dex和patch.jar。
    6.将outputs/robust/patch.jar补丁包，push到手机对应的/sdcard/robust/patch_temp.jar上：
    7.成功