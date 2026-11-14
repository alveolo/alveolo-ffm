package test;

interface Parent {
  long invoke(long value);
}

@org.alveolo.ffm.Callback(name = "Renamed")
interface Child extends Parent {}
