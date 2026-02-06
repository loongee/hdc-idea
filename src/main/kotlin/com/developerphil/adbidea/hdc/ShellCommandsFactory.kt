package com.developerphil.adbidea.hdc

/**
 * HDC Shell 命令工厂
 * 生成鸿蒙系统的各种 shell 命令
 */
object ShellCommandsFactory {

    /**
     * 启动 Ability
     * 鸿蒙使用 aa (Ability Assistant) 命令
     *
     * @param bundleName 包名 (Bundle Name)
     * @param abilityName Ability 名称
     * @param attachDebugger 是否附加调试器
     * @return shell 命令字符串
     */
    @JvmStatic
    fun startAbility(bundleName: String, abilityName: String, attachDebugger: Boolean): String {
        val debugFlag = if (attachDebugger) "-D " else ""
        return "aa start ${debugFlag}-b $bundleName -a $abilityName"
    }

    /**
     * 强制停止应用
     * 鸿蒙使用 aa force-stop 命令
     *
     * @param bundleName 包名
     * @return shell 命令字符串
     */
    @JvmStatic
    fun forceStop(bundleName: String): String {
        return "aa force-stop $bundleName"
    }

    /**
     * 清除应用数据
     * 鸿蒙使用 bm clean 命令
     *
     * @param bundleName 包名
     * @param clearCache 是否清除缓存 (-c)
     * @param clearData 是否清除数据 (-d)
     * @return shell 命令字符串
     */
    @JvmStatic
    fun clearAppData(bundleName: String, clearCache: Boolean = false, clearData: Boolean = true): String {
        val flags = buildString {
            if (clearCache) append("-c ")
            if (clearData) append("-d ")
        }.trim()
        return "bm clean -n $bundleName $flags".trim()
    }

    /**
     * 获取包信息
     * 鸿蒙使用 bm dump 命令
     *
     * @param bundleName 包名
     * @return shell 命令字符串
     */
    @JvmStatic
    fun dumpPackageInfo(bundleName: String): String {
        return "bm dump -n $bundleName"
    }

    /**
     * 列出所有已安装的包
     *
     * @return shell 命令字符串
     */
    @JvmStatic
    fun listPackages(): String {
        return "bm dump -a"
    }

    /**
     * 授予权限
     * 鸿蒙使用 bm grant 命令
     *
     * @param bundleName 包名
     * @param permission 权限名
     * @return shell 命令字符串
     */
    @JvmStatic
    fun grantPermission(bundleName: String, permission: String): String {
        return "bm grant -n $bundleName -p $permission"
    }

    /**
     * 撤销权限
     * 鸿蒙使用 bm revoke 命令
     *
     * @param bundleName 包名
     * @param permission 权限名
     * @return shell 命令字符串
     */
    @JvmStatic
    fun revokePermission(bundleName: String, permission: String): String {
        return "bm revoke -n $bundleName -p $permission"
    }

    /**
     * 获取设备信息
     *
     * @return shell 命令字符串
     */
    @JvmStatic
    fun getDeviceInfo(): String {
        return "param get const.product.model"
    }

    /**
     * 获取系统版本
     *
     * @return shell 命令字符串
     */
    @JvmStatic
    fun getSystemVersion(): String {
        return "param get const.ohos.apiversion"
    }
}
