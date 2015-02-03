/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;
import static javafx.scene.input.KeyCode.N;
/**
 *
 * @author Shivanu
 */
public class tor_client {
    
    
    
    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br ;
    Socket client;
    
    String directory;
    String listdir[]=new String[9]; 
        
    tor_client()throws IOException{
    //connect to directorey
    client=new Socket("192.168.0.104",9090);
    dout=new DataOutputStream(client.getOutputStream());
    dout.writeUTF("1");
    }
    
    
public static void main(String args[])throws IOException
    {
        tor_client object=new tor_client();
        String dir=object.dir_data();
        object.splitstring(dir);
   
        //get request from client
        Scanner sc=new Scanner(System.in);
        System.out.println("enter request");
        String message=sc.next();
        //encrypt thrice
        byte[] encrypted=object.encrypt(message.getBytes());
        String encrypted_string=bytesToString(encrypted);
        //sending to bridge node
        object.torouter1(encrypted_string);
        System.out.println("sent to first router");
        
        
    }
    
    
String dir_data()throws IOException
 {
     
// client=new Socket("192.168.0.105",9090);
 din=new DataInputStream(client.getInputStream());
 
 directory=din.readUTF();
     
 //System.out.println(directory);
return directory;
 
 
 }
 //split ip
String[] splitstring(String directory){
this.directory=directory;
int i=0;
String[] temp=directory.split("/");
    System.out.println(temp[2]);

for (int j=0;j<temp.length-1;j++)
{
    listdir[j]=temp[j+1];
    System.out.println(temp[j+1]);
}
return listdir;
}



byte[] encrypt(byte[] message)
{
  //router3
  String finalip="192.168.0.1";
    byte[] encrypt3=encryptmethod(message,finalip,listdir[1],listdir[2]);
    byte[] encrypt2=encryptmethod(encrypt3,listdir[0],listdir[4],listdir[5]);
    byte[] encrypt1=encryptmethod(encrypt2,listdir[3],listdir[7],listdir[8]);
    
    
  /*  byte[] ip_for_router3="final ip/".getBytes();

   byte[] router3bytes = new byte[ip_for_router3.length + message.length];
    for (int i = 0; i < router3bytes.length; ++i)
{
    router3bytes[i] = i < ip_for_router3.length ? ip_for_router3[i] : message[i - ip_for_router3.length];
}
    System.out.println(listdir[1]);
   BigInteger e=BigInteger.valueOf(Long.parseLong(listdir[1]));
   BigInteger n=BigInteger.valueOf(Long.parseLong(listdir[2]));
   byte[] encrypt3= (new BigInteger(router3bytes)).modPow(e,n).toByteArray();
    System.out.println(bytesToString(encrypt3));
  //router2
   byte[] ip_for_router2=listdir[3].getBytes();
   byte[] router2bytes = new byte[ip_for_router2.length + encrypt3.length];

for (int i = 0; i < router2bytes.length; ++i)
{
    router2bytes[i] = i < ip_for_router2.length ? ip_for_router2[i] : encrypt3[i - ip_for_router2.length];
}
   byte[] encrypt2= (new BigInteger(router2bytes)).modPow(new BigInteger(listdir[4].getBytes()),new BigInteger(listdir[5].getBytes())).toByteArray();
 
  //router1
   
   byte[] ip_for_router1=listdir[6].getBytes();
   byte[] router1bytes = new byte[ip_for_router1.length + encrypt2.length];

for (int i = 0; i < router1bytes.length; ++i)
{
    router1bytes[i] = i < ip_for_router1.length ? ip_for_router1[i] : encrypt2[i - ip_for_router1.length];
}
   byte[] encrypt1= (new BigInteger(router1bytes)).modPow(new BigInteger(listdir[7].getBytes()),new BigInteger(listdir[8].getBytes())).toByteArray();
    System.out.println(encrypt1);  
return encrypt1;
*/
    return encrypt1;
}

 private static String bytesToString(byte[] encrypted) { 
        String test = ""; 
        for (byte b : encrypted) { 
            test += Byte.toString(b); 
        } 
        return test; 
    }    
void torouter1(String message_router1)throws IOException{

client =new Socket(listdir[6],9090);
dout=new DataOutputStream(client.getOutputStream());
dout.writeUTF(message_router1);

}
private static byte[] encryptmethod(byte[] data,String ip,String e,String n)
{
String ipconcat=ip+"/";

byte[] ip_to_send=ipconcat.getBytes();
byte[] combine = new byte[ip_to_send.length + data.length];
    for (int i = 0; i < combine.length; ++i)
{
    combine[i] = i < ip_to_send.length ? ip_to_send[i] : data[i - ip_to_send.length];
}
   BigInteger E=BigInteger.valueOf(Long.parseLong(e));
   BigInteger N=BigInteger.valueOf(Long.parseLong(n));
   byte[] encrypt= (new BigInteger(combine)).modPow(E,N).toByteArray();
   System.out.println(bytesToString(combine));
return encrypt;
}
}

