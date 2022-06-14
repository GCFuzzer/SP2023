package distanceCalculator.CallGraphLoader;

import distanceCalculator.ControlFlowGraphGenerator.InsnBlock;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Arrays;

public class GraphTest {
    public static void main(String[] args) throws IOException {
        LoadCallGraphFromChain graph = new LoadCallGraphFromChain();
        String targetChain = "<java.util.PriorityQueue: void readObject(java.io.ObjectInputStream)>--><java.util.PriorityQueue: void heapify()>--><java.util.PriorityQueue: void siftDown(int,java.lang.Object)>--><java.util.PriorityQueue: void siftDownUsingComparator(int,java.lang.Object)>--><org.apache.commons.collections4.comparators.TransformingComparator: int compare(java.lang.Object,java.lang.Object)>--><org.apache.commons.collections4.functors.InvokerTransformer: java.lang.Object transform(java.lang.Object)>--><java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>";
        graph.callGraphLoader(targetChain);
        graph.setBlockMethodMap();
        System.out.println(graph.getClassNodeMap());
        System.out.println(graph.getSize());
        System.out.println(graph.getSink());
        System.out.println(graph.getMethodDistance());
        System.out.println(graph.getBlockMethodMap());
        int i =0;
        for (InsnBlock ib: graph.getBlockMethodMap().keySet()) {
            System.out.println(ib.getDistance());
            if (ib.getDistance()==3.0) {
                System.out.println(ib.lines);
            }
            i++;
        }
        System.out.println(i);
/*        String[] MethodList = targetChain.split("-->");
        int chainLength = MethodList.length;
        String sink;
        for (int i=0; i<chainLength;i++) {
            String[] split = MethodList[i].substring(1,MethodList[i].length()-1).split(" ");
            String className = split[0].replace(":","");
            String methodName = split[2].split("\\(")[0];
            if (i==(chainLength-1)) {
                sink = className.split("\\.")[className.split("\\.").length-1] + "." + methodName;
                System.out.println(sink);
                break;
            }
        }*/
    }
}
