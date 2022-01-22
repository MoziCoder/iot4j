import org.mozi.iot4j.CoAPClient;
import org.mozi.iot4j.CoAPPackage;
import org.mozi.iot4j.event.ResponseEvent;

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
            client.get("coap://100.100.0.105/sensor/getinfo");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        System.out.println("2342342342");
    }
}