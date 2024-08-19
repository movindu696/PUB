package com.movindu.pub;

public interface IComResponse {
    default void onSuccess(){}
    default void onFailed(String msg){}
}
