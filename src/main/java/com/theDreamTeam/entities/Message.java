package com.theDreamTeam.entities;


import java.io.Serializable;


// this class is intended for sending messages between the server and the client.
public class Message implements Serializable {

    private final Query msg;

    private Object object;

    public Message(Query msg) {
        this.msg = msg;
    }

    public Message(Query msg, Object object) {
        this.msg = msg;
        this.object = object;
    }

    public Query getMsg() {
        return msg;
    }


    public Object getObject() {
        return object;
    }
}
