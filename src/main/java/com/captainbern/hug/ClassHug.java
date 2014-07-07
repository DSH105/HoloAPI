/*
 * This file is part of Hug.
 *
 * Hug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hug (or the project it is being used in).  If not, see <http://www.gnu.org/licenses/>.
 */

package com.captainbern.hug;

import com.google.common.base.Preconditions;

import java.io.*;

import static com.captainbern.hug.Constant.*;

/**
 * A class used to patch some of our classes to work
 * with custom servers like Cauldron.
 *
 * It's some very basic bytecode editing. Also note that "this class" refers to
 * the class that is being edited. (It does not refer to <i>this</i> class object.
 */
public class ClassHug {

    // The Magic of this class file. I'd better be cafebabe or else the party won't continue
    private int magic;

    // The minor java version of this class file
    private int minor;

    // The major java version of this class
    private int major;

    // The size of the ConstantPool
    // Note that when adding items to the pool (see below) you also need
    // to increment this value (same counts for removing)
    private int poolSize;

    // A very, very basic ConstantPool implementation
    private Constant[] pool;

    // The access-flags of this class
    private int accessFlags;

    // The position of the name of our class in the ConstantPool
    private int thisClass;

    // Same as above but with the SuperClass
    private int superClass;

    // All the other code of this class
    private byte[] otherCode;

    // If, for some reason, you need the "old" bytecode, here it is
    private byte[] code;

    public ClassHug(byte[] b) {
        this(b, 0, b.length);
    }

    public ClassHug(byte[] b, int offset, int length) {
        Preconditions.checkNotNull(b);
        Preconditions.checkPositionIndex(offset, b.length);
        Preconditions.checkState(length >= b.length);

        byte[] code = new byte[length];
        System.arraycopy(b, 0, code, 0, b.length);

        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(code), 8192));

        try {

            this.code = code;

            this.magic = inputStream.readInt();
            this.minor = inputStream.readUnsignedShort();
            this.major = inputStream.readUnsignedShort();

            if (major > 0x34) // Lolwat, seems like someone compiled this class with JDK 9 or above. (FYI; We're at 8 now)
                throw new RuntimeException("Unsupported class file!");

            this.poolSize = inputStream.readUnsignedShort();
            this.pool = new Constant[this.poolSize];

            // The first item in the pool is reserved by the jvm
            for (int i = 1; i < poolSize; i++) {
                // The type
                byte type = inputStream.readByte();
                // Used to read values with a bigger size blah
                byte[] data;
                switch (type) {
                    case CONSTANT_Utf8:
                        pool[i] = new Constant(type, i, inputStream.readUTF().getBytes());
                        break;
                    case CONSTANT_Class:
                    case CONSTANT_String:
                    case CONSTANT_MethodType:
                        data = new byte[2];
                        inputStream.readFully(data);
                        pool[i] = new Constant(type, i, data);
                        break;
                    case CONSTANT_Integer:
                    case CONSTANT_Float:
                    case CONSTANT_FieldRef:
                    case CONSTANT_MethodRef:
                    case CONSTANT_InterfaceMethodRef:
                    case CONSTANT_NameAndType:
                    case CONSTANT_InvokeDynamic:
                        data = new byte[4];
                        inputStream.readFully(data);
                        pool[i] = new Constant(type, i, data);
                        break;
                    case CONSTANT_Long:
                    case CONSTANT_Double:
                        data = new byte[8];
                        inputStream.readFully(data);
                        pool[i] = new Constant(type, i, data);
                        i++;
                        break;
                    case CONSTANT_MethodHandle:
                        data = new byte[3];
                        inputStream.readFully(data);
                        pool[i] = new Constant(type, i, data);
                        break;
                    default:
                        throw new RuntimeException("Illegal constant-type: " + type + "!");
                }
            }

            this.accessFlags = inputStream.readUnsignedShort();
            this.thisClass = inputStream.readUnsignedShort();
            this.superClass = inputStream.readUnsignedShort();

            // Interfaces                         <-- Not parsing those because
            // Fields                                 It's quite a task to do so,
            // Methods                                and because it would increase
            // Attributes/Metadata                    the size of this class drastically
            //                                                         |
            // So instead we're just writing them to another array   <-
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteStream, 8192));
            byte[] buffer = new byte[inputStream.available()];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            this.otherCode = byteStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to hug class: " + b.toString() + "!", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                // We'll just swallow this... For now.
            }
        }
    }

    /**
     * Constructs a new ClassHug with the given source.
     * The "source" = the canonical class name.
     * So for the Object class, this would be java.lang.Object
     * @param classSource
     */
    public ClassHug(String classSource) {
        this(toBytes(ClassLoader.getSystemResourceAsStream(classSource.replace('.', '/') + ".class"), true));
    }

    /**
     * Converts a given InputStream to a byte-array.
     * @param inputStream
     * @param close
     * @return
     */
    private static byte[] toBytes(final InputStream inputStream, boolean close) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[inputStream.available()];

            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }

            return buffer;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (close) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns the raw-bytecode representation of this class.
     * @return
     */
    public byte[] getHugCode() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream, 8192));

        try {
            write(dataOutputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            try {
                dataOutputStream.close();
            } catch (IOException ioe1) {
                ioe1.printStackTrace();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Writes this class to the given OutputStream.
     */
    public final void write(DataOutputStream codeStream) throws IOException {
        codeStream.writeInt(this.magic);

        codeStream.writeShort(this.minor);
        codeStream.writeShort(this.major);

        codeStream.writeShort(this.poolSize);
        for (int i = 1; i < this.poolSize; i++) {
            Constant constant = this.pool[i];
            if (constant != null) {
                codeStream.write(constant.getBytes());
            }
        }

        codeStream.writeShort(this.accessFlags);
        codeStream.writeShort(this.thisClass);
        codeStream.writeShort(this.superClass);

        codeStream.write(this.otherCode);

        codeStream.flush();
    }

    /**
     * Returns the "original" bytecode of this class
     * @return
     */
    public byte[] getUneditedCode() {
        return this.code;
    }

    /**
     * Returns the access flags of this class
     * @return
     */
    public int getAccessFlags() {
        return this.accessFlags;
    }

    /**
     * Allows you to set the access flags of this class
     * @param accessFlags
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
     * Returns the name of this class
     * @return
     */
    public String getClassName() {
        Constant constant = this.pool[this.thisClass];

        if (constant.getType() == CONSTANT_Class) {
            byte[] val = constant.getRawData();
            int index = ((val[0] << 8) | (val[1]));

            Constant utf8 = this.pool[index];
            if (utf8.getType() == CONSTANT_Utf8)
                return utf8.rawStringValue();
        }

        throw new RuntimeException("Failed to return the Class-name! Perhaps a corrupted ConstantPool?");
    }

    /**
     * Allows you to set the name of this class
     * @param className
     */
    public void setClassName(String className) {
        Constant constant = this.pool[this.thisClass];

        if (constant.getType() == CONSTANT_Class) {
            byte[] val = constant.getRawData();
            int index = ((val[0] << 8) | val[1]);

            Constant utf8 = this.pool[index];
            if (utf8.getType() == CONSTANT_Utf8) {
                utf8.setRawData(className.getBytes());
                return;
            }
        }

        throw new RuntimeException("Failed to set the Class-name! Perhaps a corrupted ConstantPool?");
    }

    /**
     * Returns the name of the super-class of this class
     * @return
     */
    public String getSuperClassName() {
        Constant constant = this.pool[this.superClass];

        if (constant.getType() == CONSTANT_Class) {
            byte[] val = constant.getRawData();
            int index = ((val[0] << 8) | (val[1]));

            Constant utf8 = this.pool[index];
            if (utf8.getType() == CONSTANT_Utf8)
                return utf8.rawStringValue();
        }

        throw new RuntimeException("Failed to return the SuperClass-name! Perhaps a corrupted ConstantPool?");
    }

    /**
     * Allows you to set the super class of this class
     * @param className
     */
    public void setSuperClassName(String className) {
        Constant constant = this.pool[this.superClass];

        if (constant.getType() == CONSTANT_Class) {
            byte[] val = constant.getRawData();
            int index = ((val[0] << 8) | val[1]);

            Constant utf8 = this.pool[index];
            if (utf8.getType() == CONSTANT_Utf8) {
                utf8.setRawData(className.getBytes());
                return;
            }
        }

        throw new RuntimeException("Failed to set the SuperClass-name! Perhaps a corrupted ConstantPool?");
    }

    /**
     * Replaces all instances of the given string to {@param to} in the ConstantPool
     * @param from
     * @param to
     */
    public void replace(String from, String to) {
        for (int i = 1; i < this.poolSize; i++) {
            Constant constant = this.pool[i];
            if (constant != null) {
                if (constant.getType() == CONSTANT_Utf8) {
                    constant.setRawData(constant.rawStringValue().replaceAll(from, to).getBytes());
                }
            }
        }
    }

    /**
     * Creates a Class Object of the byte-code of this class. This method
     * should be fairly safe unless you messed up something.
     * @return
     */
    public Class giveAHug() {
        return new ClassLoader() {
            public Class defineClass(String name, byte[] code) {
                return super.defineClass(name, code, 0, code.length);
            }
        }.defineClass(this.getClassName().replace('/', '.'), this.getHugCode());
    }
}
