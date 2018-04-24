package com.hao.util;

import org.springframework.stereotype.Component;

@Component
public class MyService {
    public void wrong() {
        String aa=null;
        System.out.println("====================");
        System.out.println(aa.toLowerCase());
    }
}
