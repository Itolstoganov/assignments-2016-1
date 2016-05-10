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
        if (index >= size() || index < 0) {
            throw new IndexOutOfBoundsException();
        } else {
            E ans;
            if (size == 1) {
                ans = (E) reference;
                reference = null;
                return ans;
            } else if (size >= ARR_SIZE_LOWER && size <= ARR_SIZE_UPPER) {
                ans = ((E[]) reference)[index];
                if (size == ARR_SIZE_LOWER || index == 1) {
                    reference = ((E[]) reference)[0];
                }
                if (size == ARR_SIZE_LOWER || index == 0) {
                    reference = ((E[]) reference)[1];
                }
            } else if (size == ARR_SIZE_UPPER + 1) {
                ans = ((ArrayList<E>) reference).remove(index);
                Object[] arr = ((ArrayList<E>) reference).toArray();
            } else {
                ans = ((ArrayList<E>) reference).remove(index);
            }
            size--;
            return ans;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int i, E element) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
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
