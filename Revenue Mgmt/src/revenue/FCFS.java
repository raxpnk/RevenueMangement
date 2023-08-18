/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package revenue;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.awt.Color;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.axis.NumberAxis;

import java.text.DecimalFormat;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 *
 * @author admin
 */


public class FCFS 
{
    
    String path;
    
    ArrayList JobNo=new ArrayList();
    ArrayList Price=new ArrayList();
    ArrayList Price_per_Slot=new ArrayList();
    ArrayList SlotNo=new ArrayList();
    ArrayList TempSlotNo=new ArrayList();
    ArrayList Cust_Type	=new ArrayList();
    ArrayList Res_Type=new ArrayList();	
    ArrayList CPU=new ArrayList();
    ArrayList Storage=new ArrayList();
    ArrayList Bandwidth=new ArrayList();
    
    ArrayList graph=new ArrayList();
    
    int timeSlot=0;
    
    int CPU_max=50;
    int Band_max=32;
    int Store_max=24;
    
    double revenue=0;
    
    double mean=0;
    double sigma=0;
    double meangold;
    double sigmagold;
    
    FCFS(String pp)
    {
        path=pp;
    }
    
    
    
    public void find()
    {
        try
        {
            ArrayList cst=new ArrayList();
            
            String res="";
            
            File fe=new File(path);
            FileInputStream fis=new FileInputStream(fe);
            byte bt[]=new byte[fis.available()];
            fis.read(bt);
            fis.close();
            
            String str=new String(bt);
            String s1[]=str.split("\n");           
            
            
            
            for(int i=1;i<s1.length;i++)
            {
                String s2[]=s1[i].split("\t");
                JobNo.add(s2[0].trim());
                Price.add(s2[1].trim());
                Price_per_Slot.add(s2[2].trim());
                SlotNo.add(s2[3].trim());
                TempSlotNo.add(s2[3].trim());
                Cust_Type.add(s2[4].trim());
                Res_Type.add(s2[5].trim());
                CPU.add(s2[6].trim());
            	Storage.add(s2[7].trim());
                Bandwidth.add(s2[8].trim());
            }
            
            int max=0;
            for(int i=0;i<SlotNo.size();i++)
            {
                max=Math.max(max, Integer.parseInt(SlotNo.get(i).toString()));
            }
            
            int min=max;
            for(int i=0;i<SlotNo.size();i++)
            {
                min=Math.min(min, Integer.parseInt(SlotNo.get(i).toString()));
            }
            System.out.println(max+" : "+min);
            timeSlot=max+min;
            System.out.println("Time Slot "+timeSlot);
            System.out.println("Band "+Bandwidth);
            int c1=0;
            int c2=0;
            int c3=0;
            int c4=0;
            for(int i=0;i<timeSlot;i++)
            {
                
                int vmid = 1;
                int cid=1;
                int mips = 250;
                long size = 10000; //image size (MB)
                int ram = Store_max; //vm memory (MB)
                long bw = Band_max; // bandwidth 
                int pesNumber = CPU_max; //number of cpus
                String vmm = "Xen"; //VMM name
                     
                Vm vm1 = new Vm(vmid,cid, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                
                BwProvisionerSimple BwPro=new BwProvisionerSimple(Band_max);
                PeProvisionerSimple CPUPro=new PeProvisionerSimple(CPU_max);
                RamProvisionerSimple StePro=new RamProvisionerSimple(Store_max);
                
                
                for(int j=0;j<JobNo.size();j++)
                {
                    String jb1=JobNo.get(j).toString();
                    int slt1=Integer.parseInt(TempSlotNo.get(j).toString());
                    
                    if(slt1>0)
                    {
                        int cpu=Integer.parseInt(CPU.get(j).toString());
                        int band=Integer.parseInt(Bandwidth.get(j).toString());
                        int store=Integer.parseInt(Storage.get(j).toString());
                        
                        boolean bo1=CPUPro.allocateMipsForVm(vm1, cpu);
                        boolean bo2=StePro.allocateRamForVm(vm1, band);
                        boolean bo3=BwPro.allocateBwForVm(vm1,store);
                        
                        if(bo1 && bo2 && bo3)
                        {
                            int flag1=0;
                            for(int k=j+1;k<JobNo.size();k++)
                            {
                                String jb2=JobNo.get(k).toString();
                                int slt2=Integer.parseInt(TempSlotNo.get(k).toString());
                                
                                if(slt2>0)
                                {
                                    int cpu2=Integer.parseInt(CPU.get(k).toString());
                                    int band2=Integer.parseInt(Bandwidth.get(k).toString());
                                    int store2=Integer.parseInt(Storage.get(k).toString());
                                    
                                    int cp=cpu+cpu2;
                                    int ba=band+band2;
                                    int ste=store+store2;
                                   
                                    if(cp==CPU_max)
                                        c1++;
                                    
                                    if(ba==Band_max)
                                        c2++;
                                    
                                    if(ste==Store_max)
                                        c3++;
                                    
                                    if(c1==3||c2==3||c3==3)
                                        c4++;
                                    
                                    boolean bool1=CPUPro.allocateMipsForVm(vm1, cp);
                                    boolean bool2=StePro.allocateRamForVm(vm1, ste);
                                    boolean bool3=BwPro.allocateBwForVm(vm1,ba);
                                    
                                    if(bool1 && bool2 && bool3 && c1<=3 && c2<=3 && c3<=3)
                                    {
                                        if(c4<2)
                                        {
                                            
                                                cst.add(jb1);
                                            
                                            
                                                cst.add(jb2);
                                            
                                            System.out.println("Job "+jb1+","+jb2 +" running in "+(i+1)+" Time Slot");
                                            res=res+"Time Slot = "+(i+1)+"\n\n";
                                            res=res+"=========================================\n\n";
                                            res=res+"Job "+jb1+" , "+jb2 +" running in "+(i+1)+" Time Slot\n\n";
                                            res=res+"CPU = "+cp+" , "+"Bandwidth = "+ba+" , "+"Storage = "+ste+"\n\n";
                                            res=res+"=========================================\n\n";
                                            TempSlotNo.set(k, slt2-1);
                                            TempSlotNo.set(j, slt1-1);
                                            
                                            graph.add(cp+"#"+"CPU#"+(i+1)+" ("+jb1+" , "+jb2+")");
                                            graph.add(ba+"#"+"BandWidth#"+(i+1)+" ("+jb1+" , "+jb2+")");
                                            graph.add(ste+"#"+"Storage#"+(i+1)+" ("+jb1+" , "+jb2+")");
                                            
                                            flag1=1;
                                            break;
                                        }
                                        else
                                        {
                                            
                                                cst.add(jb1);
                                            
                                            System.out.println("Job "+jb1+" running in "+(i+1)+" Time Slot");   
                                            res=res+"Time Slot = "+(i+1)+"\n\n";
                                            res=res+"=========================================\n\n";
                                            res=res+"Job "+jb1+" running in "+(i+1)+" Time Slot\n\n";
                                            res=res+"CPU = "+cpu+" , "+"Bandwidth = "+band+" , "+"Storage = "+store+"\n\n";
                                            res=res+"=========================================\n\n";
                                            TempSlotNo.set(j, slt1-1);
                                            
                                            graph.add(cpu+"#"+"CPU#"+(i+1)+" ("+jb1+")");
                                            graph.add(band+"#"+"BandWidth#"+(i+1)+" ("+jb1+")");
                                            graph.add(store+"#"+"Storage#"+(i+1)+" ("+jb1+")");
                                            
                                            flag1=1;
                                            break;
                                        }
                                    }
                                    else
                                    {
                                         cst.add(jb1);
                                            
                                            System.out.println("Job "+jb1+" running in "+(i+1)+" Time Slot");   
                                            res=res+"Time Slot = "+(i+1)+"\n\n";
                                            res=res+"=========================================\n\n";
                                            res=res+"Job "+jb1+" running in "+(i+1)+" Time Slot\n\n";
                                            res=res+"CPU = "+cpu+" , "+"Bandwidth = "+band+" , "+"Storage = "+store+"\n\n";
                                            res=res+"=========================================\n\n";
                                            TempSlotNo.set(j, slt1-1);
                                            
                                            graph.add(cpu+"#"+"CPU#"+(i+1)+" ("+jb1+")");
                                            graph.add(band+"#"+"BandWidth#"+(i+1)+" ("+jb1+")");
                                            graph.add(store+"#"+"Storage#"+(i+1)+" ("+jb1+")");
                                            
                                            flag1=1;
                                            break;
                                    }
                                }
                            }
                            if(flag1==1)
                               break;
                        }                        
                    }
                }
            }
            
            String gs="";
            double rev=0;
            double rr=0;
            for(int i=0;i<cst.size();i++)
            {
                String g1=cst.get(i).toString();
                int ind1=JobNo.indexOf(g1);
                
                rev=rev+Double.parseDouble(Price_per_Slot.get(ind1).toString());
                gs=gs+Price_per_Slot.get(ind1).toString()+"\n";
                
                String cust=Cust_Type.get(ind1).toString();
                if(cust.equals("yes"))
                    rr=rr+Double.parseDouble(Price_per_Slot.get(ind1).toString());
            }
            
            
            DecimalFormat df=new DecimalFormat("#.##");
            
            double mn=rev/cst.size();
            
            double mn1=rr/cst.size();
            
            double sig=0;
            double sig2=0;
            for(int i=0;i<cst.size();i++)
            {
                String g1=cst.get(i).toString();
                int ind1=JobNo.indexOf(g1);
                String cust=Cust_Type.get(ind1).toString();
                
                double x=Double.parseDouble(Price_per_Slot.get(ind1).toString());
                sig=sig+((x-mn)*(x-mn));
                
                if(cust.equals("yes"))
                    sig2=sig2+((x-mn1)*(x-mn1));
                
            }
            sig=sig/cst.size();
            sig2=sig2/cst.size();
            
            revenue=Double.parseDouble(df.format(rev));
            mean=Double.parseDouble(df.format(mn));
            sigma=Double.parseDouble(df.format(sig));
            
            meangold=Double.parseDouble(df.format(mn1));
            sigmagold=Double.parseDouble(df.format(sig2));
            
            System.out.println("FCFS - Policy");
            System.out.println(res);
            System.out.println("Overall Revenue = "+df.format(rev));
            
            displayGraph();
            System.out.println("mean "+mean);
            System.out.println("Sigma "+sig);
            System.out.println("Sigma/mean "+sig/mean);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void displayGraph()
    {
        try
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i=0;i<graph.size();i++)
            {
                String g1[]=graph.get(i).toString().split("#");
                dataset.setValue(Integer.parseInt(g1[0]),g1[1],g1[2]);
            }
            
            JFreeChart chart = ChartFactory.createBarChart
            ("Performance","Time Slots With Running Job", "Resource Unit", dataset, 
  
            PlotOrientation.VERTICAL, true,true, false);
  
            chart.getTitle().setPaint(Color.blue); 
  
            CategoryPlot p = chart.getCategoryPlot(); 
            final NumberAxis rangeAxis = (NumberAxis) p.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            p.setRangeGridlinePaint(Color.red); 
              
            CategoryItemRenderer renderer = p.getRenderer();
      

            renderer.setSeriesPaint(0, Color.red);
            renderer.setSeriesPaint(1, Color.blue);	  
            renderer.setSeriesPaint(2, Color.green);
            
            ChartFrame frame1=new ChartFrame("FCFS Graph",chart);
  
            frame1.setSize(700,500);
  
            frame1.setVisible(true);
  
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String ar[])
    {
        FCFS fc=new FCFS("RevIn1.txt");
       fc.find();
    }
}
