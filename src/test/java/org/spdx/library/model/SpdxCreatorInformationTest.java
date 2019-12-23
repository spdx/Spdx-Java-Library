package org.spdx.library.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

import junit.framework.TestCase;

public class SpdxCreatorInformationTest extends TestCase {
	
	GenericModelObject gmo;

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVerify() throws InvalidSPDXAnalysisException {
		SpdxCreatorInformation ci = new SpdxCreatorInformation();
		assertEquals(2, ci.verify().size());
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		String date = format.format(new Date());
		ci.setCreated(date);
		assertEquals(1, ci.verify().size());
		ci.getCreators().add("Person: me");
		assertEquals(0, ci.verify().size());
		// bad creator
		ci.getCreators().add("bad");
		assertEquals(1, ci.verify().size());
		// bad date
		ci.setCreated("bad date");
		assertEquals(2, ci.verify().size());
	}

	public void testGetSetCreators() throws InvalidSPDXAnalysisException {
		List<String> creators = new ArrayList<>(Arrays.asList(new String[] {"Person: me"}));
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		String date = format.format(new Date());
		SpdxCreatorInformation ci = gmo.createCreationInfo(creators, date);
		assertEquals(0, ci.verify().size());
		Collection<String> result = ci.getCreators();
		assertEquals(creators.size(), result.size());
		assertTrue(result.containsAll(creators));
		String addedCreator = "Organization: org";
		result.add(addedCreator);
		creators.add(addedCreator);
		assertEquals(creators.size(), result.size());
		assertTrue(result.containsAll(creators));
		assertTrue(ci.getCreators().containsAll(creators));
		assertEquals(0, ci.verify().size());
	}

	public void testGetSetLicenseListVersion() throws InvalidSPDXAnalysisException {
		List<String> creators = new ArrayList<>(Arrays.asList(new String[] {"Person: me"}));
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		String date = format.format(new Date());
		SpdxCreatorInformation ci = gmo.createCreationInfo(creators, date);
		assertEquals(0, ci.verify().size());
		assertFalse(ci.getLicenseListVersion().isPresent());
		String licenseVersion = "1.1";
		ci.setLicenseListVersion(licenseVersion);
		assertEquals(licenseVersion, ci.getLicenseListVersion().get());
		assertEquals(0, ci.verify().size());
	}

	public void testGetSetComment() throws InvalidSPDXAnalysisException {
		List<String> creators = new ArrayList<>(Arrays.asList(new String[] {"Person: me"}));
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		String date = format.format(new Date());
		SpdxCreatorInformation ci = gmo.createCreationInfo(creators, date);
		assertEquals(0, ci.verify().size());
		assertFalse(ci.getComment().isPresent());
		String comment = "a comment";
		ci.setComment(comment);
		assertEquals(comment, ci.getComment().get());
		assertEquals(0, ci.verify().size());
	}

	public void testGetSetCreated() throws InvalidSPDXAnalysisException {
		List<String> creators = new ArrayList<>(Arrays.asList(new String[] {"Person: me"}));
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		String date = format.format(new Date());
		SpdxCreatorInformation ci = gmo.createCreationInfo(creators, date);
		assertEquals(0, ci.verify().size());
		assertEquals(date, ci.getCreated().get());
		String oldDate = format.format(new Date(10101));
		ci.setCreated(oldDate);
		assertEquals(oldDate, ci.getCreated().get());
		assertEquals(0, ci.verify().size());
	}

}
