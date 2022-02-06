package com.mozicoder.iot4j.utils;

import com.mozicoder.iot4j.CoAPProtocol;
import com.mozicoder.iot4j.DNSClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URI信息类 带DNS解析功能
 * @author Jason
 * @date 2021/12/31
 */
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

    /**
     * 分解请求地址
     * @param url
     * @return
     */
    public static UriInfo Parse(String url) {
        UriInfo uri = new UriInfo();

        String address = "", sPort = "", path = "";

        String[] paths;
        boolean isDomain = false;

        Pattern reg = Pattern.compile("^[a-zA-Z]+://((([a-zA-Z0-9.-]+){2,})|(\\[?[a-zA-Z0-9.:]+){2,}]?)(:\\d+)?((/[a-zA-Z0-9-.%]+){0,}(\\?)?([%=a-zA-Z0-9]+(&)?)*)$",Pattern.DOTALL);

        Pattern regProto = Pattern.compile("[a-zA-Z]+(?=://)");
        Pattern regHost = Pattern.compile("(?<=://)(([a-zA-Z0-9-]+\\.?){2,}|(\\[?[a-zA-Z0-9-.:]+){2,}]?)(:\\d+)?");

        Pattern regIPV4 = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+(?=:\\d+))|(\\d+\\.\\d+\\.\\d+\\.\\d+)$");
        Pattern regIPV6 = Pattern.compile("^((?<=\\[)(([a-zA-Z0-9]+([.:])?){2,})(?=]))|([a-zA-Z0-9]+([.:])?){2,}$");
        Pattern regDomain = Pattern.compile("^(([a-zA-Z0-9-]+(\\.)?){2,})|(([a-zA-Z0-9-]+(\\.)?){2,}(?=:\\d+))$");

        Pattern regPath = Pattern.compile("(?<=://[a-zA-Z0-9.:]{0,1024})(/[a-zA-Z0-9-.%]{0,1024}){1,1024}((?=\\?))?");
        Pattern regQuery = Pattern.compile("(?<=\\?)([%=a-zA-Z0-9-]+(&)?){1,1024}");

        if (reg.matcher(url).find())
        {
            uri.Url = url;

            //分离协议类型
            Matcher mtProto=regProto.matcher(url);
            if(mtProto.find()) {
                uri.Protocol = mtProto.group();
            }
            //分离域名和端口
            Matcher mtHost=regHost.matcher(url);
            if(mtHost.find()) {
                address = mtHost.group();
            }
            //IPV4
            Matcher mtAddress=regIPV4.matcher(address);
            Matcher mtDomain=regDomain.matcher(address);
            Matcher mtIPV6=regIPV6.matcher(address);

            if (mtAddress.find())
            {
                uri.Host = mtAddress.group();
                sPort = address.replace(uri.Host, "").replace(":", "");
                //domain
            }
            else if (mtDomain.find())
            {
                uri.Host = mtDomain.group();
                uri.Domain = uri.Host;
                sPort = address.replace(uri.Host, "").replace(":", "");
                isDomain = true;
            }
            //IPV6
            else
            {
                uri.Host = mtIPV6.group();

                sPort = address.replace(uri.Host, "").replace("[]:", "");
            }

            int port;
            try {
                uri.Port = Integer.parseInt(sPort, 10);
            }catch(Exception ex){
                uri.Port= CoAPProtocol.Port;
            }
            if (isDomain)
            {
                uri.Host = DNSClient.getDomainAddress(uri.Host);
            }

            //分离路径地址
            Matcher mtPath=regPath.matcher(url);
            if(mtPath.find()) {
                uri.Path = mtPath.group();
                paths = uri.Path.split("/");
                if (paths.length > 0) {
                    uri.Paths = new String[paths.length - 1];
                    System.arraycopy(paths, 1, uri.Paths, 0, uri.Paths.length);
                }
            }
            //分离查询参数
            Matcher mtQuery= regQuery.matcher(url);
            if(mtQuery.find()) {
                uri.Query = mtQuery.group();
                if (uri.Query.length() > 0) {
                    uri.Queries = uri.Query.split("&");
                }
            }
        }
        else
        {
            try {
                throw new Exception(String.format("URL格式不正确%s",url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uri;
    }
}
