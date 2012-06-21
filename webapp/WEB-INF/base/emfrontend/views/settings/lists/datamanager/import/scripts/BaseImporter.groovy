import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;

import org.openedit.*;
import org.openedit.data.BaseData;
import org.openedit.data.PropertyDetail;
import org.openedit.data.PropertyDetails;
import org.openedit.data.Searcher;
import org.openedit.entermedia.util.CSVReader;
import org.openedit.entermedia.util.Header
import org.openedit.entermedia.util.ImportFile;
import org.openedit.entermedia.util.Row;

import com.openedit.WebPageRequest;
import com.openedit.entermedia.scripts.EnterMediaObject;
import com.openedit.page.Page;
import com.openedit.util.*;

public class BaseImporter extends EnterMediaObject 
{
	protected Map<String,String> fieldLookUps;
	 
	public void importData() throws Exception 
	{
		Searcher searcher = loadSearcher(context);

		String path = context.getRequestParameter("path");
		Page upload = getPageManager().getPage(path);
		Reader reader = upload.getReader();
		List data = new ArrayList();
		
		try 
		{
			ImportFile file = new ImportFile();
			file.setParser(new CSVReader(reader, ',', '\"'));
			file.read(reader);
						
			createMetadata(searcher,file.getHeader());

			Row trow = null;
			int rowNum = 0;
			while( (trow = file.getNextRow()) != null )
			{
				rowNum++;

				String idCell = trow.get("id");

				if (idCell != null) 
				{
					Data target = findExistingData(searcher,idCell);
					for (Iterator iterator = data.iterator(); iterator.hasNext();) 
					{
						Data one = (Data) iterator.next();
						if(idCell.equals(one.getId()))
						{
							target = one;
						}
					}
					if (target == null) 
					{

						target = searcher.createNewData();
						target.setId(idCell);

					}
					addProperties(searcher, trow, target);
					data.add(target);
				}
				if ( data.size() == 100 )
				{
					searcher.saveAllData(data, context.getUser());
					data.clear();
				}
			}
		} 
		finally 
		{
			FileUtils.safeClose(reader);
			getPageManager().removePage(upload);
		}
		searcher.saveAllData(data, context.getUser());
		//searcher.reIndexAll();
	}
	
	/** Might be overriden by scripts */
	protected Data findExistingData(Searcher inSearcher, String inId )
	{
		return (Data) inSearcher.searchById(inId);
	}
	protected Map<String,Map> getLookUps()
	{
		if( fieldLookUps == null )
		{
			fieldLookUps = new HashMap<String,Map>();
			//fieldLookUps.put("Division", "val_Archive_Division");
		}
		return fieldLookUps;
	}
	protected void createMultiSelect(String inCatalogId, MultiValued inRow, String inField, String inTable)
	{
		String value = inRow.get(inField);
		if( value != null )
		{			
			Map datavalues = loadValueList(inField)
			Collection values = EmStringUtils.split(value);
			List valueids = new ArrayList();
			for (String val: values)
			{
				String id = PathUtilities.makeId(val).toLowerCase();
				Data data = datavalues.get(id);
				if( data == null )
				{
					//create it
					Searcher searcher = getSearcherManager().getSearcher(inCatalogId, inTable);
					data = searcher.searchById(id);
					if( data == null )
					{
						data = searcher.createNewData();
						data.setId(id);
						data.setName(val);
						searcher.saveData(data, null);
					}
					datavalues.put(id,  data);
				}
				valueids.add(id); //save it
			}
			inRow.setValues(inField, valueids);
		}

	}

	private HashMap loadValueList(String inField) {
		Map datavalues = getLookUps().get(inField);
		if( datavalues == null )
		{
			datavalues = new HashMap();
			getLookUps().put( inField, datavalues);
		}
		return datavalues
	}
	protected void createLookUp(String inCatalogId, Data inRow, String inField, String inTable)
	{
		String value = inRow.get(inField);
		if( value != null )
		{
			int comma = value.indexOf(",");
			if( comma > 0 )
			{
				value = value.substring(0,comma);
			}
			Map datavalues = loadValueList(inField)
			Data data = datavalues.get(value);
			if( data == null )
			{
				//create it
				String id = PathUtilities.makeId(value);
				Searcher searcher = getSearcherManager().getSearcher(inCatalogId, inTable);
				data = searcher.searchById(id);
				if( data == null )
				{
					data = searcher.createNewData();
					data.setId(id);
					data.setName(value);
					searcher.saveData(data, null);
				}
				datavalues.put(value,  data);
			}
			inRow.setProperty(inField, data.id);
		} 
	}
	
	protected void createMetadata(Searcher inSearcher, Header inHeader)
	{
		PropertyDetails details = inSearcher.getPropertyDetails();
		for (Iterator iterator = inHeader.getHeaderNames().iterator(); iterator.hasNext();)
		{
			String header = (String)iterator.next();
			//String header = inHeaders[i];
			String id = PathUtilities.makeId(header);
			PropertyDetail detail = details.getDetail(id);
			if( detail == null )
			{
				detail = new PropertyDetail();
				detail.setId(id);
				detail.setText(header);
				detail.setEditable(true);
				detail.setIndex(true);
				detail.setStored(true);
		
				details.addDetail(detail);
				inSearcher.getPropertyDetailsArchive().savePropertyDetails(details, inSearcher.getSearchType(), context.getUser());
			}
		}

	}

	protected void addProperties(Searcher inSearcher, Row inRow, Data inData) 
	{
		for (int i = 0; i < inRow.getData().length; i++)
		{
			String val = inRow.getData(i);
			String header = inRow.getHeader().getColumn(i);
			String headerid = PathUtilities.makeId(header);

			val = URLUtilities.xmlEscape(val);
			if("sourcepath".equals(header))
			{
				inData.setSourcePath(val);
			}
			else if (val != null && val.length() > 0) 
			{
				inData.setProperty(headerid, val);
			}
		}
	}
}