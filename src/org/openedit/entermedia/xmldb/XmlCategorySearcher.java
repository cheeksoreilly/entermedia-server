package org.openedit.entermedia.xmldb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.openedit.Data;
import org.openedit.data.BaseSearcher;
import org.openedit.entermedia.Category;
import org.openedit.entermedia.CategoryArchive;

import com.openedit.OpenEditException;
import com.openedit.hittracker.HitTracker;
import com.openedit.hittracker.ListHitTracker;
import com.openedit.hittracker.SearchQuery;
import com.openedit.hittracker.Term;
import com.openedit.users.User;

public class XmlCategorySearcher extends BaseSearcher implements CategorySearcher
{
	protected CategoryArchive fieldCategoryArchive;
	
	public CategoryArchive getCategoryArchive()
	{
		return fieldCategoryArchive;
	}

	public void setCategoryArchive(CategoryArchive inCategoryArchive)
	{
		fieldCategoryArchive = inCategoryArchive;
	}
	
	@Override
	public void reIndexAll() throws OpenEditException
	{
		getCategoryArchive().reloadCategories();
	}
	@Override
	public Data createNewData()
	{
		return new Category();
	}
	@Override
	public SearchQuery createSearchQuery()
	{
		return new SearchQuery();
	}

	@Override
	public HitTracker search(SearchQuery inQuery)
	{
		ListHitTracker hits = new ListHitTracker();
		//load them all up?
		Term desc = inQuery.getTermByDetailId("description");
		if( desc == null)
		{
			desc = inQuery.getTermByDetailId("id");
		}
		if( desc != null && "*".equals( desc.getValue() ) ) 
		{
			hits.setList( getCategoryArchive().listAllCategories() );
			return hits;
		}
		
		Term id = inQuery.getTermByDetailId("id");
		if( id != null)
		{
			Category category = getCategoryArchive().getCategory(id.getValue());
			if( category != null)
			{
				hits.getList().add(category);
			}
		}
		
		Term name = inQuery.getTermByDetailId("name");
		if( name != null)
		{
			Category category = getCategoryArchive().getCategoryByName(name.getValue());
			if( category != null)
			{
				hits.getList().add(category);
			}
		}
		
		Term parentterm = inQuery.getTermByDetailId("parentid");
		if( parentterm != null)
		{
			Category category = getCategoryArchive().getCategory(parentterm.getValue());
			if( category != null)
			{
				hits.getList().addAll(category.getChildren());
			}
		}

		Term path = inQuery.getTermByTermId("path");
		if( path != null)
		{
			String paths = path.getValue();
			if( paths != null)
			{
				String[] parents = paths.split("/");
				Category hit = getCategoryArchive().getRootCategory();
				int i = 1;
				for (; i < parents.length; i++)
				{
					hit = hit.getChildByName(parents[i]);
					if( hit == null)
					{
						break;
					}
				}
				if( i == parents.length && hit != null)
				{
					hits.getList().add(hit);
				}
			}
		}

		
		return hits;
	}

	@Override
	public String getIndexId()
	{
		return String.valueOf( getCategoryArchive().getRootCategory().hashCode() );
	}

	@Override
	public void clearIndex()
	{
		
	}

	@Override
	public void deleteAll(User inUser)
	{
		getCategoryArchive().deleteCategory(getCategoryArchive().getRootCategory());
	}

	@Override
	public void delete(Data inData, User inUser)
	{
		Category cat = getCategoryArchive().getCategory(inData.getId());
		getCategoryArchive().deleteCategory(cat);
	}

	@Override
	public void saveData(Data inData, User inUser)
	{
		Collection<Data> list = new ArrayList<Data>(1);
		list.add(inData);
		saveAllData(list,inUser);
	}	
	@Override
	public void saveAllData(Collection<Data> inAll, User inUser)
	{
		//TODO: Remove all this. Categories are hard to maintain 
		for (Iterator iterator = inAll.iterator(); iterator.hasNext();)
		{
			Category cat = (Category) iterator.next();
			//this might be a temporary copy of a category
			if( cat.getParentCategory() == null)
			{
				Category existingcat = getCategoryArchive().getCategory(cat.getId());
				if( existingcat != null )
				{
					existingcat.getProperties().putAll(cat.getProperties());
					cat = existingcat;
				}
				else
				{
					//new one
					String parentid = cat.get("parentid");
					if( parentid != null)
					{
						Category parent = getCategoryArchive().getCategory(parentid);
						if( parent != null)
						{
							cat.setParentCategory(parent);
							parent.addChild(cat);
						}
					}
				}
			}
			getCategoryArchive().saveCategory(cat);
		}
	}

	@Override
	public Category getRootCategory()
	{
		return getCategoryArchive().getRootCategory();
	}
	@Override
	public void setCatalogId(String inCatalogId)
	{
		getCategoryArchive().setCatalogId(inCatalogId);
		super.setCatalogId(inCatalogId);
	}
}
