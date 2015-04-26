/*
I KNOW THIS DOESN'T FEEL RIGHT BUT FULFILLS THE CRITERIA
*/

public class Practice{
    public static void main(String[] args) {
    String s="";
        for(int i=1;i<10;i++)
        {
        
            if(i<6)
            {
                s+=i;
            }
            else
            {
                s=s.substring(0, s.length()-1);
            }
            System.out.println(s);
        }
    
    }
}