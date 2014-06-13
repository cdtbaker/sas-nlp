package sanlp.nlp.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import sanlp.nlp.resources.OutputResources;

/**
 * Interface setting out the contract for writing the XML files to disk.
 * 
 */
public abstract class XMLWriter {
	private static final String OUTPUT_FOLDER = "/Users/danhowden/Desktop/";

	private Document doc = null;

	private Element rootElement = null;

	private String stylesheet = "";

	protected DecimalFormat finerDecimalFormat = new DecimalFormat("0.000");

	public XMLWriter(String root, String stylesheet) {
		this.createNewDocument();
		this.stylesheet = stylesheet;

		rootElement = doc.createElement(root);
		doc.appendChild(rootElement);

		ProcessingInstruction pi = doc.createProcessingInstruction(
				"xml-stylesheet",
				"type=\"text/xsl\" href=\"" + this.getStylesheetName()
						+ ".xsl\"");
		doc.insertBefore(pi, rootElement);
	}

	private void createNewDocument() {
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("CANNOT GENERATE DOCUMENT... " + e.getMessage());
		}

		this.doc = docBuilder.newDocument();
	}

	protected Document doc() {
		return this.doc;
	}

	protected Element root() {
		return this.doc().getDocumentElement();
	}

	public void generate() {
		try {
			this.generateXMLContents();
		} catch (XPathExpressionException e1) {
			System.err.println("FAILURE");
			e1.printStackTrace();
		}
		this.save();
	}

	public void save() {
		this.saveStylehsheet();

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			System.err.println("CANNOT SAVE XML DOCUMENT..." + e.getMessage());
		}

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(OUTPUT_FOLDER + ""
				+ this.getXMLFilename()));

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			System.err.println("FAILURE");
			System.err.println("CANNOT SAVE XML DOCUMENT..." + e.getMessage());
		}
	}

	private void saveStylehsheet() {
		if (this.stylesheet.equals(""))
			return;

		File f = new File(OUTPUT_FOLDER + this.getStylesheetName() + ".xsl");
		OutputStream output = null;

		try {
			output = new FileOutputStream(f);

			if (!f.exists()) {
				f.createNewFile();
			}

			InputStream content = OutputResources.class
					.getResourceAsStream(this.getStylesheetName() + ".xsl");

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = content.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			content.close();
			output.flush();
			output.close();

		} catch (FileNotFoundException e) {
			System.err.println("SDFSDFSWf");
			// ignore...
		} catch (IOException e) {
			System.err.println("tyity");
			// ignore...
		}
	}

	protected abstract void generateXMLContents()
			throws XPathExpressionException;

	public abstract String getXMLFilename();

	private String getStylesheetName() {
		return this.stylesheet;
	}
}
