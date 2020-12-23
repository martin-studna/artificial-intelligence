public class Result<A> {
  public double value;
  public A action;
  public long level;

  public Result(double value, A action, long level) {
    this.value = value;
    this.action = action;
    this.level = level;
  }
}
