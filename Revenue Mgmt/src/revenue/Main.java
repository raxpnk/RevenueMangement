
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package revenue;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.ArrayList;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static ArrayList graph=new ArrayList();
    static ArrayList graph2=new ArrayList();
    static ArrayList graph3=new ArrayList();
    
    public static void main(String[] args) {
        // TODO code application logic here
        try
        {
            
            String path="RevIn1.txt";
            
            
            ResultFrame rf=new ResultFrame();
            DefaultTableModel dm=(DefaultTableModel)rf.jTable1.getModel();
            DecimalFormat df=new DecimalFormat("#.###");
            
            FCFS fc=new FCFS(path);
            fc.find();
            
            double rev1=fc.revenue;
           
            Vector v1=new Vector();
            v1.add("S1");
            v1.add("FCFS");
            v1.add(fc.mean);
            v1.add(df.format(fc.sigma/fc.mean));
            v1.add("-");
            v1.add(fc.meangold);
            v1.add(df.format(fc.sigmagold/fc.meangold));
            dm.addRow(v1);
            
            
            DPP dp=new DPP(path);
            dp.find();
            
            
            Vector v2=new Vector();
            v2.add("S1");
            v2.add("DPP");
            v2.add(dp.mean);
            v2.add(df.format(dp.sigma/dp.mean));
            v2.add("-");
            v2.add(dp.meangold);
            v2.add(df.format(dp.sigmagold/dp.meangold));
            dm.addRow(v2);
            
            double rev2=dp.revenue;
            
            CCP cp=new CCP(path);
            cp.find();
            
            double rev3=cp.revenue;
            
            Vector v3=new Vector();
            v3.add("S1");
            v3.add("CCP");
            v3.add(cp.mean);
            v3.add(df.format(cp.sigma/cp.mean));
            v3.add("-");
            v3.add(cp.meangold);
            v3.add(df.format(cp.sigmagold/cp.meangold));
            dm.addRow(v3);
           
            Vector v=new Vector();
            v.add("");
            v.add("");
            v.add("");
            v.add("");
            v.add("");
            v.add("");
            v.add("");
            dm.addRow(v);
            
            graph2.add(fc.mean+"#P1#S1");
            graph2.add(dp.mean+"#P2#S1");
            graph2.add(cp.mean+"#P3#S1");
                
            graph3.add(fc.meangold+"#P1#S1");
            graph3.add(dp.meangold+"#P2#S1");
            graph3.add(cp.meangold+"#P3#S1");
            
            for(int i=2;i<=4;i++)
            {
                Fuzzy_FCFS ffc2=new Fuzzy_FCFS(path,i);
                ffc2.find();
            
                Vector v4=new Vector();
                v4.add("S"+i);
                v4.add("FCFS");
                v4.add(ffc2.mean);
                v4.add(df.format(ffc2.sigma/ffc2.mean));                
                v4.add(df.format((rev1-ffc2.revenue)/ffc2.revenue));
                v4.add(ffc2.meangold);
                v4.add(df.format(ffc2.sigmagold/ffc2.meangold));
                dm.addRow(v4);
                
                Fuzzy_DPP fdp2=new Fuzzy_DPP(path,i);
                fdp2.find();
            
                Vector v5=new Vector();
                v5.add("S"+i);
                v5.add("DPP");
                v5.add(fdp2.mean);
                v5.add(df.format(fdp2.sigma/fdp2.mean));
                v5.add(df.format((rev2-fdp2.revenue)/fdp2.revenue));
                v5.add(fdp2.meangold);
                v5.add(df.format(fdp2.sigmagold/fdp2.meangold));
                dm.addRow(v5);
                
                Fuzzy_CCP fcp2=new Fuzzy_CCP(path,i);
                fcp2.find();
                
                Vector v6=new Vector();
                v6.add("S"+i);
                v6.add("CCP");
                v6.add(fcp2.mean);
                v6.add(df.format(fcp2.sigma/fcp2.mean));
                v6.add(df.format((rev3-fcp2.revenue)/fcp2.revenue));
                v6.add(fcp2.meangold);
                v6.add(df.format(fcp2.sigmagold/fcp2.meangold));
                dm.addRow(v6);
                
                
                Vector vv=new Vector();
                vv.add("");
                vv.add("");
                vv.add("");
                vv.add("");
                vv.add("");
                vv.add("");
                vv.add("");
                dm.addRow(vv);
                
                String ct1=df.format((rev1-ffc2.revenue)/ffc2.revenue);
                String ct2=df.format((rev2-fdp2.revenue)/fdp2.revenue);
                String ct3=df.format((rev3-fcp2.revenue)/fcp2.revenue);
                graph.add(ct1+"#P1#S"+i);
                graph.add(ct2+"#P2#S"+i);
                graph.add(ct3+"#P3#S"+i);
                
                graph2.add(ffc2.mean+"#P1#S"+i);
                graph2.add(fdp2.mean+"#P2#S"+i);
                graph2.add(fcp2.mean+"#P3#S"+i);
                
                graph3.add(ffc2.meangold+"#P1#S"+i);
                graph3.add(fdp2.meangold+"#P2#S"+i);
                graph3.add(fcp2.meangold+"#P3#S"+i);
            }
            
            
            rf.setVisible(true);
            rf.setResizable(false);
            rf.setTitle("Result");
            
            
            displayGraph2();
            displayGraph3();
            displayGraph1();
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void displayGraph1()
    {
        try
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i=0;i<graph.size();i++)
            {
                String g1[]=graph.get(i).toString().split("#");
                dataset.setValue(Double.parseDouble(g1[0]),g1[1],g1[2]);
            }
            
            JFreeChart chart = ChartFactory.createBarChart
            ("Performance","", "Relative Cost", dataset, 
  
            PlotOrientation.VERTICAL, true,true, false);
  
            chart.getTitle().setPaint(Color.blue); 
  
            CategoryPlot p = chart.getCategoryPlot(); 
            //final NumberAxis rangeAxis = (NumberAxis) p.getRangeAxis();
            //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            p.setRangeGridlinePaint(Color.red); 
              
            CategoryItemRenderer renderer = p.getRenderer();
      

            renderer.setSeriesPaint(0, Color.red);
            renderer.setSeriesPaint(1, Color.blue);	  
            renderer.setSeriesPaint(2, Color.green);
            
            ChartFrame frame1=new ChartFrame("Relative Cost Grpah ",chart);
  
            frame1.setSize(700,500);
  
            frame1.setVisible(true);
  
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void displayGraph2()
    {
        try
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i=0;i<graph2.size();i++)
            {
                String g1[]=graph2.get(i).toString().split("#");
                dataset.setValue(Double.parseDouble(g1[0]),g1[1],g1[2]);
            }
            
            JFreeChart chart = ChartFactory.createBarChart
            ("Performance","", "Mean Revenue", dataset, 
  
            PlotOrientation.VERTICAL, true,true, false);
  
            chart.getTitle().setPaint(Color.blue); 
  
            CategoryPlot p = chart.getCategoryPlot(); 
            //final NumberAxis rangeAxis = (NumberAxis) p.getRangeAxis();
            //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            p.setRangeGridlinePaint(Color.red); 
              
            CategoryItemRenderer renderer = p.getRenderer();
      

            renderer.setSeriesPaint(0, Color.red);
            renderer.setSeriesPaint(1, Color.blue);	  
            renderer.setSeriesPaint(2, Color.green);
            
            ChartFrame frame1=new ChartFrame("Mean Revenue Grpah ",chart);
  
            frame1.setSize(700,500);
  
            frame1.setVisible(true);
  
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void displayGraph3()
    {
        try
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i=0;i<graph3.size();i++)
            {
                String g1[]=graph3.get(i).toString().split("#");
                dataset.setValue(Double.parseDouble(g1[0]),g1[1],g1[2]);
            }
            
            JFreeChart chart = ChartFactory.createBarChart
            ("Performance","", "Mean Gold", dataset, 
  
            PlotOrientation.VERTICAL, true,true, false);
  
            chart.getTitle().setPaint(Color.blue); 
  
            CategoryPlot p = chart.getCategoryPlot(); 
            //final NumberAxis rangeAxis = (NumberAxis) p.getRangeAxis();
            //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            p.setRangeGridlinePaint(Color.red); 
              
            CategoryItemRenderer renderer = p.getRenderer();
      

            renderer.setSeriesPaint(0, Color.red);
            renderer.setSeriesPaint(1, Color.blue);	  
            renderer.setSeriesPaint(2, Color.green);
            
            ChartFrame frame1=new ChartFrame("Mean Gold Grpah ",chart);
  
            frame1.setSize(700,500);
  
            frame1.setVisible(true);
  
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
