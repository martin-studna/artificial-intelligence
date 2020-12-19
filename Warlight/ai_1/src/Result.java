public class Result<A> {
  public double value;
  public A action;

  public Result(double value, A action) {
    this.value = value;
    this.action = action;
  }
}
