package com.webScraper.webScraper;

import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    private WebScraper service;

    @Override
    public void run(String... args) throws Exception {
        String[] urlValidationSchemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(urlValidationSchemes);

        for (; ; ) {
            System.out.println("To exit the program, please type EXIT on the console");
            System.out.println("Enter a valid url (http/https) for testing the App");
            //Enter data using BufferReader
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            if (input.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            if (urlValidator.isValid(input)) {
                URL url = new URL(input);
                String host = url.getHost();
                String domainName = host.startsWith("www.") ? host.substring(4) : host;
                System.out.println(host);
                System.out.println(domainName);
                service.exploreHyperLink(input, domainName);
            } else {
                System.out.println("invalid url");
            }
        }
    }

}
