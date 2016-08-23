
package in.entrylog.entrylog.util;

/**
 * ArrayUitl
 * deal with byte array ,int ,hexStrin
 * @author EKEMP-Sen
 *
 */
public class ArrayUtil {

	/**
	   *  converted int to byte[]
	   *  low bytes in the former, high byte in the after
	   * 将int转为低字节在前，高字节在后的byte数组  
	   * @param n int
	   * @return byte[]
	   */ 
	 public static byte[] IntToLBytes(int n) {  
		   byte[] b = new byte[4];  
		   b[0] = (byte) (n & 0xff );  
		   b[1] = (byte) (n >> 8 & 0xff );  
		   b[2] = (byte) (n >> 16 & 0xff );  
		   b[3] = (byte) (n >> 24 & 0xff );  
		   return b;  
		}
	 
	 
	 
	 public static byte[] ShortToBytes(short n){
		 byte[] b = new byte[2];
		  b[0] = (byte) (n & 0xff );  
		  b[1] = (byte) (n >> 8 & 0xff );  
		  return b;
	 }
	 

	  /** 
	   * Converts the low byte byte array to int
	   * @param b byte[] 
	   * @return int 
	   */  
     public static int BytesToInt(byte[] b) { 
    	 int s = 0 ; 
    	 for (int i = 0; i < 3 ; i++) { 
    	  if (b[3 -i] >= 0 ) { 
    	    s = s + b[3 -i]; 
    	  } 
    	  else { 
    	    s = s + 256 + b[ 3 -i]; 
    	  } 
    	  s = s * 256 ; 
    	 } 
    	 if (b[ 0 ] >= 0 ) { 
    	   s = s + b[0 ]; 
    	 } else { 
    	   s = s + 256 + b[ 0 ]; 
    	 } 
    	 return s; 
    	}
     
     /**
      * int array to byte, if a byte>127 ,it will show a negative value
      * @param in
      * @return
      */
     public static byte[] intArrayToByte(int[] in){
     	byte[] ret=new byte[in.length];
     	for(int i=0;i<in.length;i++){
     		if(in[i]>127){
     			ret[i]=(byte)(in[i]-256);
     		}else{
     			ret[i]=(byte)in[i];
     		}
     			
     	}
     	
     	return ret;
     }
     
     
     

	 
     /**
      * merge the second byte array after to the first byte array
      * @param First
      * @param Second
      * @return
      */
     public static byte[] MergerArray(byte[] First, byte[] Second) {
    	 byte[] result = new byte[First.length+Second.length];  
        System.arraycopy(First, 0, result, 0, First.length);  
        System.arraycopy(Second, 0, result, First.length, Second.length);  
        return result;  
     }
     
     /**
      * append a byte to the byte array
      * @param Source
      * @param n
      * @return
      */
     public static byte[] MergerArray(byte[] Source, byte n) {
    	 byte[] result = new byte[Source.length+1];  
         System.arraycopy(Source, 0, result, 0, Source.length);  
         result[Source.length] = n;  
         return result;  
     }
	 
     /**
      * intercept a new byte array from a old byte array
      * from StartIndex to StartIndex+cCount
      * @param Source
      * @param StartIndex
      * @param Count
      * @return
      */
     public static byte[] SubArray(byte[] Source, int StartIndex, int Count) {
    	 byte[] result = new byte[Count];
    	 try
         {
             for (int i = 0; i < Count; i++) {
            	 result[i] = Source[i + StartIndex];
             }
             return result;
         }
    	 catch (Exception e) {
				e.printStackTrace();
    	 }
    	 return new byte[]{0};
     }
     
     /**
      * deal with the byte[] to a new length
      * if byte[] less than new length fill 0 in the front, beyond the new length will remove the front part
      * 处理数组Source的长度为BytesLen，不足长度的前面补0, 超出长度的前面部分去掉
      * @param Source
      * @param BytesLen
      * @return
      */
     public static byte[] DealWithArray(byte[] Source, byte BytesLen) {
    	 byte[] result;
         if (Source.length > BytesLen)
         {
             result = new byte[BytesLen];
             for (int i = 0; i < BytesLen; i++) 
            	result[i] = Source[i + Source.length - BytesLen];
             return result;
         }
         else
         {
             result = new byte[BytesLen];
             for (int i = 0; i < BytesLen; i++) result[i] = 0;
           //要拷贝的数组，拷贝开始位置，目的数组，目的数组的 位置，要持拷贝的长度
             System.arraycopy(Source, 0, result, BytesLen - Source.length, Source.length); 
             return result;
         } 
     }
     
     /**
      * Convert byte[] to hex string. 
      * @param src byte[] data
      * @return hex string
      */   
     public static String BytesToString(byte[] src) {  
    	 StringBuilder stringBuilder = new StringBuilder("");
         if (src == null || src.length <= 0) {
             return null;
         }
         for (int i = 0; i < src.length; i++) {
         	String hv="";
         	if (src[i]<0) hv = Integer.toHexString(src[i]+256);
         	else hv = Integer.toHexString(src[i]);
             if (hv.length() < 2) {
             	hv = "0" + hv;
             }
             stringBuilder.append("0x"+hv+" ");
         }
         return stringBuilder.toString();
     }
     
     /** 
      * convert string to byte[]
      */  
    public static byte[] stringToBytes(String s, int length) {  
      while (s.getBytes().length < length) {  
        s += " ";  
      }  
      return s.getBytes();  
    } 
    
    public static byte[] intsToBytes(int[] data){
    	byte[] tmp = new byte[data.length];
    	for(int i=0;i<data.length;i++)
    		tmp[i] = (byte)data[i];
    	
    	return tmp;
    }
    

    

    

}

