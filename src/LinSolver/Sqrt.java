package LinSolver;

/**
 *
 * @author Jon
 */
public class Sqrt {
    public static final String s = "sqrt";
    private RationalBigInteger i;
    public Sqrt(int i) {
        this.i = new RationalBigInteger(i);
    }
    public Sqrt(long i) {
        this.i = new RationalBigInteger(i);
    }
    public Sqrt(RationalBigInteger i) {
        this.i = i;
    }
    public RationalBigInteger squared() {
        return i;
    }
    @Override
    public String toString() {
        if (Math.sqrt(i.getNumerator().longValue()) - (long)Math.sqrt(i.getNumerator().longValue()) == 0
                && Math.sqrt(i.getDenominator().longValue()) - Math.sqrt(i.getDenominator().longValue()) == 0) {
            return new RationalBigInteger((long)Math.sqrt(i.getNumerator().longValue()), (long)Math.sqrt(i.getDenominator().longValue())).toString();
        }
        return s + "(" + i + ")";
    }
    public static void main(String[] args) {
        for(int i = -200; i < 10000; i++) {
            Sqrt s = new Sqrt(i);
            System.out.println(s);
        }
    }
}
