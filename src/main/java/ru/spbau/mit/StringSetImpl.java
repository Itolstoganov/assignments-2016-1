package ru.spbau.mit;

/*
  created by itolstoganov
  on 26.02.2015
*/

public class StringSetImpl implements StringSet {

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
    }

}

