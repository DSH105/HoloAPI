package com.dsh105.holoapi.util;

import com.captainbern.reflection.ClassTemplate;
import com.captainbern.reflection.Reflection;
import com.captainbern.reflection.SafeField;
import com.captainbern.reflection.accessor.FieldAccessor;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassModifier<T> {

    /**
     * The handle, used to get fields with.
     */
    protected Object handle;

    /**
     * A ClassTemplate object which makes it easier to work with the class fields.
     */
    protected ClassTemplate<?> template;

    /**
     * The fields of the current Visitor
     */
    protected List<FieldAccessor> data;

    /**
     * A basic Cache system
     */
    protected Map<Class, ClassModifier> cache;

    public ClassModifier(Object handle) {
        this(handle, new Reflection().reflect(handle.getClass()));
    }

    public ClassModifier(Object handle, ClassTemplate template) {
        this(handle, template, new ArrayList<FieldAccessor>());
    }

    public ClassModifier(Object handle, ClassTemplate template, List<FieldAccessor> data) {
        this(handle, template, data, new HashMap<Class, ClassModifier>());
    }

    public ClassModifier(Object handle, ClassTemplate template, List<FieldAccessor> data, Map<Class, ClassModifier> cache) {
        initialize(handle, template, data, cache);
    }

    protected void initialize(Object handle, ClassTemplate template, List<FieldAccessor> data, Map<Class, ClassModifier> cache) {
        this.handle = handle;
        this.template = template;
        this.data = data;
        this.cache = cache;
    }

    /**
     * Reads the field at the given index.
     * @param index
     * @return
     */
    public T read(int index) {
        if(index < 0 || index >= this.data.size()) {
            throw new IndexOutOfBoundsException("Size: " + this.data.size() + ". Requested: " + index);
        }

        if(this.handle == null) {
            throw new RuntimeException("Handle is NULL (aka I don't know what to read of!)!");
        }

        // Should never be null
        if(this.data.get(index) != null) {
            FieldAccessor<T> accessor = this.data.get(index);

            return accessor.get(getHandle());
        }
        return null;
    }

    /**
     * Returns the field at the given index as a FieldAccessor
     * @param index
     * @return
     */
    public FieldAccessor<T> getAsFieldAccessor(int index) {
        if(index < 0 || index >= this.data.size()) {
            throw new IndexOutOfBoundsException("Size: " + this.data.size() + ". Requested: " + index);
        }

        // Should never be null
        if(this.data.get(index) != null) {
            FieldAccessor<T> accessor = this.data.get(index);

            return accessor;
        }
        return null;
    }

    /**
     * Writes a value to the given field at the given index.
     * @param index
     * @param value
     * @return
     */
    public ClassModifier<T> write(int index, T value) {
        if(index < 0 || index >= this.data.size()) {
            throw new IndexOutOfBoundsException("Size: " + this.data.size() + ". Requested: " + index);
        }

        if(this.handle == null) {
            throw new RuntimeException("Handle is NULL! (aka I don't know what to write to!)");
        }

        getAsFieldAccessor(index).set(getHandle(), value);

        return this;
    }

    /**
     * Whether or not the field at the given index is public.
     * @param index
     * @return
     */
    public boolean isPublic(int index) {
        return Modifier.isPublic(getAsFieldAccessor(index).getField().getModifiers());
    }

    /**
     * Whether or not the field at the given index is final or not.
     * @param index
     * @return
     */
    public boolean isReadOnly(int index) {
        return Modifier.isFinal(getAsFieldAccessor(index).getField().getModifiers());
    }

    /**
     * Makes the field at the given index final.
     * @param index
     * @param state
     */
    public void setFinalState(int index, boolean state) {
        if(index < 0 || index >= this.data.size()) {
            throw new IndexOutOfBoundsException("Size: " + this.data.size() + ". Requested: " + index);
        }

        SafeField field = getAsFieldAccessor(index).getField();
        if (state)
            field.setModifiers(field.getModifiers() | Modifier.FINAL);
        else
            field.setModifiers(field.getModifiers() & ~Modifier.FINAL);
    }

    /**
     * Returns a new FieldVisitor with fields of the given type.
     * @param type
     * @return
     */
    public ClassModifier<T> withType(@Nonnull Class type) {
        ClassModifier<T> visitor = cache.get(type);

        if(visitor == null) {

            List<FieldAccessor> fields = new LinkedList<FieldAccessor>();

            for(SafeField field : getTemplate().getSafeFields()) {
                if(type.isAssignableFrom(field.member().getType())) {
                    if(!fields.contains(field))
                        fields.add(field.getAccessor());
                }
            }

            visitor = constructNewVisitor(type, fields, this.cache);

            this.cache.put(type, visitor);
        }
        return visitor;
    }

    /**
     * Returns a list of FieldAccessors of the given class.
     * @return
     */
    public List<FieldAccessor> getFields() {
        if(this.data == null) {
            throw new RuntimeException("Fields are not initialized! (= NULL)");
        }
        return this.data;
    }

    /**
     * Constructs a new FieldVisitor with the given parameters.
     * @param type
     * @param fields
     * @param visitorMap
     * @return
     */
    protected ClassModifier<T> constructNewVisitor(Class type, List<FieldAccessor> fields, Map<Class, ClassModifier> visitorMap) {
        ClassModifier<T> visitor = new ClassModifier<T>(this.handle);
        visitor.initialize(getHandle(), new Reflection().reflect(type), fields, visitorMap);
        return visitor;
    }

    /**
     * Returns the ClassTemplate.
     * @return
     */
    public ClassTemplate<?> getTemplate() {
        if(this.template == null) {
            throw new RuntimeException("ClassTemplate is NULL!");
        }
        return this.template;
    }

    /**
     * Returns the handle.
     * @return
     */
    public Object getHandle() {
        if(this.handle == null) {
            throw new RuntimeException("Handle is NULL!");
        }
        return this.handle;
    }

    /**
     * Sets the handle
     * @param handle
     */
    public void setHandle(Object handle) {
        if(handle == null) {
            throw new IllegalArgumentException("Handle can't be NULL!");
        }
        this.handle = handle;
    }

    /**
     * Returns the size of this Visitor.
     * @return
     */
    public int size() {
        return this.data.size();
    }
}