package distanceCalculator.CallGraphLoader;

import distanceCalculator.ControlFlowGraphGenerator.ControlFlowGraph;
import distanceCalculator.ControlFlowGraphGenerator.ControlFlowGraphBuilder;
import distanceCalculator.ControlFlowGraphGenerator.InsnBlock;
import janala.instrument.GlobalStateForInstrumentation;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class LoadCallGraphFromChain {
/*    public static LoadCallGraphFromChain graph = new LoadCallGraphFromChain();*/
    public String sink;
    private final double c = 5.0;
    private final double infiniteNumber = Double.MAX_VALUE;
    public Map<MethodNode, String> classNodeMap = new HashMap<>();
    public Map<String, Double> methodDistance = new HashMap<>();
    public Map<InsnBlock, String> blockMethodMap  = new HashMap<>();

    public Map<MethodNode, String> getClassNodeMap() {
        return classNodeMap;
    }

    public int getSize() {
        return blockMethodMap.size();
    }

    public Map<InsnBlock, String> getBlockMethodMap() {
        return blockMethodMap;
    }

    public String getSink() {
        return sink;
    }

    public Map<String, Double> getMethodDistance() {
        return methodDistance;
    }

    public void callGraphLoader(String chain){
        // <com.google.common.collect.Maps$FilteredKeyMap: boolean containsKey(java.lang.Object)>--> 按箭头划分
        String[] MethodList = chain.split("-->");
        //Map<MethodNode, String> classNodeMap = new HashMap<>();
        int chainLength = MethodList.length;
        int count = 0;
        for (int i=0; i<chainLength;i++) {
            String[] split = MethodList[i].substring(1,MethodList[i].length()-1).split(" ");
            String className = split[0].replace(":","");
            String methodName = split[2].split("\\(")[0];
            if (i==(chainLength-1)) {
                this.sink = className.split("\\.")[className.split("\\.").length-1] + "." + methodName;
                break;
            }
            ClassNode cn = getClassNodeByClassName(className+".class");
            MethodNode targetMethod = getMethod(cn,methodName);
            String invokedName = cn.name.split("/")[cn.name.split("/").length-1] + "."+targetMethod.name;
            double distance = MethodList.length-count-1;
            this.classNodeMap.put(targetMethod, invokedName);
            this.methodDistance.put(invokedName, distance);
            count++;
        }
    }

    // 根据className新建具体的classNode
    public ClassNode getClassNodeByClassName(String path){
        String filepath = FileUtils.getFilePath(path);
        byte[] bytes = FileUtils.readBytes(filepath);

        //构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //生成ClassNode
        ClassNode cn = new ClassNode();
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cn, parsingOptions);
        return cn;
    }

    public MethodNode getMethod(ClassNode cn, String name){
        for ( MethodNode mn: cn.methods) {
            if (name.equals(mn.name)){
                return mn;
            }
        }
        return null;
    }

    public void setBlockMethodMap() {
        String methodName;

        for (MethodNode mn: this.classNodeMap.keySet()) {
            List<Integer> targetUnreachableID = new ArrayList<>();
            List<Integer> targetReachableID = new ArrayList<>();
            methodName = classNodeMap.get(mn);
            ControlFlowGraph cfg = ControlFlowGraphBuilder.GraphBuilder(mn);
            InsnBlock[] InsnBlock = cfg.getCfg().vertexSet().toArray(new InsnBlock[0]);
            DijkstraShortestPath<InsnBlock, DefaultEdge> dijkstraAlg =
                    new DijkstraShortestPath<>(cfg.getCfg());
/*
            DOTExporter<InsnBlock, DefaultEdge> exporter =
                    new DOTExporter<>(v -> String.valueOf(v.getHead().hashCode()));
            exporter.setVertexAttributeProvider((v) -> {
                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(v.lines.toString()));
                return map;
            });
            Writer writer = new StringWriter();
            exporter.exportGraph(cfg.getCfg(), writer);
            exporter.exportGraph(cfg.getCfg(), new FileWriter(mn.name + ".dot"));*/

/*            if (methodName.equals(this.sink)) {
                for (int idx = 0; idx < cfg.getCfg().vertexSet().size(); idx++) {
                    InsnBlock[idx].setDistance(0.0);
                    this.blockMethodMap.put(InsnBlock[idx], methodName);
                }
                continue;
            }*/

            for (int i = 0; i < cfg.getCfg().vertexSet().size(); i++) {
                for (int j = 0; j < InsnBlock[i].lines.size(); j++) {
                    if (InsnBlock[i].lines.get(j).startsWith("invoke") && InsnBlock[i].lines.get(j).endsWith(this.sink)) {
                        InsnBlock[i].setDistance(0.0);
                        this.blockMethodMap.put(InsnBlock[i], methodName);
                        targetReachableID.add(i);
                        break;
                    }
                    if (InsnBlock[i].lines.get(j).startsWith("invoke") &&
                            methodDistance.containsKey(InsnBlock[i].lines.get(j).split(" ")[1])) {
                        InsnBlock[i].setDistance(c * methodDistance.get(InsnBlock[i].lines.get(j).split(" ")[1]));
                        this.blockMethodMap.put(InsnBlock[i], methodName);
                        targetReachableID.add(i);
                        break;
                    }
                    if (j == InsnBlock[i].lines.size() - 1) {
                        InsnBlock[i].setDistance(infiniteNumber);
                        this.blockMethodMap.put(InsnBlock[i], methodName);
                        targetUnreachableID.add(i);
                    }
                }
            }

            for (Integer tUnreachable : targetUnreachableID) {
                ShortestPathAlgorithm.SingleSourcePaths<InsnBlock, DefaultEdge> iPaths = dijkstraAlg.getPaths(InsnBlock[tUnreachable]);
                double b2t_distance = infiniteNumber;
                int flag = 0;
                for (Integer tReachable : targetReachableID) {
                    if (flag == 0 && iPaths.getPath(InsnBlock[tReachable]) != null) {
                        b2t_distance = 1.0 / (iPaths.getPath(InsnBlock[tReachable]).getLength() + InsnBlock[tReachable].getDistance());
                        flag++;
                        continue;
                    }
                    if (iPaths.getPath(InsnBlock[tReachable]) != null) {
                        b2t_distance = b2t_distance + 1.0 / (iPaths.getPath(InsnBlock[tReachable]).getLength() + InsnBlock[tReachable].getDistance());
                    }
                }
                if (b2t_distance!=infiniteNumber) {
                    InsnBlock[tUnreachable].setDistance(1.0/b2t_distance);
                }
            }
        }
    }

    public void clear() {
        this.sink = null;
        this.blockMethodMap.clear();
        this.classNodeMap.clear();
        this.methodDistance.clear();
    }
}
