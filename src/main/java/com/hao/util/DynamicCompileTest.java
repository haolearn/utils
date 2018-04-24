package com.hao.util;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.Descriptors;
import com.xingyun.customize.openapi.dto.request.ListProjectRequest;
import com.xingyun.customize.openapi.dto.request.PageRequest;
import com.xingyun.customize.openapi.dto.request.RequestHeader;
import net.openhft.compiler.CompilerUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DynamicCompileTest {
  private static final Logger logger = LoggerFactory.getLogger(DynamicCompileTest.class);

  @Test
  public void testDynamicSource() {
    ListProjectRequest listProjectRequest1 =
        ListProjectRequest.newBuilder()
            .setHeader(
                RequestHeader.newBuilder()
                    .setTraceId("traceId")
                    .setToken("token")
                    .setSource("123456")
                    .setKey("key")
                    .setExtJson("")
                    .build())
            .addPrjIds("123")
            .addPrjIds("456")
            .setPage(PageRequest.newBuilder().setPageNum(1).setPageSize(10).build())
            .build();
    try {
      logger.info("getAllFields:{}", listProjectRequest1.getAllFields());
      for (Descriptors.FieldDescriptor fd :
          listProjectRequest1.getDescriptorForType().getFields()) {
        logger.info(
            "{},{},{},{},value:{}",
            fd.getFullName(),
            fd.getIndex(),
            fd.getJsonName(),
            fd.getLiteJavaType(),
            listProjectRequest1.getField(fd));
      }

      Map<String, String> sourceMap = source(ListProjectRequest.getDefaultInstance());
      logger.info("Dynamic jsource:{}", sourceMap);
      Class dClazz =
          CompilerUtils.CACHED_COMPILER.loadFromJava(
              sourceMap.get("class"), sourceMap.get("source"));
      logger.info("Dynamic create class:{}", dClazz.getCanonicalName());
      ProtoInterface protoInterface = (ProtoInterface) dClazz.newInstance();
      ProtoInterface info2 = protoInterface.fromProto(listProjectRequest1);

      logger.info("info2:{}", JSONObject.toJSONString(info2));
      logger.info("pb class:{}", info2.getClass().getCanonicalName());
      ListProjectRequest pb2 = (ListProjectRequest) info2.toProto();
      logger.info("pb2 :{}", pb2);
      Assert.assertEquals(listProjectRequest1, pb2);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 转换成成员变量代码public Type field=default，如果为Message类型（嵌套）同时生成内部类
   *
   * @param fd
   * @return
   */
  public static String fd2field(Descriptors.FieldDescriptor fd) {
    StringBuilder sb = new StringBuilder();
    switch (fd.getLiteJavaType()) {
      case BOOLEAN:
        if (!fd.isRepeated()) {
          sb.append("public boolean ").append(fd.getJsonName()).append(" = false;\n");
        } else {
          sb.append("public java.util.List<Boolean> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case INT:
        if (!fd.isRepeated()) {
          sb.append("public int ").append(fd.getJsonName()).append(" = 0;\n");
        } else {
          sb.append("public java.util.List<Integer> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case ENUM:
        if (!fd.isRepeated()) {
          sb.append("public String ").append(fd.getJsonName()).append(" = \"\";\n");
        } else {
          sb.append("public java.util.List<String> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case LONG:
        if (!fd.isRepeated()) {
          sb.append("public long ").append(fd.getJsonName()).append(" = 0L;\n");
        } else {
          sb.append("public java.util.List<Long> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case FLOAT:
        if (!fd.isRepeated()) {
          sb.append("public float ").append(fd.getJsonName()).append(" = 0.0F;\n");
        } else {
          sb.append("public java.util.List<Float> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case DOUBLE:
        if (!fd.isRepeated()) {
          sb.append("public double ").append(fd.getJsonName()).append(" = 0.0D;\n");
        } else {
          sb.append("public java.util.List<Double> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case STRING:
        if (!fd.isRepeated()) {
          sb.append("public String ").append(fd.getJsonName()).append(" = \"\";\n");
        } else {
          sb.append("public java.util.List<String> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      case MESSAGE:
        if (!fd.isRepeated()) {
          sb.append("public _")
              .append(fd.getJsonName().toUpperCase())
              .append("_ ")
              .append(fd.getJsonName())
              .append(";\n");
        } else {
          sb.append("public java.util.List<_")
              .append(fd.getJsonName().toUpperCase())
              .append("_> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        sb.append("//INNER Dynamic\n")
            .append("public static class _")
            .append(fd.getJsonName().toUpperCase())
            .append("_ {\n");

        for (Descriptors.FieldDescriptor _fd : fd.getMessageType().getFields()) {
          sb.append(fd2field(_fd));
        }
        sb.append("}\n");
        break;
      case BYTE_STRING:
        if (!fd.isRepeated()) {
          sb.append("public com.google.protobuf.ByteString ")
              .append(fd.getJsonName())
              .append(" ;\n");
        } else {
          sb.append("public java.util.List<com.google.protobuf.ByteString> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
        break;
      default:
        if (!fd.isRepeated()) {
          sb.append("public String ").append(fd.getJsonName()).append(" = \"\";\n");
        } else {
          sb.append("public java.util.List<String> ")
              .append(fd.getJsonName())
              .append("= new java.util.ArrayList<>();\n");
        }
    }
    return sb.toString();
  }

  /**
   * proto 类型对象转换成标准java bean源代码，用于动态编译，无getter、setter，成员变量全部public，嵌套则生成嵌入类
   *
   * @param v
   * @param <T>
   * @return Map {"package":"","class":"","source":""}
   */
  public static <
          T extends com.google.protobuf.GeneratedMessageV3 & com.google.protobuf.MessageOrBuilder>
      Map<String, String> source(T v) throws IllegalAccessException, InstantiationException {
    String className = v.getClass().getCanonicalName();
    Map<String, String> out = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    StringBuilder camelName = new StringBuilder("Dynamic");
    for (String s : className.split("\\.")) {
      camelName.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
    }
    String pkg = v.getClass().getPackage().getName();
    sb.append("package ")
        .append(pkg)
        .append(";\n")
        .append("//Dynamic class !!\n")
        .append("public class ")
        .append(camelName)
        .append(" implements com.xingyun.customize.gateway.ProtoInterface {\n");
    sb.append("@Override\n public ")
        .append(className)
        .append(" toProto() throws com.google.protobuf.InvalidProtocolBufferException {\n");
    sb.append(" ")
        .append(className)
        .append(".Builder builder = ")
        .append(className)
        .append(".newBuilder();\n");
    sb.append("    PARSER.merge(com.alibaba.fastjson.JSONObject.toJSONString(this), builder);\n");
    sb.append("    return builder.build();\n");
    sb.append("}\n");
    sb.append(
        "    @Override\n"
            + "    public <T extends com.google.protobuf.GeneratedMessageV3 & com.google.protobuf.MessageOrBuilder> com.xingyun.customize.gateway.ProtoInterface fromProto(T v) throws com.google.protobuf.InvalidProtocolBufferException{\n"
            + "        return (com.xingyun.customize.gateway.ProtoInterface)com.alibaba.fastjson.JSONObject.parseObject(PRINTER.print(v),this.getClass());\n"
            + "    }");

    for (Descriptors.FieldDescriptor fd : v.getDescriptorForType().getFields()) {
      sb.append(fd2field(fd)).append("\n");
    }

    sb.append("}\n");
    out.put("package", pkg);
    out.put("class", pkg + "." + camelName.toString());
    out.put("source", sb.toString());
    return out;
  }
}
