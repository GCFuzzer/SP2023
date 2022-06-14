package distanceCalculator.ControlFlowGraphGenerator;

import org.objectweb.asm.tree.MethodNode;

/*构建CFG的API，输入一个方法，返回一张CFG图，图中节点为InsnBlock对象；
 * 主要调用ControlFlowGraphAnalyzer分析输入的方法，返回一个ControlFlowGraph对象，
 * 该对象维护一个Graph<InsnBlock, DefaultEdge> */
public class ControlFlowGraphBuilder {

    public static ControlFlowGraph GraphBuilder(MethodNode mn){

        ControlFlowGraphAnalyzer analyzer = new ControlFlowGraphAnalyzer();
        analyzer.analyze(mn);
        return analyzer.getControlFlowGraph();
    }
}
