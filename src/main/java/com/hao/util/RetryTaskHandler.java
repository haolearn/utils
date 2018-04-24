package com.hao.util;

public interface RetryTaskHandler<K,V> {
   V call(K req);
}
