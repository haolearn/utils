package com.hao.util.builder;

import com.hao.util.BuilderUtils;

final public class BuilderUtilsBuilder {
    public interface _checkSuper_ {
        _clazz_ setCheckSuper(boolean checkSuper);
    }
    public interface _clazz_ {
        _lambdaStyle_ setClazz(Class clazz);
    }
    public interface _lambdaStyle_ {
        _Build_ setLambdaStyle(boolean lambdaStyle);
    }
    public interface _Build_ {
        BuilderUtils build();
    }
    public static _checkSuper_ newBuilder() {
        return  new _checkSuper_() {
            @Override
            public _clazz_ setCheckSuper(boolean checkSuper) {
                return  new _clazz_() {
                    @Override
                    public _lambdaStyle_ setClazz(Class clazz) {
                        return  new _lambdaStyle_() {
                            @Override
                            public _Build_ setLambdaStyle(boolean lambdaStyle) {
                                return new _Build_() {
                                    @Override
                                    public BuilderUtils build() {
                                        BuilderUtils o = new BuilderUtils();
                                        o.setCheckSuper(checkSuper);
                                        o.setClazz(clazz);
                                        o.setLambdaStyle(lambdaStyle);
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