/**
 * Copyright (c) 2011 Forschungszentrum Juelich GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Carsten Karbach, Claudia Knobloch, FZ Juelich
 */
package org.eclipse.ptp.rm.lml.ui.views;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.ptp.rm.lml.core.model.ILguiItem;
import org.eclipse.ptp.rm.lml.internal.core.elements.Nodedisplay;
import org.eclipse.ptp.rm.lml.ui.providers.DisplayNode;
import org.eclipse.ptp.rm.lml.ui.providers.LMLWidget;
import org.eclipse.ptp.rm.lml.ui.providers.NodedisplayComp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Parent composite of NodedisplayComp
 * This class allows to zoom or switch the viewports of
 * nodedisplaycomps. Every nodedisplaycomp is connected with
 * one instance of this class. NodedisplayView represents one zoomable
 * NodedisplayComp.
 */
public class NodedisplayView extends LMLWidget{

	private Nodedisplay model;//LML-Data-model for this view
	
	private NodedisplayComp root;//root nodedisplay which is currently shown
	
	private Deque<String> zoomstack=new ArrayDeque<String>();//Saves zoom-levels to zoom out later, saves full-implicit name of nodes to create Displaynodes from these ids
	
	/**
	 * Create a composite as surrounding component for NodedisplayComps.
	 * This class encapsulates zooming functionality. It saves a stack
	 * 
	 * @param pmodel
	 * @param parent
	 */
	public NodedisplayView(ILguiItem lguiItem, Nodedisplay pmodel, Composite parent){
		
		super(lguiItem, parent, SWT.None);
		
		model=pmodel;
		
		setLayout(new FillLayout());
		
		root=new NodedisplayComp(lguiItem, model, this, SWT.None);
		
	}
	
	/**
	 * The stack which saves the last zoom-levels is restarted
	 */
	public void restartZoom(){
		zoomstack=new ArrayDeque<String>();
	}
	
	/**
	 * Go one level higher in zoomstack
	 */
	public void zoomOut(){
		if(! zoomstack.isEmpty()){
			String impname=zoomstack.pop();
			//Get back null-values
			if(impname.equals(""))
				impname=null;
			
			//Switch view to node with impname
			goToImpname(impname);
		}
	}
	
	public void zoomIn(String impname){
		String oldshown=root.getShownImpname();
		
		if(goToImpname(impname)){
			if(oldshown==null)//Not allowed to insert null-values into ArrayDeque
				oldshown="";
			zoomstack.push(oldshown);
		}
	}
	
	/**
	 * @return currently shown nodedisplaycomp
	 */
	public NodedisplayComp getRootNodedisplay(){
		return root;
	}
	
	
	/**
	 * Set node with impname as implicit name as root-node within this nodedisplay-panel
	 * @param impname implicit name of a node, which identifies every node within a nodedisplay
	 * @return true, if root was changed, otherwise false
	 */
	public boolean goToImpname(String impname){
		
		String shownimpname=root.getShownImpname();
		
		//Do not create a new panel if panel is already on the right view
		if(shownimpname==null){
			if(impname==null){
				return false;
			}
		}
		else if(shownimpname.equals(impname)){//Do not create new panel, if current viewport is the same to which this panel should be set
			return false;
		}
		
		NodedisplayComp newcomp=null;
		
		root.dispose();//Delete old root-element
		System.gc();
		
		if(impname!=null){
			DisplayNode newnode=DisplayNode.getDisplayNodeFromImpName(lguiItem, impname, model);
			
			newcomp=new NodedisplayComp(lguiItem, model, newnode, this, SWT.None);
		}
		else newcomp=new NodedisplayComp(lguiItem, model, this, SWT.None);//if impname is null => go up to root-level
		
		root=newcomp;
		
		this.layout();
		
		return true;
	}
	
}