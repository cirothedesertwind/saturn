/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn.client;

import com.codingcrucible.saturn.Applier;
import com.codingcrucible.saturn.CSTuple;
import com.codingcrucible.saturn.Listener;
import com.codingcrucible.saturn.Message;
import com.codingcrucible.saturn.OperationalTransformation;
import com.codingcrucible.saturn.Sender;
import com.codingcrucible.saturn.Transform;

/**
 *
 * @author aurix
 */
public class Main {

    class Op implements Runnable {
        String name;
        int writtenValue;

        private Op(String name, int value) {
           this.name = name;
           this.writtenValue = value;
        }

        @Override
        public void run() {
            System.out.print(name + ": ");
            System.out.println(writtenValue);
        }
    }
    
     class NoOp implements Runnable {
        String name;

        private NoOp(String name) {
           this.name = name;
        }

        @Override
        public void run() {
            System.out.print(name + ": ");
            System.out.println("No Change");
        }
    }
    
    
    class ServerWinsClientSide implements Transform {

        @Override
        public CSTuple xform(CSTuple t) {
            return new CSTuple(new NoOp("Client"), t.getServerOp());
        }
    }
    
    class ServerWinsServerSide implements Transform {

        @Override
        public CSTuple xform(CSTuple t) {
            return new CSTuple(t.getClientOp(), new NoOp("Server"));
        }
    }
    
    
    class HelloWorld implements Sender, Applier, Listener {

        Message storedM;
        String name;
        Listener l;
        OperationalTransformation ot;

        public HelloWorld(String name, Transform t, Listener l) {
            this.name = name;
            this.l = l;
            ot = new OperationalTransformation(t, this, this);
        }
        
        public void generate(int value){
            Runnable op = new Op(name, value);
            ot.generate(op);
        }
        
        public void send(Message m) {
            l.recieve(m);
        }
     
        public void apply(Runnable o) {
            o.run();
        }

        public void recieve(Message m) {
           this.storedM = m;
        }
        
        public void activateReceive(){
            ot.recieve(storedM);
        }
        
        public void addListener(Listener l){
            this.l = l;
        }
    }

    public Main() {
        
        HelloWorld client, server;
        server = new HelloWorld("server", new ServerWinsServerSide(), null);
        client = new HelloWorld("client", new ServerWinsClientSide(), server);
        server.addListener(client);
        
        
        server.generate(9);
        
        client.activateReceive();
        
         client.generate(10);
        
        server.activateReceive();
        
        client.generate(25);
        
        server.generate(42);
        
        client.activateReceive();
        server.activateReceive();
        
        
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Main m = new Main();
        
        
        
    }
    
}
