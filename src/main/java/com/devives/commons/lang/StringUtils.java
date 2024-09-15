/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devives.commons.lang;

import com.devives.commons.lang.exception.EmptyStringException;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Вспомогательные методы по работе со строками
 */
public abstract class StringUtils {

    /**
     * Проверяет строку на равенство Null и пустой строке
     *
     * @param value строка
     * @return true, если строка {@code null} или пустая, иначе false.
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Проверяет строку на равенство Null и пустой строке
     *
     * @param value строка
     * @return true, если строка НЕ {@code null} и НЕ пустая, иначе false.
     */
    public static boolean nonEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    /**
     * Проверяет последовательность на равенство Null и пустой последовательности
     *
     * @param cs последовательность символов
     * @return true, если последовательность {@code null} или пустая, иначе false.
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Проверяет последовательность на НЕ равенство Null и пустой последовательности
     *
     * @param cs последовательность символов
     * @return true, если последовательность НЕ {@code null} и НЕ пустая, иначе false.
     */
    public static boolean notEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * Проверяет строку на равенство Null и пустой строке
     *
     * @param value строка
     * @param def   значение по умолчанию
     * @return <code>Boolean</code>
     */
    public static String getNonEmptyOrElse(String value, String def) {
        return isEmpty(value) ? requireNonEmpty(def) : value;
    }

    /**
     * Проверяет строку на равенство Null и пустой строке
     *
     * @param value строка
     * @param def   значение по умолчанию
     * @return <code>Boolean</code>
     */
    public static String getNonNullOrElse(String value, String def) {
        return value != null ? value : requireNonEmpty(def);
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение.
     *
     * @param values Значения
     * @return Значение
     */
    public static String getFirstNonEmpty(String... values) {
        for (String value : values) {
            if (nonEmpty(value)) return value;
        }
        throw new EmptyStringException("One of values must be not null or empty string.");
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение, полученное из функций-источников.
     *
     * @param suppliers Поставщики строк
     * @return первое не isNull и не IsEmpty значение.
     * @throws RuntimeException if no one of suppliers return value.
     */
    @SafeVarargs
    public static String getFirstNonEmpty(Supplier<String>... suppliers) {
        for (Supplier<String> supplier : suppliers) {
            String value = supplier.get();
            if (nonEmpty(value)) return value;
        }
        throw new RuntimeException("One of the suppliers must return not null or empty string.");
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение.
     *
     * @param values Поставщики строк
     * @return null, если все значения null, иначе первое не isNull и не IsEmpty значение.
     */
    public static String findFirstNonEmpty(String... values) {
        for (String value : values) {
            if (nonEmpty(value)) return value;
        }
        return null;
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение.
     *
     * @param suppliers Поставщики строк
     * @return null, если все значения null, иначе первое не isNull и не IsEmpty значение.
     */
    @SafeVarargs
    public static String findFirstNonEmpty(Supplier<String>... suppliers) {
        for (Supplier<String> supplier : suppliers) {
            String value = supplier.get();
            if (nonEmpty(value)) return value;
        }
        return null;
    }

    /**
     * Проверяет значение на не равенство `null` и `пустой строке`.
     *
     * @param value значение
     * @return значение
     * @throws NullPointerException если значение `null'
     * @throws EmptyStringException если значение == `пустая строка`
     */
    public static String requireNonEmpty(String value) {
        Objects.requireNonNull(value);
        if (value.isEmpty()) {
            throw new EmptyStringException();
        }
        return value;
    }

    /**
     * Проверяет значение на не равенство `null` и `пустой строке`.
     *
     * @param value   значение
     * @param message сообщение исключения
     * @return значение
     * @throws NullPointerException если значение `null'
     * @throws EmptyStringException если значение == `пустая строка`
     */
    public static String requireNonEmpty(String value, String message) {
        Objects.requireNonNull(value, message);
        if (value.isEmpty()) {
            throw new EmptyStringException(message);
        }
        return value;
    }

    /**
     * Remove <tt>quoteChar</tt> from begin and end of the string.
     *
     * @param value     quoted string
     * @param quoteChar quote char
     * @return string
     */
    public static String dequote(String value, char quoteChar) {
        if (value == null || value.length() < 2) {
            return value;
        }
        if (value.charAt(0) == quoteChar && value.charAt(value.length() - 1) == quoteChar) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Append <tt>quoteChar</tt> to begin and end of the string.
     *
     * @param value     string
     * @param quoteChar quote char
     * @return quoted string
     */
    public static String enquote(String value, char quoteChar) {
        if (value == null) {
            return null;
        }
        return quoteChar + value + quoteChar;
    }

    /**
     * Compares two Strings, returning {@code true} if they are equal ignoring case.
     *
     * @param first  first sting
     * @param second second string
     * @return {@code true} if are equal else {@code false}.
     */
    public static boolean equalsIgnoreCase(String first, String second) {
        return first != null && first.equalsIgnoreCase(second);
    }

    /**
     * Compares two Strings, returning {@code true} if they are equal ignoring case.
     *
     * @param first          first sting
     * @param second         second string
     * @param nullsAreEquals flag indicates whether the values <tt>null</tt> are equals or not.
     * @return {@code true} if are equal else {@code false}.
     */
    public static boolean equalsIgnoreCase(String first, String second, boolean nullsAreEquals) {
        if (first == null && second == null) {
            return nullsAreEquals;
        }
        return first != null && first.equalsIgnoreCase(second);
    }

    /**
     * Удаляет указанный символ из начала и конца строки, в т.ч. если символ повторяется.
     *
     * @param str Строка
     * @param chr Символ
     * @return Строка без указанного символа в начале и конце.
     */
    public static String trim(String str, char chr) {
        if (str == null) return null;
        int len = str.length();
        int st = 0;
        char[] val = str.toCharArray();

        while ((st < len) && (val[st] == chr)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == chr)) {
            len--;
        }
        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }

    /**
     * Удаляет указанный символ из начала строки, в т.ч. если символ повторяется.
     *
     * @param str Строка
     * @param chr Символ
     * @return Строка без указанного символа в начале.
     */
    public static String trimStart(String str, char chr) {
        if (str == null) return null;
        int begin = 0;
        int end = str.length();
        while (begin != end) {
            int b = (str.charAt(begin) == chr) ? begin + 1 : begin;
            if (b != begin) {
                begin = b;
            } else {
                break;
            }
        }
        return begin != 0 ? str.substring(begin, end) : str;
    }

    /**
     * Возвращает подстроку от начала строки {@code string} до начала подстроки {@code suffix}
     *
     * @param string Исходная строка
     * @param suffix Суффикс
     * @return Подстрока
     */
    public static String substringBefore(String string, String suffix) {
        if (string == null) {
            return null;
        }
        int index = string.indexOf(suffix);
        if (index >= 0) {
            return string.substring(0, index);
        }
        return null;
    }

    /**
     * Возвращает подстроку от конца подстроки {@code prefix} до конца строки {@code string}
     *
     * @param string Исходная строка
     * @param prefix Префикс
     * @return Подстрока
     */
    public static String substringAfter(String string, String prefix) {
        if (string == null) {
            return null;
        }
        int index = string.indexOf(prefix);
        if (index >= 0) {
            return string.substring(index + prefix.length());
        }
        return null;
    }

    /**
     * Represents a failed index search.
     */
    private static final int INDEX_NOT_FOUND = -1;

    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     * <p>A {@code null} input String returns {@code null}.</p>
     * <p>A {@code null} tag returns {@code null}.</p>
     * <pre>
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str the String containing the substring, may be null
     * @param tag the String before and after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open and close returns an empty string.</p>
     * <pre>
     * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween(*, null, *)          = null
     * StringUtils.substringBetween(*, *, null)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "]")         = null
     * StringUtils.substringBetween("", "[", "]")        = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str   the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close the String after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    // Port: org.apache.commons.lang3.StringUtils#countMatches
    public static int countMatches(CharSequence str, CharSequence sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = indexOf(str, sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    // Port: org.apache.commons.lang3.CharSequenceUtils#indexOf
    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    public static String trim(String str) {
        if (str != null) {
            return str.trim();
        }
        return null;
    }

    /**
     * Приводит первую букву строки к нижнему регистру
     *
     * @param str исходная строка
     * @return результирующая строка
     */
    public static String decapitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char c[] = str.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    /**
     * Приводит первую букву строки к верхнему регистру
     *
     * @param str исходная строка
     * @return результирующая строка
     */
    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char c[] = str.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    /**
     * Приводит строку к нижнему регистру если не null
     *
     * @param str строка
     * @return строка в верхнем регистре
     */
    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    /**
     * Приводит строку к верхнему регистру если не null
     *
     * @param str строка
     * @return строка в верхнем регистре
     */
    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }


}
