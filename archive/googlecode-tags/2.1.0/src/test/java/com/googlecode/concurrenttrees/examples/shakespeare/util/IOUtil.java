/**
 * Copyright 2012-2013 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.concurrenttrees.examples.shakespeare.util;

import com.googlecode.concurrenttrees.examples.shakespeare.BuildShakespeareWordRadixTree;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Utility methods used only by unit tests. Not especially efficient or robust.
 *
 * @author Niall Gallagher
 */
public class IOUtil {

    public static Set<String> loadWordsFromTextFileOnClasspath(String resourceName, boolean convertToLowerCase) {
        BufferedReader in = null;
        try {
            Set<String> results = new TreeSet<String>();
            InputStream is = BuildShakespeareWordRadixTree.class.getResourceAsStream(resourceName);
            if (is == null) {
                throw new IllegalStateException("File not found on classpath");
            }
            in = new BufferedReader(new InputStreamReader(is));
            String line;
            while (true) {
                line = in.readLine();
                if (line == null) {
                    break;
                }
                if (convertToLowerCase) {
                    line = line.toLowerCase();
                }
                results.addAll(Arrays.asList(line.split("\\W+", 0)));
            }
            results.remove("");
            return results;

        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to load file from classpath: " + resourceName, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception ignore) {
                    // Ignore
                }
            }
        }
    }

    public static String loadTextFileFromClasspath(String resourceName, boolean stripPunctuation, boolean stripLineBreaks, boolean convertToLowerCase) {
        BufferedReader in = null;
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = BuildShakespeareWordRadixTree.class.getResourceAsStream(resourceName);
            if (is == null) {
                throw new IllegalStateException("File not found on classpath");
            }
            in = new BufferedReader(new InputStreamReader(is));
            String line;
            final String lineBreak = System.getProperty("line.separator");
            while (true) {
                line = in.readLine();
                if (line == null) {
                    break;
                }
                if (stripPunctuation) {
                    line = line.replaceAll("[^\\w\\s]", "");
                    line = line.replaceAll("\\s+", " ");
                    line = line.trim();
                }
                if (convertToLowerCase) {
                    line = line.toLowerCase();
                }
                if (stripLineBreaks) {
                    if (line.equals("")) {
                        // Skip blank lines...
                        continue;
                    }
                    // Insert a space instead of line break...
                    sb.append(line);
                    sb.append(" ");
                }
                else {
                    sb.append(line);
                    sb.append(lineBreak);
                }
            }
            return sb.toString();

        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to load file from classpath: " + resourceName, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception ignore) {
                    // Ignore
                }
            }
        }
    }
}
