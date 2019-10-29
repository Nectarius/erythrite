package freesia.memoryleak;

class BadKey {
   // no hashCode or equals();
   public final String key;
   public BadKey(String key) { this.key = key; }
}
