package com.webScraper.webScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Using the JSOUP API for parsing the wbepage
 * It is a Java library (API) for working with real-world HTML.
 * It provides a very convenient API for extracting and manipulating
 * data, using the best of DOM, CSS, and jquery-like methods.
 */
@Service
public class WebScraper {

    public static LocalTime time = null;

    public static Trie trie = new Trie();

    //storing the top 10 frequent words
    public static PriorityQueue<FrequentWords> singleWord = new PriorityQueue<FrequentWords>();

    //storing the top 10 word pair
    public static PriorityQueue<FrequentWords> pair = new PriorityQueue<FrequentWords>();

    public static String domainName = null;

    //maximum depth allowed to go as link within a link
    public static final int hypelinkDepth = 4;

    //hashMap to store the explored urls, so that if same hyperlink
    //is found again inside another hyperlink we don't hit it again
    public static Map<String, Integer> encounterdUrl = new HashMap<String, Integer>();

    /**
     * main function of the service
     *
     * @param url
     */
    public void exploreHyperLink(String url, String domain) {

        try {
            intialise(domain);
            openhyperLink(url, 0);
            countwords();
        } catch (IOException e) {
            System.out.println("Exception while exploring the url" + e.getMessage());
        }

        //print the top 10 words
        while (!singleWord.isEmpty()) {
            System.out.println("single: " + singleWord.remove());

        }

        //print the top 10 word pairs
        while (!pair.isEmpty()) {
            System.out.println("pair" + pair.remove());
        }

        System.out.println("Printing all the urls that have been explored");
        System.out.println("_________________________________________________________________________________");
        //print all the urls that have been explored
        for (Map.Entry mapElement : encounterdUrl.entrySet()) {
            System.out.println("key: " + mapElement.getKey().toString() + "  value: " + mapElement.getValue().toString());
        }

        LocalTime time2 = LocalTime.now();

        System.out.println("Finished in " + ChronoUnit.MINUTES.between(time, time2) + " minutes");
    }

    /**
     * reset all data components for the new url
     */
    private void intialise(String domain) {
        time = LocalTime.now();
        trie = new Trie();
        singleWord.clear();
        pair.clear();
        encounterdUrl.clear();
        domainName = domain;
    }


    /**
     * open the hyperlink and call word count on the same recursively goes to
     * other hyperlinks till a depth of 4
     * Here the links from a page has been extracted first
     * all the links are stored in the HashMap, along with the depth it was found at
     * However if the same page is again found ata lower depth, say "https://www.314e.com/314e-healthcare-blog/"
     * was first found at the depth 4, now it is again found at a depth of 2, the page is replaced in the hashMap
     * with a new depth of 2
     * @param hyperlink
     * @param hyperLinkdepthOld depth traversed till now for a link in a link
     * @throws IOException
     */
    public void openhyperLink(String hyperlink, int hyperLinkdepthOld) throws IOException {

        int hyperLinkdepthNew = hyperLinkdepthOld + 1;
        if (hyperLinkdepthNew <= hypelinkDepth) {
            //here a case like user enters https://www.314e.com
            //however inside there is a link like https://www.314e.com/
            if (hyperlink != null && hyperlink.endsWith("/")) {
                hyperlink = hyperlink.substring(0, hyperlink.length() - 1);
            }
            if (hyperlink.contains(domainName)) {
                //also check if the link is already there it is for a greater value of found depth
                if (!encounterdUrl.containsKey(hyperlink) || (encounterdUrl.containsKey(hyperlink)
                        && encounterdUrl.get(hyperlink) > hyperLinkdepthNew)) {
                    //check if hyperlink is not a email address e.g. mailto:info@314e.com
                    Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
                    Matcher matcher = p.matcher(hyperlink);
                    if (!matcher.find()) {
                        //replace in the map with a lower depth value
                        encounterdUrl.put(hyperlink, hyperLinkdepthNew);
                        try {
                            Document doc = Jsoup.connect(hyperlink).timeout(10000).get();
                            Elements element = doc.select("loc");
                            Elements resultLinks = doc.select("a");
                            for (Element link : resultLinks) {
                                String href = link.attr("href");
                                openhyperLink(href, hyperLinkdepthNew);
                            }

                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        }
    }


    /**
     *
     * parses web page using JSOUP, take cares of parsing words
     * ignoring braces, commas full stops
     * and also reading the words containing unicode string also as a single word
     * however the word contaiinng apostrophe "it's" are not properly parsed
     * as of now, did not have much time to research into that, "it's"
     * will go as it and s, as of now
     */
    private void countwords() throws IOException {

        for (Map.Entry mapElement : encounterdUrl.entrySet()) {

            List<String> pair = new ArrayList<String>();

            try {
                Document doc = Jsoup.connect(mapElement.getKey().toString()).timeout(10000).get();
                //Get the actual text from the page, excluding the HTML
                String text = doc.body().text();

                //Create BufferedReader so the words can be counted
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
                String line;
                String lastWord = null;
                while ((line = reader.readLine()) != null) {
                    //using the regex expression so that words containing unicode characters like
                    //latin characters are also counted as word
                    //System.out.println("line: " + line);
                    //System.out.println("_________________________________________________________________________________");
                    String[] words = line.trim().split("(?U)[^\\p{Alpha}0-9']+");
                    if (lastWord != null) {
                        pair.add(lastWord + " " + words[0]);
                    }
                    for (int i = 0; i < words.length - 1; i++) {
                        pair.add(words[i] + " " + words[i + 1]);
                        //System.out.println(words[i] + " " + words[i + 1]);
                    }
                    lastWord = words[words.length - 1];
                }
                reader.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }

            //insert the word pair in trie
            for (String wordPair : pair) {
                trie.insert(wordPair);
            }
        }
    }
}
