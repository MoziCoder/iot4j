# iot4j

iot4j 是Java版的IoT物联网客户端标准通讯组件包，即IoT客户端包。该项目是Mozi.IoT.Client的Java版客户端实现，项目功能与Mozi.IoT保持同步。由于作者的技术偏好为.Net向，故而会优先开发.Net版本，然后再开发Java版,Java的版本更迭相对会出现一点滞后。

## [Mozi.IoT][mozinetwork]物联网项目介绍

Mozi.IoT是一个物联网标准通讯组件(CoAP协议)，包含网关服务器和客户端。该项目并不是一个应用框架,而是CoAP的标准通协议实现，项目目前是Mozi.Network的子项目，有可能发展成为一个独立的项目。精力有限，当前仅实现和充实CoAP相关功能。

## CoAP协议简介
CoAP（Constrained Application Protocol）受限应用协议，是一种在物联网世界的类web协议，RFC规范定义为RFC7252,7959,8613,8974。该协议使用在资源受限的物联网设备上。物联网设备的ram，rom都通常非常小，不能运行消耗资源比较多的TCP和HTTP。

## 功能模块
### Java

### [iot4j][iot4j]
    iot4j是IoT(CoAP)的Java客户端实现

## CoAP协议实现完成度
	实现了RFC7252中约定的大部分功能，目前可以应对基本的CoAP协议调试。
 
## 总体规划

预期实现如下功能模块：

1. IoT服务端/网关 
2. .Net客户端调用类库
3. .Java客户端调用包
4. C/C++可移植客户端项目

## 待完成的功能
- 拥塞算法
- 安全认证
- 消息缓存
- 分块传输 RFC 7959
- 对象安全

## 版权说明
	本项目采用MIT开源协议，复制请注明出处，引用请附带证书。欢迎复制，引用和修改。意见建议疑问请联系软件作者，或提交ISSUE。

## 用例说明
    项目集成如果遇到困难可以联系软件作者
    
### Java

~~~Java

import com.mozicoder.iot4j.CoAPClient;

public class Main{

    /**
     *
     * @param args
     */
    public static void main(String[] args){

        CoAPClient client=new CoAPClient();
        //绑定本地端口
        client.start(12345);
        //数据侦听回调
        client.setResponseListener(new ResponseEvent() {
            @Override
            public void onResponse(String host,int port,CoAPPackage cp) {
                //这里处理包的数据，加入业务逻辑
            }
        });

        //发起请求
        try {
            //127.0.0.1为CoAP服务器地址，调试时请修改为对应的服务器地址
            client.get("coap://127.0.0.1/sensor/getinfo");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }
}

~~~

### By [Jason][1] on Dec. 5,2021

[1]:mailto:brotherqian@163.com
[iot4j]:https://gitee.com/myui/mozi.iot4j
[mozinetwork]:https://gitee.com/myui_admin/mozi