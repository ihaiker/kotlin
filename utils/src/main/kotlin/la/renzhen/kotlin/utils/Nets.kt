package la.renzhen.kotlin.utils

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 05/09/2018 5:26 PM
 */

import lombok.extern.slf4j.Slf4j
import java.io.IOException
import java.net.*
import java.util.*
import java.util.regex.Pattern

/**
 * 网络工具类
 */
object Nets {

    val LOCALHOST = "127.0.0.1"
    val ANY_HOST = "0.0.0.0"

    //随机端口结束位置
    private val RND_PORT_START = 1000
    //随机端口开始位置
    private val RND_PORT_RANGE = Character.MAX_VALUE.toInt() - RND_PORT_START

    private val MIN_PORT = RND_PORT_START
    private val MAX_PORT = RND_PORT_RANGE + RND_PORT_START

    private val IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$")
    private val IP_HOST_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$")


    @Volatile
    private var LOCAL_ADDRESS_CACHE: InetAddress? = null

    /**
     * 获取本地地址
     *
     * @return 本机地址（如果存在多块网卡给出第一个网卡的地址）
     */
    val localHost: String
        get() = getLocalHost()

    val localAddress: InetAddress?
        get() = getLocalAddress()

    private val hostNameCache = HashMap<String, String>()

    private val hostNameForLinux: String
        get() {
            try {
                return InetAddress.getLocalHost().hostName
            } catch (uhe: UnknownHostException) {
                val host = uhe.message
                if (host != null) {
                    val colon = host.indexOf(':')
                    if (colon > 0) {
                        return host.substring(0, colon)
                    }
                }
                return "UnknownHost"
            }

        }

    private val hostNameForWindow: String
        get() = if (System.getenv("COMPUTERNAME") != null) {
            System.getenv("COMPUTERNAME")
        } else {
            hostNameForLinux
        }

    /**
     * 获取一个随机可用的端口<br></br>
     * 次随机端口在10000~65535之间
     *
     * @return 端口号
     */
    fun randomAvailablePort(): Int {
        var ss: ServerSocket? = null
        try {
            ss = ServerSocket()
            ss.bind(null)
            return ss.localPort
        } catch (e: IOException) {
            return 0
        } finally {
            if (ss != null) {
                try {
                    ss.close()
                } catch (e: IOException) {
                }

            }
        }
    }

    /**
     * 随机获取一个端口，并且此端口开始于port
     *
     * @param port 端口开始位置
     * @return 随机端口
     */
    fun getAvailablePort(port: Int): Int {
        if (!isValidPort(port)) {
            return randomAvailablePort()
        }
        for (i in port until MAX_PORT) {
            var ss: ServerSocket? = null
            try {
                ss = ServerSocket(i)
                return i
            } catch (e: IOException) {
                // continue
            } finally {
                if (ss != null) {
                    try {
                        ss.close()
                    } catch (e: IOException) {
                    }

                }
            }
        }
        return 0
    }

    /**
     * 检测是否是合理的端口
     *
     * @param port 判断的端口
     * @return 是否可用
     */
    fun isValidPort(port: Int): Boolean {
        return port > MIN_PORT && port <= MAX_PORT
    }

    /**
     * 检测是否是一个可用户IP:PORT书写方式
     *
     * @param address 是否地址可用
     * @return T/F
     */
    fun isValidAddress(address: String): Boolean {
        return IP_HOST_PATTERN.matcher(address).matches()
    }

    /**
     * 检查是否是本地IP
     *
     * @param host IP地址
     * @return T/F
     */
    fun isLocalHost(host: String?): Boolean {
        return host != null && (LOCALHOST == host || host.equals("localhost", ignoreCase = true))
    }

    /**
     * 是否并绑定到任何IP
     *
     * @param host address
     * @return T/F
     */
    fun isAnyHost(host: String): Boolean {
        return ANY_HOST == host
    }

    /**
     * 检查给定地址是否是无效的可访问地址
     *
     * @param host 地址
     * @return true
     */
    fun isInvalidHost(host: String?): Boolean {
        return (host == null
                || host.length == 0
                || host.equals("localhost", ignoreCase = true)
                || host == "0.0.0.0"
                || !IP_PATTERN.matcher(host).matches())
    }

    /**
     * 检查是否是一个有效的可访问地址
     *
     * @param host host地址
     * @return true/false
     */
    fun isValidHost(host: String): Boolean {
        return !isInvalidHost(host)
    }

    fun getLocalSocketAddress(host: String, port: Int): InetSocketAddress {
        return if (isAnyHost(host))
            InetSocketAddress(port)
        else
            InetSocketAddress(host, port)
    }

    /**
     * 是否是一个可用的外部地址
     *
     * @param address 地址
     * @return T/F
     */
    private fun isValidAddress(address: InetAddress?): Boolean {
        if (address == null || address.isLoopbackAddress)
            return false

        val name = address.hostAddress
        return (name != null
                && ANY_HOST != name
                && LOCALHOST != name
                && IP_PATTERN.matcher(name).matches())
    }

    /**
     * @param prefix 本地地址开始段
     * @return ip地址
     */
    fun getLocalHost(prefix: String = ""): String {
        val address = getLocalAddress(prefix)
        return if (address == null) LOCALHOST else address.hostAddress
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @param prefix 前段
     * @return 本地网卡IP
     */
    fun getLocalAddress(prefix: String = ""): InetAddress? {
        if (LOCAL_ADDRESS_CACHE != null)
            return LOCAL_ADDRESS_CACHE

        val localAddress = getLocalAddress0(prefix)
        LOCAL_ADDRESS_CACHE = localAddress
        return localAddress
    }

    /**
     * 是否是真是网卡
     *
     * @param address 地址
     * @return T/F
     */
    private fun isRealNet(address: InetAddress): Boolean {
        val link = address.isLinkLocalAddress
        val loop = address.isLoopbackAddress
        val site = address.isSiteLocalAddress
        return !link and !loop and site
    }

    private fun isPrefix(address: InetAddress, prefix: String?): Boolean {
        if (prefix == null || "" == prefix.trim { it <= ' ' }) {
            return true
        }
        val items = prefix.trim { it <= ' ' }.split("[;,\\|]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in items) {
            if (address.hostAddress.startsWith(item)) {
                return true
            }
        }
        return false
    }

    private fun getLocalAddress0(prefix: String = ""): InetAddress? {
        var localAddress: InetAddress? = null
        try {
            localAddress = InetAddress.getLocalHost()
            localAddress = localAddress?.takeIf {
                isRealNet(it) && isValidAddress(it) && isPrefix(it, prefix)
            }
            if (localAddress != null) {
                return localAddress
            }
        } catch (e: Throwable) {

        }

        try {
            val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                try {
                    val network = interfaces.nextElement()
                    val name = network.name
                    if (name.startsWith("docker") // docker 虚拟网络
                            || name.startsWith("w")) {//虚拟网络
                        continue
                    }
                    if (network.isVirtual || network.isLoopback || !network.isUp) {
                        continue
                    }
                    val addresses = network.inetAddresses
                    if (addresses != null) {
                        while (addresses.hasMoreElements()) {
                            try {
                                val address = addresses.nextElement()
                                if (isRealNet(address) && isValidAddress(address) && isPrefix(address, prefix)) {
                                    return address
                                }
                            } catch (e: Throwable) {
                            }

                        }
                    }
                } catch (e: Throwable) {
                }

            }
        } catch (e: Throwable) {
        }
        return localAddress
    }

    /**
     * @param hostName hostname
     * @return ip address or hostName if UnknownHostException
     */
    fun getIpByHost(hostName: String): String {
        try {
            return InetAddress.getByName(hostName).hostAddress
        } catch (e: UnknownHostException) {
            return hostName
        }

    }


    fun hostname(): String {
        return if (System.getProperty("os.name").indexOf("Windows") != -1) {
            hostNameForWindow
        } else hostNameForLinux
    }

    fun toAddressString(address: InetSocketAddress): String {
        return address.address.hostAddress + ":" + address.port
    }

    fun toAddress(address: String): InetSocketAddress {
        val i = address.indexOf(':')
        val host: String
        val port: Int
        if (i > -1) {
            host = address.substring(0, i)
            port = Integer.parseInt(address.substring(i + 1))
        } else {
            host = address
            port = 0
        }
        return InetSocketAddress(host, port)
    }

    fun toURL(protocol: String, host: String, port: Int, path: String): String {
        val sb = StringBuilder()
        sb.append(protocol).append("://")
        sb.append(host).append(':').append(port)
        if (path[0] != '/')
            sb.append('/')
        sb.append(path)
        return sb.toString()
    }

}