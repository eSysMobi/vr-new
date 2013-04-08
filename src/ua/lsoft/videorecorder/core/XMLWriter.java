package ua.lsoft.videorecorder.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import ua.lsoft.videorecorder.utils.Utils;

import android.util.Xml;

public class XMLWriter {
	
	private static String fileName = "";

	public XMLWriter() {

	}

	public void writeState(DataPicking dataPicking, File folder, String fileName) throws IllegalArgumentException, IllegalStateException, IOException {
		FileOutputStream fos;
		File xml = new File(folder, fileName + ".xml");
		fos = new FileOutputStream(xml, true);

		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "UTF-8");
		if (!XMLWriter.fileName.equals(fileName)) {
			serializer.startDocument(null, Boolean.valueOf(true));
			XMLWriter.fileName = fileName;
		}
		
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

		serializer.startTag(null, "block");

		serializer.startTag(null, "time");
		serializer.text(Utils.getCurrentData());
		serializer.endTag(null, "time");

		serializer.startTag(null, "latitude");
		serializer.text(String.valueOf(dataPicking.getlGPS().getLatitude()));
		serializer.endTag(null, "latitude");
		
		serializer.startTag(null, "longitude");
		serializer.text(String.valueOf(dataPicking.getlGPS().getLongitude()));
		serializer.endTag(null, "longitude");
		
		serializer.startTag(null, "sensorX");
		serializer.text(String.valueOf(dataPicking.getlSensor().getX()));
		serializer.endTag(null, "sensorX");
		
		serializer.startTag(null, "sensorY");
		serializer.text(String.valueOf(dataPicking.getlSensor().getY()));
		serializer.endTag(null, "sensorY");
		
		serializer.startTag(null, "sensorZ");
		serializer.text(String.valueOf(dataPicking.getlSensor().getZ()));
		serializer.endTag(null, "sensorZ");
		
		serializer.startTag(null, "battery");
		serializer.text(String.valueOf(dataPicking.getrBattary().getLevel()));
		serializer.endTag(null, "battery");
		
		serializer.startTag(null, "power");
		serializer.text(String.valueOf(dataPicking.getrPower().isConnected()));
		serializer.endTag(null, "power");
		
		serializer.endDocument();

		serializer.flush();

		fos.close();
	}

}
