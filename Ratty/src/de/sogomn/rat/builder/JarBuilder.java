package de.sogomn.rat.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.Server;

public final class JarBuilder {
	
	private static final File JAR_FILE;
	
	static {
		File jarFile = null;
		
		try {
			jarFile = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (final URISyntaxException ex) {
			ex.printStackTrace();
		}
		
		JAR_FILE = jarFile;
	}
	
	private JarBuilder() {
		//...
	}
	
	public static void removeFile(final File jar, final String file) throws IOException {
		final Path jarPath = jar.toPath();
		final FileSystem fileSystem = FileSystems.newFileSystem(jarPath, null);
		final Path path = fileSystem.getPath(file);
		
		Files.delete(path);
		
		fileSystem.close();
	}
	
	public static void build(final File destination, final String replacement, final byte[] replacementData) throws IOException {
		FileUtils.copyFile(JAR_FILE, destination);
		
		final Path destinationPath = destination.toPath();
		final ByteArrayInputStream in = new ByteArrayInputStream(replacementData);
		final FileSystem fileSystem = FileSystems.newFileSystem(destinationPath, null);
		final Path replacementPath = fileSystem.getPath(replacement);
		
		Files.copy(in, replacementPath, StandardCopyOption.REPLACE_EXISTING);
		
		fileSystem.close();
		in.close();
	}
	
}
