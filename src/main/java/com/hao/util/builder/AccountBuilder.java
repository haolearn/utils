package com.hao.util.builder;

import com.hao.util.Account;

//Source code is generated by com.hao.util.BuilderUtils,don't change logic
//You can move the class into model class with static,and change public interface to private
//Don't forget import
final public class AccountBuilder {
    public interface _id_ {
        _age_ setId(long id);
    }
    public interface _age_ {
        _name_ setAge(int age);
    }
    public interface _name_ {
        _Build_ setName(String name);
    }
    public interface _Build_ {
        Account build();
    }
    public static _id_ newBuilder() {
        return  new _id_() {
            @Override
            public _age_ setId(final long id) {
                return  new _age_() {
                    @Override
                    public _name_ setAge(final int age) {
                        return  new _name_() {
                            @Override
                            public _Build_ setName(final String name) {
                                return new _Build_() {
                                    @Override
                                    public Account build() {
                                        Account o = new Account();
                                        o.setId(id);
                                        o.setAge(age);
                                        o.setName(name);
                                        return o;
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }
}
