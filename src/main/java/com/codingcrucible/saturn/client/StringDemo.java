/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn.client;

import com.codingcrucible.saturn.Message;
import com.codingcrucible.saturn.MessageConsumer;
import com.codingcrucible.saturn.Node;
import com.codingcrucible.saturn.OperationConsumer;
import com.codingcrucible.saturn.OperationalTransform;
import com.codingcrucible.saturn.ServerMessagePasser;
import com.codingcrucible.saturn.Transform;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StringDemo {
    
    String[] clientString = new String[2];
    String serverString = ""; //for demo purposes only
    
    class InsertOp implements StringOp {

        int index;
        String c;

        private InsertOp(int index, String c) {
            this.index = index;
            this.c = c;
        }
        
        @Override
        public String run(String obj) {
            System.out.println("(+," + index + "," + c + ")");
            obj = obj.substring(0, index)
                    .concat(c)
                    .concat(obj.substring(index));
            return obj;
        }
    }
    
    class DeleteOp implements StringOp {

        int index;
        
        private DeleteOp(int value) {
            this.index = value;
        }
        
        @Override
        public String run(String obj) {
            System.out.println("(-," + index + "," + " " + ")");
            obj = obj.substring(0, index)
                    .concat(obj.substring(index+1));
            return obj;
        }
    }
    
    final static class NoOp implements StringOp {
        
        @Override
        public String run(String obj) {
            return "";
        }        
    }
    
    class ServerWinsClientSide implements Transform<StringOp> {
        
        @Override
        public StringOp[] xform(StringOp client, StringOp server) {
            return new StringOp[]{new NoOp(), server};
        }
    }
    
    class ServerWinsServerSide implements Transform<StringOp> {
        
        @Override
        public StringOp[] xform(StringOp client, StringOp server) {
            return new StringOp[]{client, new NoOp()};
        }
    }
    
    class HelloWorldServer implements MessageConsumer<StringOp>, OperationConsumer<StringOp> {
        
        Queue<Message<StringOp>> storedMessages;
        Transform<StringOp> t;
        ServerMessagePasser p;
        Message<StringOp> storedM;
        String name;
        Node node;
        
        String s;
        
        public HelloWorldServer(String name, Transform t) {
            storedMessages = new ConcurrentLinkedQueue<>();
            p = OperationalTransform.createServerMessagePasser();
            this.name = name;
            this.t = t;
        }
        
        public void init() {
            node = OperationalTransform.createServer(0, this, p, t);
        }
        
        public void addClient(MessageConsumer<StringOp> c) {
            p.getClients().add(c);
        }
        
        public void generateInsert(int index, String c) {
            StringOp op = new InsertOp(index, c);
            System.out.println(name + "sends:");
            node.generate(op);
        }
        
        public void generateDelete(int index) {
            StringOp op = new DeleteOp(index);
            System.out.println(name + "sends:");
            node.generate(op);
        }
        
        @Override
        public void consume(Message<StringOp> m) {
            storedMessages.add(m);
        }
        
       public void activateReceive() {
            Iterator<Message<StringOp>> i = storedMessages.iterator();
            while(i.hasNext()){
                Message<StringOp> m = i.next();
                System.out.println(name + "receives:");
                node.consume(m);
                i.remove();
            }
        }
        
        @Override
        public void consume(StringOp r) {
            serverString = r.run(serverString);
            System.out.println(serverString);
        }

        @Override
        public int getGuid() {
            return 0;
        }
        
    }
    
    class HelloWorldClient implements MessageConsumer<StringOp>, OperationConsumer<StringOp> {
        
        int guid;
        Transform t;
        Queue<Message<StringOp>> storedMessages;
        String name;
        Node node;
        MessageConsumer<StringOp> server;
        
        String s;
        
        public HelloWorldClient(String name, int guid, Transform t, MessageConsumer<StringOp> server) {
            storedMessages = new ConcurrentLinkedQueue<>();
            this.server = server;
            this.name = name;
            this.t = t;
            this.guid = guid;
        }
        
        public void init() {
            node = OperationalTransform.createClient(guid, this, server, t);
        }
        
        public void generateInsert(int index, String c) {
            StringOp op = new InsertOp(index, c);
            System.out.println(name + "sends:");
            node.generate(op);
        }
        
        public void generateDelete(int index) {
            StringOp op = new DeleteOp(index);
            System.out.println(name + "sends:");
            node.generate(op);
        }
        
        @Override
        public void consume(Message<StringOp> m) {
            storedMessages.add(m);
        }
        
        public void activateReceive() {
            Iterator<Message<StringOp>> i = storedMessages.iterator();
            while(i.hasNext()){
                Message<StringOp> m = i.next();
                System.out.println(name + "receives:");
                node.consume(m);
                i.remove();
            }
        }
        
        @Override
        public void consume(StringOp r) {
            clientString[guid] = r.run(clientString[guid]);
            System.out.println(clientString[guid]);
        }

      
        @Override
        public int getGuid() {
            return guid;
        }
        
    }
    
    public StringDemo() {
        
         clientString[0] = "";
         clientString[1] = "";
        
        HelloWorldServer server;
        HelloWorldClient client1, client2;
        server = new HelloWorldServer("server", new ServerWinsServerSide());
        
        server.init();
        
        client1 = new HelloWorldClient("client1", 0, new ServerWinsClientSide(), server);
        client2 = new HelloWorldClient("client2", 1, new ServerWinsClientSide(), server);
        
        server.addClient(client1);
        server.addClient(client2);
        
        client1.init();
        client2.init();
        
        server.generateInsert(0, "a");
        
        client1.activateReceive();
        client2.activateReceive();
        
        client1.generateInsert(1, "b");
        
        server.activateReceive();
        client2.activateReceive();
        
        client1.generateInsert(2, "c");
        
        server.generateInsert(2, "d");
        
        client1.activateReceive();
        server.activateReceive();
        client2.activateReceive();
        
        //client1.generateInsert(4, "e");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        StringDemo m = new StringDemo();
        
        
        
    }
    
}
