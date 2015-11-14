/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

public class Message<E> {
    MessageConsumer<E> node;
    E op;
    int msgGenerated;
    int msgReceived;

    Message(MessageConsumer<E> node, E op, int msgGenerated, int msgReceived) {
        this.node = node;
        this.op = op;
        this.msgGenerated = msgGenerated;
        this.msgReceived = msgReceived;
    }
    
}
