package de.julielab.xml;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BinaryJeDISNodeDecoder {
    public ByteArrayOutputStream decode(Collection<InputStream> input, TypeSystem ts, Map<Integer, String> mapping, Set<String> mappedFeatures, Map<String, String> namespaceMap) {
        final ByteArrayOutputStream ret = new ByteArrayOutputStream();
        try {

            for (InputStream is : input) {
                final ByteBuffer bb = readInputStreamIntoBuffer(is);
                String prefixedNameType = mapping.get(bb.getInt());
                // '<type:Token '
                ret.write('<');
                writeWs(prefixedNameType, ret);
                final String[] prefixAndTypeName = prefixedNameType.split(":");
                final String typeName = XmiSplitUtilities.convertNSUri(namespaceMap.get(prefixAndTypeName[0])) + prefixAndTypeName[1];
                final Type type = ts.getType(typeName);
                final byte numAttributes = bb.get();
                for (int i = 0; i < numAttributes; i++) {
                    readAttribute(bb, typeName, type, mappedFeatures, mapping, ts, ret);
                }
                // 0 = that's it, 1 = there comes more which would then be the values of StringArrays
                final byte finishedIndicator = bb.get();
                if (finishedIndicator == 0) {
                    writeStringArray(is, ret);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void readAttribute(ByteBuffer bb, String typeName, Type type, Set<String> mappedFeatures, Map<Integer, String> mapping, TypeSystem ts, ByteArrayOutputStream ret) {
        final String attrName = mapping.get(bb.getInt());
        final Feature feature = type.getFeatureByBaseName(attrName);
        // 'attrName="attrvalue" '
        write(attrName, ret);
        if (attrName.equals("xmi:id") || attrName.equals("sofa") || XmiSplitUtilities.isReferenceAttribute(type, attrName, ts)) {
            handleReferenceAttributes(bb, attrName, ret);
        } else if (XmiSplitUtilities.isListTypeName(typeName)) {
            handleListTypes(bb, typeName, attrName, mapping, mappedFeatures, ret);
        } else if (XmiSplitUtilities.isMultiValuedFeatureAttribute(type, attrName) || feature.getRange().isArray()) {
            handleArrayElementFeature(bb, feature, ret);
        } else if (feature.getRange().isPrimitive()) {
            handlePrimitiveFeatures(bb, mappedFeatures, mapping, ret, attrName, feature);
        }

    }

    private void handlePrimitiveFeatures(ByteBuffer bb, Set<String> mappedFeatures, Map<Integer, String> mapping, ByteArrayOutputStream ret, String attrName, Feature feature) {
        if (feature.getRange().getName().equals(CAS.TYPE_NAME_STRING)) {
            writeStringAttrValueWithMapping(bb, attrName, mappedFeatures, mapping, ret);
        }
        if (feature.getRange().getName().equals(CAS.TYPE_NAME_DOUBLE)) {
            writeAttrValue(bb.getDouble(), ret);
        }
        if (feature.getRange().getName().equals("uima.cas.Short")) {
            writeAttrValue(bb.getShort(), ret);
        }
        if (feature.getRange().getName().equals("uima.cas.Byte")) {
            writeAttrValue(bb.get(), ret);
        }
        if (feature.getRange().getName().equals("uima.cas.Integer")) {
            writeAttrValue(bb.getInt(), ret);
        }
        if (feature.getRange().getName().equals("uima.cas.Long")) {
            writeAttrValue(bb.getLong(), ret);
        }
    }

    private void handleArrayElementFeature(ByteBuffer bb, Feature feature, ByteArrayOutputStream ret) {
        ret.write('=');
        ret.write('"');
        final String componentTypeName = feature.getRange().getComponentType().getName();
        int length = bb.getInt();
        if (componentTypeName.equals(CAS.TYPE_NAME_DOUBLE)) {
            for (int i = 0; i < length; i++)
                write(bb.getDouble(), ret);
        }
        if (componentTypeName.equals(CAS.TYPE_NAME_SHORT)) {
            for (int i = 0; i < length; i++)
                write(bb.getShort(), ret);
        }
        if (componentTypeName.equals(CAS.TYPE_NAME_BYTE)) {
            for (int i = 0; i < length; i++)
                write(bb.get(), ret);
        }
        if (componentTypeName.equals(CAS.TYPE_NAME_INTEGER)) {
            for (int i = 0; i < length; i++)
                write(bb.getInt(), ret);
        }
        if (componentTypeName.equals(CAS.TYPE_NAME_LONG)) {
            for (int i = 0; i < length; i++)
                write(bb.getLong(), ret);
        }
        ret.write('"');
        ret.write(' ');
    }

    private void handleListTypes(ByteBuffer bb, String typeName, String attrName, Map<Integer, String> mapping, Set<String> mappedFeatures, ByteArrayOutputStream ret) {
        // Handle the list node elements themselves. Their features are "head" and "tail", head being
        // the value of the linked list node, tail being a reference to the next node, if it exists.
        // The tail is a xmi:id reference to the next list node
        if (attrName.equals(CAS.FEATURE_BASE_NAME_TAIL)) {
            write(bb.getInt(), ret);
        } else if (attrName.equals(CAS.FEATURE_BASE_NAME_HEAD)) {
            // The head contains the actual value
            if (typeName.equals(CAS.TYPE_NAME_FLOAT_LIST)) {
                write(bb.getDouble(), ret);
            }
            if (typeName.equals(CAS.TYPE_NAME_FS_LIST)) {
                write(bb.getInt(), ret);
            }
            if (typeName.equals(CAS.TYPE_NAME_INTEGER_LIST)) {
                write(bb.getInt(), ret);
            }
            if (typeName.equals(CAS.TYPE_NAME_STRING_LIST)) {
                writeStringAttrValueWithMapping(bb, attrName, mappedFeatures, mapping, ret);
            }
        }
    }

    private void handleReferenceAttributes(ByteBuffer bb, String attrName, ByteArrayOutputStream ret) {
        if (attrName.equals("xmi:id"))
            writeAttrValue(bb.getInt(), ret);
        else if (attrName.equals("sofa"))
            writeAttrValue(bb.get(), ret);
        else { // is reference attribute
            final int numReferences = bb.getInt();
            ret.write('"');
            for (int j = 0; j < numReferences; j++) {
                final int referenceXmiId = bb.getInt();
                // -1 means "null"
                if (referenceXmiId >= 0)
                    write(referenceXmiId, ret);
                else write("null", ret);
                if (j < numReferences - 1)
                    ret.write(' ');
            }
            ret.write('"');
            ret.write(' ');
        }
    }

    private ByteBuffer readInputStreamIntoBuffer(InputStream is) throws IOException {
        final ByteBuffer bb;
        byte[] buffer = new byte[8192];
        int read;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((read = is.read(buffer)) != -1)
            baos.write(buffer, 0, read);

        bb = ByteBuffer.wrap(baos.toByteArray());
        return bb;
    }

    private void writeStringArray(InputStream is, ByteArrayOutputStream ret) {
    }

    private void writeAttrValue(int i, ByteArrayOutputStream baos) {
        baos.write('=');
        baos.write('"');
        write(i, baos);
        baos.write('"');
        baos.write(' ');
    }

    private void writeAttrValue(double d, ByteArrayOutputStream baos) {
        baos.write('=');
        baos.write('"');
        write(d, baos);
        baos.write('"');
        baos.write(' ');
    }

    private void writeAttrValue(long l, ByteArrayOutputStream baos) {
        baos.write('=');
        baos.write('"');
        write(l, baos);
        baos.write('"');
        baos.write(' ');
    }

    private void writeAttrValue(String s, ByteArrayOutputStream baos) {
        baos.write('=');
        baos.write('"');
        write(s, baos);
        baos.write('"');
        baos.write(' ');
    }

    private void writeStringAttrValueWithMapping(ByteBuffer bb, String attrName, Set<String> mappedFeatures, Map<Integer, String> mapping, ByteArrayOutputStream baos) {
        baos.write('=');
        baos.write('"');
        writeStringWithMapping(bb, attrName, mappedFeatures, mapping, baos);
        baos.write('"');
        baos.write(' ');
    }


    private void write(String s, ByteArrayOutputStream baos) {
        baos.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    private void writeWs(String s, ByteArrayOutputStream baos) {
        baos.writeBytes(s.getBytes(StandardCharsets.UTF_8));
        baos.write(' ');
    }

    private void write(int i, ByteArrayOutputStream baos) {
        baos.writeBytes(getStringBytes(i));
    }

    private void write(double d, ByteArrayOutputStream baos) {
        baos.writeBytes(getStringBytes(d));
    }

    private void write(long l, ByteArrayOutputStream baos) {
        baos.writeBytes(getStringBytes(l));
    }

    private byte[] getStringBytes(int i) {
        return String.valueOf(i).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getStringBytes(long l) {
        return String.valueOf(l).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getStringBytes(double d) {
        return String.valueOf(d).getBytes(StandardCharsets.UTF_8);
    }

    private void writeStringWithMapping(ByteBuffer bb, String attrName, Set<String> mappedFeatures, Map<Integer, String> mapping, ByteArrayOutputStream baos) {
        if (mappedFeatures.contains(attrName)) {
            write(mapping.get(bb.getInt()), baos);
        } else {
            int length = bb.getInt();
            baos.write(bb.array(), bb.position(), length);
        }
    }


}
