package com.webScraper.webScraper;

import java.util.Objects;

/**
 * This data structure will be stored in the priority queue
 * to store the word and its count
 */
public class FrequentWords implements Comparable<FrequentWords> {

    private String word;
    private int wordCount;

    /**
     * Constructor
     */
    public FrequentWords(String word, int wordCount) {
        this.word = word;
        this.wordCount = wordCount;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrequentWords that = (FrequentWords) o;
        return wordCount == that.wordCount &&
                Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, wordCount);
    }

    @Override
    public String toString() {
        return "FrequentWords{" +
                "word='" + word + '\'' +
                ", wordCount=" + wordCount +
                '}';
    }

    /**
     * Based on the count of word
     *
     * @param word class object
     * @return the integer value of the  comparison
     */
    public int compareTo(FrequentWords word) {
        if (this.getWordCount() > word.getWordCount()) {
            return 1;
        } else if (this.getWordCount() < word.getWordCount()) {
            return -1;
        } else {
            return 0;
        }
    }
}
