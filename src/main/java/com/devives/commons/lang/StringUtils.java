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

import java.util.List;
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
     * @return <code>Boolean<code/>
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * @param value
     * @return
     */
    public static boolean notNullOrEmpty(String value) {
        return !isNullOrEmpty(value);
    }

    /**
     * @param cs
     * @return
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * @param cs Последовательность символов
     * @return
     */
    public static boolean notEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * Проверяет строку на равенство Null и пустой строке
     *
     * @param value строка
     * @param def   значение по умолчанию
     * @return <code>Boolean<code/>
     */
    public static String getNullOrEmptyDef(String value, String def) {
        return isNullOrEmpty(value) ? requireNotNullOrEmpty(def) : value;
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение.
     *
     * @param values Значения
     * @return Значение
     */
    public static String getNotNullOrEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isEmpty()) return value;
        }
        throw new RuntimeException("One of values must be not null or empty string.");
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение.
     *
     * @param values Поставщики строк
     * @return null, если все значения null, иначе первое не isNull и не IsEmpty значение.
     */
    @SafeVarargs
    public static String findFirstNotNullOrEmpty(String... values) {
        for (String value : values) {
            if (notNullOrEmpty(value)) return value;
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
    public static String requireNotNullOrEmpty(String value) {
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
    public static String requireNotNullOrEmpty(String value, String message) {
        Objects.requireNonNull(value, message);
        if (value.isEmpty()) {
            throw new EmptyStringException(message);
        }
        return value;
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение, полученное из функций-источников.
     *
     * @param suppliers Поставщики строк
     * @return первое не isNull и не IsEmpty значение.
     * @throws RuntimeException if no one of suppliers return value.
     */
    @SafeVarargs
    public static String getFirstNotNullOrEmpty(Supplier<String>... suppliers) {
        for (Supplier<String> supplier : suppliers) {
            String value = supplier.get();
            if (value != null && !value.isEmpty()) return value;
        }
        throw new RuntimeException("One of the suppliers must return not null or empty string.");
    }

    /**
     * Возвращает первое не isNull и не IsEmpty значение.
     *
     * @param suppliers Поставщики строк
     * @return null, если все значения null, иначе первое не isNull и не IsEmpty значение.
     */
    @SafeVarargs
    public static String findFirstNotNullOrEmpty(Supplier<String>... suppliers) {
        for (Supplier<String> supplier : suppliers) {
            String value = supplier.get();
            if (value != null && !value.isEmpty()) return value;
        }
        return null;
    }

    /**
     * Приводит строку к верхнему регистру если не null
     *
     * @param value строка
     * @return строка в верхнем регистре
     */
    public static String toUpperCase(String value) {
        return value == null ? null : value.toUpperCase();
    }

    /**
     * @param value
     * @param quoteChar
     * @return
     */
    public static String dequote(String value, char quoteChar) {
        if (value == null || value.length() < 2) {
            return value;
        }
        String quoteStr = Character.toString(quoteChar);
        if (value.startsWith(quoteStr) && value.endsWith(quoteStr)) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * @param value
     * @param quoteChar
     * @return
     */
    public static String enquote(String value, char quoteChar) {
        if (value == null) {
            return value;
        }
        return quoteChar + value + quoteChar;
    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    public static boolean equalsIgnoreCase(String value1, String value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        return value1.equalsIgnoreCase(value2);
    }

    /**
     * Удаляет указанный символ из начала и конца строки, в т.ч. если символ повторяется.
     *
     * @param str Строка
     * @param chr Символ
     * @return Строка без указанного символа в начае и конце.
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

    public static String join(String[] stringArray, String delimiter) {
        return join(stringArray, obj -> obj, delimiter);
    }

    public static <T> String join(List<T> list, String delimiter) {
        return join(list.toArray(new Object[0]), Object::toString, delimiter);
    }

    /**
     * Метод объединяет элементы массива в строку с разделителем
     *
     * @param objectArray     массив объектов
     * @param joinTransformer трансформатор объекта
     * @param delimiter       разделитель
     * @param <T>             тип элемента массива
     * @return строка с разделителями
     */
    public static <T> String join(T[] objectArray, JoinTransformer<T> joinTransformer, String delimiter) {
        return join(objectArray, joinTransformer, delimiter, false);
    }

    /**
     * Метод объединяет элементы массива в строку с разделителем
     *
     * @param objectArray     массив объектов
     * @param joinTransformer трансформатор объекта
     * @param delimiter       разделитель
     * @param ignoreNulls     если true, значения Null не будут попадать в результирующую строку
     * @param <T>
     * @return строка стразделителями
     */
    public static <T> String join(T[] objectArray, JoinTransformer<T> joinTransformer, String delimiter, boolean ignoreNulls) {
        return join(objectArray, objectArray.length, joinTransformer, delimiter, ignoreNulls);
    }

    /**
     * Метод объединяет элементы массива в строку с разделителем
     *
     * @param objectArray     массив объектов
     * @param length          кол-во объектов массива, которые будут преобразованы в строку
     * @param joinTransformer трансформатор объекта
     * @param delimiter       разделитель
     * @param ignoreNulls     если true, значения Null не будут попадать в результирующую строку
     * @param <T>
     * @return строка стразделителями
     */
    public static <T> String join(T[] objectArray, int length, JoinTransformer<T> joinTransformer, String delimiter, boolean ignoreNulls) {
        StringBuilder sb = new StringBuilder();
        // счётчик добавленных элементов.
        int count = 0;
        for (int i = 0; i < length; i++) {
            String v = joinTransformer.getName(objectArray[i]);
            if (!ignoreNulls || v != null) {
                if (count > 0) {
                    sb.append(delimiter);
                }
                sb.append(v);
                count++;
            }
        }
        return sb.toString();
    }

    public interface JoinTransformer<T> {
        String getName(T obj);
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
     *
     * @since 2.1
     */
    public static final int INDEX_NOT_FOUND = -1;

    // Substring between
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     * <p/>
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} tag returns {@code null}.</p>
     * <p/>
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
     * <p/>
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open and close returns an empty string.</p>
     * <p/>
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
    public static String lowerFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char c[] = str.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    public static String lowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    public static String upperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    /**
     * Приводит первую букву строки к верхнему регистру
     *
     * @param str исходная строка
     * @return результирующая строка
     */
    public static String upperFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char c[] = str.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

}
