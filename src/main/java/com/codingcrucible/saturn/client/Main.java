/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn.client;

import com.codingcrucible.saturn.Message;
import com.codingcrucible.saturn.MessageConsumer;
import com.codingcrucible.saturn.MessagePasserImpl;
import com.codingcrucible.saturn.Node;
import com.codingcrucible.saturn.OperationConsumer;
import com.codingcrucible.saturn.OperationalTransform;
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
    
    class ServerWinsClientSide implements Transform<Runnable> {
        
        @Override
        public Runnable[] xform(Runnable client, Runnable server) {
            return new Runnable[]{new NoOp(), server};
        }
    }
    
    class ServerWinsServerSide implements Transform<Runnable> {
        
        @Override
        public Runnable[] xform(Runnable client, Runnable server) {
            return new Runnable[]{client, new NoOp()};
        }
    }
    
    class HelloWorldServer implements MessageConsumer<Runnable>, OperationConsumer<Runnable> {
        
        Transform<Runnable> t;
        MessagePasserImpl p;
        Message<Runnable> storedM;
        String name;
        Node node;
        
        public HelloWorldServer(String name, Transform t) {
            p = new MessagePasserImpl(true);
            this.name = name;
            this.t = t;
        }
        
        public void init() {
            node = OperationalTransform.createServer(0, this, p, t);
        }
        
        public void addClient(MessageConsumer<Runnable> c) {
            p.getClients().add(c);
        }
        
        public void generate(int value) {
            Runnable op = new Op(value);
            System.out.println(name + "sends:");
            node.generate(op);
        }
        
        @Override
        public void consume(Message<Runnable> m) {
            this.storedM = m;
        }
        
        public void activateReceive() {
            System.out.println(name + "receives:");
            node.consume(storedM);
        }
        
        @Override
        public void consume(Runnable r) {
            r.run();
        }

        @Override
        public int getGuid() {
            return 0;
        }
        
    }
    
    class HelloWorldClient implements MessageConsumer<Runnable>, OperationConsumer<Runnable> {
        
        Transform t;
        Message storedM;
        String name;
        Node node;
        MessageConsumer<Runnable> server;
        
        public HelloWorldClient(String name, Transform t, MessageConsumer<Runnable> server) {
            this.server = server;
            this.name = name;
            this.t = t;
        }
        
        public void init() {
            node = OperationalTransform.createClient(1, this, server, t);
        }
        
        public void generate(int value) {
            Runnable op = new Op(value);
            System.out.println(name + "sends:");
            node.generate(op);
        }
        
        @Override
        public void consume(Message m) {
            this.storedM = m;
        }
        
        public void activateReceive() {
            System.out.println(name + "receives:");
            node.consume(storedM);
        }
        
        @Override
        public void consume(Runnable r) {
            r.run();
        }

      
        @Override
        public int getGuid() {
            return 1;
        }
        
    }
    
    public Main() {
        
        HelloWorldServer server;
        HelloWorldClient client1, client2;
        server = new HelloWorldServer("server", new ServerWinsServerSide());
        
        server.init();
        
        client1 = new HelloWorldClient("client1", new ServerWinsClientSide(), server);
        client2 = new HelloWorldClient("client2", new ServerWinsClientSide(), server);
        
        server.addClient(client1);
        server.addClient(client2);
        
        client1.init();
        client2.init();
        
        server.generate(9);
        
        client1.activateReceive();
        client2.activateReceive();
        
        client1.generate(10);
        
        server.activateReceive();
        client2.activateReceive();
        
        client1.generate(25);
        
        server.generate(42);
        
        client1.activateReceive();
        server.activateReceive();
        client2.activateReceive();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Main m = new Main();
        
    }
    
}
