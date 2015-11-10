/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

public interface Transform {
    /*returns client in index 0, server in index 1*/
    Runnable[] xform(Runnable client, Runnable server);
}
