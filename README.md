# webscraper
The project can be build using mvn clean install -DskipTests, unit testing has not been addressed with this checkin.
run the project using: mvn spring-boot:run
It is command line based on input, need to enter a propr ur with http/https properly e.g. https://www.314e.com
To come out of the project press "exit"

Using the JSOUP API for parsing the wbepage
 *It is a Java library (API) for working with real-world HTML.
 *It provides a very convenient API for extracting and manipulating data, using the best of DOM, CSS, and jquery-like methods.
 
 To store the word pair "Trie" has been used. 
 *count has been assigned at first node for the word count 
 *count in the second will keep track of the word pair count
 
 Trie has been preferred over hashmap so that with every insert we know the count for word as well as word pair and 
 count for the word and pair can be verified in the two priority queues (containg top 10 elements for word and another queue 
 containing pair) so that we have a smaller datastructure to find for the top 10 words. Thus Trie and Priority queue combination will make the
 execution faster.
 
 Using the regex, it has been taken care that the words containg unicodes e.g Latin characters are also counted as a single word.
 A few were encountered while navigating the website 314.com. However the words containg "apostrope" has been ignored in this 
 version e.g. "it's" will go as it and s.
 
 Here is a brief description of the logic
 *we start with https://www.314e.com
 *inside https://www.314e.com we will get one more link as https://www.314e.com/, this has been taken care in the code
 *any email hyperlink e.g "mailto:info@314e.com" has been ignored
 *suppose we get a link "https://www.314e.com/314e-healthcare-blog/" at a depth of 4 intially, but in furthr recurrsions
 *the link "https://www.314e.com/314e-healthcare-blog/" is again found at a lower depth of 2, we will replace the above one with lower
 *depth in the used hashMap to store the encounterd hypelinks, so that the links inside this can also be explored
 *The logic goes in two steps, find all urls till ha depth of 4 and then get url from the hashmap and find words and pairs
 *The logic is reading each page twice as raeding the page contents and hypelinks in one go has its own pit falls, like
 *"https://www.314e.com/314e-healthcare-blog/" if read and once for words and links will be marked as read and we will not be able to explore
 *further links inside this page again
*Any external urls has been ignored using the domain name

I have not used any configuation at this moment
