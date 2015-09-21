/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DenseSubgraph;

import GraphUtilities.Graph;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
/**
 *
 * @author Charalampos E. Tsourakakis
 */
public class GreedyF {
    
    public int[] S; 
    public int[] Sbar; 
    private Graph G;

    public String datasetname;
    public double optFGreedy;
    public int donttouch[]; 
    
    public GreedyF(Graph myG)
    {
        this.G = myG;
        S = new int[G.V()+1];
        Sbar = new int[G.V()+1];                
    }
    
    
    public void initializeCocktailPartition(int initial[])
    {
        donttouch = new int[G.V()+1];
        int n = G.V();
        for(int i=1;i<=n;i++)
        {
            if(initial[i]==1)
            {
                S[i]=1;
                Sbar[i]=0;
                donttouch[i]=1;
            }
            else
            {
                donttouch[i]=0;
                Sbar[i]=1;
                S[i]=0;
            }
        }
    }
    
    public void initializePartition()
    {
        Random generator = new Random(); 
        int n = G.V();
        int v = generator.nextInt(n)+1;
        //int v = n/2;
        donttouch = new int[G.V()+1];
        for(int i=1;i<=n;i++)
        {
            S[i]=0;
            Sbar[i]=1;
            donttouch[i]=0; 
        }
        Sbar[v]=0; 
        S[v]=1;
    }
    
    public void initializePartition_HashSet(Collection<Integer> S_HashSet, Collection<Integer> Sbar_HashSet)
    {
        donttouch = new int[G.V()+1];
        /*
        for(int i=1;i<donttouch.length;i++)
        {
            donttouch[i]=0; 
        }
        */
        
        for (int i=1; i<=G.V(); i++)
        {
            if(G.exist[i])
            {
                Sbar_HashSet.add(i);
            }
        }

        int v = pickRandom();
        //int v = n/2;
        Sbar_HashSet.remove(v);
        S_HashSet.add(v);
    }
    
    public int pickRandom()
    {
        ArrayList<Integer> active = new ArrayList<Integer>();
        for(int i=1; i<=G.V(); i++)
        {
            if(G.exist[i])
            {
                active.add(i);
            }
        }
        
        Random generator = new Random();
        int n = active.size();
        int v = active.get(generator.nextInt(n));
        
        return v;
    }
    
    public void initializeCocktailPartition_HashSet(int seed[],Collection<Integer> S_HashSet, Collection<Integer> Sbar_HashSet)
    {
        donttouch = new int[G.V()+1];
        for(int i:seed)
        {
            donttouch[i] = 1;
        }
        /*
        for(int i=1;i<donttouch.length;i++)
        {
            donttouch[i]=0; 
        }
        */
        for (int i=1; i<=G.V(); i++)
        {
            Sbar_HashSet.add(i);
        }
        
        for(int i:seed)
        {
            Sbar_HashSet.add(i);
            S_HashSet.add(i);
        }
    }
    
    
    private void printPartition()
    {
        System.out.println("Printing Partition");
        System.out.print("S={");
        for(int i = 1;i<S.length;i++)
            if(S[i]==1)
                System.out.print(i+" ");
        System.out.println("}");
        System.out.print("Sbar={");
        for(int i = 1;i<S.length;i++)
            if(Sbar[i]==1)
                System.out.print(i+" ");
        System.out.println("}");
        System.out.println("End Printing Partition");
            
    }
    
    
    public void GreedyOptimizeF(double epsilon) throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();
        initializePartition();
        G.setAllActive();
        boolean b1=true;
        boolean b2=true;
        boolean found;
        int tmpS[] = new int[G.V()+1];
        for( int i = 1; i< tmpS.length;i++)
                tmpS[i]=0;
        int counter = 0;
        while(b1 && counter <=G.TMAX)
        {
            while(b2 && counter <=G.TMAX)
            {
                System.out.println("Iteration "+counter);
                counter = counter + 1;
                found = false;
                for(int i=1;i<Sbar.length;i++)
                {
                    if(Sbar[i] == 1)
                    {
                        System.out.println("Investigative vertex "+i);
                        for(int k=1; k<=G.V();k++)
                           tmpS[k] =  S[k];
                        System.out.println(" done copying");
                        tmpS[i] = 1; 
                        // debug
                        if( S[i] == 1)
                        {
                            System.err.println("Error\n"); 
                            System.exit(-1);
                        }
                        if( G.Score(tmpS)>=(1+epsilon)*G.Score(S))
                        {
                            found = true;
                            System.out.println("Vertex "+(i+1)+" improves things when added from "+G.Score(S)+" to "+G.Score(tmpS));
                            S[i] = 1;
                            Sbar[i]=0;
                        }
                    }
                }
                //printPartition();
                if( found == false)
                {
                    b2 = false;
                    //System.out.println("No additions to S can make improvemens now");
                }
                //else
                    //System.out.println("something that improved things was found");
                
            }
            //System.out.println("Set found to false now ...");
            found = false;
            for(int i=1;i<S.length;i++)       
            {
                for(int k=1; k<=G.V();k++)
                           tmpS[k] =  S[k]; 
                if( S[i] == 1 && donttouch[i]==0)
                {
                    tmpS[i]=0;
                    if( G.Score(tmpS)>=(1+epsilon)*G.Score(S))
                    {
                        found = true;
                       // System.out.println("Vertex "+(i+1)+" improves things when removed from "+G.Score(S)+" to "+G.Score(tmpS));
                        S[i] = 0;
                        Sbar[i]=1;
                    }
                }
                
            }
            if( found == false)
                b1 = false; 
            //else
                //System.out.println("something that improved things was found");
            
        }
        if( G.Score(S)< G.Score(Sbar))
        {
           tmpS = Sbar;
           Sbar = S;
           S     = tmpS;
        }
        optFGreedy = G.Score(S); 
       // System.out.println("The end, score"+optFGreedy);
        long endTime = System.currentTimeMillis();
        double seconds =  (double)(endTime - startTime)/1000;
         System.out.println("Running Algorithm took " +seconds + " seconds");
         System.out.println("Objective function value= "+optFGreedy);
        printOutput(0);
    }
    
    public void GreedyOptimizeF_FAST(double epsilon) throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();

        //initialize partition
        Collection<Integer> S_HashSet = new HashSet<Integer>();
        Collection<Integer> Sbar_HashSet = new HashSet<Integer>();
        G.setAllActive();
        initializePartition_HashSet(S_HashSet, Sbar_HashSet);
        
        runGreedyOptimize_FAST(epsilon,S_HashSet,Sbar_HashSet);
        

       // System.out.println("The end, score"+optFGreedy);
        long endTime = System.currentTimeMillis();
        double seconds =  (double)(endTime - startTime)/1000;
         System.out.println("Running Algorithm took " +seconds + " seconds");
         System.out.println("Objective function value= "+optFGreedy);
        printOutput_FAST(0);
    }
    
    public double[][] GreedyOptimizeF_FAST_TopK(double epsilon, int k) throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();
        
        int[][] output = new int[k][G.V()+1];
        S = new int[G.V()+1];
        Sbar = new int[G.V()+1];
        
        G.setAllActive();
        boolean stop = false;
        int i=0;
        double[][] out = new double[k][7];
        String[] outString = new String[k];
        for (; i<k && !stop; i++)
        {
        //initialize partition
            S = new int[G.V()+1];
            Sbar = new int[G.V()+1];
            Collection<Integer> S_HashSet = new HashSet<Integer>();
            Collection<Integer> Sbar_HashSet = new HashSet<Integer>();
            initializePartition_HashSet(S_HashSet, Sbar_HashSet);

            runGreedyOptimize_FAST(epsilon,S_HashSet,Sbar_HashSet);
            
            //printOutput_FAST(0,i+1);
            String[] s = new String[1];
            double[] v = outputTopK(i+1, s);
            for(int j=0; j<v.length; j++)
            {
                out[i][j] += v[j];
            }
            outString[i] = s[0];
            
            for(int j=1; j<=G.V(); j++)
            {
                if(S[j] == 1)
                {
                    G.exist[j] = false;
                }
                output[i][j] = S[j];
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
        
        /*
        for(int j=0; j<out.length; j++)
        {
            out[j] /= k;
        }
        */
        //printOutputTopK(outString);
        
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
    
    public void CocktailParty(int seed[], double epsilon) throws IOException
    {
        int[] mynewseed = new int[G.V()+1];
        for(int i = 1;i<= G.V();i++)
            mynewseed[i]=0;
        for(int i = 0; i<seed.length; i++)
            mynewseed[seed[i]]=1;
        initializeCocktailPartition(mynewseed);
        G.setAllActive();
        boolean b1=true;
        boolean b2=true;
        boolean found;
        int tmpS[] = new int[G.V()+1];
        for( int i = 1; i< tmpS.length;i++)
                tmpS[i]=0;
        while(b1)
        {
             
            while(b2)
            {
                found = false;
                for(int i=1;i<Sbar.length;i++)
                {
                    if(Sbar[i] == 1)
                    {
                        for(int k=1; k<=G.V();k++)
                           tmpS[k] =  S[k]; 
                        tmpS[i] = 1; 
                        // debug
                        if( S[i] == 1)
                        {
                            System.err.println("Error\n"); 
                            System.exit(-1);
                        }
                        if( G.Score(tmpS)>=(1+epsilon)*G.Score(S))
                        {
                            found = true;
                            //System.out.println("Vertex "+(i+1)+" improves things when added from "+G.Score(S)+" to "+G.Score(tmpS));
                            S[i] = 1;
                            Sbar[i]=0;
                        }
                    }
                }
                //printPartition();
                if( found == false)
                {
                    b2 = false;
                    //System.out.println("No additions to S can make improvemens now");
                }
                //else
                    //System.out.println("something that improved things was found");
                
            }
            //System.out.println("Set found to false now ...");
            found = false;
            for(int i=1;i<S.length;i++)       
            {
                for(int k=1; k<=G.V();k++)
                           tmpS[k] =  S[k]; 
                if( S[i] == 1 && donttouch[i]==0)
                {
                    tmpS[i]=0;
                    if( G.Score(tmpS)>=(1+epsilon)*G.Score(S))
                    {
                        found = true;
                       // System.out.println("Vertex "+(i+1)+" improves things when removed from "+G.Score(S)+" to "+G.Score(tmpS));
                        S[i] = 0;
                        Sbar[i]=1;
                    }
                }
                
            }
            if( found == false)
                b1 = false; 
            //else
                //System.out.println("something that improved things was found");
            
        }
        if( G.Score(S)< G.Score(Sbar))
        {
           tmpS = Sbar;
           Sbar = S;
           S     = tmpS;
        }
        optFGreedy = G.Score(S); 
       // System.out.println("The end, score"+optFGreedy);
        printOutput(1);

    }
    
    public void CocktailParty_FAST(int seed[], double epsilon) throws IOException
    {
        System.out.println(" Running ...");
        long startTime = System.currentTimeMillis();

        //initialize partition
        Collection<Integer> S_HashSet = new HashSet<Integer>();
        Collection<Integer> Sbar_HashSet = new HashSet<Integer>();
        initializeCocktailPartition_HashSet(seed, S_HashSet, Sbar_HashSet);
        
        runGreedyOptimize_FAST(epsilon,S_HashSet,Sbar_HashSet);
        

       // System.out.println("The end, score"+optFGreedy);
        long endTime = System.currentTimeMillis();
        double seconds =  (double)(endTime - startTime)/1000;
         System.out.println("Running Algorithm took " +seconds + " seconds");
         System.out.println("Objective function value= "+optFGreedy);
        printOutput_FAST(1);
    }
    
    public  void printOutput(int cocktail) throws IOException 
    {
            System.out.println(" Computing induced edges ...");
            long startTime = System.currentTimeMillis();
            int induced = G.InducedEdges(S);
            long endTime = System.currentTimeMillis();
            double seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced Edges took " +seconds + " seconds");

            System.out.println(" Computing induced triangles ...");
            startTime = System.currentTimeMillis();
            int triangles = G.InducedTriangles(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced triangles took " +seconds + " seconds");


            System.out.println(" Computing order of S ...");
            startTime = System.currentTimeMillis();
            int sizeofS = 0;
            for(int i = 1; i<=G.V();i++)
                if(S[i]==1)
                    sizeofS+=1;
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing order of S took " +seconds + " seconds");

            double avgdegree = (double)induced/(double)sizeofS;
            double density = (double)induced/((double)(sizeofS*(sizeofS-1))/2);
            double tdensity = (double)triangles/((double)(sizeofS*(sizeofS-1)*(sizeofS-2))/6);

            System.out.println(" Computing Conductance of S ...");
            startTime = System.currentTimeMillis();
            double phi=G.Conductance(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing Conductance of S took " +seconds + " seconds");

            System.out.println(" Computing Ncut of S ...");
            startTime = System.currentTimeMillis();
            double ncut = G.NCut(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing Ncut of S took " +seconds + " seconds");


            System.out.println(" Computing Expansion of S ...");
            startTime = System.currentTimeMillis();
            double expansion =G.Expansion(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing Expansion of S took " +seconds + " seconds");


            System.out.println(" Computing edges across the cut (S,V-S) ...");
            startTime = System.currentTimeMillis();
            int edgesacross = G.EdgesAcrossCut(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing edges across took " +seconds + " seconds");


            System.out.println(" Computing cut ratio of S ...");
            startTime = System.currentTimeMillis();
            double cutratio = G.CutRatio(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Computing  cut ratio of S took " +seconds + " seconds");


            System.out.println(" Computing induced diameter ...");
            startTime = System.currentTimeMillis();
            int diam = G.InducedDiameter(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced Diameter took " +seconds + " seconds");

            System.out.println(" Computing heuristic induced diameter ...");
            startTime = System.currentTimeMillis();
            int hdiam = G.HeuristicInducedDiameter(S);
            endTime = System.currentTimeMillis();
            seconds =  (double)(endTime - startTime)/1000;
            System.out.println("Induced Diameter took " +seconds + " seconds");


            String filename = "MyGreedyAlpha"+G.alpha+datasetname+"_"+Math.random();
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
            for(int i=0;i<S.length;i++)
                if(S[i]==1)
                       bw.write(i+"\n");
            bw.flush();
            bw.close();
    }
    
    public  void printOutput_FAST(int cocktail) throws IOException 
    {
        printOutput_FAST(cocktail,-1);
    }
    
    public double[] outputTopK(int k, String[] output)
    {
        String c = ",";
        String s = datasetname+c+k+c;
        
        Collection<Integer> S_HashSet = Graph.toHashSet(S);

        int size = S_HashSet.size();
        int eS = G.InducedEdges_FAST(S_HashSet);
        int D = G.InducedDiameter_FAST(S_HashSet);
        //int D = G.HeuristicInducedDiameter_FAST(S_HashSet);
        double fd = (size==0)?0.0:((double)(2*eS)/(double)size);
        double falpha = optFGreedy;
        double fe = (size<=1)?0.0:((double)eS/((double)(size*(size-1))/2));
        int triangles = G.InducedTriangles_FAST(S_HashSet);
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
    
    public  void printOutput_FAST(int cocktail, int q) throws IOException 
    {
            Collection<Integer> S_HashSet = Graph.toHashSet(S);
            
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


            String suffix = (q==-1)?"":("Top"+q+"_");
            String filename = "MyGreedyAlpha_FAST"+G.alpha+datasetname+"_"+suffix+Math.random();
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

    private void runGreedyOptimize_FAST(double epsilon, Collection<Integer> S_HashSet, Collection<Integer> Sbar_HashSet) 
    {
        //G.setAllActive();
        int inducedEdgesS = 0;
        double scoreS = 0;
        
        
        boolean b1=true;
        boolean b2=true;
        boolean found;
        //int tmpS[] = new int[G.V()+1];
        //for( int i = 1; i< tmpS.length;i++)
                //tmpS[i]=0;
        int counter = 0;
        while(b1 && counter <=G.TMAX)
        {
            while(b2 && counter <=G.TMAX)
            {
                System.out.println("Iteration "+counter);
                counter = counter + 1;
                found = false;
                HashSet<Integer> toBeRemoved = new HashSet<Integer>();
                for (int i:Sbar_HashSet)
                {
                    //debug
                    if(S_HashSet.contains(i))
                    {
                        System.err.println("Error\n"); 
                        System.exit(-1);
                    }
                    
                    double[] tmpEvaluation = G.Score_IncrementalADD(S_HashSet,i,inducedEdgesS);
                    double tmpScore = tmpEvaluation[0];
                    if (tmpScore >= (1+epsilon)*scoreS)
                    {
                        //update S
                        found = true;
                        //System.out.println("Vertex "+(i+1)+" improves things when added from "+scoreS+" to "+tmpScore);
                        S_HashSet.add(i);
                        toBeRemoved.add(i);
                        scoreS = tmpScore;
                        inducedEdgesS = (int)tmpEvaluation[1];
                    }
                }
                
                for(int i:toBeRemoved)
                {
                    Sbar_HashSet.remove(i);
                }
                
                if(!found)
                {
                    b2 = false;
                }                
            }
            //System.out.println("Set found to false now ...");
            found = false;
            HashSet<Integer> tmp = new HashSet<Integer>();
            for(int i:S_HashSet)
            {
                tmp.add(i);
            }
            for(int i:tmp)
            {
                if (donttouch[i] == 0)
                {
                    double[] tmpEvaluation = G.Score_IncrementalREMOVE(S_HashSet,i,inducedEdgesS);
                    double tmpScore = tmpEvaluation[0];
                    if (tmpScore >= (1+epsilon)*scoreS)
                    {
                        found = true;
                        S_HashSet.remove(i);
                        Sbar_HashSet.add(i);
                        scoreS = tmpScore;
                        inducedEdgesS = (int)tmpEvaluation[1];
                    }
                }
            }

            if(!found)
            {
                b1 = false;
            }            
        }
        
        //build solution
        for(int i:S_HashSet)
        {
            S[i] = 1;
            Sbar[i] = 0;
        }
        for(int i:Sbar_HashSet)
        {
            S[i] = 0;
            Sbar[i] = 1;
        }

        double scoreSbar = G.Score_FAST(Sbar_HashSet);
        if(scoreS < scoreSbar)
        {
           int[] tmpS = Sbar;
           Sbar = S;
           S     = tmpS;
           
           double tmp = scoreSbar;
           scoreSbar = scoreS;
           scoreS = tmp;
        }
        optFGreedy = scoreS;
    }


     private void checkOverlap(int[][] output) 
    {
        for (int j=0; j<output[0].length; j++)
        {
            int x = 0;
            for (int i=0; i<output.length; i++)
            {
                x += output[i][j];
            }
            
            if (x > 1)
            {
                throw new RuntimeException("ERROR");
            }
        }
        
    }

    private void printOutputTopK(String[] out) throws IOException
    {
        String filename = "MyGreedyAlpha_FAST"+G.alpha+datasetname+"_TopK_"+Math.random();
        File f = new File(filename);
        f.createNewFile();
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        String c = ",";
        String head = "dataset"+c+"k"+c+"|S|"+c+"e[S]"+c+"D"+c+"f_d"+c+"f_alpha"+c+"f_e"+c+"f_t";
        
        bw.write(head);
        bw.newLine();
        
        for(int i=0; i<out.length; i++)
        {
            bw.write(out[i]);
            bw.newLine();
        }
        
        bw.flush();
        bw.close();
    }
    
    
    
    
     
}
