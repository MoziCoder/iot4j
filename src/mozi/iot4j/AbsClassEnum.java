package mozi.iot4j;

import java.lang.reflect.Field;

/**
 * 仿枚举 抽象类
 * @author Jason
 * @date 2021/12/29
*/
public abstract class AbsClassEnum
{
    protected abstract String getTag();
    /// <summary>
    /// 获取方法 不区分标识符大小写
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    public static AbsClassEnum get(String tag, Class cls)
    {
        //T t = Activator.CreateInstance<T>();
        AbsClassEnum rv=null;
        try {
            Field[] pis = cls.getDeclaredFields();
            for(Field info:pis)
            {
                info.setAccessible(true);
                //TODO 此处能否得到预期结果

                Object obj=info.get(null);
                if(null!=obj&&(obj.getClass().isInstance( cls.getClass()))){
                      if(((AbsClassEnum)obj).getTag().equals(tag)){
                          rv=(AbsClassEnum)obj;
                          return rv;
                      }
                }
            }
        }catch (Exception ex) {
            return null;
        }
        return rv;
    }

//    /// <summary>
//    /// 此处判断标识符是否相等,区分大小写
//    /// <para>
//    ///     如果要判断子对象是否等于<see cref="null"/>，请使用<see cref="object.Equals(object, object)"/>
//    /// </para>
//    /// </summary>
//    /// <param name="obj"></param>
//    /// <returns></returns>
//    public override bool Equals(object obj)
//{
//    return obj is AbsClassEnum && ((AbsClassEnum)obj).Tag.Equals(Tag);
//}
//    /// <summary>
//    /// 重载==
//    /// <para>
//    ///     如果要判断子对象是否等于<see cref="null"/>，请使用<see cref="object.Equals(object, object)"/>
//    /// </para>
//    /// </summary>
//    /// <param name="a"></param>
//    /// <param name="b"></param>
//    /// <returns></returns>
//    public static bool operator ==(AbsClassEnum a, AbsClassEnum b)
//    {
//        return (object)b != null && (object)a != null && a.Tag.Equals(b.Tag);
//    }
//
//    /// <summary>
//    /// 重载!=
//    /// <para>
//    ///     如果要判断子对象是否等于<see cref="null"/>，请使用<see cref="object.Equals(object, object)"/>
//    /// </para>
//    /// </summary>
//    /// <param name="a"></param>
//    /// <param name="b"></param>
//    /// <returns></returns>
//    public static bool operator !=(AbsClassEnum a, AbsClassEnum b)
//    {
//        return (object)a == null || (object)b == null || !a.Tag.Equals(b.Tag);
//    }
//
//    public override int GetHashCode()
//{
//    return Tag.GetHashCode();
//}
}
