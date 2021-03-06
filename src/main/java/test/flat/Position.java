// automatically generated by the FlatBuffers compiler, do not modify
package test.flat;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Position extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static Position getRootAsPosition(ByteBuffer _bb) { return getRootAsPosition(_bb, new Position()); }
  public static Position getRootAsPosition(ByteBuffer _bb, Position obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Position __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public long at() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long to() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }

  public static int createPosition(FlatBufferBuilder builder,
      long at,
      long to) {
    builder.startTable(2);
    Position.addTo(builder, to);
    Position.addAt(builder, at);
    return Position.endPosition(builder);
  }

  public static void startPosition(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addAt(FlatBufferBuilder builder, long at) { builder.addLong(0, at, 0L); }
  public static void addTo(FlatBufferBuilder builder, long to) { builder.addLong(1, to, 0L); }
  public static int endPosition(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Position get(int j) { return get(new Position(), j); }
    public Position get(Position obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

