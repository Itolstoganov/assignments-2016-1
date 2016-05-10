package ru.spbau.mit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SmartList<E> extends AbstractList<E> implements List<E> {
    private static final int ARR_SIZE_LOWER = 2;
    private static final int ARR_SIZE_UPPER = 5;
    private int size = 0;
    private Object reference = null;


    public SmartList() {
        size = 0;
        reference = null;
    }


    public SmartList(Collection<? extends E> collection) {
        for (E item : collection) {
            add(item);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean add(E element) {
        size++;
        if (size == 1) {
            reference = element;
        } else if (size == ARR_SIZE_LOWER) {
            Object[] array = new Object[ARR_SIZE_UPPER];
            array[0] = reference;
            array[1] = element;
            reference = array;
        } else if (size <= ARR_SIZE_UPPER && size > ARR_SIZE_LOWER) {
            ((Object[]) reference)[size - 1] = element;
        } else if (size == ARR_SIZE_UPPER + 1) {
            List<E> list = new ArrayList<>();
            for (int i = 0; i < size - 1; ++i) {
                list.add(get(i));
            }
            list.add(element);
            reference = list;
        } else {
            ((ArrayList<E>) reference).add(element);
        }
        return true;
    }


    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Object element;
        if (size == 1) {
            element = reference;
            reference = null;
        } else if (size == 2) {
            Object[] arr = (Object[]) reference;
            element = arr[index];
            reference = arr[1 - index];
        } else if (size <= ARR_SIZE_UPPER) {
            Object[] arr = (Object[]) reference;
            element = arr[index];
            System.arraycopy(arr, index + 1, arr, index, size - 1 - index);
            arr[size - 1] = null;
        } else if (size == ARR_SIZE_UPPER + 1) {
            ArrayList list = (ArrayList) reference;
            element = list.remove(index);
            reference = list.toArray();
        } else {
            ArrayList list = (ArrayList) reference;
            element = list.remove(index);
        }
        --size;
        return (E) element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int i, E element) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        Object previous;
        if (size == 1) {
            previous = reference;
            reference = element;
        } else if (size <= ARR_SIZE_UPPER) {
            Object[] arr = (Object[]) reference;
            previous = arr[i];
            arr[i] = element;
        } else {
            ArrayList list = (ArrayList<Object>) reference;
            previous = list.set(i, element);
        }
        return (E) previous;
    }


    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index >= size() || index < 0) {
            throw new IndexOutOfBoundsException();
        } else if (size == 0) {
            return null;
        } else if (size == 1) {
            return (E) reference;
        } else if (size >= ARR_SIZE_LOWER && size <= ARR_SIZE_UPPER) {
            return ((E[]) reference)[index];
        } else {
            return ((ArrayList<E>) reference).get(index);
        }
    }


    @Override
    public int size() {
        return size;
    }
}
