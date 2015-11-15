package com.codingcrucible.saturn;

public interface Node<E> extends MessageConsumer<E> {

    @Override
    void consume(Message<E> m);

    @Override
    boolean equals(Object o);

    void generate(E op);

    @Override
    int getGuid();

    @Override
    int hashCode();
    
}
