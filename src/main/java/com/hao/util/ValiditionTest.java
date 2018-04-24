package com.hao.util;
import java.util.Objects;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Verify.*;
public class ValiditionTest {
    public static void main(String[] args){
        int i=0;
        String aa=null;
        // Preconditions 用于前提条件判断：IllegalArgumentException，IndexOutOfBoundsException,NullPointerException
//        checkArgument(i>0,"aaa:%s",i);
        checkNotNull(aa);
        checkElementIndex(2,3);
//        checkElementIndex(2,2);// IndexOutOfBoundsException.
        checkPositionIndex(0,2);
        checkPositionIndex(2,1);// ok
        // Verify 用于非预定义替代jdk Objects.noNull等，用于非前提条件检查，性能敏感慎用，异常VerifyException
        verifyNotNull(i);
        verify(i>0,"i:%s",i);
        // jdk Objects性能不好
        Objects.nonNull(i);
    }
}
