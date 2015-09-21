
package GraphUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author tsourolampis
 */
public class Graph {
    
       // symbol table: key = string vertex, value = set of neighboring vertices
    public ST<Integer, SET<Integer>> st;

    // number of edges
    private int E;
    
    // number of vertices
    private int V;
   
    
    public boolean[] exist;

    public double TMAX;
    /**
     * parameter for our score
     */
    public double alpha;
    
    
    public void setAllActive()
    {
        for(int i = 1 ; i <= V; i++)
            exist[i] = true;
    }
    
    public void setAlpha(double a)
    {
        alpha = a;
    }
    
    /**
     * Create an empty graph with no vertices or edges.
     */
    public Graph() {
        st = new ST<Integer, SET<Integer>>();
    }

   /**
     * Create an graph from given input stream using given delimiter.
     */
    public Graph(String edgefile, String delimiter) throws IOException {
        st = new ST<Integer, SET<Integer>>();
        BufferedReader br = new BufferedReader(new FileReader(edgefile));
        String line = br.readLine();
        String[] result = line.split(delimiter);
        V = Integer.parseInt(result[0]);
        int ignore = Integer.parseInt(result[1]);
        line = br.readLine();
        while( line!= null )
        {
            result = line.split(delimiter);
            int u = Integer.parseInt(result[0]);
            int v = Integer.parseInt(result[1]);
            addEdge(u,v);
            line = br.readLine();
        }
        makeAllActive();
        if( V!= st.size()){
            System.out.println( " V "+V+" st.size "+st.size());
            System.out.println("G is not connected ");
            //System.exit(0);
        }
        //System.out.println(result[0]+ " V "+V+" st.size "+st.size());
        br.close();
    }
    
    public void makeAllActive()
    {
        
        exist = new boolean[V+1];
        for(int i = 1 ; i <= V; i++)
            exist[i] = true;
       
        
    }
    
    public Graph(Graph myG)
    {
        

            
        /*this.st = myG.st;
        this.E = myG.E;
        this.V = myG.V;
        this.alpha = myG.alpha;
        System.out.println("HA! "+myG.exist.length+" hey"+myG.V());
        System.arraycopy(myG.exist, 0, this.exist, 0, myG.V()+1);
        this.exist = myG.exist;
        /*this.exist = new boolean[myG.V+1];
        for(int i = 0; i< myG.exist.length;i++)
            this.exist[i] = myG.exist[i];*/
    }

   /**
     * Number of vertices.
     */
    public int V() {
        return st.size();
    }
    
    
    public int ActiveV()
    {
        int res=0;
        for( int i=1;i<=V();i++)
            if(exist[i])
                res+=1;
        return res;
    }
    
    public double ActiveE()
    {
       double res = 0.0;
       for( int i = 1; i<= V(); i++)
          if( exist[i])
                res+=degree(i);
       res = res/2; 
       return res;
    }

   /**
     * Number of edges.
     */
    public int E() {
        return E;
    }

   /**
     * Degree of this vertex.
    
    public int degree(int v) {
        if (!st.contains(v)) throw new RuntimeException(v + " is not a vertex");
        else return st.get(v).size();
    } */
    
    
    /**
     * Active Degree of this vertex.
     */
    public int degree(int v) {
        int deg=0;
        if (!st.contains(v)) throw new RuntimeException(v + " is not a vertex");
        else {
            for( int i: st.get(v) )
                if( exist[i])
                    deg+=1;
        }
        return deg;
    }
    
    public int degreeAssumingAllActive(int v) 
    {    
        if (!st.contains(v)) throw new RuntimeException(v + " is not a vertex");
        
        return st.get(v).size();
    }
    
    
    public void Graph2EdgeFile( String filename) throws IOException
    {
        File f = new File("InducedGraph"+filename);
        f.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        for(int u = 1; u< V;u++ )
            for(int v=u+1; v<=V; v++)
                if( hasEdge(u,v))
                    bw.write(u+"\t"+v+"\n");
        bw.close();
    }
    /**
     * This function returns the degree of vertex u in set S, which is indicated
     * by the indicator vector v
     */
    public int InducedDegree(int v[],int u)
    {
        int deg = 0; 
        if(!st.contains(u))  throw new RuntimeException(u + " is not a vertex");
        else{
            for(int i=1;i<=V;i++)
            {
                if(v[i]==1 && hasEdge(u,i))
                    deg+=1;
                    
            }
        }
        return deg;
    }
    
    public int InducedDegree_FAST(Collection<Integer> S_HashSet,int u)
    {
        int deg = 0; 
        if(!st.contains(u))  
        {
            throw new RuntimeException(u + " is not a vertex");
        }
        

        for(int y:adjacentTo(u))
        {
            if(u != y && S_HashSet.contains(y))
            {
                deg++;
            }
        }
        
        return deg;
    }
    
    
    /* This function creates a new graph, whose vertices are labelled 
     from 1 to k, where k is the number of 1s in v*/
    public Graph InducedGraph(int v[])
    {
        Graph myG =  new Graph();
        int howmany = 0;
        for(int i = 1;i<v.length;i++)
        {
            //System.out.print(v[i]+" ");
            if( v[i]==1)
                howmany+=1;
        }
     /*   System.out.println("v[0]"+v[0]);
        System.out.println("howmany="+howmany);*/
        myG.V=howmany; 
        if( howmany == 0)
            return myG;
        
        //rename the vertices 
        int VAL = 1; 
        HashMap hm = new HashMap();
        for(int i = 1;i<v.length;i++)
        {
            if( v[i] == 1 ){
                hm.put(i, VAL);
                VAL+=1;
            }
        }
        
        Iterable<Integer> hey;
        int counter = 1;
        int id;
        for(int i=1;i<=V;i++)
        {
            if( v[i] == 1)
            {                           
                hey = adjacentTo(i);
                Iterator it = hey.iterator();
                while(it.hasNext())
                {
                    id = (Integer)it.next();
                    //System.out.println("id "+id+ " length of v "+v.length);
                    //System.out.println(" i="+i+" hm.get(i)="+(Integer)hm.get(i)+" neighbor of i "+id+" hm.get(id)"+(Integer)hm.get(id) );
                    if( v[id]==1 )
                        myG.addEdge((Integer)hm.get(i), (Integer)hm.get(id));
                }
            }
            
        }
        myG.makeAllActive();
       // System.out.println("myG.V "+myG.V+" active V " + myG.ActiveV());
       /* int v2[] = new int[myG.V+1];
        v2[0]=0;
        for(int i = 1; i<=myG.V;i++ )
            v2[i]=1;
       System.out.println("myG.E "+myG.E+" induced edges "+InducedEdges(v)+" induced from myG "+myG.InducedEdges(v2));
         * */
 
        return myG;
    }
    
    
    
    
    public Graph InducedGraph_FAST(Collection<Integer> S_HashSet)
    {
        Graph myG =  new Graph();
        int howmany = S_HashSet.size();
        
     /*   System.out.println("v[0]"+v[0]);
        System.out.println("howmany="+howmany);*/
        myG.V=howmany; 
        if( howmany == 0)
            return myG;
        
        //rename the vertices 
        int VAL = 1; 
        HashMap hm = new HashMap();
        for(int i:S_HashSet)
        {
            hm.put(i, VAL);
            VAL++;
        }
        
        Iterable<Integer> hey;
        for(int i:S_HashSet)
        {
            for(int id:adjacentTo(i))
            {
                if(S_HashSet.contains(id))
                {
                    myG.addEdge((Integer)hm.get(i), (Integer)hm.get(id));
                }
            }
        }
        myG.makeAllActive();
       // System.out.println("myG.V "+myG.V+" active V " + myG.ActiveV());
       /* int v2[] = new int[myG.V+1];
        v2[0]=0;
        for(int i = 1; i<=myG.V;i++ )
            v2[i]=1;
       System.out.println("myG.E "+myG.E+" induced edges "+InducedEdges(v)+" induced from myG "+myG.InducedEdges(v2));
         * */
 
        return myG;
    }
    
    
    /**
     * Returns the number of edges in the graph induced by vertex set S
     * Vector v is an indicator vector
     * @param v
     * @return 
     */       
    public int InducedEdges(int v[])
    {
        int edges = 0;
        //for(int i = 0; i < v.length; i++)
          //  System.out.print(" "+v[i]);
       // System.out.println("");
        for(int i = 1;i<v.length-1;i++)
        {
            for(int j = i+1; j<= v.length-1;j++)
            {
                
                if(v[i]==1 && v[j]==1)
                {
                  //  System.out.println("Both "+i+" "+j+" are active");
                    if(hasEdge(i,j))
                    {
                      //  System.out.println("edge between "+i+" "+j);
                        edges+=1;
                    }
                }
            }
        }
                
        return edges;
    }
    
    
    public double Conductance(int v[])
    {
        double phi; 
        double deltaS=0.0; 
        for(int i = 1; i<=V();i++)
        {
            if(v[i]==1)
            {
                
                deltaS += st.get(i).size()- InducedDegree(v,i);
                //System.out.println("Vertex "+i+" is in S, deg(i)="+st.get(i).size()+" and degS(i)="+InducedDegree(v,i));
                
            }
            //System.out.println("After treating vertex"+i+" deltaS is "+deltaS);
        }
        //System.out.println("Now deltaS is "+deltaS);
        phi = deltaS/(deltaS+2*InducedEdges(v));
        return phi;
    }
    
    public double Conductance_FAST(Collection<Integer> S_HashSet)
    {
        double phi; 
        double deltaS=0.0;
        double inducedEdges = this.InducedEdges_FAST(S_HashSet);
        
        for(int x:S_HashSet)
        {
            deltaS += st.get(x).size() - InducedDegree_FAST(S_HashSet, x);
        }
        
        phi = deltaS/(deltaS+2*inducedEdges);
        return phi;
    }
    
    /**
     * This function takes as input a set S (the ones of vector v) and returns 
     * the normalized cut of bipartition (S,Sbar).
     * @param v
     * @return ncut 
     */
    public double NCut(int v[])
    {
        double ncut; 
        double deltaS=0.0; 
        for(int i = 1; i<=V();i++)
        {
            if(v[i]==1)
            {
                
                deltaS += st.get(i).size()- InducedDegree(v,i);
       //         System.out.println("Vertex "+i+" is in S, deg(i)="+st.get(i).size()+" and degS(i)="+InducedDegree(v,i));
                
            }
         //   System.out.println("After treating vertex"+i+" deltaS is "+deltaS);
        }
       // System.out.println("Now deltaS is "+deltaS);
        ncut = deltaS/(deltaS+2*InducedEdges(v))+deltaS/(deltaS+2*(E-InducedEdges(v))) ;
        return ncut;
    }
    
    public double NCut_FAST(Collection<Integer> S_HashSet)
    {
        double ncut; 
        double deltaS=0.0; 
        double inducedEdges = InducedEdges_FAST(S_HashSet);
        
        for(int x:S_HashSet)
        {
            for(int y:adjacentTo(x))
            {
                if (y != x && !S_HashSet.contains(y))
                {
                    deltaS++;
                }
            }
        }
        ncut = deltaS/(deltaS+2*inducedEdges)+deltaS/(deltaS+2*(E-inducedEdges)) ;
        return ncut;
    }
    
    /**
     * This function takes as input a set S (the ones of vector v) and returns 
     * the expansion (edges across cut/|S|) of bipartition (S,Sbar).
     * @param v
     * @return expansion 
     */
    public double Expansion(int v[])
    {
        double expansion; 
        double deltaS=0.0; 
        double sizeofS=0;
        for(int i = 1; i<=V();i++)
            if(v[i]==1)                
            {
                deltaS += st.get(i).size()- InducedDegree(v,i);        
                sizeofS+=1;
            }        
        //Graph myG = InducedGraph(v);
        expansion = deltaS/sizeofS; 
        return expansion;
    }
    
    public double Expansion_FAST(Collection<Integer> S_HashSet)
    {
        double expansion; 
        double deltaS=0.0; 
        double sizeofS=S_HashSet.size();
        
        for(int x:S_HashSet)
        {
            deltaS += st.get(x).size()- InducedDegree_FAST(S_HashSet,x);
        }      
        expansion = deltaS/sizeofS; 
        return expansion;
    }
    
    /*
    private void print(String s)
    {
        System.out.println(s);
    }*/
    
    
    /**
     * This function takes as input a set S (the ones of vector v) and returns 
     * the cut ratio (edges across cut/|S|*(n-|S|)) of bipartition (S,Sbar).
     * @param v
     * @return cutratio 
     */
    public double CutRatio(int v[])
    {
        double cutratio; 
        double deltaS=0.0; 
        double sizeofS=0;
        for(int i = 1; i<=v.length-1;i++)
        {
            if(v[i]==1)                
            {
                deltaS += st.get(i).size()- InducedDegree(v,i);        
                sizeofS+=1;
                //System.out.println("Vertex "+i);
            }        
        }
        //System.out.println("deltaS "+deltaS+" sizeofS "+sizeofS+" n-sizeofS"+(v.length-1-sizeofS));
        cutratio = deltaS/(sizeofS*(V()-sizeofS)); 
        return cutratio;
    }
    
    public double CutRatio_FAST(Collection<Integer> S_HashSet)
    {
        double cutratio; 
        double deltaS=0.0; 
        double sizeofS=S_HashSet.size();
        for(int x:S_HashSet)
        {
            deltaS += st.get(x).size()- InducedDegree_FAST(S_HashSet, x); 
        }
        
        //System.out.println("deltaS "+deltaS+" sizeofS "+sizeofS+" n-sizeofS"+(v.length-1-sizeofS));
        cutratio = deltaS/(sizeofS*(V()-sizeofS)); 
        return cutratio;
    }
    
    
        /**
     * This function takes as input a set S (the ones of vector v) and returns 
     * the edges across cut between the bipartition (S,Sbar).
     * @param v
     * @return deltaS
     */
    public int EdgesAcrossCut(int v[])
    {
        int deltaS=0;
        for(int i = 1; i<=V();i++)
            if(v[i]==1)                
                deltaS += st.get(i).size()- InducedDegree(v,i);                
        return deltaS;
    } 
    
    public int EdgesAcrossCut_FAST(Collection<Integer> S_HashSet)
    {
        int deltaS=0;
        for(int x:S_HashSet)
        {
            deltaS += st.get(x).size()- InducedDegree_FAST(S_HashSet,x);
        }              
        return deltaS;
    } 
    
    
    public int InducedDiameter(int v[])
    {
        int diam = -1; 
        Graph myG = InducedGraph(v);
        
        for(int i=1;i<=myG.V();i++)
        {
            PathFinder finder = new PathFinder(myG, i);
            for(int j=1;j<=myG.V();j++)
            {
                 if (finder.isReachable(j) && finder.distanceTo(j) > diam) {
                     diam = finder.distanceTo(j);
                 }

            }
        }
        return diam;
    }
    
    public int InducedDiameter_FAST(Collection<Integer> S_HashSet)
    {
        int diam = -1; 
        Graph myG = InducedGraph_FAST(S_HashSet);
        
        for(int i=1;i<=myG.V();i++)
        {
            PathFinder finder = new PathFinder(myG, i);
            for(int j:finder.getAllReachable())
            {
                 if (finder.distanceTo(j) > diam) 
                 {
                     diam = finder.distanceTo(j);
                 }
            }
        }
        return diam;
    }

    public int HeuristicInducedDiameter(int v[])
    {
        int diam = -1;
        int argj=-1;
        Graph myG = InducedGraph(v);
        PathFinder finder = new PathFinder(myG, 1);
        for(int j=1;j<=myG.V();j++)
        {
            if (finder.isReachable(j) && finder.distanceTo(j) > diam) {
                     diam = finder.distanceTo(j);
                     argj = j;
                 }

        }
        if( argj == -1)
        {
            System.err.println("Oops!");
            System.exit(-1);
        }
        finder = new PathFinder(myG, argj);
        for(int j=1;j<=myG.V();j++)
        {
            if (finder.isReachable(j) && finder.distanceTo(j) > diam) {
                     diam = finder.distanceTo(j);
                     argj = j;
                 }

        }
        finder = new PathFinder(myG, argj);
        for(int j=1;j<=myG.V();j++)
        {
            if (finder.isReachable(j) && finder.distanceTo(j) > diam) {
                     diam = finder.distanceTo(j);
                     argj = j;
                 }

        }
        finder = new PathFinder(myG, argj);
        for(int j=1;j<=myG.V();j++)
        {
            if (finder.isReachable(j) && finder.distanceTo(j) > diam) {
                     diam = finder.distanceTo(j);
                     argj = j;
                 }

        }
        return diam;
    }
    
    public int HeuristicInducedDiameter_FAST(Collection<Integer> S_HashSet)
    {

        Graph myG = InducedGraph_FAST(S_HashSet);
        
        
        int iterations = 5;
        
        int argj = 1;
        int diam = -1;
        for (int k=0; k<iterations; k++)
        {
            int diamTmp = -1;
            PathFinder finder = new PathFinder(myG, argj);
            for(int j:finder.getAllReachable())
            {
                if(finder.distanceTo(j) > diamTmp)
                {
                    diamTmp = finder.distanceTo(j);
                    argj = j;
                }
            }
            if(diamTmp > diam)
            {
                diam = diamTmp;
            }
        }

        return diam;
    }

    public int InducedTriangles(int v[])
    {
        int triangles = 0;
        for(int i = 1; i<v.length-2;i++)
        {
            for(int j = i+1; j< v.length-1;j++)
            {
                for(int k=j+1;k<v.length;k++)
                {
                     if(v[i]==1 && v[j]==1 && v[k]==1)
                     {
                            if(hasEdge(i,j) && hasEdge(i,k) && hasEdge(j,k))
                            {
                                triangles+=1;
                            }
                     }
                }
            }
        }
                
        return triangles;
    }
    
    public int InducedTriangles_FAST(Collection<Integer> S_HashSet)
    {
        int triangles = 0;
        
        for(int x:S_HashSet)
        {
            SET<Integer> neighbors = this.st.get(x);
            for(int y:neighbors)
            {
                if (S_HashSet.contains(y))
                {
                    SET<Integer> neighborsY = this.st.get(y);
                    for (int z:neighborsY)
                    {
                        if (z != x && S_HashSet.contains(z) && neighbors.contains(z))
                        {
                            triangles++;
                        }
                    }
                }
            }
        }
        
        return triangles/6;
    }
    
    
    public double Score(int v[])
    {
        int howmany = 0;
        for(int i=1; i<v.length;i++)
            if( v[i]==1)
                howmany +=1;
        return (double)alpha*InducedEdges(v)-(double)howmany*(double)(howmany-1)/2;
    }
   /**
     * Add edge v-w to this graph (if it is not already an edge)
     */
    public void addEdge(int v, int w) {
        if (!hasEdge(v, w)) E++;
        if (!hasVertex(v)) addVertex(v);
        if (!hasVertex(w)) addVertex(w);
        st.get(v).add(w);
        st.get(w).add(v);
    }

   /**
     * Add vertex v to this graph (if it is not already a vertex)
     */
    public void addVertex(int v) {
        if (!hasVertex(v)) st.put(v, new SET<Integer>());
    }


   /**
     * Return the set of vertices as an Iterable.
     */
    public Iterable<Integer> vertices() {
        return st;
    }

   /**
     * Return the set of neighbors of vertex v as in Iterable.
     */
    public Iterable<Integer> adjacentTo(int v) {
        // return empty set if vertex isn't in graph
        if (!hasVertex(v)) return new SET<Integer>();
        else               return st.get(v);
    }

   /**
     * Is v a vertex in this graph?
     */
    public boolean hasVertex(int v) {
        return st.contains(v);
    }

   /**
     * Is v-w an edge in this graph?
     */
    public boolean hasEdge(int v, int w) {
        if (!hasVertex(v)) return false;
        return st.get(v).contains(w);
    }
    
    
    public void removeVertex(int v)
    {
       if (!hasVertex(v)) return;
       int deg = degree(v);
       for( int i: st.get(v) )
           st.get(i).delete(v);
       //st.delete(v);
       exist[v] = false;
    }

    public double[] Score_IncrementalADD(Collection<Integer> S_HashSet, int i, int inducedEdgesS) 
    {
        SET<Integer> neighbors = this.st.get(i);
        int delta = 0;
        
        if(neighbors != null)
        {    
            for(int v:neighbors)
            {
                if(S_HashSet.contains(v))
                {
                    delta++;
                }
            }
        }
        
        int newInducedEdgesS = inducedEdgesS+delta;
        int size = S_HashSet.size()+1;
        double newScore = (double)alpha*newInducedEdgesS-(double)size*(double)(size-1)/2;
        
        return new double[]{newScore,newInducedEdgesS};
    }
    
    public double[] Score_IncrementalREMOVE(Collection<Integer> S_HashSet, int i, int inducedEdgesS) 
    {
        SET<Integer> neighbors = this.st.get(i);
        
        int delta = 0;
        if(neighbors != null)
        {
            for(int v:neighbors)
            {
                if(S_HashSet.contains(v))
                {
                    delta++;
                }
            }
        }
        
        int newInducedEdgesS = inducedEdgesS-delta;
        int size = S_HashSet.size()-1;
        double newScore = (double)alpha*newInducedEdgesS-(double)size*(double)(size-1)/2;
        
        return new double[]{newScore,newInducedEdgesS};
    }
    
    public double Score_FAST(Collection<Integer> S_HashSet) 
    {
        int inducedEdges = InducedEdges_FAST(S_HashSet);
        
        int size = S_HashSet.size();
        double score = (double)alpha*inducedEdges-(double)size*(double)(size-1)/2;
        //System.out.println("Size of S "+S_HashSet.size()+" induced edges "+inducedEdges+" score "+score);
        return score;
    }
    
    public int InducedEdges_FAST(Collection<Integer> S_HashSet)
    {
        int inducedEdges = 0;
        for(int i:S_HashSet)
        {
            SET<Integer> neighbors = this.st.get(i);
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
    
    public int InducedEdges_FAST(int[] S)
    {
        return InducedEdges_FAST(Graph.toHashSet(S));
    }
    
    public static Collection<Integer> toHashSet(int[] S) 
    {
        Collection<Integer> v = new HashSet<Integer>();
        for(int i=1; i<S.length; i++)
        {
            if (S[i]==1)
            {
                v.add(i);
            }
        }
        return v;
    }
}
