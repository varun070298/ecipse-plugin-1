/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.workingset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.help.internal.base.HelpBasePlugin;
import org.eclipse.help.internal.criteria.CriterionResource;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The working set manager stores help working sets. Working sets are persisted
 * whenever one is added or removed.
 * 
 * @since 2.1
 */
public class WorkingSetManager implements IHelpWorkingSetManager {

	// Working set persistence
	private static final String WORKING_SET_STATE_FILENAME = "workingsets.xml"; //$NON-NLS-1$

	private static final String UNCATEGORIZED = "Uncategorized"; //$NON-NLS-1$
	
	private SortedSet workingSets = new TreeSet(new WorkingSetComparator());

	private AdaptableTocsArray root;
	
	private Map allCriteriaValues;

	private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();

	private static final TransformerFactory transformerFactory = TransformerFactory
			.newInstance();

	/**
	 * Constructor
	 */
	public WorkingSetManager() {
		restoreState();
	}

	public AdaptableTocsArray getRoot() {
		if (root == null)
			root = new AdaptableTocsArray(HelpPlugin.getTocManager().getTocs(
					Platform.getNL()));
		return root;
	}

	/**
	 * Adds a new working set and saves it
	 */
	public void addWorkingSet(WorkingSet workingSet) {
		if (workingSet == null || workingSets.contains(workingSet))
			return;
		workingSets.add(workingSet);
		saveState();
	}

	/**
	 * Creates a new working set
	 */
	public WorkingSet createWorkingSet(String name,
			AdaptableHelpResource[] elements) {
		return new WorkingSet(name, elements);
	}
	
	public WorkingSet createWorkingSet(String name, AdaptableHelpResource[] elements, CriterionResource[] criteria) {
		return new WorkingSet(name, elements, criteria);
	}
	
	

	/**
	 * Tests the receiver and the object for equality
	 * 
	 * @param object
	 *            object to compare the receiver to
	 * @return true=the object equals the receiver, it has the same working
	 *         sets. false otherwise
	 */
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object instanceof WorkingSetManager) {
			WorkingSetManager workingSetManager = (WorkingSetManager) object;
			return workingSetManager.workingSets.equals(workingSets);
		}
		return false;
	}

	/**
	 * Returns a working set by name
	 *  
	 */
	public WorkingSet getWorkingSet(String name) {
		if (name == null || workingSets == null)
			return null;

		Iterator iter = workingSets.iterator();
		while (iter.hasNext()) {
			WorkingSet workingSet = (WorkingSet) iter.next();
			if (name.equals(workingSet.getName()))
				return workingSet;
		}
		return null;
	}

	/**
	 * Returns the hash code.
	 * 
	 * @return the hash code.
	 */
	public int hashCode() {
		return workingSets.hashCode();
	}

	/**
	 * Implements IWorkingSetManager.
	 * 
	 * @see org.eclipse.ui.IWorkingSetManager#getWorkingSets()
	 */
	public WorkingSet[] getWorkingSets() {
		return (WorkingSet[]) workingSets.toArray(new WorkingSet[workingSets
				.size()]);
	}

	/**
	 * Returns the file used as the persistence store
	 * 
	 * @return the file used as the persistence store
	 */
	private File getWorkingSetStateFile() {
		IPath path = HelpBasePlugin.getDefault().getStateLocation();
		path = path.append(WORKING_SET_STATE_FILENAME);
		return path.toFile();
	}

	/**
	 * Removes specified working set
	 */
	public void removeWorkingSet(WorkingSet workingSet) {
		workingSets.remove(workingSet);
		saveState();
	}

	/**
	 * Reads the persistence store and creates the working sets stored in it.
	 */
	public boolean restoreState() {
		File stateFile = getWorkingSetStateFile();

		if (stateFile.exists()) {
			try {
				FileInputStream input = new FileInputStream(stateFile);
				InputStreamReader reader = new InputStreamReader(input, "utf-8"); //$NON-NLS-1$

				InputSource inputSource = new InputSource(reader);
				inputSource.setSystemId(stateFile.toString());

				DocumentBuilder parser = documentBuilderFactory
						.newDocumentBuilder();
				Document d = parser.parse(inputSource);

				Element rootElement = d.getDocumentElement();
				restoreWorkingSetState(rootElement);
				input.close();

				return true;
			} catch (ParserConfigurationException pce) {
				HelpPlugin
						.logError(
								"DocumentBuilder implementation could not be loaded, to restore working set state.", pce); //$NON-NLS-1$
				return false;
			} catch (SAXException se) {
				HelpBasePlugin
						.logError(
								"Error occurred parsing file " + stateFile.toString() + ", while restoring working set state.", se); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			} catch (IOException ioe) {
				HelpBasePlugin
						.logError(
								"Error occurred parsing file " + stateFile.toString() + ", while restoring working set state.", ioe); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
		}
		return false;
	}

	/**
	 * Recreates all working sets from the persistence store and adds them to
	 * the receiver.
	 * 
	 * @param parent
	 *            the xml element containing serialized working sets
	 */
	private void restoreWorkingSetState(Element parent) {
		NodeList workingSets = parent.getChildNodes();

		for (int i = 0; i < workingSets.getLength(); i++) {
			if (workingSets.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;

			WorkingSet workingSet = restoreWorkingSet((Element) workingSets.item(i));
			if (workingSet != null) {
				this.workingSets.add(workingSet);
			}
		}
	}

	/**
	 * Recreates a working set from the persistence store.
	 * 
	 * @return the working set created from the memento or null if creation
	 *         failed.
	 */
	private WorkingSet restoreWorkingSet(Element workingSetNode) {
		String name = workingSetNode.getAttribute("name"); //$NON-NLS-1$

		// scope
		List helpResources = new ArrayList();
		NodeList contents = workingSetNode.getElementsByTagName("contents"); //$NON-NLS-1$
		for (int i = 0; i < contents.getLength(); i++) {
			Element content = (Element) contents.item(i);
			NodeList items = content.getElementsByTagName("item"); //$NON-NLS-1$
			for(int j = 0; j < items.getLength(); j++){
				Element itemI = (Element) items.item(j);
				String href = itemI.getAttribute("toc"); //$NON-NLS-1$
				if (href == null || href.length() == 0)
					continue;

				String child_pos = itemI.getAttribute("topic"); //$NON-NLS-1$
				int pos = -1;
				if (child_pos != null) {
					try {
						pos = Integer.parseInt(child_pos);
					} catch (Exception e) {
					}
				}

				AdaptableHelpResource toc = getAdaptableToc(href);

				if (toc == null)
					return null;

				if (pos == -1) {
					// Create the adaptable toc.
					helpResources.add(toc);
				} else {
					// Create the adaptable topic
					AdaptableTopic[] topics = (AdaptableTopic[]) toc.getChildren();
					if (pos >= 0 && topics.length > pos)
						helpResources.add(topics[pos]);
				}
			}
		}
		AdaptableHelpResource[] elements = new AdaptableHelpResource[helpResources.size()];
		helpResources.toArray(elements);
				
		//criteria
		
		List criteriaResource = new ArrayList();
		NodeList criteriaContents = workingSetNode.getElementsByTagName("criterion"); //$NON-NLS-1$
		for (int i = 0; i < criteriaContents.getLength(); ++i) {
			Element criterion = (Element) criteriaContents.item(i);
			String criterionName = criterion.getAttribute("name"); //$NON-NLS-1$
			if(null != name && 0 != name.length()){
				NodeList items = criterion.getElementsByTagName("item"); //$NON-NLS-1$
				List criterionValues = new ArrayList();
				for(int j = 0; j < items.getLength(); ++j){
					String value = ((Element) items.item(j)).getAttribute("value"); //$NON-NLS-1$
					if(null != value && 0 != value.length()){
						criterionValues.add(value);
					}
				}
				if(criterionValues.size() > 0){
					CriterionResource criterionResource = new CriterionResource(criterionName, criterionValues);
					criteriaResource.add(criterionResource);
				}
			}	
		}

		CriterionResource[] criteria = new CriterionResource[criteriaResource.size()];
		criteriaResource.toArray(criteria);

		WorkingSet ws = createWorkingSet(name, elements, criteria);

		return ws;
	}

	/**
	 * Saves the working sets in the persistence store
	 */
	public synchronized boolean saveState() {
		File stateFile = null;
		try {
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("workingSets"); //$NON-NLS-1$
			doc.appendChild(rootElement);

			saveWorkingSetState(rootElement);

			stateFile = getWorkingSetStateFile();
			stateFile.getParentFile().mkdir();
			FileOutputStream stream = new FileOutputStream(stateFile);

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(stream);

			transformer.transform(source, result);
			stream.close();
			return true;
		} catch (ParserConfigurationException pce) {
			HelpPlugin.logError(
					"DocumentBuilder implementation could not be loaded.", pce); //$NON-NLS-1$
			return false;
		} catch (TransformerException e) {
			HelpPlugin.logError("Problems occurred while saving working sets."); //$NON-NLS-1$
			return false;
		} catch (IOException e) {
			stateFile.delete();
			HelpPlugin.logError(
					"Problems occurred while saving working set file."); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Saves all persistable working sets in the persistence store.
	 * 
	 * @param parent:
	 *            the xml node to save to
	 */
	private void saveWorkingSetState(Element parent) {
		Iterator iterator = workingSets.iterator();

		while (iterator.hasNext()) {
			WorkingSet workingSet = (WorkingSet) iterator.next();
			workingSet.saveState(parent);
		}
	}

	/**
	 * Persists all working sets. Should only be called by the webapp working
	 * set dialog.
	 * 
	 * @param changedWorkingSet
	 *            the working set that has changed
	 */
	public void workingSetChanged(WorkingSet changedWorkingSet) {
		saveState();
	}

	public AdaptableToc getAdaptableToc(String href) {
		return getRoot().getAdaptableToc(href);
	}

	public AdaptableTopic getAdaptableTopic(String id) {

		if (id == null || id.length() == 0)
			return null;

		// toc id's are hrefs: /pluginId/path/to/toc.xml
		// topic id's are based on parent toc id and index of topic:
		// /pluginId/path/to/toc.xml_index_
		int len = id.length();
		if (id.charAt(len - 1) == '_') {
			// This is a first level topic
			String indexStr = id.substring(id.lastIndexOf('_', len - 2) + 1,
					len - 1);
			int index = 0;
			try {
				index = Integer.parseInt(indexStr);
			} catch (Exception e) {
			}

			String tocStr = id.substring(0, id.lastIndexOf('_', len - 2));
			AdaptableToc toc = getAdaptableToc(tocStr);
			if (toc == null)
				return null;
			IAdaptable[] topics = toc.getChildren();
			if (index < 0 || index >= topics.length)
				return null;
			return (AdaptableTopic) topics[index];
		}

		return null;
	}

	public String getCurrentWorkingSet() {
		return Platform.getPreferencesService().getString(HelpBasePlugin.PLUGIN_ID,
				BaseHelpSystem.WORKING_SET, "", null); //$NON-NLS-1$
	}

	public void setCurrentWorkingSet(String workingSet) {
		InstanceScope instanceScope = new InstanceScope();
		IEclipsePreferences prefs = instanceScope.getNode(HelpBasePlugin.PLUGIN_ID);
		prefs.put(BaseHelpSystem.WORKING_SET, workingSet);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
		}
	}

	public void tocsChanged() {
		saveState();
		root = null;
		workingSets = new TreeSet(new WorkingSetComparator());
		restoreState();
	}
	
	public boolean isCriteriaScopeEnabled(){
		if(null == allCriteriaValues){
			allCriteriaValues = HelpPlugin.getCriteriaManager().getAllCriteriaValues(Platform.getNL());
		}
		if(HelpPlugin.getCriteriaManager().isCriteriaEnabled() && !allCriteriaValues.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public String[] getCriterionIds() {
		if(null == allCriteriaValues){
			allCriteriaValues = HelpPlugin.getCriteriaManager().getAllCriteriaValues(Platform.getNL());
		}
		List criterionIds = new ArrayList();
		if(null != allCriteriaValues){
			for(Iterator iter = allCriteriaValues.keySet().iterator(); iter.hasNext();){
				String criterion = (String) iter.next();
				if(null == criterion || 0 == criterion.length() || 0 == getCriterionValueIds(criterion).length)
					continue;
				criterionIds.add(criterion);
			}
			Collections.sort(criterionIds);
		}
		String[] ids = new String[criterionIds.size()];                                        		
		criterionIds.toArray(ids);
		return ids;
	}

	public String[] getCriterionValueIds(String criterionName) {
		if(null == allCriteriaValues){
			allCriteriaValues = HelpPlugin.getCriteriaManager().getAllCriteriaValues(Platform.getNL());
		}
		List valueIds = new ArrayList();
		if(null != criterionName && null != allCriteriaValues) {
			Set criterionValues = (Set)allCriteriaValues.get(criterionName);
			if(null != criterionValues && !criterionValues.isEmpty()) {
				valueIds.addAll(criterionValues);
				Collections.sort(valueIds);
				valueIds.add(UNCATEGORIZED);
			}
		}
		String[] valueIdsArray = new String[valueIds.size()];                                        		
		valueIds.toArray(valueIdsArray);
		return valueIdsArray;
	}
	
	public String getCriterionDisplayName(String criterionId) {
		return HelpPlugin.getCriteriaManager().getCriterionDisplayName(criterionId, Platform.getNL());
	}

	public String getCriterionValueDisplayName(String criterionId, String criterionValueId) {
		return HelpPlugin.getCriteriaManager().getCriterionValueDisplayName(criterionId, criterionValueId, Platform.getNL());
	}

}
