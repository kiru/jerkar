package org.jake.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public final class JakeUtilsString {

	public static String[] split(String str, String delimiters) {
		final StringTokenizer st = new StringTokenizer(str, delimiters);
		final List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			final String token = st.nextToken();
			tokens.add(token);
		}
		return tokens.toArray(new String[tokens.size()]);
	}

	public static String substringAfterLast(String string, String delimiter) {
		final int index = string.lastIndexOf(delimiter);
		if (index == -1 || string.endsWith(delimiter)) {
			return "";
		}
		return string.substring(index+1);
	}

	public static String substringBeforeLast(String string, String delimiter) {
		final int index = string.lastIndexOf(delimiter);
		if (index == -1 || string.startsWith(delimiter)) {
			return "";
		}
		return string.substring(0, index);
	}

	public static String repeat(String pattern, int count) {
		final StringBuilder builder = new StringBuilder();
		for (int i=0; i<count; i++) {
			builder.append(pattern);
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(Class<T> type, String stringValue) throws IllegalArgumentException {
		if (type.equals(String.class)) {
			return (T) stringValue;
		}

		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return (T) Boolean.valueOf(stringValue);
		}
		try {
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return (T) Integer.valueOf(stringValue);
			}
			if (type.equals(Long.class) || type.equals(long.class)) {
				return (T) Long.valueOf(stringValue);
			}
			if (type.equals(Short.class) || type.equals(short.class)) {
				return (T) Short.valueOf(stringValue);
			}
			if (type.equals(Byte.class) || type.equals(byte.class)) {
				return (T) Byte.valueOf(stringValue);
			}
			if (type.equals(Double.class) || type.equals(double.class)) {
				return (T) Double.valueOf(stringValue);
			}
			if (type.equals(Float.class) || type.equals(float.class)) {
				return (T) Float.valueOf(stringValue);
			}
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		if (type.isEnum()) {
			@SuppressWarnings("rawtypes")
			final
			Class enumType = type;
			return (T) Enum.valueOf(enumType, stringValue);
		}
		throw new IllegalArgumentException("Can't handle type " + type);

	}

	public static String toString(Object object) {
		if (object == null) {
			return "null";
		}
		return object.toString();
	}

	private static final byte[] HEX_CHAR_TABLE = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'a', (byte)'b',
		(byte)'c', (byte)'d', (byte)'e', (byte)'f'
	};



	public static String toHexString(byte[] raw) throws IllegalArgumentException  {
		final byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (final byte b : raw) {
			final int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		try {
			return new String(hex, "ASCII");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Illegal Hex string", e);
		}
	}

	public static boolean equalsAny(String stringToMatch, String...stringToCheckEquals) {
		for (final String candidate : stringToCheckEquals) {
			if (stringToMatch.equals(candidate)) {
				return true;
			}
		}
		return false;
	}

	public static boolean endsWithAny(String stringToMatch, String...stringToCheckEquals) {
		for (final String candidate : stringToCheckEquals) {
			if (stringToMatch.endsWith(candidate)) {
				return true;
			}
		}
		return false;
	}

	public static String toString(Iterable<?> it, String separator) {
		final StringBuilder builder = new StringBuilder();
		final Iterator<?> iterator = it.iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next().toString());
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	/**
	 * Checks if a String is whitespace, empty ("") or null.
	 */
	public static boolean isBlank(String string) {
		if (string == null) {
			return true;
		}
		return string.isEmpty() || " ".equals(" ");
	}





}
