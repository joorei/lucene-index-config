package org.codeturnery.lucene.transfer.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.codeturnery.characters.ReplacingReader;

public class XsdPreprocessor {
	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"Invalid number of arguments. Expected 3 arguments, i.e. the path to the properties file, the path to the input file to process and the path to the output file to write.");
		}

		final Path propertiesFilePath = Paths.get(args[0]);
		final Path inputFilePath = Paths.get(args[1]);
		final Path outputFilePath = Paths.get(args[2]);

		final var properties = new Properties();
		try (final var fis = new FileInputStream(propertiesFilePath.toFile())) {
			properties.loadFromXML(fis);
		}

		try (final var fileReader = new FileReader(inputFilePath.toFile());
				final var bufReader = new BufferedReader(fileReader);
				final var reader = ReplacingReader.fromStrings(bufReader, '{', '}', properties::getProperty);
				final var fileWriter = new FileWriter(outputFilePath.toFile());
				final var bufWriter = new BufferedWriter(fileWriter);) {
			reader.transferTo(bufWriter);
			bufWriter.flush();
		}

		// do not invoke System.exit, as this closes the whole process,
		// which is undesired when running this class via Maven
	}
}
