public class SymTab<Whatever extends Base> extends Tree<Whatever> {
    public SymTab (String datafile, Whatever sample, String caller) {
        super (datafile, sample, caller);
    }
}
