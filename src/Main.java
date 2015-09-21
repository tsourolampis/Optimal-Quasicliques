
import DenseSubgraph.GreedyAdditive;
import DenseSubgraph.GreedyF;
import GraphUtilities.Graph;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Charalampos Tsourakakis
 */
public class Main 
{ 
    public static final int k = 1;
    public static void main(String[] args) throws IOException
    {
        String[] datasets = loadDatasets();
        boolean[] methods = loadMethods();
        
        boolean greedyF = methods[0];
        boolean greedyAdditive = methods[1];

        String c = ",";
        
        File f = new File("TopK_TABLE_"+Math.random()+".csv");
        f.createNewFile();
        BufferedWriter bwTable = new BufferedWriter(new FileWriter(f));
        
        File g = new File("TopK_PLOTS_"+Math.random()+".csv");
        g.createNewFile();
        BufferedWriter bwPlots = new BufferedWriter(new FileWriter(g));
        
        String[] headTable = new String[]{"dataset","|S|","e[S]","D","f_d","f_alpha","f_e","f_t"};
        String[] headPlots = new String[]{"dataset","measure","k","our method (|S|)","our method (value)","our method--additive (|S|)","our method--additive (value)"};
        
        
        String headTable1 = "";
        String headTable2 = "";

        for(int i=1; i<headTable.length; i++)
        {
            if (i==1)
            {
                headTable1 += "our method"+c;
            }
            else
            {
                headTable1 += c;
            }
            
            headTable2 += headTable[i]+c;
        }
        for(int i=1; i<headTable.length; i++)
        {
            if (i==1)
            {
                headTable1 += "our method (additive)"+c;
            }
            else
            {
                headTable1 += c;
            }
            
            headTable2 += headTable[i]+c;
        }
        bwTable.write(headTable1); bwTable.newLine();
        bwTable.write(headTable2); bwTable.newLine();
        
        String headPlotsS = "";
        for(int i=0; i<headPlots.length; i++)
        {
            headPlotsS += headPlots[i]+c;
        }
        bwPlots.write(headPlotsS); bwPlots.newLine();
        
        for (String d:datasets)
        {
            Graph G = new Graph(d,"\t");
            G.setAlpha(3);
            G.TMAX=2000;
            System.out.println("Graph read\nNumber of vertices="+G.V()+", number of edges="+G.E());
            
            double[][] outputF = null;
            if (greedyF)
            {
                DenseSubgraph.GreedyF DSF = new GreedyF(G);
                DSF.datasetname=d;
                outputF = DSF.GreedyOptimizeF_FAST_TopK(0.0,k);
            }
            
            
            double[][] outputCharikar = null;
          
            
            double[][] outputAdditive = null;
            if(greedyAdditive)
            {
                DenseSubgraph.GreedyAdditive DSAdditive = new GreedyAdditive(G);
                DSAdditive.datasetname = d;
                outputAdditive = DSAdditive.GreedyAdditiveOptimize_TopK(k);
                //outputAdditive = null;
            }
            
            
            int rows = -1;
            int columns = -1;
            if(outputF != null)
            {
                rows = outputF.length;
                columns = outputF[0].length;
            }
            else if (outputCharikar != null)
            {
                rows = outputCharikar.length;
                columns = outputCharikar[0].length;
            }
            else
            {
                rows = outputAdditive.length;
                columns = outputAdditive[0].length;
            }
            
            
            
            for(int j=0; j<columns; j++)
            {
                for(int h=0; h<rows; h++)
                {
                    String charikar1 = (outputCharikar==null)?"---":""+outputCharikar[h][0];
                    String charikar2 = (outputCharikar==null)?"---":""+outputCharikar[h][j];
                    String f1 = (outputF==null)?"---":""+outputF[h][0];
                    String f2 = (outputF==null)?"---":""+outputF[h][j];
                    String add1 = (outputAdditive==null)?"---":""+outputAdditive[h][0];
                    String add2 = (outputAdditive==null)?"---":""+outputAdditive[h][j];
                    
                    String t = d+c+headTable[j+1]+c+(h+1)+c+charikar1+c+charikar2+c+f1+c+f2+c+add1+c+add2+c;
                    bwPlots.write(t);
                    bwPlots.newLine();
                    bwPlots.flush();
                }
            }
            
            double[] avgF = (outputF==null)?null:new double[outputF[0].length];
            double[] avgAdditive = (outputAdditive==null)?null:new double[outputAdditive[0].length];
            for(int a=0; a<columns; a++)
            {
                for(int b=0; b<rows; b++)
                {
                    if(avgF != null) avgF[a] += outputF[b][a];
                    if(avgAdditive != null) avgAdditive[a] += outputAdditive[b][a];
                }
            }
            for(int a=0; a<columns; a++)
            {
                if(avgF != null) avgF[a] /= outputF.length;
                if(avgAdditive != null) avgAdditive[a] /= outputAdditive.length;
            }
            
            String t = d+c;
            for(int i=0; i<columns; i++)
            {
                String s = (avgF == null)?"---":""+avgF[i];
                t += s+c;
            }
            for(int i=0; i<columns; i++)
            {
                String s = (avgAdditive == null)?"---":""+avgAdditive[i];
                t += s+c;
            }
            bwTable.write(t);
            bwTable.newLine();
            bwTable.flush();
        }
        
        bwTable.flush();
        bwTable.close();
        bwPlots.flush();
        bwPlots.close();
    }

    private static String[] loadDatasets() throws IOException
    {
        ArrayList<String> v = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("datasetsTopK.txt"));
        String line = br.readLine();
        while (line != null)
        {
            v.add(line);
            line = br.readLine();
        }
        
        br.close();
        
        String[] x = new String[v.size()];
        v.toArray(x);
        return x;
    }
    
    private static boolean[] loadMethods() throws IOException
    {
        boolean[] methods = new boolean[2];
        BufferedReader br = new BufferedReader(new FileReader("methodsTopK.txt"));
        String line = br.readLine();
        while (line != null)
        {
 
            if(line.equalsIgnoreCase("greedyF"))
            {
                methods[0] = true;
            }
            else if (line.equalsIgnoreCase("greedyAdditive"))
            {
                methods[1] = true;
            }
            line = br.readLine();
        }
        
        br.close();
        
        return methods;
    }
    
    
}
