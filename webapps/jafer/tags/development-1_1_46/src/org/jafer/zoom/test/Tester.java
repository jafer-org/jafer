package org.jafer.zoom.test;

import java.io.StringWriter;

import org.z3950.zoom.ConnectionFactory;
import org.z3950.zoom.Connection;
import org.z3950.zoom.query.QueryFactory;
import org.z3950.zoom.Query;
import org.z3950.zoom.ResultSet;
import org.z3950.zoom.Record;
import org.z3950.zoom.SystemException;
import org.z3950.zoom.DiagnosticException;
import org.z3950.zoom.Exception;

import org.w3c.dom.Node;
import org.jafer.util.xml.XMLSerializer;

/***
	* Test class for demoing/testing the Zoom interface to Jafer. First runs a zoom
	* query with default for record syntax (returns oai_marc xml), then a test
	* returning mods xml. Note that the query is in CQL.
	*/
public class Tester {
	
	final int MAX_RECORDS = 3;
	
	public static void main(String[] args) {
		Tester tester = new Tester();
		tester.doDefaultTest();
		tester.doModsTest();
	}

	/**
	* Sample zoom query. Returns results in default (oai_marc xml) format.
	*/
	public void doDefaultTest() {
		Connection conn = null;
		try {
			Query q;
			conn = ConnectionFactory.newUnconnectedConnection("library.ox.ac.uk", 210);
			conn.connect();
			conn.setDatabaseName("advance");
			(q = QueryFactory.newQuery("org.z3950.zoom.query.CQL")).setValue("dc.title=frog");
			ResultSet results = conn.search(q);
			if (results == null) {
				System.out.println("Results is null.");
				return;
			}
			int n = results.getSize() > MAX_RECORDS ? MAX_RECORDS : results.getSize();
			for (int i = 0; i < n; i++) {
				Record record = results.getRecord(i+1);
				Object obj = record.getRawData();
				System.out.println("Zoom record returned...");
				System.out.println(toXMLString((Node)obj));
			}
		}
		catch (org.z3950.zoom.Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) try { conn.close(); } catch (Exception e) {}
		}
	}

	/**
	* Sample zoom query. Returns results in default (mods xml) format.
	*/
	public void doModsTest() {
		Connection conn = null;
		try {
			Query q;
			conn = ConnectionFactory.newUnconnectedConnection("library.ox.ac.uk", 210);
			conn.connect();
			conn.setDatabaseName("advance");
			conn.setPreferredRecordSyntax("http://www.loc.gov/mods/");
			(q = QueryFactory.newQuery("org.z3950.zoom.query.CQL")).setValue("dc.title=frog");
			ResultSet results = conn.search(q);
			if (results == null) {
				System.out.println("Results is null.");
				return;
			}
			int n = results.getSize() > MAX_RECORDS ? MAX_RECORDS : results.getSize();
			for (int i = 0; i < n; i++) {
				Record record = results.getRecord(i+1);
				Object obj = record.getRawData();
				System.out.println("Zoom record returned...");
				System.out.println(toXMLString((Node)obj));
			}
		}
		catch (org.z3950.zoom.Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) try { conn.close(); } catch (Exception e) {}
		}
	}

	/**
	* Helper class to print out the xml returned.
	*/
	public String toXMLString(Node node) {
		try	{
			StringWriter writer = new StringWriter();
			XMLSerializer.out(node, "xml", writer);
			writer.flush();
			return writer.toString();
		}	catch (org.jafer.exception.JaferException e) {
			return "Unable to convert XML to string:"+e.toString();
		}
	}


}


