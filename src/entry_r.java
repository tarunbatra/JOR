/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author Shivanu
 */
public class entry_r {
    
    private Socket router;
    
    private DataOutputStream dout;
    private DataInputStream din;
    
    
    //rsa variables
    private BigInteger p;
    private BigInteger q;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private BigInteger n;
    private int bitlength=1024;
    private int blocksize=256;
    
    //for random function
    private Random r;
    
    public static void main(String args[]) throws IOException
    {
    entry_r object=new entry_r();
    //read data from client
   String client_data= object.get_data_client(); 
   String client_data_decrypt=object.decrypt(client_data);
   
   object.send(client_data_decrypt);
    
    }
    
    
    //when router starts it tells the directory 
    
    void entry_r()throws IOException{
    
    router = new Socket("192.168.0.104",9090);
    dout=new DataOutputStream(router.getOutputStream());
    //telling this i am a router
    dout.writeUTF("0");
        System.out.println("identification sent to directory"); 
        dout.flush();
        
              
        //RSA 
     r=new Random();
     p=BigInteger.probablePrime(bitlength, r);
     q=BigInteger.probablePrime(bitlength, r);
     n=p.multiply(q);
     phi=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
     e=BigInteger.probablePrime(bitlength/2, r);
     
     while(phi.gcd(e).compareTo(BigInteger.ONE)>0 && e.compareTo(phi)<0)
     {
     e.add(BigInteger.ONE);
     }
    
    d=e.modInverse(phi);
    String e_str=e.toString();
    String n_str=n.toString();
    //sending key
    dout.writeUTF(e_str+"/"+n_str);
    System.out.println("public key sent");
    router.close();
    dout.flush();
    }
    
   //geting data from client
String get_data_client()throws IOException
    {
    din=new DataInputStream(router.getInputStream());
    ServerSocket router_as_server1=new ServerSocket(9090);
    Socket router_as_server=router_as_server1.accept();
    
        System.out.println("waiting for client to send data");
    String data=din.readUTF();
    din.close();
    router_as_server1.close();
    router_as_server.close();
    return data;
    }
    
String decrypt(String client_data){
    
byte[] client_data_bytes=client_data.getBytes();
byte[] client_data_decrypt=(new BigInteger(client_data_bytes)).modPow(d, n).toByteArray();
return client_data_decrypt.toString();
}

void send(String client_data_decrypt)throws IOException{
String[] ip_and_data=client_data_decrypt.split("/");

Socket send_data=new Socket(ip_and_data[0],9090);
dout=new DataOutputStream(send_data.getOutputStream());
dout.writeUTF(ip_and_data[1]);
dout.flush();
send_data.close();
}
}
