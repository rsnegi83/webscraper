package com.webScraper.webScraper;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Using Trie here, as it seems much better option than using a hashMap or
 * a priority queue, as we need to store pair and single words in different collections.
 * Intent here is to use a Trie so that first word in the pair goes immediately after
 * the root and has its count incremented and second word in the pair will go to first
 * word node's child node and here we increment the pair count.
 * On each insertion for a word or pair we know its count, same we can check/put/delete in a
 * fixed size PriorityQueue where we can check the required output laterS
 **/

public class Trie {

    @Autowired
    WebScraper webScanner;

    public static final Integer QUEUE_SIZE = 10;

    class TrieNode {
        String data;
        boolean isEnd;
        /**
         * counter for the single word
         */
        public int count;
        /**
         * counter for the pair
         */
        int countPair;
        List<TrieNode> childList;

        public TrieNode(String s) {
            childList = new LinkedList<TrieNode>();
            isEnd = false;
            data = s;
            count = 0;
            countPair = 0;
        }

        public TrieNode getChild(String s) {
            if (childList != null)
                for (TrieNode child : childList)
                    if (child.data.equals(s))
                        return child;
            return null;
        }
    }

    public TrieNode root;

    /**
     * Constructor
     */
    public Trie() {
        root = new TrieNode("");
    }

    /**
     * This function is used to insert a word pair in trie
     */
    public void insert(String word) {
        //words will come in pair always
        if (word != null && word.contains(" ")) {
            //System.out.println("Ravindra" + word);
            //make all words to lowercase for better match
            String wordLowerCase = word.toLowerCase();
            String[] words = wordLowerCase.split("\\s");
            TrieNode current = root;
            boolean endNode = false;
            current = addTrieNode(current, words[0], endNode);
            addToPriorityQueue(webScanner.singleWord, words[0], current.count, QUEUE_SIZE);
            //a check hopefully another word in pair should never be null
            if (words[1] != null) {
                endNode = true;
                current = addTrieNode(current, words[1], endNode);
                addToPriorityQueue(webScanner.pair, wordLowerCase, current.countPair, QUEUE_SIZE);
            }
        }
    }

    /**
     * add the node to trie
     * 1. first word from the pair to the root node
     * 2. second word from the pair to the above first node
     *
     * @param current current trie node
     * @param word    string we need to add
     * @param endNode mark if this is the end node of pair
     * @return current node after adding a new node or after incrementing the count
     */
    private TrieNode addTrieNode(TrieNode current, String word, boolean endNode) {
        if (current.getChild(word) == null) {
            current.childList.add(new TrieNode(word));
            current = current.getChild(word);
            if (endNode) {
                current.countPair++;
                current.isEnd = true;
            } else
                current.count++;
        } else {
            current = current.getChild(word);
            if (endNode) {
                current.countPair++;
                current.isEnd = true;
            } else
                current.count++;
        }
        return current;
    }

    /**
     * add the word/pair to the queue
     * 1. checks if word/pair already there replaces it
     * 2. if not there adds it, removing the  one with least count from the priority queue
     *
     * @param queue     single/pair word Priority queue
     * @param word      word/pair that needs to be added
     * @param wordCount current count of the word/pair as fetched from the docs
     * @param queueSize predefined queue size
     */
    private void addToPriorityQueue(PriorityQueue<FrequentWords> queue, String word, int wordCount, int queueSize) {
        if (wordAlreadyPresent(queue, word)) {
            //old value removed from queue and now new value added
            queue.add(new FrequentWords(word, wordCount));
        } else if (queue.size() < queueSize) {
            queue.add(new FrequentWords(word, wordCount));
        } else {
            FrequentWords leastOccuringWord = queue.peek();
            if (wordCount > leastOccuringWord.getWordCount()) {
                queue.poll();
                queue.add(new FrequentWords(word, wordCount));
            }
        }
    }

    /**
     * This function is used to search a word in trie
     */
    public boolean search(String word) {
        TrieNode current = root;
        if (current.getChild(word) == null)
            return false;
        return true;
    }

    /**
     * Iterator through the PriorityQueue containing single and paired words
     * to find out if the queue is already having the word.
     *
     * @param queue priorityqueue containing the word or pair
     * @param word  string that need to be searched
     * @return boolean value if word is there
     */
    private boolean wordAlreadyPresent(PriorityQueue<FrequentWords> queue, String word) {
        Iterator it = queue.iterator();
        while (it.hasNext()) {
            FrequentWords f = (FrequentWords) it.next();
            if (word.equals(f.getWord())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

}