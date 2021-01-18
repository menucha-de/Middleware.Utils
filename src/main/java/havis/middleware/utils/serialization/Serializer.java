package havis.middleware.utils.serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This class can be used to simple serialize or de-serialize XML data from or
 * to a file.
 */
public class Serializer {

	/**
	 * Gets the context
	 * 
	 * @param clazz
	 * @return The context
	 * @throws JAXBException
	 */
	public static JAXBContext getContext(Class<?> clazz) throws JAXBException {
		return JAXBContext.newInstance(clazz);
	}

	/**
	 * Deserializes a new instance of class given by type de-serialized from
	 * file.
	 * 
	 * @param reader
	 *            The reader
	 * @param unmarshaller
	 *            The unmarshaller
	 * @return The deserialized instance
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Reader reader, Unmarshaller unmarshaller)
			throws JAXBException {
		return (T) unmarshaller.unmarshal(reader);
	}

	/**
	 * Deserializes a new instance of class given by type de-serialized from
	 * file.
	 * 
	 * @param stream
	 *            The stream on a XML document
	 * @param unmarshaller
	 *            The unmarshaller
	 * @return The deserialized object
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	public static <T> T deserialize(InputStream stream,
			Unmarshaller unmarshaller) throws JAXBException {
		return deserialize(new InputStreamReader(stream), unmarshaller);
	}

	/**
	 * Deserializes a new instance of class given by type de-serialized from
	 * file.
	 * 
	 * @param file
	 *            The file of the XML document
	 * @param unmarshaller
	 *            The unmarshaller
	 * @return The deserialized object
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static <T> T deserialize(File file, Unmarshaller unmarshaller)
			throws IOException, JAXBException {
		if (file.isFile()) {
			FileReader reader = new FileReader(file);
			try {
				return deserialize(reader, unmarshaller);
			} finally {
				reader.close();
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' not found");
		}
	}

	/**
	 * Returns the class instance from XML String
	 * 
	 * @param string
	 *            The input string
	 * @param unmarshaller
	 *            The unmarshaller
	 * @return The class instance
	 * @throws JAXBException
	 */
	public static <T> T deserialize(String string, Unmarshaller unmarshaller)
			throws JAXBException {
		return deserialize(new StringReader(string), unmarshaller);
	}

	/**
	 * Serializes the given object to a XML document with specific filename.
	 * 
	 * @param file
	 *            The output file
	 * @param t
	 *            The object to serialize
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static <T> void serialize(File file, T t, JAXBContext context)
			throws IOException, JAXBException {
		FileWriter writer = new FileWriter(file);
		try {
			serialize(writer, t, context);
		} finally {
			writer.close();
		}
	}

	/**
	 * Serializes the given object to a stream.
	 * 
	 * @param writer
	 *            The output writer
	 * @param t
	 *            The object to serialize
	 * @throws JAXBException
	 */
	public static <T> void serialize(Writer writer, T t, JAXBContext context)
			throws JAXBException {
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(t, writer);
	}

	/**
	 * Returns the object XML String representation
	 * 
	 * @param t
	 *            The object instance
	 * @return The serialized object as XML string
	 * @throws JAXBException
	 */
	public static <T> String serialize(T t, JAXBContext context)
			throws JAXBException {
		StringWriter writer = new StringWriter();
		serialize(writer, t, context);
		return writer.toString();
	}
}
