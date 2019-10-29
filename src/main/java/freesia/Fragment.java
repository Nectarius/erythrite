
package freesia;

import java.util.ArrayList;
import java.util.List;

public class Fragment {

  private final List<String> data;

  public Fragment(List<String> data) {
    this.data = data;
  }

  public List<String> getData() {
    return data;
  }

  public List<Fragment> divide(int num) {

    if (this.data == null)
      return null;

    List<Fragment> inputs = new ArrayList<>();

    int chunk = data.size()/num +1;
    for (int i = 0; i < data.size(); i += chunk) {

      List<String> sublist = data.subList(i, Math.min(data.size(), i + chunk));
      inputs.add(new Fragment(sublist));
    }
    return inputs;
  }
}
