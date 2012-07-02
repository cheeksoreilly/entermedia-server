/*
 * Created on Oct 2, 2005
 */
package org.openedit.entermedia.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openedit.Data;
import org.openedit.entermedia.Asset;
import org.openedit.entermedia.AssetUtilities;
import org.openedit.entermedia.Category;
import org.openedit.entermedia.CompositeAsset;
import org.openedit.entermedia.MediaArchive;
import org.openedit.entermedia.fetch.UrlMetadataImporter;
import org.openedit.entermedia.search.AssetSearcher;
import org.openedit.repository.ContentItem;
import org.openedit.util.DateStorageUtil;

import com.openedit.OpenEditException;
import com.openedit.hittracker.HitTracker;
import com.openedit.hittracker.SearchQuery;
import com.openedit.page.Page;
import com.openedit.page.manage.PageManager;
import com.openedit.users.User;
import com.openedit.util.PathProcessor;
import com.openedit.util.PathUtilities;
import com.openedit.util.ZipUtil;

public class AssetImporter
{
	// protected List fieldInputTypes;
	protected PageManager fieldPageManager;
	private static final Log log = LogFactory.getLog(AssetImporter.class);
	protected Boolean fieldLimitSize;
	protected AssetUtilities fieldAssetUtilities;
    protected List<UrlMetadataImporter> fieldUrlMetadataImporters;
    protected boolean fieldUseFolders = false;
//    
//	protected List fieldIncludeExtensions;
//	protected List fieldExcludeExtensions;
//	protected List fieldExcludeFolderMatch;
    
    protected List<String> fieldExcludeMatches;
    protected String fieldIncludeExtensions;
    protected Collection fieldAttachmentFilters;
    protected Boolean fieldOnWindows;
    
	public Collection getAttachmentFilters()
	{
		return fieldAttachmentFilters;
	}

	public void setAttachmentFilters(Collection inAttachmentFilters)
	{
		fieldAttachmentFilters = inAttachmentFilters;
	}

	public List<String> getExcludeMatches()
	{
		return fieldExcludeMatches;
	}

	public void setExcludeMatches(List<String> inExcludeFolders)
	{
		fieldExcludeMatches = inExcludeFolders;
	}

	public String getIncludeExtensions()
	{
		return fieldIncludeExtensions;
	}

	public void setIncludeExtensions(String inIncludeFiles)
	{
		fieldIncludeExtensions = inIncludeFiles;
	}

	public boolean isUseFolders()
	{
		return fieldUseFolders;
	}

	public void setUseFolders(boolean inUseFolders)
	{
		fieldUseFolders = inUseFolders;
	}

	public AssetUtilities getAssetUtilities()
	{
			return fieldAssetUtilities;
	}

	public void setAssetUtilities(AssetUtilities inAssetUtilities)
	{
		fieldAssetUtilities = inAssetUtilities;
	}

	public void processOnAll(String inRootPath, final MediaArchive inArchive, User inUser)
	{
		for (Iterator iterator = getPageManager().getChildrenPaths(inRootPath).iterator(); iterator.hasNext();)
		{
			String path = (String) iterator.next();
			Page topLevelPage = getPageManager().getPage(path);
			if (topLevelPage.isFolder() && !topLevelPage.getPath().endsWith("/CVS") && !topLevelPage.getPath().endsWith(".versions"))
			{
				processOn(inRootPath, path,inArchive, 0, inUser);
			}
		}
	}
	
	public List<String> processOn(String inRootPath, String inStartingPoint, final MediaArchive inArchive, final long inLackCheckedTime, User inUser)
	{
		final List assets = new ArrayList();
		final List<String> assetsids = new ArrayList<String>();
		PathProcessor finder = new PathProcessor()
		{
			public void process(ContentItem inInput, User inUser)
			{
				if (inInput.isFolder())
				{
					if (acceptDir(inInput))
					{
						processAssetFolder(inArchive, inInput, inUser);
					}
				}
				else
				{
					if (acceptFile(inInput))
					{
						processFile(inInput, inUser);
					}
				}
			}
			protected void processAssetFolder(final MediaArchive inArchive, ContentItem inInput, User inUser)
			{
				String sourcepath = getAssetUtilities().extractSourcePath(inInput, inArchive);
				Asset asset = inArchive.getAssetArchive().getAssetBySourcePath(sourcepath);
				if( asset != null)
				{
					//check this one primary asset to see if it changed
					if( asset.getPrimaryFile() != null)
					{
						inInput = getPageManager().getRepository().getStub(inInput.getPath() + "/" + asset.getPrimaryFile());
						asset = getAssetUtilities().populateAsset(asset, inInput, inArchive, sourcepath, inUser);
						if( asset != null)
						{
							assets.add(asset);
							if (assets.size() > 100)
							{
								saveImportedAssets(assets, assetsids, inArchive, inUser);
							}
						}
					}
					//dont process sub-folders
				}
				else
				{
					//look deeper for assets
					List paths = getPageManager().getChildrenPaths(inInput.getPath());
					if( paths.size() == 0 )
					{
						return;
					}
					boolean processchildren = true;
					if( isUseFolders() || createAttachments(paths) )
					{
						//Use the first file that is not a folder
						ContentItem found = findPrimary(paths);
						if( found == null )
						{
							found = inInput;
						}
						String foundprimary = PathUtilities.extractFileName(found.getPath());
						String soucepath = getAssetUtilities().extractSourcePath(inInput, inArchive);

						asset = inArchive.createAsset(soucepath);
						asset.setFolder(true);
						asset.setProperty("datatype", "original");
						if( inUser != null )
						{
							asset.setProperty("owner", inUser.getUserName());
						}
						asset.setProperty("assetaddeddate",DateStorageUtil.getStorageUtil().formatForStorage(new Date()));
						asset.setProperty("assetviews", "1");
						asset.setProperty("importstatus", "imported");
						asset.setPrimaryFile(foundprimary);
						getAssetUtilities().readMetadata(asset, found, inArchive);
						getAssetUtilities().populateCategory(asset, inInput, inArchive, inUser);
						//asset = getAssetUtilities().createAssetIfNeeded(item, inArchive, inUser);
						//set the primary file
						assets.add(asset);
						if (assets.size() > 100)
						{
							saveImportedAssets(assets, assetsids, inArchive, inUser);
						}

						processchildren = false;
					}
					else
					{
						processchildren = true;
					}
					if( processchildren)
					{
						boolean checkfiles = true;
						if( inLackCheckedTime > 0 && isOnWindows() && inLackCheckedTime < inInput.getLastModified() )  //On Windows the folder times stamp matches the most recently modified file
						{
							checkfiles = false;
						}
						for (Iterator iterator = paths.iterator(); iterator.hasNext();)
						{
							String path = (String) iterator.next();
							ContentItem item = getPageManager().getRepository().getStub(path);
							if( isRecursive() )
							{
								if( checkfiles ||  item.isFolder())
								{
									process(item, inUser);
								}
							}
						}
					}
				}
			}
			protected ContentItem findPrimary(List inPaths)
			{
				for (Iterator iterator = inPaths.iterator(); iterator.hasNext();)
				{
					String path = (String) iterator.next();
					ContentItem item = getPageManager().getRepository().getStub(path);
					if( !item.isFolder() && acceptFile(item))
					{
						return item;
					}
				}
				return null;
			}
			public void processFile(ContentItem inContent, User inUser)
			{
				if( !isUseFolders() ) 
				{
					Asset asset = getAssetUtilities().createAssetIfNeeded(inContent, inArchive, inUser);
					if( asset != null)
					{
						assets.add(asset);
						if (assets.size() > 100)
						{
							saveImportedAssets(assets,assetsids, inArchive, inUser);
						}
					}
				}
			}
		};
		finder.setPageManager(getPageManager());
		finder.setRootPath(inRootPath);
		finder.setExcludeMatches(getExcludeMatches()); //The rest should be filtered by the mount itself
		finder.setIncludeExtensions(getIncludeExtensions());
		finder.process(inStartingPoint, inUser);

		// Windows, for instance, has an absolute file system path limit of 256
		// characters
		if( isOnWindows() )
		{
			checkPathLengths(inArchive, assets);
		}
		saveImportedAssets(assets, assetsids, inArchive,  inUser);
		return assetsids;
	}
	protected boolean createAttachments(List inPaths)
	{
		if( fieldAttachmentFilters == null )
		{
			return false;
		}
		for (Iterator iterator = getAttachmentFilters().iterator(); iterator.hasNext();)
		{
			String check = (String) iterator.next();
			for (Iterator iterator2 = inPaths.iterator(); iterator2.hasNext();)
			{
				String path = (String) iterator2.next();
				if( PathUtilities.match(path, check) )
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * This method removes any assets from the list that have absolute file
	 * paths which are too long. Should be used on windows servers that can't
	 * handle more than 256 characters in file names.
	 * 
	 * @param inArchive
	 * @param inAssets
	 */
	private void checkPathLengths(MediaArchive inArchive, List inAssets)
	{
		if (isSizeLimited().booleanValue())
		{
			int absolutepathlimit = 260;
			for (Iterator iterator = inAssets.iterator(); iterator.hasNext();)
			{
				Asset asset = (Asset) iterator.next();
				String path = inArchive.getAssetArchive().buildXmlPath(asset);
				ContentItem item = getPageManager().getPageSettingsManager().getRepository().get(path);
				if (item.getAbsolutePath().length() > absolutepathlimit)
				{
					log.info("Path too long. Couldn't save " + item.getPath());
					iterator.remove();
				}
			}
		}
	}

	protected void saveImportedAssets(List inAssets, List allassetids, MediaArchive inArchive, User inUser) throws OpenEditException
	{
		if (inAssets.size() == 0)
		{
			return;
		}
		Asset	eventasset = (Asset)inAssets.get(0);	
		List<String> someids = new ArrayList();

		for (Iterator iter = inAssets.iterator(); iter.hasNext();)
		{
			Asset asset = (Asset) iter.next();
			inArchive.getAssetArchive().saveAsset(asset);
			inArchive.fireMediaEvent("assetcreated",inUser, asset);
			someids.add(asset.getId());
		}
		allassetids.addAll(someids);
		inArchive.fireMediaEvent("importing/assetsimported", inUser, eventasset, someids);

		inArchive.getAssetSearcher().updateIndex(inAssets, false);
		inArchive.getAssetArchive().clearAssets();

		inAssets.clear();
	}

	public PageManager getPageManager()
	{
		return fieldPageManager;
	}

	public void setPageManager(PageManager inPageManager)
	{
		fieldPageManager = inPageManager;
	}

	public Boolean isSizeLimited()
	{
		if (fieldLimitSize == null)
		{
			if (System.getProperty("os.name").toLowerCase().contains("windows"))
			{
				fieldLimitSize = Boolean.TRUE;
			}
			else
			{
				fieldLimitSize = Boolean.FALSE;
			}
		}
		return fieldLimitSize;
	}

	//TODO: Reconcile this code with AssetUtilities.pupulateAsset


	public Data createAssetFromExistingFile( MediaArchive inArchive, User inUser, boolean unzip,  String inSourcepath)
	{
		String catalogid = inArchive.getCatalogId();
		
		String originalspath = "/WEB-INF/data/" + catalogid + "/originals/";
		Page page = getPageManager().getPage(originalspath + inSourcepath );
		if( !page.exists() )
		{
			return null;
		}

		String ext = PathUtilities.extractPageType(page.getName());
		if(unzip && "zip".equalsIgnoreCase(ext))
		{
			//unzip and create
			CompositeAsset results = new CompositeAsset();
			//the folder we are in
			Page parentfolder = getPageManager().getPage( page.getParentPath() );
			File dest = new File(parentfolder.getContentItem().getAbsolutePath());
			String destpath = dest.getAbsolutePath();
			ZipUtil zip = new ZipUtil();
			zip.setPageManager(getPageManager());
			try
			{
				List files = zip.unzip(page.getContentItem().getInputStream(), dest);
				for(Object o: files)
				{
					File f = (File) o;
					String path = f.getAbsolutePath().substring(destpath.length());
					path = path.replace('\\', '/');
					path =parentfolder.getPath() + path; //fix slashes
					Page p = getPageManager().getPage(path);
					Asset asset = createAssetFromPage(inArchive, inUser, p);
					if(asset != null)
					{
						results.addData(asset);
					}
				}
				
				getPageManager().removePage(page);
				return results;
			}
			catch (Exception e)
			{
				throw new OpenEditException(e);
			}
		}
		else
		{
			return createAssetFromPage(inArchive, inUser, page);
		}
	}
	
	protected Asset createAssetFromPage(MediaArchive inArchive, User inUser, Page inAssetPage)
	{
		String originals = "/WEB-INF/data" +  inArchive.getCatalogHome() + "/originals/";
		String sourcepath = inAssetPage.getPath().substring(originals.length());
		Asset asset = inArchive.getAssetBySourcePath(sourcepath);
		
		if(asset == null)
		{
			String id = inArchive.getAssetSearcher().nextAssetNumber();
			asset = new Asset();
			asset.setId(id);
			asset.setSourcePath(sourcepath);
			//asset.setProperty("datatype", "original");
			asset.setFolder(inAssetPage.isFolder());
			String name = inAssetPage.getName();
			String ext = PathUtilities.extractPageType(name);
			if( ext != null)
			{
				ext = ext.toLowerCase();
			}
			asset.setProperty("fileformat", ext);
			asset.setName(name);
			asset.setCatalogId(inArchive.getCatalogId());
	
			String categorypath = PathUtilities.extractDirectoryPath(sourcepath);
			Category category = inArchive.getCategoryArchive().createCategoryTree(categorypath);
			asset.addCategory(category);
		}

		String absolutepath = inAssetPage.getContentItem().getAbsolutePath();
		File itemFile = new File(absolutepath);
		getAssetUtilities().getMetaDataReader().populateAsset(inArchive, itemFile, asset);
		inArchive.saveAsset(asset, inUser);
		
		return asset;
	}
	
	public List removeExpiredAssets(MediaArchive archive, String sourcepath, User inUser)
	{
		AssetSearcher searcher = archive.getAssetSearcher();
		SearchQuery q = searcher.createSearchQuery();
		HitTracker assets = null;
		if(sourcepath == null)
		{
			assets = searcher.getAllHits();
		}
		else
		{
			q.addStartsWith("sourcepath", sourcepath);
			assets = searcher.search(q);
		}
		List<String> removed = new ArrayList<String>();
		List<String> sourcepaths= new ArrayList<String>();
		
		for(Object obj: assets)
		{
			Data hit = (Data)obj;
			sourcepaths.add(hit.get("sourcepath")); //TODO: Move to using page of hits
			if( sourcepaths.size() > 250000)
			{
				log.error("Should not load up so many paths");
				break;
			}
		}
		for(String path: sourcepaths)
		{
			Asset asset = archive.getAssetBySourcePath(path);
			if( asset == null)
			{
				continue;
			}
			String assetsource = asset.getSourcePath();
			String pathToOriginal = "/WEB-INF/data" + archive.getCatalogHome() + "/originals/" + assetsource;
			if(asset.isFolder() && asset.getPrimaryFile() != null)
			{
				pathToOriginal = pathToOriginal + "/" + asset.getPrimaryFile();
			}
			Page page = getPageManager().getPage(pathToOriginal);
			if(!page.exists())
			{
				removed.add(asset.getSourcePath());
				archive.removeGeneratedImages(asset);
				archive.getAssetSearcher().delete(asset, inUser);
			}
		}
		return removed;
	}

	public Asset createAssetFromFetchUrl(MediaArchive inArchive, String inUrl, User inUser)
	{
		for(UrlMetadataImporter importer: getUrlMetadataImporters())
		{
			Asset asset = importer.importFromUrl(inArchive, inUrl, inUser);
			if( asset != null )
			{
				return asset;
			}
		}
		return null;
	}
		
	public List<UrlMetadataImporter> getUrlMetadataImporters()
	{
		if(fieldUrlMetadataImporters == null)
		{
			return new ArrayList<UrlMetadataImporter>();
		}
		return fieldUrlMetadataImporters;
	}

	public void setUrlMetadataImporters(List<UrlMetadataImporter> urlMetadataImporters)
	{
		fieldUrlMetadataImporters = urlMetadataImporters;
	}

	public void fetchMediaForAsset(MediaArchive inArchive, Asset inAsset, User inUser)
	{
			for(UrlMetadataImporter importer: getUrlMetadataImporters())
			{
				importer.fetchMediaForAsset(inArchive, inAsset,inUser);
			}
	}
	public Boolean isOnWindows()
	{
		if (fieldOnWindows == null)
		{
			if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
			{
				fieldOnWindows = Boolean.TRUE;
			}
			else
			{
				fieldOnWindows = Boolean.FALSE;
			}
			
		}
		return fieldOnWindows;
	}
	
}