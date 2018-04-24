package com.hao.util;

/**
 * 因根据ProtoBuf封装的java对象，无法用fastjson等常规方式转换。 <br>
 *     目前是根据ProtoBuf类动态生成代码编译为普通的实现此接口的Java
 * Bean类，，获得从ProtoBuf对象转换功能
 *
 * @author haohao
 */
public interface ProtoInterface {
    final static com.google.protobuf.util.JsonFormat.Printer PRINTER =
            com.google.protobuf.util.JsonFormat.printer()
                    .includingDefaultValueFields()
                    .omittingInsignificantWhitespace();
    final static com.google.protobuf.util.JsonFormat.Parser PARSER =
            com.google.protobuf.util.JsonFormat.parser().ignoringUnknownFields();

    /**
     * 从ProtoBuf对象转换为ProtoInterface普通Java对象
     * @param v
     * @param <T>
     * @return
     * @throws com.google.protobuf.InvalidProtocolBufferException
     */
    <T extends com.google.protobuf.GeneratedMessageV3 & com.google.protobuf.MessageOrBuilder> ProtoInterface fromProto(T v) throws com.google.protobuf.InvalidProtocolBufferException;

    /**
     * 从ProtoInterface普通Java对象转换成ProtoBuf对象
     * @param <T>
     * @return
     * @throws com.google.protobuf.InvalidProtocolBufferException
     */
    <T> T toProto() throws com.google.protobuf.InvalidProtocolBufferException;

}
