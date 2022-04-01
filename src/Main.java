import com.mozicoder.iot4j.*;
import com.mozicoder.iot4j.event.MessageTransmitEvent;
import com.mozicoder.iot4j.optionvalues.BlockOptionValue;
import com.mozicoder.iot4j.utils.Uint32;
import java.io.IOException;
import java.util.ArrayList;

public class Main{
    /**
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        CoAPClient client=new CoAPClient();
        //绑定本地端口
        client.start(32325);
        //数据侦听回调
        client.setResponseListener(new MessageTransmitEvent() {
            @Override
            public void onTransmit(String host, int port, CoAPPackage cp) {
                //这里处理包的数据，加入业务逻辑
                for (CoAPOption op:cp.getOptions()){
                    if(op.getOption()== CoAPOptionDefine.Block1||op.getOption()==CoAPOptionDefine.Block2){
                        op.setValue(new BlockOptionValue(){}.setPack(op.getValue().getPack()));
                    }
                }
            }
        });

        //发起请求
        try {
            ArrayList<CoAPOption> opts=new ArrayList<>();
            BlockOptionValue bv=new BlockOptionValue();
            bv.setNum(new Uint32(10485777));
            bv.setMoreFlag(false);
            bv.setSize((char)128);
            CoAPOption cp=new CoAPOption();
            cp.setValue(bv);
            cp.setOption(CoAPOptionDefine.Block2);
            opts.add(cp);
            client.get("coap://coap.me/.well-known/core", CoAPMessageType.Confirmable,opts);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        System.in.read();
    }
}