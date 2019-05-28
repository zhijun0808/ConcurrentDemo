package com.demo.zk;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

public class MyZkSerializer implements ZkSerializer {

    String charset = "UTF-8";

    @Override
    public byte[] serialize(Object obj) throws ZkMarshallingError {
        try {
            return String.valueOf(obj).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }

    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }
}
