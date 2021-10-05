import java.util.*;
import java.io.*;

class Graph {

    public Graph()
    {
        edges = new HashMap<>();

        try {
            FileReader file_in = new FileReader("src/myfiles/grafo01.txt");
            BufferedReader reader = new BufferedReader(file_in);

            String thisLine;
            while ((thisLine = reader.readLine()) != null) {
                thisLine = thisLine.replaceAll("\\s+", " ");
                String input_line_split[] = thisLine.split(" ");

                int v1 = Integer.parseInt(input_line_split[0]);
                for (int i = 2; i < input_line_split.length; i++) {
                    int v2 = Integer.parseInt(input_line_split[i]);

                    this.addEdge(v1, v2,true);
                }
            }
        }
        catch(FileNotFoundException | NumberFormatException e)
        {
            e.printStackTrace();
            System.out.println("Reading error");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // We use Hashmap to store the edges in the graph
    private Map<Integer, List<Integer> > edges;
    private List<Map<Integer, List<Integer>>> powerSet = new LinkedList<>();

    // This function adds a new vertex to the graph
    public void addVertex(Integer s)
    {
        edges.put(s, new LinkedList<Integer>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(Integer source,
                        Integer destination,
                        boolean bidirectional)
    {

        if (!edges.containsKey(source))
            addVertex(source);

        if (!edges.containsKey(destination))
            addVertex(destination);

        if(!edges.get(source).contains(destination))
        {

            edges.get(source).add(destination);
            if (bidirectional == true && !edges.get(destination).contains(source))
            {
                edges.get(destination).add(source);
            }
        }
    }


    public void combinationUtil(int arr[], int data[], int start,
                                int end, int index, int r)
    {
        // Current combination is ready to be printed, print it
        if (index == r)
        {
            Map<Integer, List<Integer>> temp = new HashMap<>();
            for (int j=0; j<r; j++)
            {
                List<Integer> tempList = new LinkedList<>(edges.get(data[j]));
                temp.put(data[j],tempList);
            }

            this.powerSet.add(temp);
            return;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    public void printCombination(int r)
    {
        // A temporary array to store all combination one by one
        int data[]=new int[r];
        Integer temp[];
        temp = edges.keySet().toArray(new Integer[0]);
        int arr[] = new int[temp.length];

        for(int i = 0; i < temp.length; i++)
        {
            arr[i] = temp[i].intValue();
        }

        // Print all combination using temporary array 'data[]'
        combinationUtil(arr, data, 0, temp.length-1, 0, r);
    }


    // A function used by DFS
    private void DFSUtil(int v,Set<Integer> visited, Map<Integer, List<Integer>> g)
    {
        // Mark the current node as visited and print it
        visited.add(v);

        // Recur for all the vertices adjacent to this
        // vertex
        if(g.get(v) == null) return;
        for (int neighbour : g.get(v))
        {
            if (!visited.contains(neighbour))
                DFSUtil(neighbour, visited, g);
        }

    }


    private Set<Integer> DFS(Map<Integer, List<Integer>> g)
    {
        // Mark all the vertices as
        // not visited(set as
        // false by default in java)
        int v = g.keySet().iterator().next();
        Set<Integer> visited = new HashSet<>();

        // Call the recursive helper
        // function to print DFS
        // traversal
        DFSUtil(v,visited, g);

        return visited;
    }

    private boolean isConnected(Map<Integer, List<Integer>> g)
    {
        var visited = DFS(g);
        for(int key : g.keySet())
        {
            if(!visited.contains(key)) return false;
        }
        return true;
    }

    public boolean checkC4(Map<Integer, List<Integer>> graph)
    {
        //all vertex degree 2
        for(int key : graph.keySet())
        {
            if(graph.get(key).size() != 2) return false;
        }
        //connected

        if(!isConnected(graph)) return false;

        return true;
    }


    private void treatPowerSet()
    {
        for(var graph : this.powerSet)
        {
            for(var vertex : edges.keySet())
            {
                if(graph.containsKey(vertex))
                {
                    for(var neigh : edges.get(vertex)){
                        if(!graph.containsKey(neigh))
                        {
                            graph.get(vertex).remove(neigh);
                        }
                    }

                }
            }
        }
    }


    private boolean DFSUtilP4(int v,int p,Set<Integer> visited, Map<Integer, List<Integer>> g)
    {

        // Mark the current node as visited and print it
        visited.add(v);

        // Recur for all the vertices adjacent to this
        // vertex
        if(g.get(v) == null) return false;
        for (int neighbour : g.get(v))
        {
            if(neighbour == p) continue;
            if (!visited.contains(neighbour))
                return DFSUtilP4(neighbour,v, visited, g);
            else return false;
        }
        return true;

    }


    private boolean DFSP4(Map<Integer, List<Integer>> g)
    {
        // Mark all the vertices as
        // not visited(set as
        // false by default in java)
        int v = g.keySet().iterator().next();
        Set<Integer> visited = new HashSet<>();

        // Call the recursive helper
        // function to print DFS
        // traversal
        return DFSUtilP4(v,-1,visited, g);
    }


    private boolean isTree(Map<Integer, List<Integer>> g)
    {
        return DFSP4(g);
    }




    public boolean checkP4(Map<Integer, List<Integer>> graph)
    {
        for(int key : graph.keySet())
        {
            if(graph.get(key).size() > 2) return false;
        }
        //connected

        if(!isConnected(graph)) return false;

        if(!isTree(graph)) return false;


        return true;
    }

    public boolean isTP()
    {
        printCombination(4);
        treatPowerSet();
        for(var graph : this.powerSet)
        {
            if(checkC4(graph))
            {
                System.out.println("Not Trivially perfect, has a C4.");
                return false;

            }
            if(checkP4(graph))
            {
                System.out.println("Not Trivially perfect, has a P4.");
                return false;
            }
        }
        System.out.println("Is Trivially Perfect.");
        return true;
    }

}