/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

public final class EnumeratedOpTuple {

    private Operation op;
    private int v;

    public EnumeratedOpTuple(Operation op, int v) {
        this.op = op;
        this.v = v;
    }

    public Operation getOp() {
        return op;
    }
    
    public void setOp(Operation op) {
        this.op = op;
    }

    public int getValue() {
        return v;
    }
    
}
