package distanceCalculator.ControlFlowGraphGenerator;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/*存放控制流图的类，通过getCfg方法返回一个可供操作的图（jgrapht.graph.DefaultDirectedGraph类对象）*/
public class ControlFlowGraph {

    // 存放控制流图对象
    private final Graph<InsnBlock, DefaultEdge> cfg;

    // 控制流图是有向图
    public ControlFlowGraph() {
        this.cfg = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    public Graph<InsnBlock, DefaultEdge> getCfg() {
        return this.cfg;
    }


}
