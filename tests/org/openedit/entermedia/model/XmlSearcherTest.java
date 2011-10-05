package org.openedit.entermedia.model;

import org.openedit.Data;
import org.openedit.data.Searcher;
import org.openedit.entermedia.BaseEnterMediaTest;
import org.openedit.xml.XmlSearcher;

import com.openedit.hittracker.HitTracker;
import com.openedit.hittracker.SearchQuery;

public class XmlSearcherTest extends BaseEnterMediaTest
{
	public void testXmlSearchMultipleMatches()
	{
		Searcher testsearcher = getMediaArchive().getSearcherManager().getSearcher("entermedia", "testsearch");
		assertTrue(testsearcher instanceof XmlSearcher);
		Data data = testsearcher.createNewData();
		data.setProperty("val", "17");
		data.setId("1");
		testsearcher.saveData(data, null);
		SearchQuery query = testsearcher.createSearchQuery();
		query.setAndTogether(false);
		query.addMatches("val", "17");
		HitTracker hits = testsearcher.search(query);
		assertTrue(hits.size() > 0);
		query.addMatches("val", "18");
		hits = testsearcher.search(query);
		assertTrue(hits.size() > 0);
	}
}
