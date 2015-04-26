public class NewClass
{
    public static void main(String args[])
    {
    
    	String str1 = "AMITABH BACHCHAN";
    	String str2 = "RAJNIKANTH";
    		
        String compare="";

        for(int i=0;i<str1.length();i++)
        {
            String a="";
            if(i==str1.length()-1)
            {
                a=str1.substring(i);
            }
            else if(!compare.contains(str1.substring(i, i+1)))
            {
                a=str1.substring(i, i+1);
            }
            compare+=a;
        }
        for(int i=0;i<str2.length();i++)
        {
            String a="";
            if(i==str2.length()-1)
            {
                a=str2.substring(i);
            }
            else if(!compare.contains(str2.substring(i, i+1)))
            {
                a=str2.substring(i, i+1);
            }
            compare+=a;
        }
        System.out.println(compare);
    }
}