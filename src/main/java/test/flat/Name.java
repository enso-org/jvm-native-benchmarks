// automatically generated by the FlatBuffers compiler, do not modify
package test.flat;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Name extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static Name getRootAsName(ByteBuffer _bb) { return getRootAsName(_bb, new Name()); }
  public static Name getRootAsName(ByteBuffer _bb, Name obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Name __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String str() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer strAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer strInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }

  public static int createName(FlatBufferBuilder builder,
      int strOffset) {
    builder.startTable(1);
    Name.addStr(builder, strOffset);
    return Name.endName(builder);
  }

  public static void startName(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addStr(FlatBufferBuilder builder, int strOffset) { builder.addOffset(0, strOffset, 0); }
  public static int endName(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Name get(int j) { return get(new Name(), j); }
    public Name get(Name obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}
