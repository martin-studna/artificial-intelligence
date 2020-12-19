import java.util.ArrayList;
import java.util.List;

public class Node<S, A> {
  public List<Node<S, A>> children;
  public S state;
  public double value;
  
  public Node(S state, double value) {
    this.children = new ArrayList<Node<S, A>>();
    this.state = state;
    this.value = value;
  }

}
