package ru.spbau.mit;

/*
  created by itolstoganov
  on 26.02.2015
*/

import java.io.*;

public class StringSetImpl implements StringSet, StreamSerializable {

    private static final int CHILDREN_MAX = 52;          /* Latin alphabet length */
    private Node root = new Node();

    @Override
    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }
        root.addFromNode(element, 0);
        return true;
    }

    @Override
    public boolean contains(String element) {
        return root.containsFromNode(element);
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        root.removeFromNode(element, 0);
        return true;
    }

    @Override
    public int size() {
        return root.getSize();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return root.countPrefixFromNode(prefix);
    }

    @Override
    public void serialize(OutputStream out) {
        DataOutputStream outStream = new DataOutputStream(out);
        root.serializeFromNode(outStream);
    }

    @Override
    public void deserialize(InputStream in) {
        try (DataInputStream inStream = new DataInputStream(in)) {
            if (inStream.available() != 0) {
                root.deserializeFromNode(inStream);
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private static int latinLetterToInt(char letter) {
        if (Character.isLowerCase(letter)) {
            return letter - 'a';
        }
        return Character.toLowerCase(letter) - 'a' + (CHILDREN_MAX / 2);
    }

    private static char intToLatinLetter(int index) {
        return (char) (index + 'a');
    }

    /*
    Trie vertice. Size stands for number of
    nodes in corresponding subtrie which end of word flag is on.
    */
    private static final class Node {
        private boolean isWordEnd;
        private int size;
        private Node[] children;

        private Node() {
            isWordEnd = false;
            children = new Node[CHILDREN_MAX];
            size = 0;
        }

        private Node descent(String stringToBeChecked, int pos) {
            if (pos == stringToBeChecked.length()) {
                return this;
            }
            int nextIndex = latinLetterToInt(stringToBeChecked.charAt(pos));
            if (children[nextIndex] != null) {
                return children[nextIndex].descent(stringToBeChecked, pos + 1);
            }
            return null;
        }

        private void addFromNode(String stringToBeAdded, int pos) {
            if (pos == stringToBeAdded.length()) {
                size++;
                isWordEnd = true;
                return;
            }

            int nextIndex = latinLetterToInt(stringToBeAdded.charAt(pos));
            if (children[nextIndex] == null) {
                children[nextIndex] = new Node();
            }
            size++;
            children[nextIndex].addFromNode(stringToBeAdded, pos + 1);
        }

        private boolean containsFromNode(String stringToBeFound) {
            Node node = descent(stringToBeFound, 0);
            return node != null && node.isWordEnd;
        }

        private void removeFromNode(String stringToBeRemoved, int pos) {
            if (pos == stringToBeRemoved.length()) {
                size--;
                isWordEnd = false;
                return;
            }

            int nextIndex = latinLetterToInt(stringToBeRemoved.charAt(pos));
            children[nextIndex].removeFromNode(stringToBeRemoved, pos + 1);
            size--;
            if (children[nextIndex].size == 0) {
                children[nextIndex] = null;
            }
        }

        private int getSize() {
            return size;
        }

        private int countPrefixFromNode(String prefix) {
            Node node = descent(prefix, 0);
            if (node != null) {
                return node.getSize();
            }
            return 0;
        }

        private void serializeFromNode(DataOutputStream outStream) {
            try {
                outStream.writeBoolean(isWordEnd);
                outStream.writeInt(size);
                int numberOfChildren = numberOfChildren();
                outStream.writeInt(numberOfChildren);

                for (int i = 0; i < CHILDREN_MAX; i++) {
                    if (children[i] != null) {
                        outStream.writeChar(i);
                        children[i].serializeFromNode(outStream);
                    }
                }
            } catch (IOException e) {
                throw new SerializationException();
            }
        }

        private void deserializeFromNode(DataInputStream inStream) {
            try {
                isWordEnd = inStream.readBoolean();
                size = inStream.readInt();
                int numberOfChildren = inStream.readInt();

                for (int i = 0; i < numberOfChildren; i++) {
                    char letter = inStream.readChar();
                    int index = latinLetterToInt(letter);
                    children[index] = new Node();
                    children[index].deserializeFromNode(inStream);
                }
            } catch (IOException e) {
                throw new SerializationException();
            }
        }

        private int numberOfChildren() {
            int nodeChildren = 0;
            for (int i = 0; i < CHILDREN_MAX; i++) {
                if (children[i] != null) {
                    nodeChildren++;
                }
            }
            return nodeChildren;
        }
    }

}

