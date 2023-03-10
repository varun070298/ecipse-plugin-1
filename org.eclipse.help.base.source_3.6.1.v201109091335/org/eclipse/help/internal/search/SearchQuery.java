/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.search;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.*;

/**
 * An implementation of ISearchQuery.
 */
public class SearchQuery implements ISearchQuery {
	Collection fieldNames;
	boolean fieldSearch;
	String locale;
	String searchWord;
	public SearchQuery() {
		this("", false, new ArrayList(), Platform.getNL()); //$NON-NLS-1$
	}
	public SearchQuery(String searchWord, boolean fieldSearch,
			Collection fieldNames, String locale) {
		this.searchWord = searchWord;
		this.fieldSearch = fieldSearch;
		this.fieldNames = fieldNames;
		this.locale = locale;
	}
	/**
	 * Returns the fieldNames.
	 * 
	 * @return Collection
	 */
	public Collection getFieldNames() {
		return fieldNames;
	}

	/**
	 * Returns the fieldSearch.
	 * 
	 * @return boolean
	 */
	public boolean isFieldSearch() {
		return fieldSearch;
	}

	/**
	 * Returns the locale.
	 * 
	 * @return String
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Returns the searchWord.
	 * 
	 * @return String
	 */
	public String getSearchWord() {
		return searchWord;
	}

	/**
	 * Sets the fieldNames.
	 * 
	 * @param fieldNames
	 *            The fieldNames to set
	 */
	public void setFieldNames(Collection fieldNames) {
		this.fieldNames = fieldNames;
	}

	/**
	 * Sets the fieldSearch.
	 * 
	 * @param fieldSearch
	 *            The fieldSearch to set
	 */
	public void setFieldSearch(boolean fieldSearch) {
		this.fieldSearch = fieldSearch;
	}

	/**
	 * Sets the locale.
	 * 
	 * @param locale
	 *            The locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Sets the searchWord.
	 * 
	 * @param searchWord
	 *            The searchWord to set
	 */
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

}
