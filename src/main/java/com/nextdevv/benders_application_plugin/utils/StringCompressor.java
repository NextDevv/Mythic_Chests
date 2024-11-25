package com.nextdevv.benders_application_plugin.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Utility class for compressing and decompressing strings using DEFLATE algorithm and RLE
 *
 * @author giovanni
 */
public class StringCompressor {
    private static final int BUFFER_SIZE = 1024;

    /**
     * Compresses a string using DEFLATE algorithm and Base64 encoding
     * @param input The string to compress
     * @return Compressed string in Base64 format
     * @throws RuntimeException if compression fails
     */
    public static String compress(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        try {
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

            Deflater deflater = new Deflater();
            deflater.setLevel(Deflater.BEST_COMPRESSION);
            deflater.setInput(inputBytes);
            deflater.finish();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputBytes.length);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            deflater.end();
            outputStream.close();

            byte[] compressedBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(compressedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error compressing string", e);
        }
    }

    /**
     * Decompresses a string that was compressed using the compress method
     * @param compressedString The compressed string in Base64 format
     * @return Original decompressed string
     * @throws RuntimeException if decompression fails
     */
    public static String decompress(String compressedString) {
        if (compressedString == null || compressedString.isEmpty()) {
            return compressedString;
        }

        try {
            byte[] compressedBytes = Base64.getDecoder().decode(compressedString);

            Inflater inflater = new Inflater();
            inflater.setInput(compressedBytes);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedBytes.length);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            inflater.end();
            outputStream.close();

            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decompressing string", e);
        }
    }

    /**
     * Alternative compression method for very short strings using run-length encoding
     * @param input The string to compress
     * @return Compressed string using RLE
     */
    public static String compressRLE(String input) {
        if (input == null || input.length() <= 3) {
            return input;
        }

        StringBuilder compressed = new StringBuilder();
        char currentChar = input.charAt(0);
        int count = 1;

        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == currentChar) {
                count++;
            } else {
                compressed.append(count).append(currentChar);
                currentChar = input.charAt(i);
                count = 1;
            }
        }
        compressed.append(count).append(currentChar);

        return compressed.length() < input.length() ? compressed.toString() : input;
    }

    /**
     * Decompresses a string that was compressed using the compressRLE method
     * @param compressedString The RLE compressed string
     * @return Original decompressed string
     */
    public static String decompressRLE(String compressedString) {
        if (compressedString == null || compressedString.length() <= 3) {
            return compressedString;
        }

        StringBuilder decompressed = new StringBuilder();
        StringBuilder count = new StringBuilder();

        for (int i = 0; i < compressedString.length(); i++) {
            char c = compressedString.charAt(i);
            if (Character.isDigit(c)) {
                count.append(c);
            } else {
                int repetitions = Integer.parseInt(count.toString());
                decompressed.append(String.valueOf(c).repeat(repetitions));
                count.setLength(0);
            }
        }

        return decompressed.toString();
    }

    /**
     * Utility method to determine the best compression method and compress accordingly
     * @param input The string to compress
     * @return Compressed string using the most efficient method
     */
    public static String smartCompress(String input) {
        if (input == null || input.length() < 4) {
            return input;
        }

        String rleCompressed = compressRLE(input);
        if (rleCompressed.length() < input.length()) {
            return "RLE:" + rleCompressed;
        }

        String deflateCompressed = compress(input);
        if (deflateCompressed.length() < input.length()) {
            return "DEF:" + deflateCompressed;
        }

        return input;
    }

    /**
     * Decompresses a string that was compressed using smartCompress
     * @param input The compressed string
     * @return Original decompressed string
     */
    public static String smartDecompress(String input) {
        if (input == null || input.length() < 4) {
            return input;
        }

        if (input.startsWith("RLE:")) {
            return decompressRLE(input.substring(4));
        } else if (input.startsWith("DEF:")) {
            return decompress(input.substring(4));
        }

        return input;
    }
}