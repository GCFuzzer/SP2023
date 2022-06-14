package distanceCalculator.ControlFlowGraphGenerator;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.ArrayList;
import java.util.List;

/*代表一个Basic Block块*/
public class InsnBlock {
    //Block中指令集（是否需要这些指令，后续是否以block作为逻辑单元进行利用？）
    public final List<String> lines = new ArrayList<>();
    public final List<AbstractInsnNode> labels = new ArrayList<>();
    public double block2Target_distance;
    public List<InsnBlock> nextBlocks  = new ArrayList<>();;

    // Block中第一条指令，用于插桩
    private AbstractInsnNode headInstruction;

    public void setDistance(double block2Target_distance) {
        this.block2Target_distance = block2Target_distance;
    }

    public void addNextBlocks(InsnBlock nextBlock) {
        nextBlocks.add(nextBlock);
    }

    public double getDistance() {
        return block2Target_distance;
    }

    public void setHead(AbstractInsnNode headInstruction){
        this.headInstruction = headInstruction;
    }

    public AbstractInsnNode getHead(){
        return headInstruction;
    }

    public void addLines(List<String> list) {
        lines.addAll(list);
    }
    public void addInsns(List<AbstractInsnNode> list){
        labels.addAll(list);
    }


}
