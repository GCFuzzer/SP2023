package janala.logger.inst;

public class BLOCK_BEGIN extends Instruction{

    public BLOCK_BEGIN(int iid, int mid) {
        super(iid, mid);
    }

    public void visit(IVisitor visitor) {
        visitor.visitBLOCK_BEGIN(this);
    }

    @Override
    public String toString() {
        return "BLOCK_BEGIN"
                + " iid="
                + iid
                + " mid="
                + mid;
    }
}
