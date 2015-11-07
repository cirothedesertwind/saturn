/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

public class Message {
    Operation op;
    int msgGenerated;
    int msgReceived;

    public Message(Operation op, int msgGenerated, int msgReceived) {
        this.op = op;
        this.msgGenerated = msgGenerated;
        this.msgReceived = msgReceived;
    }
}
