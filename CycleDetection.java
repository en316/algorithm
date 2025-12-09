import java.util.*;

public class CycleDetection {
    static class Node {
        String nodeId;
        String refNodeId;
        
        public Node(String nodeId, String refNodeId) {
            this.nodeId = nodeId;
            this.refNodeId = refNodeId;
        }
    }
    
    /**
     * Detects if there are cycles in the node reference graph
     * @param nodes List of node relationships
     * @return true if cycle detected, false otherwise
     */
    public static boolean hasCycle(List<Node> nodes) {
        // Build adjacency list representation
        Map<String, Set<String>> graph = buildGraph(nodes);
        
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String nodeId : graph.keySet()) {
            if (!visited.contains(nodeId)) {
                if (detectCycleUtil(nodeId, graph, visited, recursionStack)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Helper method for cycle detection using DFS
     */
    private static boolean detectCycleUtil(String nodeId, Map<String, Set<String>> graph, 
                                          Set<String> visited, Set<String> recursionStack) {
        visited.add(nodeId);
        recursionStack.add(nodeId);
        
        Set<String> neighbors = graph.get(nodeId);
        if (neighbors != null) {
            for (String refNodeId : neighbors) {
                // If the referenced node is in recursion stack, we found a cycle
                if (recursionStack.contains(refNodeId)) {
                    return true;
                }
                
                // If we haven't visited the referenced node and it leads to a cycle
                if (!visited.contains(refNodeId) && detectCycleUtil(refNodeId, graph, visited, recursionStack)) {
                    return true;
                }
            }
        }
        
        recursionStack.remove(nodeId);
        return false;
    }
    
    /**
     * Finds all cycles in the node reference graph
     * @param nodes List of node relationships
     * @return List of cycles, each cycle represented as a list of node IDs
     */
    public static List<List<String>> findCycles(List<Node> nodes) {
        // Build adjacency list representation
        Map<String, Set<String>> graph = buildGraph(nodes);
        
        List<List<String>> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        // First pass to detect back edges
        for (String nodeId : graph.keySet()) {
            if (!visited.contains(nodeId)) {
                findCyclesUtil(nodeId, graph, visited, recursionStack, 
                              new ArrayList<>(), cycles);
            }
        }
        
        return cycles;
    }
    
    /**
     * Helper method for finding all cycles using DFS
     */
    private static void findCyclesUtil(String nodeId, Map<String, Set<String>> graph, 
                                     Set<String> visited, Set<String> recursionStack,
                                     List<String> path, List<List<String>> cycles) {
        visited.add(nodeId);
        recursionStack.add(nodeId);
        path.add(nodeId);
        
        Set<String> neighbors = graph.get(nodeId);
        if (neighbors != null) {
            for (String refNodeId : neighbors) {
                // If the referenced node is in recursion stack, we found a cycle
                if (recursionStack.contains(refNodeId)) {
                    // Found a cycle, extract it
                    List<String> cycle = new ArrayList<>();
                    int startIndex = path.indexOf(refNodeId);
                    for (int i = startIndex; i < path.size(); i++) {
                        cycle.add(path.get(i));
                    }
                    cycle.add(refNodeId); // Close the cycle
                    cycles.add(cycle);
                } 
                // If we haven't visited the referenced node
                else if (!visited.contains(refNodeId)) {
                    findCyclesUtil(refNodeId, graph, visited, recursionStack, 
                                  path, cycles);
                }
            }
        }
        
        recursionStack.remove(nodeId);
        path.remove(path.size() - 1);
    }
    
    /**
     * Builds an adjacency list representation from the list of node relationships
     */
    private static Map<String, Set<String>> buildGraph(List<Node> nodes) {
        Map<String, Set<String>> graph = new HashMap<>();
        
        for (Node node : nodes) {
            graph.computeIfAbsent(node.nodeId, k -> new HashSet<>()).add(node.refNodeId);
        }
        
        return graph;
    }
    
    // Test example
    public static void main(String[] args) {
        // Create sample data - each Node represents a relationship
        List<Node> nodes = new ArrayList<>();
        
        // Add relationships (creating a cycle A -> B -> C -> A)
        nodes.add(new Node("A", "B"));
        nodes.add(new Node("B", "C"));
        nodes.add(new Node("C", "A")); // Creates cycle
        nodes.add(new Node("D", "B"));
        
        // Check for cycles
        System.out.println("Has cycle: " + hasCycle(nodes));
        
        // Find all cycles
        List<List<String>> cycles = findCycles(nodes);
        System.out.println("Number of cycles found: " + cycles.size());
        for (int i = 0; i < cycles.size(); i++) {
            System.out.println("Cycle " + (i+1) + ": " + cycles.get(i));
        }
    }
}