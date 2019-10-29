package freesia.memoryleak;

public class StringLeaker
{
    private final String muchSmallerString;

    public StringLeaker()
    {
        // Imagine the whole Declaration of Independence here
        String veryLongString = "We hold these truths to be self-evident...";

        // The substring here maintains a reference to the internal char[]
        // representation of the original string.
        this.muchSmallerString = veryLongString.substring(0, 1).intern();
    }
}