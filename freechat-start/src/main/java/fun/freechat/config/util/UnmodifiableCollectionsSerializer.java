package fun.freechat.config.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.*;

public class UnmodifiableCollectionsSerializer extends Serializer<Object> {
    @Override
    public Object read(final Kryo kryo, final Input input, final Class<? extends Object> clazz) {
        final int ordinal = input.readInt(true);
        final UnmodifiableCollection unmodifiableCollection = UnmodifiableCollection.values()[ordinal];
        final Object sourceCollection = kryo.readClassAndObject(input);
        return unmodifiableCollection.create(sourceCollection);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Object object) {
        try {
            final UnmodifiableCollection unmodifiableCollection = UnmodifiableCollection.valueOfType(object.getClass());
            // the ordinal could be replaced by s.th. else (e.g. a explicitely managed "id")
            output.writeInt(unmodifiableCollection.ordinal(), true);
            kryo.writeClassAndObject(output, unmodifiableCollection.transfer(object));
        } catch (final RuntimeException e) {
            // Don't eat and wrap RuntimeExceptions because the ObjectBuffer.write...
            // handles SerializationException specifically (resizing the buffer)...
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object copy(Kryo kryo, Object original) {
        try {
            final UnmodifiableCollection unmodifiableCollection = UnmodifiableCollection.valueOfType(original.getClass());
            Object sourceCollectionCopy = kryo.copy(unmodifiableCollection.transfer(original));
            return unmodifiableCollection.create(sourceCollectionCopy);
        } catch (final RuntimeException e) {
            // Don't eat and wrap RuntimeExceptions
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private enum UnmodifiableCollection {
        COLLECTION(Collections.unmodifiableCollection(Arrays.asList("")).getClass()){
            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableCollection((Collection<?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return Arrays.asList(((Collection<?>) unmodifiableCollection).toArray());
            }
        },
        RANDOM_ACCESS_LIST(Collections.unmodifiableList(new ArrayList<Void>()).getClass()){
            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableList((List<?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return new ArrayList<>((List<?>) unmodifiableCollection);
            }
        },
        LIST(Collections.unmodifiableList(new LinkedList<Void>()).getClass()){
            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableList((List<?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return new LinkedList<>((List<?>) unmodifiableCollection);
            }
        },
        SET(Collections.unmodifiableSet(new HashSet<Void>()).getClass()){
            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableSet((Set<?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return new HashSet<>((Set<?>) unmodifiableCollection);
            }
        },
        SORTED_SET(Collections.unmodifiableSortedSet(new TreeSet<>()).getClass()){
            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableSortedSet((SortedSet<?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return new TreeSet<>((SortedSet<?>) unmodifiableCollection);
            }
        },
        MAP(Collections.unmodifiableMap(new HashMap<Void, Void>()).getClass()) {

            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableMap((Map<?, ?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return new HashMap<>((Map<?, ?>) unmodifiableCollection);
            }

        },
        SORTED_MAP(Collections.unmodifiableSortedMap(new TreeMap<>()).getClass()) {
            @Override
            public Object create(final Object sourceCollection) {
                return Collections.unmodifiableSortedMap((SortedMap<?, ?>) sourceCollection);
            }

            @Override
            public Object transfer(Object unmodifiableCollection) {
                return new TreeMap<>((SortedMap<?, ?>) unmodifiableCollection);
            }
        };

        private final Class<?> type;

        private UnmodifiableCollection(final Class<?> type) {
            this.type = type;
        }

        public abstract Object create(Object sourceCollection);
        public abstract Object transfer(Object unmodifiableCollection);

        static UnmodifiableCollection valueOfType(final Class<?> type) {
            for(final UnmodifiableCollection item : values()) {
                if (item.type.equals(type)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("The type " + type + " is not supported.");
        }

    }

    /**
     * Creates a new {@link UnmodifiableCollectionsSerializer} and registers its serializer
     * for the several unmodifiable Collections that can be created via {@link Collections},
     * including {@link Map}s.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on.
     *
     * @see Collections#unmodifiableCollection(Collection)
     * @see Collections#unmodifiableList(List)
     * @see Collections#unmodifiableSet(Set)
     * @see Collections#unmodifiableSortedSet(SortedSet)
     * @see Collections#unmodifiableMap(Map)
     * @see Collections#unmodifiableSortedMap(SortedMap)
     */
    public static void registerSerializers(final Kryo kryo) {
        final UnmodifiableCollectionsSerializer serializer = new UnmodifiableCollectionsSerializer();
        for (final UnmodifiableCollection item : UnmodifiableCollection.values()) {
            kryo.register(item.type, serializer);
        }
    }
}