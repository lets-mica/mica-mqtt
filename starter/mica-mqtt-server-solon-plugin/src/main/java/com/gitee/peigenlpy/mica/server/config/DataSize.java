package com.gitee.peigenlpy.mica.server.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.tio.utils.hutool.StrUtil;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataSize 兼容
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public class DataSize {

	/**
	 * Bytes per Kilobyte.
	 */
	private static final long BYTES_PER_KB = 1024;

	/**
	 * Bytes per Megabyte.
	 */
	private static final long BYTES_PER_MB = BYTES_PER_KB * 1024;

	/**
	 * Bytes per Gigabyte.
	 */
	private static final long BYTES_PER_GB = BYTES_PER_MB * 1024;

	/**
	 * Bytes per Terabyte.
	 */
	private static final long BYTES_PER_TB = BYTES_PER_GB * 1024;

	private final long bytes;

	public DataSize(String text) {
		this(parse(text).getBytes());
	}

	/**
	 * Obtain a {@link DataSize} representing the specified number of bytes.
	 * @param bytes the number of bytes, positive or negative
	 * @return a {@link DataSize}
	 */
	public static DataSize ofBytes(long bytes) {
		return new DataSize(bytes);
	}

	/**
	 * Obtain a {@link DataSize} representing the specified number of kilobytes.
	 * @param kilobytes the number of kilobytes, positive or negative
	 * @return a {@link DataSize}
	 */
	public static DataSize ofKilobytes(long kilobytes) {
		return new DataSize(Math.multiplyExact(kilobytes, BYTES_PER_KB));
	}

	/**
	 * Obtain a {@link DataSize} representing the specified number of megabytes.
	 * @param megabytes the number of megabytes, positive or negative
	 * @return a {@link DataSize}
	 */
	public static DataSize ofMegabytes(long megabytes) {
		return new DataSize(Math.multiplyExact(megabytes, BYTES_PER_MB));
	}

	/**
	 * Obtain a {@link DataSize} representing the specified number of gigabytes.
	 * @param gigabytes the number of gigabytes, positive or negative
	 * @return a {@link DataSize}
	 */
	public static DataSize ofGigabytes(long gigabytes) {
		return new DataSize(Math.multiplyExact(gigabytes, BYTES_PER_GB));
	}

	/**
	 * Obtain a {@link DataSize} representing the specified number of terabytes.
	 * @param terabytes the number of terabytes, positive or negative
	 * @return a {@link DataSize}
	 */
	public static DataSize ofTerabytes(long terabytes) {
		return new DataSize(Math.multiplyExact(terabytes, BYTES_PER_TB));
	}

	/**
	 * Obtain a {@link DataSize} representing an amount in the specified {@link DataUnit}.
	 * @param amount the amount of the size, measured in terms of the unit,
	 * positive or negative
	 * @return a corresponding {@link DataSize}
	 */
	public static DataSize of(long amount, DataUnit unit) {
		Objects.requireNonNull(unit, "Unit must not be null");
		return new DataSize(Math.multiplyExact(amount, unit.getSize().getBytes()));
	}

	/**
	 * Obtain a {@link DataSize} from a text string such as {@code 12MB} using
	 * the specified default {@link DataUnit} if no unit is specified.
	 * <p>
	 * The string starts with a number followed optionally by a unit matching one of the
	 * supported {@linkplain DataUnit suffixes}.
	 * <p>
	 * Examples:
	 * <pre>
	 * "12KB" -- parses as "12 kilobytes"
	 * "5MB"  -- parses as "5 megabytes"
	 * "20"   -- parses as "20 kilobytes" (where the {@code defaultUnit} is {@link DataUnit#KILOBYTES})
	 * </pre>
	 * @param text the text to parse
	 * @return the parsed {@link DataSize}
	 */
	public static DataSize parse(String text) {
		Objects.requireNonNull(text, "Text must not be null");
		try {
			Matcher matcher = DataSizeUtils.PATTERN.matcher(text.trim());
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid data size: " + text);
			}
			DataUnit unit = DataSizeUtils.determineDataUnit(matcher.group(2));
			long amount = Long.parseLong(matcher.group(1));
			return DataSize.of(amount, unit);
		}
		catch (Exception ex) {
			throw new IllegalArgumentException("'" + text + "' is not a valid data size", ex);
		}
	}

	/**
	 * Static nested class to support lazy loading of the {@link #PATTERN}.
	 * @since 5.3.21
	 */
	private static class DataSizeUtils {
		/**
		 * The pattern for parsing.
		 */
		private static final Pattern PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

		private static DataUnit determineDataUnit(String suffix) {
			return (StrUtil.isNotBlank(suffix) ? DataUnit.fromSuffix(suffix) : DataUnit.BYTES);
		}

	}
}
