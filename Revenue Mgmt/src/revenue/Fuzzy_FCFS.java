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
public class Fuzzy_FCFS 
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
    
    double g=0.1;
    int level;
    
    double revenue=0;
    double mean=0;
    double sigma=0;
    
    double meangold=0;
    double sigmagold=0;
    
    Fuzzy_FCFS(String pp,int lev)
    {
        path=pp;
        level=lev;
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
                    String serType1=Res_Type.get(j).toString();
                    
                    String cust1=Cust_Type.get(j).toString();                            
                    
                    if(slt1>0)
                    {
                        int cpu=Integer.parseInt(CPU.get(j).toString());
                        int band=Integer.parseInt(Bandwidth.get(j).toString());
                        int store=Integer.parseInt(Storage.get(j).toString());
                        
                        if(level==2) 
                        {
                            if(serType1.equals("1"))
                                g=0.01;
                        
                            if(serType1.equals("2"))
                                g=0.05;
                        
                            if(serType1.equals("3"))
                                g=0.1;
                        }
                        
                        if(level==3)
                        {
                            if(cust1.equals("yes"))
                                g=0.01;
                            else
                                g=0.1;
                        }
                        
                        if(level==4)
                            g=0.1;
                        String cpufy=fuzzy(cpu,g);
                        String bandfy=fuzzy(band,g);
                        String storefy=fuzzy(store,g);
                                           
                        
                        String cf[]=cpufy.split("#");
                        String bf[]=bandfy.split("#");
                        String sf[]=storefy.split("#");                        
                        
                        double sef1=Double.parseDouble(sf[2]);
                        double bef1=Double.parseDouble(bf[2]);
                        
                        boolean bo1=CPUPro.allocateMipsForVm(vm1,Double.parseDouble(cf[2]));
                        boolean bo2=StePro.allocateRamForVm(vm1,(int)sef1);
                        boolean bo3=BwPro.allocateBwForVm(vm1,(int)bef1);
                        
                        
                        //if(Double.parseDouble(cf[2])<CPU_max && Double.parseDouble(bf[2])<Band_max && Double.parseDouble(sf[2])<Store_max)
                        //if(cpu<CPU_max && band<Band_max && store<Store_max)
                        if(bo1 && bo2 && bo3)
                        {
                            int flag1=0;
                            for(int k=j+1;k<JobNo.size();k++)
                            {
                                String jb2=JobNo.get(k).toString();
                                int slt2=Integer.parseInt(TempSlotNo.get(k).toString());
                                String serType2=Res_Type.get(k).toString();
                                String cust2=Cust_Type.get(k).toString();
                                
                                if(slt2>0)
                                {
                                    int cpu2=Integer.parseInt(CPU.get(k).toString());
                                    int band2=Integer.parseInt(Bandwidth.get(k).toString());
                                    int store2=Integer.parseInt(Storage.get(k).toString());
                                    
                                    if(level==2) 
                                    {
                                        if(serType2.equals("1"))
                                            g=0.01;
                        
                                        if(serType2.equals("2"))
                                            g=0.05;
                        
                                        if(serType2.equals("3"))
                                            g=0.1;
                                    }
                        
                                    if(level==3)
                                    {
                                        if(cust2.equals("yes"))
                                            g=0.01;
                                        else
                                            g=0.1;
                                    }
                        
                                    if(level==4)
                                        g=0.1;
                        
                                    String cpufy2=fuzzy(cpu2,g);
                                    String bandfy2=fuzzy(band2,g);
                                    String storefy2=fuzzy(store2,g);
                                    
                                    int cp=cpu+cpu2;
                                    int ba=band+band2;
                                    int ste=store+store2;
                                    
                                    String cpfy=fuzzyAdd(cpufy,cpufy2);
                                    String bafy=fuzzyAdd(bandfy,bandfy2);
                                    String stefy=fuzzyAdd(storefy,storefy2);
                                    
                                    System.out.println("---------------------------------");
                                    
                                    System.out.println(cpfy+" : "+cp+" : "+CPU_max);
                                    System.out.println(bafy+" : "+ba+" : "+Band_max);
                                    System.out.println(stefy+" : "+ste+" : "+Store_max);
                                    
                                    System.out.println("---------------------------------");
                                   
                                    String cf2[]=cpfy.split("#");
                                    String bf2[]=bafy.split("#");
                                    String sf2[]=stefy.split("#");
                                    
                                    /*if(cp==CPU_max)
                                        c1++;
                                    
                                    if(ba==Band_max)
                                        c2++;
                                    
                                    if(ste==Store_max)
                                        c3++;*/
                                    
                                    if(Double.parseDouble(cf2[2])==CPU_max)
                                        c1++;
                                    
                                    if(Double.parseDouble(bf2[2])==Band_max)
                                        c2++;
                                    
                                    if(Double.parseDouble(sf2[2])==Store_max)
                                        c3++;
                                    
                                    
                                    if(c1==3||c2==3||c3==3)
                                        c4++;
                                    
                                    double sef2=Double.parseDouble(sf2[2]);
                                    double bef2=Double.parseDouble(bf2[2]);
                                    boolean bool1=CPUPro.allocateMipsForVm(vm1, Double.parseDouble(cf2[2]));
                                    boolean bool2=StePro.allocateRamForVm(vm1, (int)sef2);
                                    boolean bool3=BwPro.allocateBwForVm(vm1,(int)bef2);
                                    
                                    if(bool1 && bool2 && bool3 && c1<=3 && c2<=3 && c3<=3)
                                   // if((Double.parseDouble(cf2[2])<=CPU_max && Double.parseDouble(bf2[2])<=Band_max && Double.parseDouble(sf2[2])<=Store_max)&& c1<=3 && c2<=3 && c3<=3)
                                    //if((cp<=CPU_max && ba<=Band_max && ste<=Store_max)&& c1<=3 && c2<=3 && c3<=3)
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
            
            double rev=0;
            String mu="0#0#0";
            String gs="";
            String mn1="0#0#0";
            for(int i=0;i<cst.size();i++)
            {
                String g1=cst.get(i).toString();
                int ind1=JobNo.indexOf(g1);
                String stype=Res_Type.get(ind1).toString();
                String cust=Cust_Type.get(ind1).toString();
                
                if(level==2) 
                {
                    if(stype.equals("1"))
                        g=0.01;
                        
                    if(stype.equals("2"))
                        g=0.05;
                       
                    if(stype.equals("3"))
                        g=0.1;
                }
                        
                if(level==3)
                {
                    if(cust.equals("yes"))
                        g=0.01;
                    else
                        g=0.1;
                }
                        
                if(level==4)
                    g=0.1;
                
                double p1=Double.parseDouble(Price_per_Slot.get(ind1).toString());          
                
                
                String f1=fuzzy(p1,g);
                gs=gs+f1+"\n";
                mu=fuzzyAdd(mu,f1);
                rev=rev+Double.parseDouble(Price_per_Slot.get(ind1).toString());
                
                
                if(cust.equals("yes"))
                    mn1=fuzzyAdd(mn1,f1);
            }
            
            String ms1[]=mu.split("#");
            double mu1=Double.parseDouble(ms1[0])/cst.size();
            double mu2=Double.parseDouble(ms1[1])/cst.size();
            double mu3=Double.parseDouble(ms1[2])/cst.size();
            String mean1=mu1+"#"+mu2+"#"+mu3;
            DecimalFormat df=new DecimalFormat("#.##");
            
            String mn2[]=mn1.split("#");
            
            double zu1=Double.parseDouble(mn2[0])/cst.size();
            double zu2=Double.parseDouble(mn2[1])/cst.size();
            double zu3=Double.parseDouble(mn2[2])/cst.size();
            String mean2=zu1+"#"+zu2+"#"+zu3;
            
            
            
            String sig="0#0#0";
            String dev="0#0#0";;
            for(int i=0;i<cst.size();i++)
            {
                String g1=cst.get(i).toString();
                int ind1=JobNo.indexOf(g1);
                String stype=Res_Type.get(ind1).toString();
                String cust=Cust_Type.get(ind1).toString();
                
                if(level==2) 
                {
                    if(stype.equals("1"))
                        g=0.01;
                        
                    if(stype.equals("2"))
                        g=0.05;
                       
                    if(stype.equals("3"))
                        g=0.1;
                }
                        
                if(level==3)
                {
                    if(cust.equals("yes"))
                        g=0.01;
                    else
                        g=0.1;
                }
                        
                if(level==4)
                    g=0.1;
                
                double p1=Double.parseDouble(Price_per_Slot.get(ind1).toString());               
                
                
                String f1=fuzzy(p1,g);
                
                String dif=fuzzySub(f1,mean1);
                String sq=fuzzySqr(dif);
                
                sig=fuzzyAdd(sig,sq);
                
                if(cust.equals("yes"))
                {
                    String dif1=fuzzySub(f1,mean2);
                    String sq1=fuzzySqr(dif1);
                
                    dev=fuzzyAdd(dev,sq1);
                }
            }
            
            String si1[]=sig.split("#");
            double sig1=Double.parseDouble(si1[0])/cst.size();
            double sig2=Double.parseDouble(si1[1])/cst.size();
            double sig3=Double.parseDouble(si1[2])/cst.size();
            String sd=sig1+"#"+sig2+"#"+sig3;
            
            
            String dev2[]=dev.split("#");
            double sig4=Double.parseDouble(dev2[2])/cst.size();
            revenue=Double.parseDouble(df.format(Double.parseDouble(ms1[2])));
            mean=Double.parseDouble(df.format(mu3));
            sigma=Double.parseDouble(df.format(sig3));
            
            meangold=Double.parseDouble(df.format(zu3));
            sigmagold=Double.parseDouble(df.format(sig4));
            
            System.out.println("FCFS - Policy");
            System.out.println(res);
            System.out.println("Overall Revenue = "+df.format(rev));
            
            displayGraph();
            
            System.out.println("gs "+gs);
            System.out.println("pp "+mu);
            System.out.println("mean "+mean1);
            System.out.println("Stand. Deviation "+sd);
            String sdmu=fuzzyDivide(sd,mean1);
            System.out.println("Sigma/mean  "+sdmu);
            
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
            
            ChartFrame frame1=new ChartFrame("FCFS Graph - "+level,chart);
  
            frame1.setSize(700,500);
  
            frame1.setVisible(true);
  
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public String fuzzy(double a,double g)
    {
        String res="";
        try
        {
            double c=(1-g)*a;
            double d=(1+g)*a;
            
            res=c+"#"+a+"#"+d;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    
    public String fuzzyAdd(String r1,String r2)
    {
        String res="";
        try
        {
            String g1[]=r1.split("#");
            String g2[]=r2.split("#");
            
            double a1=Double.parseDouble(g1[0])+Double.parseDouble(g2[0]);
            double a2=Double.parseDouble(g1[1])+Double.parseDouble(g2[1]);
            double a3=Double.parseDouble(g1[2])+Double.parseDouble(g2[2]);
            
            res=a1+"#"+a2+"#"+a3;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    
    public String fuzzySub(String r1,String r2)
    {
        String res="";
        try
        {
            String g1[]=r1.split("#");
            String g2[]=r2.split("#");
            
            double a1=Double.parseDouble(g1[0])-Double.parseDouble(g2[0]);
            double a2=Double.parseDouble(g1[1])-Double.parseDouble(g2[1]);
            double a3=Double.parseDouble(g1[2])-Double.parseDouble(g2[2]);
            
            res=a1+"#"+a2+"#"+a3;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    
    public String fuzzyDivide(String r1,String r2)
    {
        String res="";
        try
        {
            String g1[]=r1.split("#");
            String g2[]=r2.split("#");
            
            double a1=Double.parseDouble(g1[0])/Double.parseDouble(g2[0]);
            double a2=Double.parseDouble(g1[1])/Double.parseDouble(g2[1]);
            double a3=Double.parseDouble(g1[2])/Double.parseDouble(g2[2]);
            
            res=a1+"#"+a2+"#"+a3;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public String fuzzySqr(String r1)
    {
        String res="";
        try
        {
            String g1[]=r1.split("#");
            double a1=Double.parseDouble(g1[0])*Double.parseDouble(g1[0]);
            double a2=Double.parseDouble(g1[1])*Double.parseDouble(g1[1]);
            double a3=Double.parseDouble(g1[2])*Double.parseDouble(g1[2]);
            
            res=a1+"#"+a2+"#"+a3;
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    
    public static void main(String ar[])
    {
       Fuzzy_FCFS fc=new Fuzzy_FCFS("RevIn1.txt",2);
       fc.find();
    }
}
