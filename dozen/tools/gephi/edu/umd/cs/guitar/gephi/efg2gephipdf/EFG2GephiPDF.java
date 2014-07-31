/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.umd.cs.guitar.gephi.efg2gephipdf;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.GenericColorizer;
import org.gephi.preview.api.ColorizerFactory;
import org.gephi.preview.api.EdgeColorizer;
import org.gephi.preview.api.NodeColorizer;
import org.gephi.preview.api.NodeChildColorizer;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 * <p>
 * This demo shows the following steps:
 * <ul><li>Create a project and a workspace, it is mandatory.</li>
 * <li>Import the <code>polblogs.gml</code> graph file in an import container.</li>
 * <li>Append the container to the main graph structure.</li>
 * <li>Run layout manually.</li>
 * <li>Configure preview to display labels and mutual edges differently.</li>
 * <li>Export graph as PDF.</li></ul>
 * 
 * @author Mathieu Bastian
 */
public class EFG2GephiPDF {

   public static void main(String[] args) {

      EFG2GephiPDF headlessSimple = new EFG2GephiPDF();
      headlessSimple.script(args[0], args[1]);
   }

   public void script(String inputFilename,
                      String outputFilename) {
      // Init a project - and therefore a workspace
      ProjectController pc =
         Lookup.getDefault().lookup(ProjectController.class);
      pc.newProject();
      Workspace workspace = pc.getCurrentWorkspace();

      /*
       * Get models and controllers for this new workspace -
       * will be useful later
       */
      AttributeModel attributeModel =
         Lookup.getDefault().lookup(AttributeController.class).getModel();
      GraphModel graphModel =
         Lookup.getDefault().lookup(GraphController.class).getModel();
      PreviewModel model =
         Lookup.getDefault().lookup(PreviewController.class).getModel();
      PreviewController controller =
         Lookup.getDefault().lookup(PreviewController.class);
      ImportController importController =
         Lookup.getDefault().lookup(ImportController.class);

      // Import file       
      Container container;
      try {
         File file =
            new File(inputFilename);

         container = importController.importFile(file);
         container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);

      } catch (Exception ex) {
         ex.printStackTrace();
         return;
      }

      // Append imported data to GraphAPI
      importController.process(container, new DefaultProcessor(), workspace);

      // See if graph is well imported
      DirectedGraph graph = graphModel.getDirectedGraph();
      System.out.println("Nodes: " + graph.getNodeCount());
      System.out.println("Edges: " + graph.getEdgeCount());

      FruchtermanReingold layout = new FruchtermanReingold(null);
      layout.setGraphModel(graphModel);
      layout.resetPropertiesValues();
      layout.setArea((float)10000);
      layout.setGravity(5.0);
      layout.setSpeed(5.0);

      layout.initAlgo();
      for (int i = 0; i < 10000 && layout.canAlgo(); i++) {
         layout.goAlgo();
      }
      layout.endAlgo();

      // Preview
      model.getNodeSupervisor().setShowNodeLabels(Boolean.TRUE);
      controller.setBackgroundColor(Color.BLACK);

      ColorizerFactory colorizerFactory = Lookup.getDefault().lookup(ColorizerFactory.class);

      // Edge
      Color red = new Color(204, 0, 0);
      Color magenta = new Color(225, 0, 225);
      Color yellow = new Color(204, 204, 0);
      Color orange = new Color(238, 160, 0);

      // Uni-directed edge
      model.getUniEdgeSupervisor().setEdgeScale(0.1f);
      model.getUniEdgeSupervisor().setColorizer((EdgeColorizer) colorizerFactory.createCustomColorMode(orange));
      model.getUniEdgeSupervisor().setCurvedFlag(Boolean.FALSE);
      model.getUniEdgeSupervisor().setArrowSize((float)10.0);

      // Bi-directional edge
      model.getBiEdgeSupervisor().setEdgeScale(0.1f);
      model.getBiEdgeSupervisor().setColorizer((EdgeColorizer) colorizerFactory.createCustomColorMode(yellow));

      model.getBiEdgeSupervisor().setCurvedFlag(Boolean.FALSE);
      model.getBiEdgeSupervisor().setArrowSize((float)10.0);

      // Un-directed edge
      model.getUndirectedEdgeSupervisor().setEdgeScale(0.1f);
      model.getUndirectedEdgeSupervisor().setColorizer((EdgeColorizer) colorizerFactory.createCustomColorMode(red));

      // Self loop
      model.getSelfLoopSupervisor().setColorizer((EdgeColorizer) colorizerFactory.createCustomColorMode(magenta));

      // Node
      Color g = new Color(0, 112, 0);
      Color dg = new Color(0, 80, 0);

      model.getNodeSupervisor().setBaseNodeLabelFont(model.getNodeSupervisor().getBaseNodeLabelFont().deriveFont(8));
      model.getNodeSupervisor().setNodeLabelColorizer((NodeChildColorizer) colorizerFactory.createCustomColorMode(Color.WHITE));
      model.getNodeSupervisor().setNodeColorizer((NodeColorizer) colorizerFactory.createCustomColorMode(g));
      model.getNodeSupervisor().setNodeBorderColorizer((GenericColorizer) colorizerFactory.createCustomColorMode(dg));

      // PDF export
      FileOutputStream file;
      try {
         file = new FileOutputStream(outputFilename);
      } catch (Exception ex) {
         ex.printStackTrace();
         return;
      }

      PDFExporter pe = new PDFExporter();
      pe.setLandscape(Boolean.TRUE);
      pe.setOutputStream(file);
      pe.setWorkspace(workspace);
      pe.execute();
   }
}
