package com.mozicoder.iot4j;

import com.sun.jndi.dns.DnsClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * DNS查询客户端
 * @author Jason
 * @date 2021/12/29
 */
public class DNSResolver {

    public static String getDomainAddress(String domain){
        //TODO DNS查询未实现
        try {
            InetAddress address = InetAddress.getByName(domain);
            return address.getHostAddress();
        }catch(Exception ex){
            return "";
        }
    }
}
