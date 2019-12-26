package ca.derekcormier.recipe;

public class DirectDispatchOven extends Oven {
  private BackendOven backendOven;

  public DirectDispatchOven(BackendOven backendOven) {
    _setDefaultDispatcher(payload -> backendOven.bake(payload));
  }

  @Override
  public void setDefaultDispatcher(Dispatcher dispatcher) {
    throw new UnsupportedOperationException(
        "cannot overwrite default dispatcher in DirectDispatchOven");
  }

  @Override
  public void addDispatcher(String domain, Dispatcher dispatcher) {
    throw new UnsupportedOperationException("cannot add dispatchers to a DirectDispatchOven");
  }
}
