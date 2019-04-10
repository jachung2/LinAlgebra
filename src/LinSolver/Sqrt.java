package LinSolver;

/**
 *
 * @author Jon
 */
public class Sqrt {
    private static final String s = "sqrt";
    public static final Sqrt ONE = new Sqrt(1);
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

    public static Sqrt multiply(Sqrt a, Sqrt b) {

        return new Sqrt(a.i.multiply(b.i));
    }
    public int stringSize() {
        return toString().length();
    }
    public boolean isONE() {
        return toString().equals("1");
    }
    @Override
    public String toString() {
        if (i.getDenominator().compareTo(i.getNumerator()) <= 0 && Math.sqrt(i.getNumerator().longValue()) - (long)Math.sqrt(i.getNumerator().longValue()) == 0
                && Math.sqrt(i.getDenominator().longValue()) - Math.sqrt(i.getDenominator().longValue()) == 0) {
            return new RationalBigInteger((long)Math.sqrt(i.getNumerator().longValue()), (long)Math.sqrt(i.getDenominator().longValue())).toString();
        }
        return s + "(" + i + ")";
    }
    public static void main(String[] args) {
        Sqrt a = new Sqrt(new RationalBigInteger("1/2"));
        System.out.println(a.toString());
        int x = 1;
        long y = 1;
        RationalBigInteger i = new RationalBigInteger(x);
        RationalBigInteger j = new RationalBigInteger(y);
        System.out.println(i.equals(j));
    }
}
