package distanceCalculator.ControlFlowGraphGenerator;

import java.util.*;
import org.objectweb.asm.tree.*;
import static org.objectweb.asm.Opcodes.*;

/*分析输入的方法构建控制流图；
 * 主要流程包括三步：
 * 1.(findJumpLabel)分析方法中指令集，找出其中涉及到跳转的指令并记录；
 * 2.(instruction2Block)迭代将指令集分为基本块；
 * 3.(linkBlocks)使用控制流边连接基本块；
 */
public class ControlFlowGraphAnalyzer {
    private final Set<LabelNode> jumpLabelSet = new HashSet<>();
    private final Map<LabelNode, Set<InsnBlock>> jumpLabelMap = new HashMap<>();
    // 记录在block和label关系
    private final Map<LabelNode, InsnBlock> labelInBlockMap = new HashMap<>();
    /* 冗余，仅用来测试生成的指令，将指令处理为Text，可以删除*/
    private final InsnText insnText = new InsnText();
    private final ControlFlowGraph controlFlowGraph = new ControlFlowGraph();

    private InsnBlock currentBlock;
    private final List<AbstractInsnNode> nodeList = new ArrayList<>();

    public ControlFlowGraphAnalyzer() {
        this.currentBlock = new InsnBlock();
    }

    public void analyze(MethodNode mn) {
        findJumpLabel(mn);
        instruction2Block(mn);
        linkBlocks();
    }

    /* 这一步主要根据指令(Node)的类型找出跳转关系，
     * 主要针对四类：顺序执行的指令、选择跳转的指令(JumpInsnNode)、多重选择跳转的指令(LookupSwitchInsnNode或TableSwitchInsnNode)
     * 和方法执行结束指令(ATHROW或RETURN)；
     * 具体地，构建Basic Block需要找到块的leader，（1）P中第一条指令必为leader （2）jump的目标必为leader（3）紧接着jump的后面一条指令为leader
     * 在该方法中，我们找到所有的跳转语句以后续确定leader。 */
    private void findJumpLabel(MethodNode mn) {
        InsnList instructions = mn.instructions;

        for (AbstractInsnNode node : instructions) {
            if (node instanceof JumpInsnNode) {
                // 当前block与跳转目标的关系
                JumpInsnNode currentNode = (JumpInsnNode) node;
                jumpLabelSet.add(currentNode.label);
            }
            else if (node instanceof TableSwitchInsnNode) {
                // 当前block与跳转目标的关系，switch结构有多个跳转目标
                TableSwitchInsnNode currentNode = (TableSwitchInsnNode) node;
                // dflt代表default
                jumpLabelSet.add(currentNode.dflt);
                jumpLabelSet.addAll(currentNode.labels);
            }
            else if (node instanceof LookupSwitchInsnNode) {
                // 当前block与跳转目标的关系
                LookupSwitchInsnNode currentNode = (LookupSwitchInsnNode) node;
                jumpLabelSet.add(currentNode.dflt);
                jumpLabelSet.addAll(currentNode.labels);
            }
        }

        List<TryCatchBlockNode> tryCatchBlocks = mn.tryCatchBlocks;
        for (TryCatchBlockNode node : tryCatchBlocks) {
            jumpLabelSet.add(node.handler);
        }
    }
    /* 迭代方法指令集，将指令划按基本块划分，同时对于顺序执行关系连接block块，添加控制流边；
     * 具体地，遍历指令集，从第一条指令开始，遍历发现到第一条跳转语句或存放于jumpLabelSet的跳转目标语句为止，经过的语句构成一个block；
     * 一旦构建了一个block，调用completeBlock方法收集当前block的信息，并分情况讨论：
     * 1. 因为跳转语句终止，调用addJumpFromBlockToLabel方法将跳转关系转到当前block上并新建nextBlock代表下一个基本块，
     * 如果跳转语句为选择跳转JumpInsnNode，那么使用控制流边将之与和当前block连接；
     * 如果跳转语句为多重选择跳转LookupSwitchInsnNode或TableSwitchInsnNode，那么不需要将控制流边与新建的nextBlock相连；
     * 将分析对象由当前块currentBlock指向nextBlock；
     * 2. 因为当前语句为跳转语句指向而终止，那么不需要把当前语句加入当前block块，其余操作类似，需要注意的是，需要判断当前指令是不是方法中的第一条指令，
     * 免得新建了额外的空block块。
     * */
    private void instruction2Block(MethodNode mn) {
        InsnList instructions = mn.instructions;

        for (AbstractInsnNode node : instructions) {
            int opcode = node.getOpcode();

            if (node instanceof JumpInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                JumpInsnNode currentNode = (JumpInsnNode) node;
                addJumpFromBlockToLabel(currentNode.label);

                // 当前block与下一个block的关系
                InsnBlock nextBlock = new InsnBlock();
                if ((opcode >= IFEQ && opcode <= IF_ACMPNE) || (opcode >= IFNULL && opcode <= IFNONNULL)) {
                    //连接控制流边
                    controlFlowGraph.getCfg().addVertex(nextBlock);
                    controlFlowGraph.getCfg().addEdge(currentBlock,nextBlock);
                    currentBlock.addNextBlocks(nextBlock);
                }

                // 下一个block成为当前block
                currentBlock = nextBlock;
            }
            else if (node instanceof TableSwitchInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                TableSwitchInsnNode currentNode = (TableSwitchInsnNode) node;
                int min = currentNode.min;
                int max = currentNode.max;
                // 加入所有分支
                for (int i = min; i <= max; i++) {
                    addJumpFromBlockToLabel(currentNode.labels.get(i - min));
                }
                addJumpFromBlockToLabel(currentNode.dflt);

                // 下一个block成为当前block
                currentBlock = new InsnBlock();
            }
            else if (node instanceof LookupSwitchInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                LookupSwitchInsnNode currentNode = (LookupSwitchInsnNode) node;
                List<LabelNode> labels = currentNode.labels;
                for (LabelNode labelNode : labels) {
                    addJumpFromBlockToLabel(labelNode);
                }
                addJumpFromBlockToLabel(currentNode.dflt);

                // 下一个block成为当前block
                currentBlock = new InsnBlock();
            }
            else if (node instanceof LabelNode) {
                LabelNode currentNode = (LabelNode) node;
                // 判断该指令是否为跳转语句的指向
                if (jumpLabelSet.contains(currentNode)) {
                    // 如果不是第一条指令，那么该块可以终止
                    if (nodeList.size() > 0) {
                        // 当前block收集数据完成
                        completeBlock();

                        // 下一个block成为当前block
                        InsnBlock nextBlock = new InsnBlock();
                        controlFlowGraph.getCfg().addVertex(nextBlock);
                        controlFlowGraph.getCfg().addEdge(currentBlock,nextBlock);
                        currentBlock.addNextBlocks(nextBlock);
                        currentBlock = nextBlock;

                    }

                }

                nodeList.add(node);
                labelInBlockMap.put(currentNode, currentBlock);
            }
            else if (node instanceof InsnNode) {
                nodeList.add(node);

                // 终止指令
                if ((opcode >= IRETURN && opcode <= RETURN) || (opcode == ATHROW)) {
                    // 当前block收集数据完成
                    completeBlock();

                    currentBlock = new InsnBlock();
                }

            }
            else {
                nodeList.add(node);
            }
        }

        // 如果该方法没有return，需要在迭代完所有语句后终止并处理当前存放的指令
        if (nodeList.size() > 0) {
            // 当前block收集数据完成
            completeBlock();
        }
    }

    /*处理跳转指令带来的控制流关系，这里用了一个jumpLabelMap记录的块的跳转*/
    private void linkBlocks() {
        for (Map.Entry<LabelNode, Set<InsnBlock>> item : jumpLabelMap.entrySet()) {
            LabelNode key = item.getKey();
            Set<InsnBlock> set = item.getValue();

            InsnBlock targetBlock = labelInBlockMap.get(key);
            for (InsnBlock block : set) {
                controlFlowGraph.getCfg().addEdge(block,targetBlock);
                block.addNextBlocks(targetBlock);
            }
        }
    }

    private void addJumpFromBlockToLabel(LabelNode labelNode) {
        Set<InsnBlock> list = jumpLabelMap.get(labelNode);
        if (list != null) {
            list.add(currentBlock);

        }
        else {
            list = new HashSet<>();
            list.add(currentBlock);
            jumpLabelMap.put(labelNode, list);
        }
    }

    private void completeBlock() {
        // 将当前节点加入控制流图
        controlFlowGraph.getCfg().addVertex(currentBlock);

        // 将头节点返回作为block中头指令，head,类型为AbstractInsnNode
        currentBlock.setHead(nodeList.get(0));

        // 将其余指令存入block体中，形式为string（冗余，仅用于返回规范化的指令以便检查）
        currentBlock.addInsns(nodeList);
        for (AbstractInsnNode node : nodeList) {
            List<String> lines = insnText.toLines(node);
            currentBlock.addLines(lines);
        }

        nodeList.clear();
    }

    public ControlFlowGraph getControlFlowGraph() {
        return controlFlowGraph;
    }
}
