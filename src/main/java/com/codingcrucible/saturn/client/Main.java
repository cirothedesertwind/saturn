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
import com.codingcrucible.saturn.Operation;
import com.codingcrucible.saturn.OperationalTransformation;
import com.codingcrucible.saturn.Sender;
import com.codingcrucible.saturn.Transform;

/**
 *
 * @author aurix
 */
public class Main {

    class Op implements Operation{
        int writtenValue;

        private Op(int value) {
           this.writtenValue = value;
        }

        @Override
        public void run() {
            System.out.println(writtenValue);
        }
    }
    
    
    class ServerWinsClientSide implements Transform {

        @Override
        public CSTuple xform(CSTuple t) {
            return new CSTuple(null, t.getServerOp());
        }
    }
    
    class ServerWinsServerSide implements Transform {

        @Override
        public CSTuple xform(CSTuple t) {
            return new CSTuple(t.getClientOp(), null);
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
            Operation op = new Op(value);
            ot.generate(op);
        }
        
        @Override
        public void send(Message m) {
            l.recieve(m);
        }
     
        @Override
        public void apply(Operation o) {
            System.out.print(name + ": ");
            o.run();
        }

        @Override
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
