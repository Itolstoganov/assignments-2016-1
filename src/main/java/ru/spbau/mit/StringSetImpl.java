package ru.spbau.mit;

/*
  created by itolstoganov
  on 26.02.2015
*/

public class StringSetImpl implements StringSet {

    private static final int CHILDREN_MAX = 52;
    private Node root = new Node();

    @Override
    public boolean add(String element) {
        return root.addFromNode(element, 0);
    }

    @Override
    public boolean contains(String element) {
        return root.containsFromNode(element);
    }

    @Override
    public boolean remove(String element) {
        return root.removeFromNode(element, 0);
    }

    @Override
    public int size() {
        return root.getSize();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return root.countPrefixFromNode(prefix);
    }

    private static int latinLetterToInt(char letter) {
        if (Character.isLowerCase(letter)) {
            return letter - 'a';
        }
        return Character.toLowerCase(letter) - 'a' + (CHILDREN_MAX / 2);
    }

    /*
    Trie vertice. Size stands for number of
    nodes in corresponding subtrie which end of word flag is on.
    */
    private static final class Node {
        private boolean isWordEnd;
        private Node[] children;
        private int size;

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

        private boolean addFromNode(String stringToBeAdded, int pos) {
            if (pos == 0 && containsFromNode(stringToBeAdded)) {
                return false;
            }

            if (pos == stringToBeAdded.length()) {
                size++;
                isWordEnd = true;
                return true;
            }

            int nextIndex = latinLetterToInt(stringToBeAdded.charAt(pos));
            if (children[nextIndex] == null) {
                children[nextIndex] = new Node();
            }
            size++;
            children[nextIndex].addFromNode(stringToBeAdded, pos + 1);
            return true;
        }

        private boolean containsFromNode(String stringToBeFound) {
            Node node = descent(stringToBeFound, 0);
            return node != null && node.isWordEnd;
        }

        private boolean removeFromNode(String stringToBeRemoved, int pos) {
            if (pos == 0 && !containsFromNode(stringToBeRemoved)) {
                return false;
            }

            if (pos == stringToBeRemoved.length()) {
                size--;
                isWordEnd = false;
                return true;
            }

            int nextIndex = latinLetterToInt(stringToBeRemoved.charAt(pos));
            children[nextIndex].removeFromNode(stringToBeRemoved, pos + 1);
            size--;
            if (children[nextIndex].size == 0) {
                children[nextIndex] = null;
            }
            return true;
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
    }

}

