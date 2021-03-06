/*
 * Copyright 2015 tbking.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author tbking
 */
public class TorClient {

    static final Logger clientlog = Logger.getLogger("client");
    private DataInputStream din;
    private DataOutputStream dout;
    private BufferedReader br;
    private Socket client;
    private String directory;
    private String IP[] = new String[3];
    private String E[] = new String[3];
    private String N[] = new String[3];
    private String FinalIP,DirIP="192.168.0.111";
    private final int DirPort=9090;

    TorClient() {
        clientlog.info("Tor Client initialized.");
        try {
            //connect to directorey
            client = new Socket(DirIP, DirPort);
            dout = new DataOutputStream(client.getOutputStream());
            dout.writeUTF("1");
        } catch (IOException ex) {
            clientlog.severe("Can't connect to the directory. Exiting program...");
            System.exit(0);
        }

    }

    public static void main(String args[]) throws IOException {
        clientlog.info("Tor Client running.");
        TorClient object = new TorClient();
        object.loginit();
        object.FinalIP = "192.168.0.1";
        String dir = object.DirData();
        object.splitString(dir);
        //get request from client
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter data :");
        String message = sc.nextLine();
        clientlog.info("Data to be sent received.");
        //encrypt thrice
        byte[] encrypted = object.makeOnion(message.getBytes());
        //sending to bridge node
        object.torouter1(encrypted);

    }

    private void loginit() {
        FileHandler logFile;
        try {
            logFile = new FileHandler("/home/tbking/Development/netbeans-8.0.2/Projects/TOR/src/client/TorClient.log");
            clientlog.addHandler(logFile);
            SimpleFormatter formatter = new SimpleFormatter();
            logFile.setFormatter(formatter);
        } catch (IOException | SecurityException ex) {
            clientlog.severe("Exception raised in creating log file. Exiting program.");
        }
    }

    private String DirData() throws IOException {
        clientlog.info("Fetching data from Tor Directory.");
        din = new DataInputStream(client.getInputStream());
        directory = din.readUTF();
        clientlog.info("Data fetching finished.");
        return directory;
    }

    //split ip

    private void splitString(String directory) {
        this.directory = directory;
        String[] splitData = directory.split("/");
        int j = 1;
        for (int i = 0; i < 3; i++) {
            //System.out.println(splitData.length+"---"+i);
            IP[i] = splitData[j++]; System.out.println(IP[i]);
            E[i] = splitData[j++];  System.out.println("E="+E[i]);
            N[i] = splitData[j++];  System.out.println("N="+N[i]);
        }
    }

    private byte[] makeOnion(byte[] message) {
        clientlog.info("Data encryption process initiated.");
        byte[] peel2=makeCell(makeCell(makeCell(message,FinalIP,2),IP[2],1),IP[1],0);
       return peel2;
    }
    
    private byte[] makeCell(byte[] data, String IP,int i)
    {
        String p = IP+"::"+new String(data);
        System.out.println(i+"th peel: "+p);
        byte[] peel = p.getBytes();
        
        /*
        byte[] peel=new byte[19+data.length];
        ByteBuffer target = ByteBuffer.wrap(peel);
        
        
        //IP
        byte[] ip= "               ".getBytes();
        ByteBuffer iptarget = ByteBuffer.wrap(ip);
        iptarget.put(IP.getBytes());
        target.put(ip);
        System.out.println("IP in string--> "+new String(ip));
        System.out.println("IP in bytes--> "+bytesToString(ip));
        
        
        //LENGTH
        byte[] len= "    ".getBytes();
        ByteBuffer lentarget = ByteBuffer.wrap(len);
        lentarget.put((""+data.length).getBytes());
        target.put(len);
        System.out.println("len in string--> "+new String(len));
        System.out.println("len in bytes--> "+bytesToString(len));
        
        //DATA
        target.put(data);
        System.out.println("data in bytes--> "+bytesToString(data));
        System.out.println("peel in bytes--> "+bytesToString(peel));
        
                */
        byte[] encPeel = encrypt(peel,i);
        System.out.println("encrypted peel in bytes--> "+bytesToString(encPeel));
        System.out.println("len of encrypted peel in bytes--> "+encPeel.length);
        return encPeel;
    }
    
    private byte[] encrypt(byte[]peel,int i)
    {
        byte[] enc;
        
        BigInteger e = new BigInteger(E[i]);
        BigInteger n = new BigInteger(N[i]);
        enc = (new BigInteger(peel)).modPow(e, n).toByteArray();
        return enc;
    }
        

    private static String bytesToString(byte[] encrypted) {
        String test = "";
        for (byte b : encrypted) {
            test += Byte.toString(b);
        }
        return test;
    }

    private void torouter1(byte[] message_router1) {
        try {
            clientlog.info("Data being sent through Proxy Routers.");
            client = new Socket(IP[2], 9091);
            dout = new DataOutputStream(client.getOutputStream());
            dout.writeInt(message_router1.length);
            dout.write(message_router1);
        } catch (IOException ex) {
            clientlog.severe("Data couldn't be sent to the Router. Exiting Program");
        }
    }
}
