package mozi.iot4j.utils;

import mozi.iot4j.DNSClient;

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

    public static UriInfo Parse(String url) throws Exception {
        UriInfo uri = new UriInfo();

        String address = "", sPort = "", path = "";

        String[] paths;
        boolean isDomain = false;

        Pattern reg = Pattern.compile("^[a-zA-Z]+://((([a-zA-Z0-9\\.-]+){2,})|(\\[?[a-zA-Z0-9\\.:]+){2,}\\]?)(:\\d+)?((/[a-zA-Z0-9-\\.%]+){0,}(\\?)?([%=a-zA-Z0-9]+(&)?){0,})$");
        Pattern regProto = Pattern.compile("[a-zA-Z]+(?=://)");
        Pattern regHost = Pattern.compile("(?<=\\://)(([a-zA-Z0-9-]+\\.?){2,}|(\\[?[a-zA-Z0-9-\\.:]+){2,}]?)(:\\d+)?");

        Pattern regIPV4 = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+(?=:\\d+))|(\\d+\\.\\d+\\.\\d+\\.\\d+)$");
        Pattern regIPV6 = Pattern.compile("^((?<=\\[)(([a-zA-Z0-9]+(\\.|:)?){2,})(?=\\]))|([a-zA-Z0-9]+(\\.|:)?){2,}$");
        Pattern regDomain = Pattern.compile("^(([a-zA-Z0-9-]+(\\.)?){2,})|(([a-zA-Z0-9-]+(\\.)?){2,}(?=:\\d+))$");

        Pattern regPath = Pattern.compile("(?<=(://(([a-zA-Z0-9-]{1,}\\.?){2,}|(\\[?[a-zA-Z0-9-\\.:]+){2,}]?)(:\\d+)?))(/[a-zA-Z0-9-\\.%]+){1,}((?=\\?))?");
        Pattern regQuery = Pattern.compile("(?<=\\?)([%=a-zA-Z0-9-]+(&)?){1,}");

        if (reg.matcher(url).matches())
        {
            uri.Url = url;

            //分离协议类型
            uri.Protocol = regProto.matcher(url).group();

            //分离域名和端口
            address = regHost.matcher(url).group();

            //IPV4
            if (regIPV4.matcher(address).matches())
            {
                uri.Host = regIPV4.matcher(address).group();
                sPort = address.replace(uri.Host, "").replace(":", "");
                //domain
            }
            else if (regDomain.matcher(address).matches())
            {
                uri.Host = regDomain.matcher(address).group();
                uri.Domain = uri.Host;
                sPort = address.replace(uri.Host, "").replace(":", "");
                isDomain = true;
            }
            //IPV6
            else
            {
                uri.Host = regIPV6.matcher(address).group();

                sPort = address.replace(uri.Host, "").replace("[]:", "");
            }

            int port;
            uri.Port=Integer.parseInt(sPort,10);

            if (isDomain)
            {
                uri.Host = DNSClient.getDomainAddress(uri.Host);
            }

            //分离路径地址
            uri.Path = regPath.matcher(url).group();
            paths = uri.Path.split( "/" );

            if (paths.length > 0)
            {
                uri.Paths = new String[paths.length - 1];
                System.arraycopy(paths, 1, uri.Paths, 0, uri.Paths.length);
            }
            //分离查询参数
            uri.Query = regQuery.matcher(url).group();
            if (uri.Query.length() > 0)
            {
                uri.Queries = uri.Query.split("&");
            }
        }
        else
        {
            throw new Exception(String.format("URL格式不正确%s",url));
        }
        return uri;
    }
}
