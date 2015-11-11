/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn.client;

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
        int writtenValue;

        private Op(int value) {
           this.writtenValue = value;
        }

        @Override
        public void run() {
            System.out.println(writtenValue);
        }
    }
    
    final static class NoOp implements Runnable {

        @Override
        public void run() {
            System.out.println("No change");
        }
    }
    
    
    class ServerWinsClientSide implements Transform {

        @Override
        public Runnable[] xform(Runnable client, Runnable server) {
            return new Runnable[]{new NoOp(), server};
        }
    }
    
    class ServerWinsServerSide implements Transform {

        @Override
        public Runnable[] xform(Runnable client, Runnable server) {
            return new Runnable[]{client, new NoOp()};
        }
    }
    
    class HelloWorld implements Sender, Listener {

        Message storedM;
        String name;
        Listener l;
        OperationalTransformation ot;

        public HelloWorld(String name, Transform t, Listener l) {
            this.name = name;
            this.l = l;
            ot = new OperationalTransformation(t, this);
        }
        
        public void generate(int value){
            Runnable op = new Op(value);
            System.out.println(name + "sends:");
            ot.generate(op);
        }
        
        public void send(Message m) {
            l.recieve(m);
        }

        public void recieve(Message m) {
           this.storedM = m;
        }
        
        public void activateReceive(){
            System.out.println(name + "receives:");
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
