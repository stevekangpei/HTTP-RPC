/*
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

package org.httprpc.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * CSV decoder.
 */
public class CSVDecoder {
    // Cursor
    private static class Cursor implements Iterable<Map<String, String>> {
        private List<String> keys;
        private Reader reader;
        private char delimiter;

        private ArrayList<String> values = new ArrayList<>();
        private LinkedHashMap<String, String> row = new LinkedHashMap<>();

        private Iterator<Map<String, String>> iterator = new Iterator<Map<String, String>>() {
            private Boolean hasNext = null;

            @Override
            public boolean hasNext() {
                if (hasNext == null) {
                    try {
                        readRecord(reader, values, delimiter);
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }

                    hasNext = !values.isEmpty();
                }

                return hasNext.booleanValue();
            }

            @Override
            public Map<String, String> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                row.clear();

                for (int i = 0, n = Math.min(keys.size(), values.size()); i < n; i++) {
                    row.put(keys.get(i), values.get(i));
                }

                hasNext = null;

                return row;
            }
        };

        private Cursor(List<String> keys, Reader reader, char delimiter) {
            this.keys = keys;
            this.reader = reader;
            this.delimiter = delimiter;
        }

        @Override
        public Iterator<Map<String, String>> iterator() {
            return iterator;
        }
    }

    private char delimiter;

    private static final int EOF = -1;

    /**
     * Constructs a new CSV decoder.
     */
    public CSVDecoder() {
        this(',');
    }

    /**
     * Constructs a new CSV decoder.
     *
     * @param delimiter
     * The character to use as a field delimiter.
     */
    public CSVDecoder(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Reads a sequence of values from an input stream.
     *
     * @param inputStream
     * The input stream to read from.
     *
     * @return
     * A cursor over the values in the input stream.
     *
     * @throws IOException
     * If an exception occurs.
     */
    public Iterable<Map<String, String>> readValues(InputStream inputStream) throws IOException {
        return readValues(new InputStreamReader(inputStream, Charset.forName("ISO-8859-1")));
    }

    /**
     * Reads a sequence of values from a character stream.
     *
     * @param reader
     * The character stream to read from.
     *
     * @return
     * A cursor over the values in the input stream.
     *
     * @throws IOException
     * If an exception occurs.
     */
    public Iterable<Map<String, String>> readValues(Reader reader) throws IOException {
        ArrayList<String> keys = new ArrayList<>();

        readRecord(reader, keys, delimiter);

        return new Cursor(keys, reader, delimiter);
    }

    private static void readRecord(Reader reader, ArrayList<String> fields, char delimiter) throws IOException {
        fields.clear();

        int c = reader.read();

        while (c != '\r' && c != '\n' && c != EOF) {
            StringBuilder fieldBuilder = new StringBuilder();

            boolean quoted = false;

            if (c == '"') {
                quoted = true;

                c = reader.read();
            }

            while ((quoted || (c != delimiter && c != '\r' && c != '\n')) && c != EOF) {
                fieldBuilder.append((char)c);

                c = reader.read();

                if (c == '"') {
                    c = reader.read();

                    if (c != '"') {
                        quoted = false;
                    }
                }
            }

            if (quoted) {
                throw new IOException("Unterminated quoted value.");
            }

            fields.add(fieldBuilder.toString());

            if (c == delimiter) {
                c = reader.read();
            }
        }

        if (c == '\r') {
            c = reader.read();

            if (c != '\n') {
                throw new IOException("Improperly terminated record.");
            }
        }
    }
}
