package mozi.iot4j;

/**
 * CoAP操作代码
*/
public class CoAPCode
 {
    private String _name = "", _description;

    private byte _category = 0, _detail = 0;
    /**
    * 分类
    */
    public int getCategory(){
        return _category;
    }
    /**
    * 明细
    */
    public byte getDetail() {
        return _detail;
    }

    /**
     * 代码名称
    */
    public String getName(){
        return _name;
    }
        /**
         * 标识符
        */
       //protected override String Tag
       //  {
       //  get { return ((byte)(_category << 5) + _detail).ToString(); }
       //  }

    /**
     *
    */
     public byte getPack() {
         return (byte)((_category << 5) | _detail);
     }

     public void setPack(byte pack) {
         _category = (byte)(pack >> 5);
         _detail = (byte)((pack << 3) >> 3);
     }

      CoAPCode(String name, String description, byte category, byte detail)
        {
            _name = name;
            _description = description;
            _category = category;
            _detail = detail;
        }
}
