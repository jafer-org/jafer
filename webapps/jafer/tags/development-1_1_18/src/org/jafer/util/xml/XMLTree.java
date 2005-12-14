/**
 * JAFER Toolkit Poject.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.util.xml;

import org.w3c.dom.*;
import java.util.Vector;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

  public class XMLTree extends JFrame {

    private Vector nodes;
    private JTree tree = new JTree();

    public XMLTree (Node xmlRoot) {

      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);
      tree.putClientProperty("JTree.lineStyle", "Angled");

      DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("top");
      nodes = new Vector();
      createNodes(treeRoot, xmlRoot);
      tree.setModel(new DefaultTreeModel(treeRoot));

      this.addWindowListener(new WindowCloser());
      JScrollPane jscroll = new JScrollPane(tree);
      this.getContentPane().add(jscroll);
      this.pack();
      this.setVisible(true);
    }

  private void createNodes(DefaultMutableTreeNode parent, Node n) {

    DefaultMutableTreeNode child = new DefaultMutableTreeNode(n);
    parent.add(child);
    if(n.getChildNodes() != null) {
      nodes.add(parent);
        for(int i=0;i<n.getChildNodes().getLength();i++)
          createNodes(child,(Node)n.getChildNodes().item(i));
    }
  }

  private class WindowCloser extends WindowAdapter {

    public WindowCloser() {
    }

    public void windowClosing(WindowEvent e) {
      e.getWindow().setVisible(false);
      System.exit(0);
    }
  }
}
