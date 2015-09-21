/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DenseSubgraph;

import GraphUtilities.Graph;
import GraphUtilities.SET;
import java.io.*;
import java.util.*;
/**
 *
 * @author Charalampos E. Tsourakakis
 */
public class GreedyAdditive {
    
    public Collection<Integer> S; 
    private Graph G;

    public String datasetname;
    public double optFGreedy;
    //public int donttouch[]; 
    
    public GreedyAdditive(Graph myG)
    {
        this.G = myG;
        S = new HashSet<Integer>();              
    }    
    
    /*
    public void GreedyAdditiveOptimize() throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();
        G.setAllActive();
        Map<Integer,Collection<Integer>> graph = loadGraph();
        System.out.print("Computing dense component...");
        
        
        
        Collection<Integer>[] degrees = new Collection[graph.size()+1];
        for (int i=0; i<degrees.length; i++)
        {
            degrees[i] = new HashSet<Integer>();
        }
        for(int x:graph.keySet())
        {
            degrees[graph.get(x).size()].add(x);
        }
        int i=0;
        while(i<degrees.length && degrees[i].isEmpty())
        {
            i++;
        }
        
        S = new HashSet<Integer>();
        Collection<Integer> Sopt = new HashSet<Integer>();
        for(int z:graph.keySet())
        {
            S.add(z);
            Sopt.add(z);
        }
        double max = G.Score_FAST(S);
       // System.out.println("MAX "+max+" alpha "+G.alpha );
        double score;
        Collection<Integer> acc = new HashSet<Integer>();  
        while(i<degrees.length)
        {
            int x = degrees[i].iterator().next();            
            
            for(int y:graph.get(x))
            {
                int d = graph.get(y).size();
                degrees[d].remove(y);
                degrees[d-1].add(y);
                graph.get(y).remove(x);
            }
            graph.remove(x);
            S.remove(x);
            score = G.Score_FAST(S);
            degrees[i].remove(x);
            acc.add(x);
            
            if (score > max)
            {
               // System.out.println("Size of Sopt now "+Sopt.size()+" score "+G.Score_FAST(Sopt));
                max = score;
                for (int y:acc)
                {
                    Sopt.remove(y);
                }
                acc = new HashSet<Integer>();
            }
            if(i>0 && !degrees[i-1].isEmpty())
            {
                i=i-1;
            }
            else
            {
                while(i<degrees.length && degrees[i].isEmpty())
                {
                    i++;
                }
            }
        }

        
       // System.out.println("The end, score"+optFGreedy);
        long endTime = System.currentTimeMillis();
        double seconds =  (double)(endTime - startTime)/1000;
       // System.out.println("Running Algorithm took " +seconds + " seconds");
      //  System.out.println("Objective function value= "+max);
        S = Sopt;
        optFGreedy = G.Score_FAST(S);
        printOutput_FAST(0);
    }
    */
    
    
    public double[][] GreedyAdditiveOptimize_TopK(int k) throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();
        
        int[][] output = new int[k][G.V()+1];
        S = new HashSet<Integer>();
        
        G.setAllActive();
        boolean stop = false;
        int i=0;
        double[][] out = new double[k][7];
        String[] outString = new String[k];
        for (; i<k && !stop; i++)
        {
        //initialize partition
            System.out.println("Starting Top-"+(i+1));
            S = new HashSet<Integer>();

            runGreedyAdditiveOptimize();
            
            String[] s = new String[1];
            double[] v = outputTopK(i+1, s);
            for(int j=0; j<v.length; j++)
            {
                out[i][j] += v[j];
            }
            outString[i] = s[0];
            
            for(int j:S)
            {
                G.exist[j] = false;
                output[i][j] = 1;
            }
            
            int count = 0;
            for (int j=1; j<=G.V(); j++)
            {
                if (G.exist[j])
                {
                    count++;
                }
            }
            if(count == 0)
            {
                stop = true;
            }
        }
        
        for (int h=i; h<output.length; h++)
        {
            output[h] = null;
        }
       // System.out.println("The end, score"+optFGreedy);
        long endTime = System.currentTimeMillis();
        double seconds =  (double)(endTime - startTime)/1000;
         System.out.println("Running Algorithm took " +seconds + " seconds");
         System.out.println("Objective function value= "+optFGreedy);
         
         //checkOverlap(output);
         
         return out;
    }
    
    public void GreedyAdditiveOptimize() throws IOException
    {
        GreedyAdditiveOptimize(-1);
    }
    
    public void GreedyAdditiveOptimize(int printing) throws IOException
    {
        G.setAllActive();
        runGreedyAdditiveOptimize();
        printOutput_FAST(0,printing);
    }
    
    public void runGreedyAdditiveOptimize() throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();
        //G.setAllActive();
        Map<Integer,Collection<Integer>> graph = loadGraph();
        System.out.print("Computing dense component...");
        
        
        
        Collection<Integer>[] degrees = new Collection[graph.size()+1];
        for (int i=0; i<degrees.length; i++)
        {
            degrees[i] = new HashSet<Integer>();
        }
        for(int x:graph.keySet())
        {
            degrees[graph.get(x).size()].add(x);
        }
        int i=0;
        while(i<degrees.length && degrees[i].isEmpty())
        {
            i++;
        }
        
        S = new HashSet<Integer>();
        Collection<Integer> Sopt = new HashSet<Integer>();
        for(int z:graph.keySet())
        {
            S.add(z);
            Sopt.add(z);
        }
        //double max = G.Score_FAST(S);
        int inducedEdges = inducedEdges_fast(graph,S);
        int size = S.size();
        double max = (double)G.alpha*inducedEdges-(double)size*(double)(size-1)/2;
        //double max = score_fast(graph,S,G.alpha);
       // System.out.println("MAX "+max+" alpha "+G.alpha );
        double score;
        Collection<Integer> acc = new HashSet<Integer>();
        int n = S.size();
        int perc = n/20;
        //System.out.println("n="+n);
        //System.out.println("perc="+perc);
        while(i<degrees.length)
        {
            //System.out.print(i+",");
            
            if ((n-S.size())%perc==0)
            {
                System.out.println(S.size());
            }
            
            //System.out.println(S.size());
            int x = degrees[i].iterator().next();            
            
            int count = 0;
            for(int y:graph.get(x))
            {
                int d = graph.get(y).size();
                degrees[d].remove(y);
                degrees[d-1].add(y);
                graph.get(y).remove(x);
                count++;
            }
            graph.remove(x);
            S.remove(x);
            //score = G.Score_FAST(S);
            //score = score_fast(graph,S,G.alpha);
            
            inducedEdges -= count;
            size = S.size();
            score = (double)G.alpha*inducedEdges-(double)size*(double)(size-1)/2;
            degrees[i].remove(x);
            acc.add(x);
            
            if (score > max)
            {
               // System.out.println("Size of Sopt now "+Sopt.size()+" score "+G.Score_FAST(Sopt));
                max = score;
                for (int y:acc)
                {
                    Sopt.remove(y);
                }
                acc = new HashSet<Integer>();
            }
            if(i>0 && !degrees[i-1].isEmpty())
            {
                i=i-1;
            }
            else
            {
                while(i<degrees.length && degrees[i].isEmpty())
                {
                    i++;
                }
            }
        }
        System.out.println("");
        
       // System.out.println("The end, score"+optFGreedy);
        long endTime = System.currentTimeMillis();
        double seconds =  (double)(endTime - startTime)/1000;
       // System.out.println("Running Algorithm took " +seconds + " seconds");
      //  System.out.println("Objective function value= "+max);
        S = Sopt;
        optFGreedy = score_fast(graph,S,G.alpha);
        //printOutput_FAST(0);
    }
    
    
    /*
    private Map<Integer, Collection<Integer>> loadGraph() 
    {
        Map<Integer,Collection<Integer>> graph = new HashMap<Integer, Collection<Integer>>();
        
        for (int x=1; x<=G.V(); x++)
        {
            Collection<Integer> neighbors = new HashSet<Integer>();
            for(int y:G.adjacentTo(x))
            {
                neighbors.add(y);
            }
            graph.put(x, neighbors);
        }
        
        return graph;
    }
    */
    
    private Map<Integer, Collection<Integer>> loadGraph() 
    {
        Map<Integer,Collection<Integer>> graph = new HashMap<Integer, Collection<Integer>>();
        
        for (int x=1; x<=G.V(); x++)
        {
            if (G.exist[x])
            {
                Collection<Integer> neighbors = new HashSet<Integer>();
                for(int y:G.adjacentTo(x))
                {
                    if(G.exist[y])
                    {
                        neighbors.add(y);
                    }
                }
                graph.put(x, neighbors);
            }
        }
        
        return graph;
    }

    
    public  void printOutput_FAST(int cocktail, int printing) throws IOException 
    {
            Collection<Integer> S_HashSet = S;
        
            System.out.println(" Computing induced edges ...");
            long startTime = System.currentTimeMillis();
            int induced = G.InducedEdges_FAST(S_HashSet);
            long endTime = System.currentTimeMillis();
            double seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced Edges took " +seconds + " seconds");

            System.out.println(" Computing induced triangles ...");
            startTime = System.currentTimeMillis();
            int triangles = G.InducedTriangles_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced triangles took " +seconds + " seconds");


            System.out.println(" Computing order of S ...");
            startTime = System.currentTimeMillis();
            int sizeofS = S_HashSet.size();
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing order of S took " +seconds + " seconds");

            double avgdegree = (double)induced/(double)sizeofS;
            double density = (double)induced/((double)(sizeofS*(sizeofS-1))/2);
            double tdensity = (double)triangles/((double)(sizeofS*(sizeofS-1)*(sizeofS-2))/6);

            System.out.println(" Computing Conductance of S ...");
            startTime = System.currentTimeMillis();
            double phi=G.Conductance_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing Conductance of S took " +seconds + " seconds");

            System.out.println(" Computing Ncut of S ...");
            startTime = System.currentTimeMillis();
            double ncut = G.NCut_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing Ncut of S took " +seconds + " seconds");


            System.out.println(" Computing Expansion of S ...");
            startTime = System.currentTimeMillis();
            double expansion =G.Expansion_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing Expansion of S took " +seconds + " seconds");


            System.out.println(" Computing edges across the cut (S,V-S) ...");
            startTime = System.currentTimeMillis();
            int edgesacross = G.EdgesAcrossCut_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing edges across took " +seconds + " seconds");


            System.out.println(" Computing cut ratio of S ...");
            startTime = System.currentTimeMillis();
            double cutratio = G.CutRatio_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing  cut ratio of S took " +seconds + " seconds");


            System.out.println(" Computing induced diameter ...");
            startTime = System.currentTimeMillis();
            int diam = G.InducedDiameter_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced Diameter took " +seconds + " seconds");

            System.out.println(" Computing heuristic induced diameter ...");
            startTime = System.currentTimeMillis();
            int hdiam = G.HeuristicInducedDiameter_FAST(S_HashSet);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced Diameter took " +seconds + " seconds");

            String suffix = (printing==-1)?"":("Top"+printing+"_");
            String filename = "GreedyPSB"+G.alpha+datasetname+"_"+suffix+Math.random();
            if( cocktail == 1)
                filename = "Cocktail"+filename;
            File f = new File(filename);
            f.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("Optimal Value "+optFGreedy+"\n");


            bw.write("Induced edges "+induced+"\n");
            bw.write("Induced triangles "+triangles+"\n");
            bw.write("Order of S "+sizeofS+"\n");
            bw.write("Edge Density "+density+"\n");
            bw.write("Triangle Density "+tdensity+"\n") ;
            bw.write("Induced Diameter "+diam+"\n");
            bw.write("Induced Diameter with Aris Heuristic "+hdiam+"\n");
            bw.write("Edges across cut "+edgesacross+"\n");
            bw.write("Conductance of bipartition "+phi+"\n");
            bw.write("Cut ratio of bipartition "+cutratio+"\n");
            bw.write("Expansion "+expansion+"\n");
            bw.write("Normalized cut "+ncut+"\n");
            bw.write("Avg. degree "+avgdegree+"\n");
            bw.write("Optimal subset\n");
            for(int i:S_HashSet)
                bw.write(i+"\n");
            bw.flush();
            bw.close();
    }


    
    public double score_fast(Map<Integer,Collection<Integer>> graph, Collection<Integer> S_HashSet, double alpha) 
    {
        int inducedEdges = inducedEdges_fast(graph,S_HashSet);
        
        int size = S_HashSet.size();
        double score = (double)alpha*inducedEdges-(double)size*(double)(size-1)/2;
        //System.out.println("Size of S "+S_HashSet.size()+" induced edges "+inducedEdges+" score "+score);
        return score;
    }
    
    public int inducedEdges_fast(Map<Integer,Collection<Integer>> graph, Collection<Integer> S_HashSet)
    {
        int inducedEdges = 0;
        for(int i:S_HashSet)
        {
            Collection<Integer> neighbors = graph.get(i);
            if (neighbors != null)
            {
                for(int v:neighbors)
                {
                    if(v!=i && S_HashSet.contains(v))
                    {
                        inducedEdges++;
                    }
                }
            }
        }
        inducedEdges /= 2;
        
        return inducedEdges;
    }
    
    public double[] outputTopK(int k, String[] output)
    {
        String c = ",";
        String s = datasetname+c+k+c;
        
        Collection<Integer> S_HashSet = S;

        int size = S_HashSet.size();
        int eS = G.InducedEdges_FAST(S_HashSet);
        int D = G.InducedDiameter_FAST(S_HashSet);
        //int D = G.HeuristicInducedDiameter_FAST(S_HashSet);
        double fd = (size==0)?0.0:((double)(2*eS)/(double)size);
        double falpha = optFGreedy;
        double fe = (size<=1)?0.0:((double)eS/((double)(size*(size-1))/2));
        int triangles = G.InducedTriangles_FAST(S_HashSet);
        //int triangles = 0;
        double ft = (size<=2)?0.0:((double)triangles/((double)(size*(size-1)*(size-2))/6));
        
        s += size+c;
        s += eS+c;
        s += D+c;
        s += fd+c;
        s += falpha+c;
        s += fe+c;
        s += ft;
        
        output[0] = s;
        return new double[]{size,eS,D,fd,falpha,fe,ft};  
    }
    
     
}
