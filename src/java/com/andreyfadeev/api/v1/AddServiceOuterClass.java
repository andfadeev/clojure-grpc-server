// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: com/andreyfadeev/api/v1/add_service.proto
// Protobuf Java Version: 4.27.0

package com.andreyfadeev.api.v1;

public final class AddServiceOuterClass {
  private AddServiceOuterClass() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 27,
      /* patch= */ 0,
      /* suffix= */ "",
      AddServiceOuterClass.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_andreyfadeev_api_v1_AddRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_andreyfadeev_api_v1_AddRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_andreyfadeev_api_v1_AddResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_andreyfadeev_api_v1_AddResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n)com/andreyfadeev/api/v1/add_service.pr" +
      "oto\022\027com.andreyfadeev.api.v1\"(\n\nAddReque" +
      "st\022\014\n\001x\030\001 \001(\003R\001x\022\014\n\001y\030\002 \001(\003R\001y\"\037\n\013AddRes" +
      "ponse\022\020\n\003sum\030\001 \001(\003R\003sum2^\n\nAddService\022P\n" +
      "\003Add\022#.com.andreyfadeev.api.v1.AddReques" +
      "t\032$.com.andreyfadeev.api.v1.AddResponseB" +
      "\033\n\027com.andreyfadeev.api.v1P\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_andreyfadeev_api_v1_AddRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_andreyfadeev_api_v1_AddRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_andreyfadeev_api_v1_AddRequest_descriptor,
        new java.lang.String[] { "X", "Y", });
    internal_static_com_andreyfadeev_api_v1_AddResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_andreyfadeev_api_v1_AddResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_andreyfadeev_api_v1_AddResponse_descriptor,
        new java.lang.String[] { "Sum", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}