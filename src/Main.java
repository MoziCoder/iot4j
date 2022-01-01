import org.mozi.iot4j.CoAPClient;

public class Main{
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        CoAPClient client=new CoAPClient();
        try {
            client.get("coap://100.100.0.105/sensor/getinfo");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        System.out.println("2342342342");
    }
}