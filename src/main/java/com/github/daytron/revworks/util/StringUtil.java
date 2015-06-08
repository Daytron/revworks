/*
 * Copyright 2015 Ryan Gilera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.daytron.revworks.util;

/**
 * String utility class for formatting Strings.
 *
 * @author Ryan Gilera
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * Capitalise only the first letter of a given string.
     *
     * @param word The string text to capitalise.
     * @return a formatted <code>String</code> with the first letter
     * capitalised.
     */
    public static String capitalizeFirstLetterWord(String word) {
        if (word == null) {
            return null;
        }

        if (word.isEmpty()) {
            return word;
        }

        if (word.length() == 1) {
            return word.toUpperCase();
        }
        
        word = word.toLowerCase();
        String newWord = word.trim();

        return newWord.substring(0, 1).toUpperCase() + newWord.substring(1);
    }

    /**
     * Capitalize the first letter of each word in a series of words separated
     * by a space character.
     *
     * @param sentence The text to capitalize.
     * @return a formatted <code>String</code> object with each first letter of
     * each word is capitalized.
     */
    public static String capitalizeFirstLetterEachWord(String sentence) {
        if (sentence == null) {
            return null;
        }

        if (sentence.isEmpty()) {
            return sentence;
        }

        sentence = sentence.trim();

        if (sentence.length() == 1) {
            return sentence.toUpperCase();
        }

        // Capitalize first word first 
        String firstLetterWordFormatted = capitalizeFirstLetterWord(sentence);

        // Converts all first letter of words separated by space to uppercase 
        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == ' ') {
                firstLetterWordFormatted = firstLetterWordFormatted
                        .substring(0, i + 1)
                        + firstLetterWordFormatted.substring(i + 1, i + 2)
                        .toUpperCase()
                        + firstLetterWordFormatted.substring(i + 2);
            }
        }

        // Get back the output string reference 
        return firstLetterWordFormatted;
    }
}
