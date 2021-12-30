package mozi.iot4j.utils;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import mozi.iot4j.DNSClient;

/**
 * 数值类型转bytes[]数组
 * @author Jason
 * @date 2021/12/30
 * 高位在前 低位在后
 */
public class ByteStreamUtil {

    public static Uint32 uint32FromBytes(byte[] data){
        Uint32 re=new Uint32(0);
        re.setValue(re.getValue()|(data[0]<<24));
        re.setValue(re.getValue()|(data[1]<<16));
        re.setValue(re.getValue()|(data[2]<<8));
        re.setValue(re.getValue()|(data[3]));
        return re;
    }
    /**
     */
    public static byte[] uint32ToBytes(Uint32 data){

        byte[] targets = new byte[4];
        targets[3] = (byte) (data.getValue() & 0xff);// 最低位
        targets[2] = (byte) ((data.getValue() >> 8) & 0xff);// 次低位
        targets[1] = (byte) ((data.getValue() >> 16) & 0xff);// 次高位
        targets[0] = (byte) (data.getValue() >>> 24);// 最高位,无符号右移。
        return targets;
    }

    public static char charFromBytes(byte[] data){
        char re=0;
        re|=data[0]<<8;
        re|=data[1];
        return re;
    }

    public static byte[] charToBytes(char data){
        byte[] targets = new byte[4];
        targets[1] = (byte) (data & 0xff);// 最低位
        targets[0] = (byte) ((data >>> 8) & 0xff);// 次低位
        return targets;
    }
}
/// <summary>
/// URI信息类 带DNS解析功能
/// </summary>
public class UriInfo
{
    public String Url;
    public String Protocol;
    public String Host;
    public String Domain;
    public int Port;
    public String Path;
    public String[] Paths;
    public String Query;
    public String[] Queries;

    public UriInfo()
    {
        Paths = new String[0];
        Queries = new String[0];
    }

    public static UriInfo Parse(String url)
    {
        UriInfo uri = new UriInfo();

        String address = "", sPort = "", path = "";

        String[] paths;
        boolean isDomain = false;

        Regex reg = new Regex("^[a-zA-Z]+://((([a-zA-Z0-9\\.-]+){2,})|(\\[?[a-zA-Z0-9\\.:]+){2,}\\]?)(:\\d+)?((/[a-zA-Z0-9-\\.%]+){0,}(\\?)?([%=a-zA-Z0-9]+(&)?){0,})$");
        Regex regProto = new Regex("[a-zA-Z]+(?=://)");
        Regex regHost = new Regex("(?<=\\://)(([a-zA-Z0-9-]+\\.?){2,}|(\\[?[a-zA-Z0-9-\\.:]+){2,}]?)(:\\d+)?");

        Regex regIPV4 = new Regex("^(\\d+\\.\\d+\\.\\d+\\.\\d+(?=:\\d+))|(\\d+\\.\\d+\\.\\d+\\.\\d+)$");
        Regex regIPV6 = new Regex("^((?<=\\[)(([a-zA-Z0-9]+(\\.|:)?){2,})(?=\\]))|([a-zA-Z0-9]+(\\.|:)?){2,}$");
        Regex regDomain = new Regex("^(([a-zA-Z0-9-]+(\\.)?){2,})|(([a-zA-Z0-9-]+(\\.)?){2,}(?=:\\d+))$");

        Regex regPath = new Regex("(?<=(://(([a-zA-Z0-9-]+\\.?){2,}|(\\[?[a-zA-Z0-9-\\.:]+){2,}]?)(:\\d+)?))(/[a-zA-Z0-9-\\.%]+){1,}((?=\\?))?");
        Regex regQuery = new Regex("(?<=\\?)([%=a-zA-Z0-9-]+(&)?){1,}");

        if (reg.IsMatch(url))
        {
            uri.Url = url;

            //分离协议类型
            uri.Protocol = regProto.Match(url).Value;

            //分离域名和端口
            address = regHost.Match(url).Value;

            //IPV4
            if (regIPV4.IsMatch(address))
            {
                uri.Host = regIPV4.Match(address).Value;
                sPort = address.Replace(uri.Host, "").Replace(":", "");
                //domain
            }
            else if (regDomain.IsMatch(address))
            {
                uri.Host = regDomain.Match(address).Value;
                uri.Domain = uri.Host;
                sPort = address.Replace(uri.Host, "").Replace(":", "");
                isDomain = true;
            }
            //IPV6
            else
            {
                uri.Host = regIPV6.Match(address).Value;

                sPort = address.Replace(uri.Host, "").Replace("[]:", "");
            }

            int port;
            if (!int.TryParse(sPort, out port))
            {
                uri.Port = 0;
            }

            if (isDomain)
            {
                uri.Host = DNSClient.GetDomainAddress(uri.Host);
            }

            //分离路径地址
            uri.Path = regPath.Match(url).Value;
            paths = uri.Path.Split(new char[] { '/' });

            if (paths.length > 0)
            {
                uri.Paths = new String[paths.length - 1];
                System.arraycopy(paths, 1, uri.Paths, 0, uri.Paths.length);
            }
            //分离查询参数
            uri.Query = regQuery.Match(url).Value;
            if (uri.Query.length > 0)
            {
                uri.Queries = uri.Query.Split(new char[] { '&' });
            }
        }
        else
        {
            throw new Exception($"URL格式不正确{url}");
        }
        return uri;
    }
}





