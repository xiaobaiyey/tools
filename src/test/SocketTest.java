package test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class SocketTest {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress inetAddress=InetAddress.getLocalHost();
        System.out.println("本机名：" + inetAddress.getHostName());
        System.out.println("IP地址：" + inetAddress.getHostAddress());
        byte[] bytes = inetAddress.getAddress();
        System.out.println("字节数组形式的IP地址：" + Arrays.toString(bytes));
        System.out.println("直接输出InetAddress对象：" + inetAddress);
    }


}
