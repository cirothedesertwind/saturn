/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class OperationalTransformation {
    
    private final Transform t;
    private final AtomicInteger msgGenerated;
    private final AtomicInteger msgRecieved;
    private final Queue<Message> outgoingQueue;
    private final Sender s;

    public OperationalTransformation(Transform t, Sender s) {
        this.t = t;
        this.s = s;
        msgGenerated = new AtomicInteger(0);
        msgRecieved = new AtomicInteger(0);
        outgoingQueue = new ConcurrentLinkedQueue();
    }
    
    public void generate(Runnable op){
        op.run();
        s.send(new Message(op, msgGenerated.get(), msgRecieved.get()));
        
        /*add to outgoing messages */
        outgoingQueue.add(new Message(op, msgGenerated.get()));
        msgGenerated.incrementAndGet();
    }
    
    public void recieve(Message m){
        Message v;
        /* discard acknowledged messages */
        Iterator<Message> i = outgoingQueue.iterator();
        
        while(i.hasNext()){
            v = i.next();
            if (v.msgGenerated < m.msgReceived)
                i.remove();
        }
        
        /* Assert that msg.msgGenerated == msgReceived */
        if (m.msgGenerated != msgRecieved.get()){
            System.err.println("Assert fails");
            return;
        }
        
        Iterator<Message> i2 = outgoingQueue.iterator();
        while(i2.hasNext()){
            v = i2.next();
            Runnable[] cst = t.xform(v.op, m.op);
            v.op = cst[0];
            m.op = cst[1];
        }
        
        m.op.run();
        msgRecieved.incrementAndGet();
            
    }

}
