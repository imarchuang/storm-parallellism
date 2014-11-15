/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package backtype.storm.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2014-11-13")
public class GlobalStreamId implements org.apache.thrift.TBase<GlobalStreamId, GlobalStreamId._Fields>, java.io.Serializable, Cloneable, Comparable<GlobalStreamId> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GlobalStreamId");

  private static final org.apache.thrift.protocol.TField COMPONENT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("componentId", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField STREAM_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("streamId", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new GlobalStreamIdStandardSchemeFactory());
    schemes.put(TupleScheme.class, new GlobalStreamIdTupleSchemeFactory());
  }

  public String componentId; // required
  public String streamId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    COMPONENT_ID((short)1, "componentId"),
    STREAM_ID((short)2, "streamId");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // COMPONENT_ID
          return COMPONENT_ID;
        case 2: // STREAM_ID
          return STREAM_ID;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.COMPONENT_ID, new org.apache.thrift.meta_data.FieldMetaData("componentId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STREAM_ID, new org.apache.thrift.meta_data.FieldMetaData("streamId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GlobalStreamId.class, metaDataMap);
  }

  public GlobalStreamId() {
  }

  public GlobalStreamId(
    String componentId,
    String streamId)
  {
    this();
    this.componentId = componentId;
    this.streamId = streamId;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GlobalStreamId(GlobalStreamId other) {
    if (other.isSetComponentId()) {
      this.componentId = other.componentId;
    }
    if (other.isSetStreamId()) {
      this.streamId = other.streamId;
    }
  }

  public GlobalStreamId deepCopy() {
    return new GlobalStreamId(this);
  }

  @Override
  public void clear() {
    this.componentId = null;
    this.streamId = null;
  }

  public String getComponentId() {
    return this.componentId;
  }

  public GlobalStreamId setComponentId(String componentId) {
    this.componentId = componentId;
    return this;
  }

  public void unsetComponentId() {
    this.componentId = null;
  }

  /** Returns true if field componentId is set (has been assigned a value) and false otherwise */
  public boolean isSetComponentId() {
    return this.componentId != null;
  }

  public void setComponentIdIsSet(boolean value) {
    if (!value) {
      this.componentId = null;
    }
  }

  public String getStreamId() {
    return this.streamId;
  }

  public GlobalStreamId setStreamId(String streamId) {
    this.streamId = streamId;
    return this;
  }

  public void unsetStreamId() {
    this.streamId = null;
  }

  /** Returns true if field streamId is set (has been assigned a value) and false otherwise */
  public boolean isSetStreamId() {
    return this.streamId != null;
  }

  public void setStreamIdIsSet(boolean value) {
    if (!value) {
      this.streamId = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case COMPONENT_ID:
      if (value == null) {
        unsetComponentId();
      } else {
        setComponentId((String)value);
      }
      break;

    case STREAM_ID:
      if (value == null) {
        unsetStreamId();
      } else {
        setStreamId((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case COMPONENT_ID:
      return getComponentId();

    case STREAM_ID:
      return getStreamId();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case COMPONENT_ID:
      return isSetComponentId();
    case STREAM_ID:
      return isSetStreamId();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GlobalStreamId)
      return this.equals((GlobalStreamId)that);
    return false;
  }

  public boolean equals(GlobalStreamId that) {
    if (that == null)
      return false;

    boolean this_present_componentId = true && this.isSetComponentId();
    boolean that_present_componentId = true && that.isSetComponentId();
    if (this_present_componentId || that_present_componentId) {
      if (!(this_present_componentId && that_present_componentId))
        return false;
      if (!this.componentId.equals(that.componentId))
        return false;
    }

    boolean this_present_streamId = true && this.isSetStreamId();
    boolean that_present_streamId = true && that.isSetStreamId();
    if (this_present_streamId || that_present_streamId) {
      if (!(this_present_streamId && that_present_streamId))
        return false;
      if (!this.streamId.equals(that.streamId))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_componentId = true && (isSetComponentId());
    list.add(present_componentId);
    if (present_componentId)
      list.add(componentId);

    boolean present_streamId = true && (isSetStreamId());
    list.add(present_streamId);
    if (present_streamId)
      list.add(streamId);

    return list.hashCode();
  }

  @Override
  public int compareTo(GlobalStreamId other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetComponentId()).compareTo(other.isSetComponentId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetComponentId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.componentId, other.componentId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStreamId()).compareTo(other.isSetStreamId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStreamId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.streamId, other.streamId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("GlobalStreamId(");
    boolean first = true;

    sb.append("componentId:");
    if (this.componentId == null) {
      sb.append("null");
    } else {
      sb.append(this.componentId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("streamId:");
    if (this.streamId == null) {
      sb.append("null");
    } else {
      sb.append(this.streamId);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (componentId == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'componentId' was not present! Struct: " + toString());
    }
    if (streamId == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'streamId' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class GlobalStreamIdStandardSchemeFactory implements SchemeFactory {
    public GlobalStreamIdStandardScheme getScheme() {
      return new GlobalStreamIdStandardScheme();
    }
  }

  private static class GlobalStreamIdStandardScheme extends StandardScheme<GlobalStreamId> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, GlobalStreamId struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // COMPONENT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.componentId = iprot.readString();
              struct.setComponentIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STREAM_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.streamId = iprot.readString();
              struct.setStreamIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, GlobalStreamId struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.componentId != null) {
        oprot.writeFieldBegin(COMPONENT_ID_FIELD_DESC);
        oprot.writeString(struct.componentId);
        oprot.writeFieldEnd();
      }
      if (struct.streamId != null) {
        oprot.writeFieldBegin(STREAM_ID_FIELD_DESC);
        oprot.writeString(struct.streamId);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class GlobalStreamIdTupleSchemeFactory implements SchemeFactory {
    public GlobalStreamIdTupleScheme getScheme() {
      return new GlobalStreamIdTupleScheme();
    }
  }

  private static class GlobalStreamIdTupleScheme extends TupleScheme<GlobalStreamId> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, GlobalStreamId struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.componentId);
      oprot.writeString(struct.streamId);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, GlobalStreamId struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.componentId = iprot.readString();
      struct.setComponentIdIsSet(true);
      struct.streamId = iprot.readString();
      struct.setStreamIdIsSet(true);
    }
  }

}

